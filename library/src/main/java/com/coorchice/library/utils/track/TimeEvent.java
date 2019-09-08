package com.coorchice.library.utils.track;

import com.coorchice.library.utils.LogUtils;

import java.sql.Time;

/**
 * Project Name:SuperTextView
 * Author:CoorChice
 * Date:2019-09-07
 * Notes:
 */
public class TimeEvent extends Event {

  public long time;

  public TimeEvent(String type) {
    super(type);
  }

  public static TimeEvent create(String type, long time){
    if (LogUtils.DEBUG){
      TimeEvent timeEvent = new TimeEvent(type);
      timeEvent.time = time;
      return timeEvent;
    }
    return null;
  }

}
