//
// Created by CoorChice on 2019-08-30.
//

#ifndef COORCHICELIBONE_GIFINFO_H
#define COORCHICELIBONE_GIFINFO_H

#include "common.h"
#include "gif_lib.h"

class GifInfo{
public:
    int width;
    int height;
    int curFrame;
    int relFrame;
    int totalFrame;
    int frameDuration;
    int totalDuration;
    GraphicsControlBlock *graphicsControlBlock;

    uint_fast32_t curPosition;
    unsigned int length;
    jbyteArray buffer;

    bool strict;

    GifInfo();
//    ~GifInfo();

};

#endif //COORCHICELIBONE_GIFINFO_H
