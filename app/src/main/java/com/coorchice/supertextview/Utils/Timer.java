/*
 * Copyright (C) 2017 CoorChice <icechen_@outlook.com>
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
 * Last modified 17-11-9 上午12:38
 */

package com.coorchice.supertextview.Utils;

import android.util.Log;

import com.coorchice.supertextview.BuildConfig;

/**
 * Created by coorchice on 2017/10/12.
 */

public enum Timer {

    ONE;

    private long startTime;

    public static final int LOG_E = 0x001;

    public void begin(){
        begin(System.currentTimeMillis());
    }

    public void begin(long startTime){
        this.startTime = startTime;
    }

    public long deltaT(long endTime){
        if (startTime != 0){
            long temp = startTime;
            startTime = 0;
            return endTime - temp;
        } else {
            return -1;
        }
    }

    public String printDeltaT(String tag){
        return printDeltaT(tag, System.currentTimeMillis());
    }

    public String printDeltaT(String tag, long endTime){
        StringBuilder sb = new StringBuilder();
        sb.append(tag);
        if (!tag.contains(":")){
            sb.append(": ");
        }
        long deltaT = deltaT(endTime);
        if (deltaT != -1){
            sb.append("+ ").append(deltaT).append(" ms");
        } else {
            sb.append("统计参数错误，请仔细核对！");
        }
        return sb.toString();
    }

    public void logDeltaT(String tag, int type){
        if (BuildConfig.DEBUG)
            if (type == LOG_E){
                Log.e("Timer: ", printDeltaT(tag));
            }
    }
}
