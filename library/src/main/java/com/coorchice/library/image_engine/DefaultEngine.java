/*
 * Copyright (C) 2018 CoorChice <icechen_@outlook.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * <p>
 * Last modified 18-5-13 上午11:36
 */

package com.coorchice.library.image_engine;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.coorchice.library.ImageEngine;
import com.coorchice.library.utils.ThreadPool;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

/**
 * Project Name:SuperTextView
 * Author:CoorChice
 * Date:2018/5/13
 * Notes:
 */

/**
 * 在调用{@link com.coorchice.library.SuperTextView#setUrlImage(String)}后，
 * 如果没有通过{@link ImageEngine#install(Engine)}配置过图片下载引擎，将使用这个简易版的
 * 图片下载引擎。
 * 建议开发者根据项目情况自行实现{@link Engine}，然后通过{@link ImageEngine#install(Engine)}配置图片下载引擎。
 */
public class DefaultEngine implements Engine {
  @Override
  public void load(final String url, final ImageEngine.Callback callback) {
    ThreadPool.run(new Runnable() {
      @Override
      public void run() {
        try {
          byte[] bytes = getBytesArrayFromNet(url);
          final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
          final BitmapDrawable drawable = new BitmapDrawable(Resources.getSystem(), bitmap);
          new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
              callback.onCompleted(drawable);
            }
          });
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }


  private static byte[] getBytesArrayFromNet(String path) throws Exception {
    // 1.实例化URL对象并指定网址
    URL url = new URL(Uri.encode(path,":/-![].,%?&="));
    // 2.打开连接并返回HttpURLConnection对象
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    // 3.设置相关参数
    httpURLConnection.setConnectTimeout(5000);// 设置连接超时时间
    httpURLConnection.setRequestMethod("GET");// 设置请求方法
    // 4.将请求提交到服务器并得到服务器端的响应结果
    int responseCode = httpURLConnection.getResponseCode();// 得到了响应码,只有响应码等于200表示OK
    if (responseCode == 200) {
      InputStream inputStream = null;
      try {
        inputStream = httpURLConnection.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
          byteArrayOutputStream.write(buffer, 0, len);
        }

        return byteArrayOutputStream.toByteArray();
      } finally {
        if (inputStream != null) {
          inputStream.close();
          inputStream = null;
        }
      }

    }
    return null;
  }
}
