// Created by 陈冰 on 2019/8/30.
#include "decoder.h"
#include <limits.h>

static bool initGifInfo(GifFileType *gif, GifInfo *info) {
    if (DGifSlurp(gif) != GIF_OK || gif->ImageCount <= 0 ||
        gif->SWidth <= 0 || gif->SHeight <= 0 ||
        static_cast<int64_t>(gif->SWidth) * gif->SHeight > INT_MAX / 4) return false;
    info->graphicsControlBlock = static_cast<GraphicsControlBlock *>(
        calloc(gif->ImageCount, sizeof(GraphicsControlBlock)));
    if (info->graphicsControlBlock == nullptr) return false;
    int64_t totalDuration = 0;
    for (int i = 0; i < gif->ImageCount; ++i) {
        const SavedImage &frame = gif->SavedImages[i];
        const GifImageDesc &d = frame.ImageDesc;
        const ColorMapObject *colors = d.ColorMap != nullptr ? d.ColorMap : gif->SColorMap;
        if (d.Left < 0 || d.Top < 0 || d.Width <= 0 || d.Height <= 0 ||
            d.Left + d.Width > gif->SWidth || d.Top + d.Height > gif->SHeight ||
            frame.RasterBits == nullptr || colors == nullptr || colors->Colors == nullptr) return false;
        for (size_t p = 0; p < static_cast<size_t>(d.Width) * d.Height; ++p) {
            if (frame.RasterBits[p] >= colors->ColorCount) return false;
        }
        GraphicsControlBlock &control = info->graphicsControlBlock[i];
        control.TransparentColor = NO_TRANSPARENT_COLOR;
        // GIF87a and GIF89a without a graphics extension have valid defaults.
        for (int j = 0; j < frame.ExtensionBlockCount; ++j) {
            const ExtensionBlock &block = frame.ExtensionBlocks[j];
            if (block.Function == GRAPHICS_EXT_FUNC_CODE) {
                if (DGifExtensionToGCB(block.ByteCount, block.Bytes, &control) != GIF_OK) return false;
                break;
            }
        }
        totalDuration += static_cast<int64_t>(control.DelayTime) * 10;
    }
    info->totalDuration = static_cast<int>(totalDuration > INT_MAX ? INT_MAX : totalDuration);
    info->frameDuration = static_cast<int>(totalDuration / gif->ImageCount);
    info->width = gif->SWidth;
    info->height = gif->SHeight;
    info->totalFrame = gif->ImageCount;
    gif->UserData = info;
    return true;
}

static void releaseFailedOpen(JNIEnv *env, GifFileType *gif, GifInfo *info) {
    if (gif != nullptr) {
        int error = 0;
        DGifCloseFile(gif, &error);
    }
    if (info != nullptr) {
        if (info->buffer != nullptr) env->DeleteGlobalRef(info->buffer);
        free(info->graphicsControlBlock);
        delete info;
    }
}

jlong openFile(JNIEnv *env, jclass, jstring path) {
    char *filename = jstring2string(env, path);
    if (filename == nullptr) return 0;
    int error = 0;
    GifFileType *gif = DGifOpenFileName(filename, &error);
    free(filename);
    if (gif == nullptr) return 0;
    GifInfo *info = new GifInfo();
    if (!initGifInfo(gif, info)) {
        releaseFailedOpen(env, gif, info);
        return 0;
    }
    return reinterpret_cast<jlong>(gif);
}

jlong openBytes(JNIEnv *env, jclass, jbyteArray bytes) {
    if (bytes == nullptr || env->GetArrayLength(bytes) < 6) return 0;
    GifInfo *info = new GifInfo();
    info->buffer = static_cast<jbyteArray>(env->NewGlobalRef(bytes));
    if (info->buffer == nullptr) { delete info; return 0; }
    info->length = static_cast<unsigned int>(env->GetArrayLength(bytes));
    int error = 0;
    GifFileType *gif = DGifOpen(info, bytesRead, &error);
    if (gif == nullptr || !initGifInfo(gif, info)) {
        releaseFailedOpen(env, gif, info);
        return 0;
    }
    return reinterpret_cast<jlong>(gif);
}

static bool isSignature(const char *header) {
    return memcmp(header, GIF87_STAMP, GIF_STAMP_LEN) == 0 ||
           memcmp(header, GIF89_STAMP, GIF_STAMP_LEN) == 0;
}

bool bytesIsGif(JNIEnv *env, jclass, jbyteArray bytes) {
    if (bytes == nullptr || env->GetArrayLength(bytes) < GIF_STAMP_LEN) return false;
    char header[GIF_STAMP_LEN];
    env->GetByteArrayRegion(bytes, 0, GIF_STAMP_LEN, reinterpret_cast<jbyte *>(header));
    return !env->ExceptionCheck() && isSignature(header);
}

bool fileIsGif(JNIEnv *env, jclass, jstring path) {
    char *filename = jstring2string(env, path);
    if (filename == nullptr) return false;
    FILE *file = fopen(filename, "rb");
    free(filename);
    if (file == nullptr) return false;
    char header[GIF_STAMP_LEN];
    const size_t count = fread(header, 1, sizeof(header), file);
    fclose(file);
    return count == sizeof(header) && isSignature(header);
}
