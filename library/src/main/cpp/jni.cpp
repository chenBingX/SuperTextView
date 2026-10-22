//
// Created by 陈冰 on 2019/8/29.
//

#include "decoder.h"
#include "drawer.h"

static JavaVM *g_jvm;
JNIEnv *getEnv() {
    // giflib reads synchronously inside a Java native call; the caller is attached.
    JNIEnv *env = nullptr;
    if (g_jvm != nullptr && g_jvm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) == JNI_OK) {
        return env;
    }
    return nullptr;
}

static bool lockBitmap(JNIEnv *env, jobject bitmap, const GifInfo *gif,
                       AndroidBitmapInfo *info, void **pixels) {
    if (bitmap == nullptr || AndroidBitmap_getInfo(env, bitmap, info) != ANDROID_BITMAP_RESULT_SUCCESS ||
        info->format != ANDROID_BITMAP_FORMAT_RGBA_8888 ||
        info->width < static_cast<uint32_t>(gif->width) ||
        info->height < static_cast<uint32_t>(gif->height)) return false;
    return AndroidBitmap_lockPixels(env, bitmap, pixels) == ANDROID_BITMAP_RESULT_SUCCESS;
}

bool checkIsNull(jlong ptr) {
    GifFileType *gifFileType = (GifFileType *) ptr;
    return gifFileType == nullptr || gifFileType->UserData == nullptr;
}

jint updateFrame(JNIEnv *env, jclass clazz, jlong ptr, jobject bitmap) {
    if (checkIsNull(ptr))
        return 0;
    GifFileType *gifFileType = (GifFileType *) ptr;
    GifInfo *gifInfo = (GifInfo *) (gifFileType->UserData);
    AndroidBitmapInfo bitmapInfo;
    long renderStartTime = getRealTime();
    // 一张图片的数组
    void *pixels;
    if (!lockBitmap(env, bitmap, gifInfo, &bitmapInfo, &pixels)) return 0;
    drawFrame(gifFileType, nullptr, bitmapInfo, pixels);
    gifInfo->relFrame = (gifInfo->curFrame);
    gifInfo->curFrame += 1;
    if (gifInfo->curFrame >= gifInfo->totalFrame) {
        gifInfo->curFrame = 0;
    }
    // 解锁画布
    AndroidBitmap_unlockPixels(env, bitmap);
    return calculateInvalidationDelay(renderStartTime, gifInfo->frameDuration);
}

jint getWidth(JNIEnv *env, jclass clazz, jlong ptr) {
    if (checkIsNull(ptr))
        return 1;
    GifFileType *gifFileType = (GifFileType *) ptr;
    GifInfo *gifInfo = (GifInfo *) (gifFileType->UserData);
    return gifInfo->width;
}

jint getHeight(JNIEnv *env, jclass clazz, jlong ptr) {
    if (checkIsNull(ptr))
        return 1;
    GifFileType *gifFileType = (GifFileType *) ptr;
    GifInfo *gifInfo = (GifInfo *) (gifFileType->UserData);
    return gifInfo->height;
}

jint getFrameCount(JNIEnv *env, jclass clazz, jlong ptr) {
    if (checkIsNull(ptr))
        return 1;
    GifFileType *gifFileType = (GifFileType *) ptr;
    GifInfo *gifInfo = (GifInfo *) (gifFileType->UserData);
    return gifInfo->totalFrame;
}

jint getFrameDuration(JNIEnv *env, jclass clazz, jlong ptr) {
    if (checkIsNull(ptr))
        return 1;
    GifFileType *gifFileType = (GifFileType *) ptr;
    GifInfo *gifInfo = (GifInfo *) (gifFileType->UserData);
    return gifInfo->frameDuration;
}

void setFrameDuration(JNIEnv *env, jclass clazz, jlong ptr, jint duration) {
    if (checkIsNull(ptr))
        return;
    GifFileType *gifFileType = (GifFileType *) ptr;
    GifInfo *gifInfo = (GifInfo *) (gifFileType->UserData);
    gifInfo->frameDuration = duration;
}

jint getCurrentFrame(JNIEnv *env, jclass clazz, jlong ptr) {
    if (checkIsNull(ptr))
        return 1;
    GifFileType *gifFileType = (GifFileType *) ptr;
    GifInfo *gifInfo = (GifInfo *) (gifFileType->UserData);
    return gifInfo->relFrame;
}

void gotoFrame(JNIEnv *env, jclass clazz, jlong ptr, jint position, jobject bitmap) {
    if (checkIsNull(ptr))
        return;
    GifFileType *gifFileType = (GifFileType *) ptr;
    GifInfo *gifInfo = (GifInfo *) (gifFileType->UserData);
    if (position >= gifInfo->totalFrame) {
        position = gifInfo->totalFrame - 1;
    }
    if (position < 0) {
        position = 0;
    }
    if (gifInfo->strict) {
        gifInfo->curFrame = 0;
        for (int i = 0; i <= position; ++i) {
            updateFrame(env, clazz, ptr, bitmap);
        }
    } else {
        gifInfo->curFrame = position;
        updateFrame(env, clazz, ptr, bitmap);
    }
    gifInfo->relFrame = position;
}

