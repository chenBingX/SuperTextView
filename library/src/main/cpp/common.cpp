// Created by 陈冰 on 2019/8/29.
#include "common.h"
#include <algorithm>
#include <time.h>

char *jstring2string(JNIEnv *env, jstring value) {
    if (value == nullptr) return nullptr;
    // File paths need real UTF-8, not JNI modified UTF-8 (supplementary chars).
    jclass type = env->FindClass("java/lang/String");
    if (type == nullptr) return nullptr;
    jmethodID method = env->GetMethodID(type, "getBytes", "(Ljava/lang/String;)[B");
    jstring encoding = env->NewStringUTF("UTF-8");
    if (method == nullptr || encoding == nullptr) {
        env->DeleteLocalRef(type);
        return nullptr;
    }
    jbyteArray utf = static_cast<jbyteArray>(env->CallObjectMethod(value, method, encoding));
    env->DeleteLocalRef(encoding);
    env->DeleteLocalRef(type);
    if (utf == nullptr || env->ExceptionCheck()) return nullptr;
    const jsize length = env->GetArrayLength(utf);
    char *result = static_cast<char *>(malloc(static_cast<size_t>(length) + 1));
    if (result != nullptr) {
        env->GetByteArrayRegion(utf, 0, length, reinterpret_cast<jbyte *>(result));
        result[length] = '\0';
        if (env->ExceptionCheck() || memchr(result, '\0', length) != nullptr) {
            free(result);
            result = nullptr;
        }
    }
    env->DeleteLocalRef(utf);
    return result;
}

// Match giflib's InputFunc exactly; an 8-bit length truncates larger reads.
int bytesRead(GifFileType *gif, GifByteType *bytes, int size) {
    GifInfo *info = static_cast<GifInfo *>(gif->UserData);
    JNIEnv *env = getEnv();
    if (env == nullptr || info == nullptr || size <= 0 ||
        info->curPosition >= info->length) return 0;
    const unsigned int remaining = info->length - info->curPosition;
    const int count = static_cast<int>(std::min(remaining, static_cast<unsigned int>(size)));
    env->GetByteArrayRegion(info->buffer, static_cast<jsize>(info->curPosition),
                           count, reinterpret_cast<jbyte *>(bytes));
    if (env->ExceptionCheck()) return 0;
    info->curPosition += count;
    return count;
}

long long calculateInvalidationDelay(long renderStartTime, uint_fast32_t frameDuration) {
    if (frameDuration == 0) return -1;
    const long elapsed = getRealTime() - renderStartTime;
    return elapsed >= frameDuration ? 0 : frameDuration - elapsed;
}

long getRealTime() {
    struct timespec ts = {};
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return ts.tv_sec * 1000L + ts.tv_nsec / 1000000L;
}
