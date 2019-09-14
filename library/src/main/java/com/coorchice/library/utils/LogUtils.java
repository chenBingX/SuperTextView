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
 * Last modified 18-5-13 上午9:37
 */

package com.coorchice.library.utils;

import java.util.Locale;

import android.util.Log;

/**
 * Project Name:AnimDveDemo
 * Author:IceChen
 * Date:16/8/23
 * Notes:
 */

/**
 * @hide
 */
public class LogUtils {

  public static boolean DEBUG = false;
  private static final int INDEX = 4;

  private static String getPrefix() {
    StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[INDEX];
    String className = stackTraceElement.getClassName();
    int classNameStartIndex = className.lastIndexOf(".") + 1;
    className = className.substring(classNameStartIndex);
    String methodName = stackTraceElement.getMethodName();
    int methodLine = stackTraceElement.getLineNumber();
    String format = "%s-%s(L:%d)";
    return String.format(Locale.CHINESE, format, className, methodName, methodLine);
  }

  public static void v(String content) {
    if (DEBUG) Log.v(getPrefix(), content);
  }

  public static void v(String content, Throwable tr) {
    if (DEBUG) Log.v(getPrefix(), content, tr);
  }

  public static void d(String content) {
    if (DEBUG) Log.d(getPrefix(), content);
  }

  public static void d(String content, Throwable tr) {
    if (DEBUG) Log.d(getPrefix(), content, tr);
  }

  public static void i(String content) {
    if (DEBUG) Log.i(getPrefix(), content);
  }

  public static void i(String content, Throwable tr) {
    if (DEBUG) Log.i(getPrefix(), content, tr);
  }

  public static void w(String content) {
    if (DEBUG) Log.e(getPrefix(), content);
  }

  public static void w(String content, Throwable tr) {
    if (DEBUG) Log.w(getPrefix(), content, tr);
  }

  public static void e(String content) {
    if (DEBUG) Log.e(getPrefix(), content);
  }

  public static void e(String content, Throwable tr) {
    if (DEBUG) Log.e(getPrefix(), content, tr);
  }

  public static String showAllElementsInfo() {
    String print = "";
    int count = 0;
    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
    for (StackTraceElement stackTraceElement : stackTraceElements) {
      count++;
      print += String.format("ClassName:%s " +
          "\nMethodName:%s " +
          "\nMethodLine:%d " +
          "\n当前是第%d个 " +
          "\n---------------------------- " +
          "\n ",
          stackTraceElement.getClassName(),
          stackTraceElement.getMethodName(),
          stackTraceElement.getLineNumber(),
          count);
    }
    return print;
  }
}
