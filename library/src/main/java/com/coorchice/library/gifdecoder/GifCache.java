/*
 * Copyright (C) 2019 CoorChice <icechen_@outlook.com>
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
 * Last modified 11/6/19 2:05 PM
 */

package com.coorchice.library.gifdecoder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.coorchice.library.ImageEngine;
import com.coorchice.library.utils.STVUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author coorchice
 * @date 2019/11/05
 */
public class GifCache {

    private final Map<String, GifDrawable> map = new ConcurrentHashMap<>();
    private final List<String> refCount = new ArrayList<>();
    private int maxSize = 10;

    private GifCache() {

    }

    private boolean refCountContainer(String key) {
        for (String temp : refCount) {
            if (TextUtils.equals(temp, key)) return true;
        }
        return false;
    }

    private void refCountRemove(String key) {
        if (key == null) return;
        if (refCount.contains(key)) {
            refCount.remove(key);
        } else {
            String r = null;
            for (String temp : refCount) {
                if (TextUtils.equals(temp, key)) {
                    r = temp;
                    break;
                }
            }
            if (r != null) refCount.remove(r);
        }
    }

    private String findKey(String key) {
        if (key == null) return null;
        String relKey = key;
        if (!map.containsKey(key)) {
            relKey = null;
            for (String tempKey : map.keySet()) {
                if (TextUtils.equals(tempKey, key)) {
                    relKey = tempKey;
                    break;
                }
            }
        }
        return relKey;
    }

    private void put(String key, GifDrawable value) {
        if (key == null || value == null) return;
        synchronized (map) {
            String relKey = findKey(key);
            if (relKey != null) {
                map.put(relKey, value);
                refCountRemove(relKey);
                refCount.add(relKey);
            } else {
                map.put(key, value);
                if (!refCountContainer(key)) {
                    if (refCount.size() >= maxSize) {
                        remove(refCount.remove(0));
                    }
                    refCount.add(key);
                } else {
                    refCountRemove(key);
                    refCount.add(key);
                }
            }
            value.setDestroyable(false);
        }
    }

    public GifDrawable get(String key) {
        if (key == null) return null;
        synchronized (map) {
            String relKey = findKey(key);
            if (relKey == null) return null;
            GifDrawable gifDrawable = map.get(relKey);
            if (gifDrawable != null) {
                refCountRemove(relKey);
                refCount.add(relKey);
            }
            return gifDrawable;
        }
    }

    public GifDrawable remove(String key) {
        if (key == null) return null;
        synchronized (map) {
            String relKey = findKey(key);
            if (relKey == null) return null;
            GifDrawable remove = map.remove(relKey);
            if (remove != null) {
                remove.setDestroyable(true);
                refCountRemove(relKey);
            }
            return remove;
        }
    }

    public void clear() {
        synchronized (map) {
            for (GifDrawable temp : map.values()) {
                temp.setDestroyable(true);
                temp.destroy();
            }
            map.clear();
            refCount.clear();
        }
    }

    private static final class Holder {
        private static final GifCache INSTANCE = new GifCache();
    }

    public static void setSize(int maxSize) {
        Holder.INSTANCE.maxSize = maxSize;
    }

    public static GifDrawable fromResource(Context context, int id) {
        String relKey = Holder.INSTANCE.findKey(String.valueOf(id));
        if (relKey == null) {
            byte[] bytes = STVUtils.getResBytes(context, id);
            if (bytes != null && GifDecoder.isGif(bytes)) {
                GifDrawable gifDrawable = GifDrawable.createDrawable(bytes);
                Holder.INSTANCE.put(String.valueOf(id), gifDrawable);
                return gifDrawable;
            }
        } else {
            GifDrawable gifDrawable = Holder.INSTANCE.get(relKey);
            if (gifDrawable == null) return null;
            if (gifDrawable.getCallback() == null) {
                gifDrawable.gotoFrame(0);
                gifDrawable.play();
                return gifDrawable;
            } else {
                return GifDrawable.copy(gifDrawable.getPtr());
            }
        }
        return null;
    }

    public static GifDrawable fromFile(String filePath) {
        String relKey = Holder.INSTANCE.findKey(filePath);
        if (relKey == null) {
            if (GifDecoder.isGif(filePath)) {
                GifDrawable gifDrawable = GifDrawable.createDrawable(filePath);
                Holder.INSTANCE.put(filePath, gifDrawable);
                return gifDrawable;
            }
        } else {
            GifDrawable gifDrawable = Holder.INSTANCE.get(relKey);
            if (gifDrawable == null) return null;
            if (gifDrawable.getCallback() == null) {
                gifDrawable.gotoFrame(0);
                gifDrawable.play();
                return gifDrawable;
            } else {
                return GifDrawable.copy(gifDrawable.getPtr());
            }
        }
        return null;
    }

    public static void fromUrl(final String url, final ImageEngine.Callback callback) {
        String relKey = Holder.INSTANCE.findKey(url);
        if (relKey == null) {
            if (GifDecoder.isGif(url)) {
                ImageEngine.load(url, new ImageEngine.Callback() {
                    @Override
                    public void onCompleted(Drawable drawable) {
                        if (drawable != null) {
                            Holder.INSTANCE.put(url, (GifDrawable) drawable);
                            callback.onCompleted(drawable);
                        }
                    }
                });
            }
        } else {
            GifDrawable gifDrawable = Holder.INSTANCE.get(relKey);
            if (gifDrawable == null) return;
            if (gifDrawable.getCallback() == null) {
                gifDrawable.gotoFrame(0);
                gifDrawable.play();
                callback.onCompleted(gifDrawable);
            } else {
                callback.onCompleted(GifDrawable.copy(gifDrawable.getPtr()));
            }
        }
    }
}
