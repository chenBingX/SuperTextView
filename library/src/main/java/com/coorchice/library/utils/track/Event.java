package com.coorchice.library.utils.track;

/**
 * Project Name:SuperTextView
 * Author:CoorChice
 * Date:2019-09-07
 * Notes:
 */
public class Event {

  public static final String OnDrawStart = "STV-OnDrawStart";
  public static final String OnDrawEnd = "STV-OnDrawEnd";
  public static final String OnDrawStrokeEnd = "STV-OnDrawStrokeEnd";
  public static final String OnDrawSolidEnd = "STV-OnDrawSolidEnd";
  public static final String OnDrawDrawableEnd = "STV-OnDrawDrawableEnd";
  public static final String OnDrawAdjustersEnd = "STV-OnDrawAdjustersEnd";
  public static final String OnDrawDrawableBackgroundEnd = "STV-OnDrawDrawableBackgroundEnd";
  public static final String OnCreateDrawableBackgroundShaderEnd = "STV-OnCreateDrawableBackgroundShaderEnd";
  public static final String OnUpdateDrawableBackgroundShaderEnd = "STV-OnUpdateDrawableBackgroundShaderEnd";
  public static final String OnDrawDrawableBackgroundShaderEnd = "STV-OnDrawDrawableBackgroundShaderEnd";
  public static final String OnCopyDrawableBackgroundToShaderEnd = "STV-OnCopyDrawableBackgroundToShaderEnd";

  public String type;

  public Event(String type){
    this.type = type;
  }

  String getType(){
    return type;
  }
}