void setFrame(JNIEnv *env, jclass clazz, jlong ptr, jint position) {
    if (checkIsNull(ptr))
        return;
    GifFileType *gifFileType = (GifFileType *) ptr;
    GifInfo *gifInfo = (GifInfo *) (gifFileType->UserData);
    if (position >= gifInfo->totalFrame) {
        position = gifInfo->totalFrame - 1;
    }
    if (position < 0) {
        position = 0;
    }
    gifInfo->curFrame = position;
    gifInfo->relFrame = position;
}

void getFrame(JNIEnv *env, jclass clazz, jlong ptr, jint position, jobject bitmap) {
    if (checkIsNull(ptr))
        return;
    GifFileType *gifFileType = (GifFileType *) ptr;
    GifInfo *gifInfo = (GifInfo *) (gifFileType->UserData);
    if (gifInfo->strict) {
        GifInfo *temp = new GifInfo();
        temp->width = gifInfo->width;
        temp->height = gifInfo->height;
        temp->totalFrame = gifInfo->totalFrame;
        temp->graphicsControlBlock = gifInfo->graphicsControlBlock;
        if (position >= temp->totalFrame) {
            position = temp->totalFrame - 1;
        }
        if (position < 0) {
            position = 0;
        }
        AndroidBitmapInfo bitmapInfo;
        // 一张图片的数组
        void *pixels;
        if (!lockBitmap(env, bitmap, gifInfo, &bitmapInfo, &pixels)) {
            delete temp;
            return;
        }
        temp->curFrame = 0;
        for (int i = 0; i <= position; ++i) {
            drawFrame(gifFileType, temp, bitmapInfo, pixels);
            temp->curFrame += 1;
            if (temp->curFrame >= temp->totalFrame) {
                temp->curFrame = 0;
            }
        }
        // 解锁画布
        AndroidBitmap_unlockPixels(env, bitmap);
        delete temp;
    } else {
        if (position >= gifInfo->totalFrame) {
            position = gifInfo->totalFrame - 1;
        }
        if (position < 0) {
            position = 0;
        }
        const int nextFrame = gifInfo->curFrame;
        const int currentFrame = gifInfo->relFrame;
        gifInfo->curFrame = position;
        updateFrame(env, clazz, ptr, bitmap);
        gifInfo->curFrame = nextFrame;
        gifInfo->relFrame = currentFrame;
    }
}

void setStrict(JNIEnv *env, jclass clazz, jlong ptr, jboolean strict) {
    if (checkIsNull(ptr))
        return;
    GifFileType *gifFileType = (GifFileType *) ptr;
    GifInfo *gifInfo = (GifInfo *) (gifFileType->UserData);
    gifInfo->strict = strict;
}

jboolean getStrict(JNIEnv *env, jclass clazz, jlong ptr) {
    if (checkIsNull(ptr))
        return false;
    GifFileType *gifFileType = (GifFileType *) ptr;
    GifInfo *gifInfo = (GifInfo *) (gifFileType->UserData);
    return gifInfo->strict;
}

void destroy(JNIEnv *env, jclass jclazz, jlong ptr) {
    GifFileType *gifFileType = (GifFileType *) ptr;
    if (gifFileType == NULL)
        return;
    GifInfo *gifInfo = (GifInfo *) (gifFileType->UserData);
    if (gifInfo != NULL) {
        if (gifInfo->graphicsControlBlock != NULL) {
            free(gifInfo->graphicsControlBlock);
            gifInfo->graphicsControlBlock = NULL;
        }
        if (gifInfo->buffer != NULL) {
            env->DeleteGlobalRef(gifInfo->buffer);
            gifInfo->buffer = NULL;
        }
        delete gifInfo;
        gifFileType->UserData = NULL;
    }
    DGifCloseFile(gifFileType, GIF_ERROR);
    gifFileType = NULL;
}


void copyGifDestroy(JNIEnv *env, jclass jclazz, jlong ptr) {
    GifFileType *gifFileType = (GifFileType *) ptr;
    if (gifFileType == NULL)
        return;
    GifInfo *gifInfo = (GifInfo *) (gifFileType->UserData);
    if (gifInfo != NULL) {
        gifInfo->width = 0;
        gifInfo->height = 0;
        gifInfo->totalFrame = 0;
        gifInfo->frameDuration = 0;
        gifInfo->totalDuration = 0;
        gifInfo->length = 0;
        gifInfo->graphicsControlBlock = NULL;
        gifInfo->buffer = NULL;
        delete gifInfo;
    }
    memset(gifFileType, 0, sizeof(GifFileType));
    free(gifFileType);
    gifFileType = NULL;
}

