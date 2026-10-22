//
// Created by CoorChice on 2019-08-30.
//

#include "drawer.h"

void
drawFrame(GifFileType *gifFileType, GifInfo *info, AndroidBitmapInfo bitmapInfo, void *pixels) {
    if (gifFileType->UserData == NULL)
        return;
    //获取当前帧
    GifInfo *gifInfo = info;
    if (gifInfo == nullptr) {
        gifInfo = (GifInfo *) (gifFileType->UserData);
    }
    if (gifInfo->curFrame == 0) {
        // A restarted/strict render must not inherit pixels from an older frame.
        for (uint32_t y = 0; y < bitmapInfo.height; ++y) {
            memset(static_cast<char *>(pixels) + y * bitmapInfo.stride, 0, bitmapInfo.stride);
        }
    }
    SavedImage savedImage = gifFileType->SavedImages[gifInfo->curFrame];
    GifImageDesc imageDesc = savedImage.ImageDesc;
    // 用于获取图形控制扩展信息
    GraphicsControlBlock GCB = gifInfo->graphicsControlBlock[gifInfo->curFrame];
    if (GCB.DisposalMode == DISPOSE_BACKGROUND) {
        //像素位置
//        GifColorType backgroundRGB = gifFileType->SColorMap->Colors[gifFileType->SBackGroundColor];
        //全局像素数组
        int *px = (int *) pixels;
        int *line;
        px = (int *) ((char *) px + bitmapInfo.stride * imageDesc.Top);
        for (int y = imageDesc.Top; y < imageDesc.Top + imageDesc.Height; y++) {
            // 一行像素数组
            line = px;
            for (int x = imageDesc.Left; x < imageDesc.Left + imageDesc.Width; x++) {
                line[x] = 0;
//                line[x] = argb(255, backgroundRGB.Red, backgroundRGB.Green, backgroundRGB.Blue);
            }
            // 处理完一行，指针移动到下一行
            px = (int *) ((char *) px + bitmapInfo.stride);
        }
    }
    //像素位置
    int pointPixel;
    //全局像素数组
    int *px = (int *) pixels;
    int *line;
    // 一个颜色
    GifColorType gifColorType;
    // 获取局部颜色列表
    ColorMapObject *colorMapObject = imageDesc.ColorMap;
    if (colorMapObject == NULL) {
        // 没有设置为全局颜色列表
        colorMapObject = gifFileType->SColorMap;
    }
    GifByteType gifByteType;
    px = (int *) ((char *) px + bitmapInfo.stride * imageDesc.Top);
    for (int y = imageDesc.Top; y < imageDesc.Top + imageDesc.Height; y++) {
        // 一行像素数组
        line = px;
        for (int x = imageDesc.Left; x < imageDesc.Left + imageDesc.Width; x++) {
            // 单点像素数组
            pointPixel = (y - imageDesc.Top) * imageDesc.Width + (x - imageDesc.Left);
            // 一个像素点的值
            gifByteType = savedImage.RasterBits[pointPixel];
            //需要给每个像素赋颜色
            if (colorMapObject != NULL) {
                if (GCB.TransparentColor == NO_TRANSPARENT_COLOR) {
                    // 通过索引，从颜色列表中获取颜色
                    gifColorType = colorMapObject->Colors[gifByteType];
                    // 合成一个像素点颜色，给它赋值
                    line[x] = argb(255, gifColorType.Red, gifColorType.Green, gifColorType.Blue);
                } else {
                    // 如果是透明的，就需要跳过
                    bool skip = gifByteType == GCB.TransparentColor;
                    if (!skip) {
                        // 通过索引，从颜色列表中获取颜色
                        gifColorType = colorMapObject->Colors[gifByteType];
                        // 合成一个像素点颜色，给它赋值
                        line[x] =
                                argb(255, gifColorType.Red, gifColorType.Green, gifColorType.Blue);
                    }
//                    else {
//                        line[x] = argb(0, 0, 0, 0);;
//                    }
                }
            }

        }
        // 处理完一行，指针移动到下一行
        px = (int *) ((char *) px + bitmapInfo.stride);
    }
}

void prepareCanvas(GifFileType *gifFileType, AndroidBitmapInfo bitmapInfo, void *pixels) {
    GifColorType backgroundRGB;
    if (gifFileType->SColorMap != NULL && gifFileType->SColorMap->Colors != NULL) {
        backgroundRGB = gifFileType->SColorMap->Colors[gifFileType->SBackGroundColor];
    }
    //全局像素数组
    int *px = (int *) pixels;
    int *line;
    for (int y = 0; y < gifFileType->SHeight; y++) {
        // 一行像素数组
        line = px;
        for (int x = 0; x < gifFileType->SWidth; x++) {
            if (gifFileType->SBackGroundColor == 0) {
                line[x] = argb(0, 0, 0, 0);
            } else {
                line[x] = argb(255, backgroundRGB.Red, backgroundRGB.Green, backgroundRGB.Blue);
            }
        }
        // 处理完一行，指针移动到下一行
        px = (int *) ((char *) px + bitmapInfo.stride);
    }
}