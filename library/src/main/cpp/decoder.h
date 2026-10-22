//
// Created by 陈冰 on 2019/8/30.
//

#ifndef COORCHICELIBONE_DECODER_H
#define COORCHICELIBONE_DECODER_H

#include "common.h"
#include "gif_lib.h"
#include "gifinfo.h"
#include <android/bitmap.h>


jlong openFile(JNIEnv *env, jclass clazz, jstring jpath);
jlong openBytes(JNIEnv *env, jclass clazz, jbyteArray bytes);

bool fileIsGif(JNIEnv *env, jclass clazz, jstring jpath);

bool bytesIsGif(JNIEnv *env, jclass clazz, jbyteArray bytes);


#endif //COORCHICELIBONE_DECODER_H