jlong copyGif(JNIEnv *env, jclass jclazz, jlong ptr) {
    GifFileType *gifFileType = (GifFileType *) ptr;
    if (gifFileType == NULL)
        return 0;
    GifFileType* copy = (GifFileType*)malloc(sizeof(GifFileType));
    copy->SWidth = gifFileType->SWidth;
    copy->SHeight = gifFileType->SHeight;
    copy->SColorResolution = gifFileType->SColorResolution;
    copy->SBackGroundColor = gifFileType->SBackGroundColor;
    copy->AspectByte = gifFileType->AspectByte;
    copy->SColorMap = gifFileType->SColorMap;
    copy->ImageCount = gifFileType->ImageCount;
    copy->Image = gifFileType->Image;
    copy->SavedImages = gifFileType->SavedImages;
    copy->ExtensionBlockCount = gifFileType->ExtensionBlockCount;
    copy->ExtensionBlocks = gifFileType->ExtensionBlocks;
    copy->Error = gifFileType->Error;
    if (gifFileType->UserData){
        GifInfo *gifInfo = (GifInfo *)(gifFileType->UserData);
        GifInfo *copyGifInfo = new GifInfo();
        copyGifInfo->width = gifInfo->width;
        copyGifInfo->height = gifInfo->height;
        copyGifInfo->curFrame = 0;
        copyGifInfo->relFrame = 0;
        copyGifInfo->totalFrame = gifInfo->totalFrame;
        copyGifInfo->frameDuration = gifInfo->frameDuration;
        copyGifInfo->totalDuration = gifInfo->totalDuration;
        copyGifInfo->graphicsControlBlock = gifInfo->graphicsControlBlock;
        copyGifInfo->curPosition = 0;
        copyGifInfo->length = gifInfo->length;
        copyGifInfo->buffer = gifInfo->buffer;
        copyGifInfo->strict = false;
        copy->UserData = copyGifInfo;
    }
    copy->Private = gifFileType->Private;
    return (long long) copy;
}


// 提供一个函数映射表，注册给JVM，这样JVM就可以通过函数映射表来调用相应的函数
// 这样的效率比静态注册的效率高
/**
 * @param1 Java中的native方法名。可以自定义。
 * @param2 函数签名，描述函数的返回值和参数
 * @param3 函数指针，指向被调用的c++函数。名车需要和函数名一样。
 */
static JNINativeMethod nativeMethod[] = {
        {"openFile",         "(Ljava/lang/String;)J",          (void *) openFile},
        {"openBytes",        "([B)J",                          (void *) openBytes},
        {"fileIsGif",        "(Ljava/lang/String;)Z",          (void *) fileIsGif},
        {"bytesIsGif",       "([B)Z",                          (void *) bytesIsGif},
        {"updateFrame",      "(JLandroid/graphics/Bitmap;)I",  (void *) updateFrame},
        {"getWidth",         "(J)I",                           (void *) getWidth},
        {"getHeight",        "(J)I",                           (void *) getHeight},
        {"getFrameCount",    "(J)I",                           (void *) getFrameCount},
        {"getFrameDuration", "(J)I",                           (void *) getFrameDuration},
        {"setFrameDuration", "(JI)V",                          (void *) setFrameDuration},
        {"getCurrentFrame",  "(J)I",                           (void *) getCurrentFrame},
        {"gotoFrame",        "(JILandroid/graphics/Bitmap;)V", (void *) gotoFrame},
        {"getFrame",         "(JILandroid/graphics/Bitmap;)V", (void *) getFrame},
        {"setFrame",         "(JI)V",                          (void *) setFrame},
        {"setStrict",        "(JZ)V",                          (void *) setStrict},
        {"getStrict",        "(J)Z",                           (void *) getStrict},
        {"destroy",          "(J)V",                           (void *) destroy},
        {"copy",             "(J)J",                           (void *) copyGif},
        {"copyDestroy",          "(J)V",                           (void *) copyGifDestroy},

};


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
    JNIEnv *env;
    g_jvm = jvm;
    if (jvm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    jclass clz = env->FindClass(JAVA_JNI_CLASS);
    if (clz == nullptr) return JNI_ERR;

    jint method_size = sizeof(nativeMethod) / sizeof(nativeMethod[0]);
    /**
      * 注册函数表
      * @param1 需要关联到那个【Java】类，Kotlin类不行
      * @param2 方法数组
      * @param3 方法数
      */
    const jint result = env->RegisterNatives(clz, nativeMethod, method_size);
    env->DeleteLocalRef(clz);
    return result == JNI_OK ? JNI_VERSION_1_6 : JNI_ERR;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *jvm, void *reserved) {
    JNIEnv *env;
    if (jvm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return;
    }
    jclass clz = env->FindClass(JAVA_JNI_CLASS);
    // 解注册函数表
    env->UnregisterNatives(clz);
}
