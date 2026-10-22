//
// Created by CoorChice on 2019-08-30.
//

#include "gifinfo.h"

GifInfo::GifInfo() {
    this->width = 1;
    this->height = 1;
    this->curFrame = 0;
    this->relFrame=0;
    this->totalFrame = 0;
    this->frameDuration = 0;
    this->totalDuration = 0;
    this->curPosition=0;
    this->length=0;
    this->buffer= nullptr;
    this->graphicsControlBlock = nullptr;
    this->strict= false;
}

//GifInfo::~GifInfo() {
//    if(graphicsControlBlock != nullptr){
//        free(graphicsControlBlock);
//        graphicsControlBlock = nullptr;
//    }
//    if(buffer != nullptr){
//        JNIEnv *env = getEnv();
//        if (env != NULL){
//            env->DeleteGlobalRef(this->buffer);
//            buffer = nullptr;
//        }
//    }
//}
