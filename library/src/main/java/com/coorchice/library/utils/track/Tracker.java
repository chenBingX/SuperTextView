package com.coorchice.library.utils.track;

import com.coorchice.library.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Project Name:SuperTextView
 * Author:CoorChice
 * Date:2019-09-07
 * Notes:
 */
public class Tracker {

  private String tag;
  private List<Watcher> set;

  public Tracker(String tag) {
    this.tag = tag;
    if (LogUtils.DEBUG) {
      set = new ArrayList<>();
    }
  }

  public final void addWatcher(Watcher watcher) {
    if (set == null || watcher == null || !LogUtils.DEBUG) return;
    synchronized (set) {
      set.add(watcher);
    }
  }

  public static void notifyEvent(Tracker tracker, Event event) {
    if (tracker == null || tracker.set == null || event == null || !LogUtils.DEBUG) return;
    List<Watcher> set = tracker.set;
    synchronized (set) {
      for (Watcher watcher : set) {
        if (watcher.type.equals(event.getType())) {
          watcher.onEvent(event);
        }
      }
    }
  }

  public static abstract class Watcher<T> {
    public String type;

    public Watcher(String type) {
      this.type = type;
    }

    public abstract void onEvent(T event);
  }


}
