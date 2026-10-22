//
// Created by CoorChice on 2019-08-30.
//

#ifndef COORCHICELIBONE_DRAWER_H
#define COORCHICELIBONE_DRAWER_H

#include "decoder.h"

void drawFrame(GifFileType *gifFileType, GifInfo *info, AndroidBitmapInfo bitmapInfo,void *pixels);
void prepareCanvas(GifFileType *gifFileType, AndroidBitmapInfo bitmapInfo,void *pixels);

#endif //COORCHICELIBONE_DRAWER_H
