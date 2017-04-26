
# [【中文文档地址：https://github.com/chenBingX/SuperTextView/blob/master/README_zh.md】](https://github.com/chenBingX/SuperTextView/blob/master/README_zh.md)

# SuperTextView

# Update Log
## v1.1
- Support Android 4.0，SdkVersion 14.
- Support elegant fascinating **【Chain Programming】** , eg:
```
mSuperTextView.setAdjuster(new MoveEffectAdjuster())
        .setAutoAdjust(true)
        .startAnim();
```
- Reduce the library memory.

# Introduction
Welcome to use **SuperTextView**, this document will show you how to use this widget.

![Cover](http://ogemdlrap.bkt.clouddn.com/SuperTextView_cover0.png)

**SuperTextView** extends TextView, it can reduce the complexity of the layout, and you can achieve the effect of some common quickly 。At the same time，It is built-in support for animation driven, you just need to write **Adjuster** and invoke `startAnim()`, then you will see what you want. It's just a widget, so you can use it in your project easily.

# Feature
1. You don't have to write and manage for specific background drawable from now on.
2. The **State Drawable** is optimized. Now, you can precise control of State Drawable's size and the location in the **SuperTextView**.
3. Corner is supported. And you can precise control it location。
4. You can implement the border effect in the widget easily.
5. Text stroke is supported.
6. Built-in animation driver. You just need to use whit **Adjuster**.
7. The emergence of the **Adjuster**, allow you to control the drawing process of the widget. The good design makes it possible to implement the beautiful effect in your mind.

# User Guide
## Attribute
**SuperTextView** properties can be set in the XML easily, and you can see the effect immediately. Just like to use TextView.
```
<SuperTextView
    android:layout_width="50dp"
    android:layout_height="50dp"

    //Set Corner.
    //If you want to get a circle, you just need to set the value of half of width.
    app:corner="25dp"
    //Corner of left-top
    app:left_top_corner="true"
    //Corner of right-top
    app:right_top_corner="true"
    //Corner of left-bottom
    app:left_bottom_corner="true"
    //Corner of right-bottom
    app:right_bottom_corner="true"
    //Fill color
    app:solid="@color/red"
    //Stroke color
    app:stroke_color="@color/black"
    //Stroke width
    app:stroke_width="2dp"
    //Set a state drawbale
    //The default size is half of the SuperTextView.
    app:state_drawable="@drawable/emoji"
    //The mode of the state drawable. Optional values:
    // left、top、right、bottom、center(Default)、
    //leftTop、rightTop、leftBottom、rightBottom、
    //fill(Fill the SuperTextView. In this case, set state drawable size will not work.)
    app:state_drawable_mode="center"
    //state drawable height
    app:state_drawable_height="30dp"
    //state drawable width
    app:state_drawable_width="30dp"
    //The padding of the left, it base on the value of state_drawable_mode.
    app:state_drawable_padding_left="10dp"
    //The padding of the top, it base on the value of state_drawable_mode.
    app:state_drawable_padding_top="10dp"
    //boolean. Whether to show the state drawble.
    app:isShowState="true"
    //Whether to use the Stroke Text Function.
    //Attention, Once you opne this function, setTextColor() will not work.
    //That means you must to uses text_fill_color to set text color.
    app:text_stroke="true"
    // Text stroke color. The default value is Color.BLACK.
    app:text_stroke_color="@color/black"
    // Stroke text width.
    app:text_stroke_width="1dp"
    // Stroke text color. The default value is Color.BLACK.
    app:text_fill_color="@color/blue"
    //boolean. Whether to use the Adjuster Function.
    //Use this function to do what you want to do.
    //If open this function, but you haven't implemented your Adjuster, the DefaultAdjuster will be used.
    //The DefaultAdjuster can auto adjust text size.
    app:autoAdjust="true"
    />

```
All the attributes can be set in the java. You can also to get their value. e.g.:
```
mSuperTextView.setCorner(10);
mSuperTextView.getCorner();
```
### Corner And Border
![image](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202017-04-18%2008.15.42.png)

Usually, you have to write and manage a lot of <shape> file to implement the effect of the above chart. But now, you can easy to do this in the XML.

### Not Simple Corner
![image](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202017-04-18%2008.15.59.png)

Different from general Corner, **SuperTextView** can support to precise control the location of corner. One, two , three, what ever you want.

### Amazing Stroke Text
![image](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202017-04-18%2008.16.13.png)

Use Stroke text is so easily！

### High-Efficient State Drawable
![image](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202017-04-18%2008.16.22.png)

Different from general state drawable, **SuperTextView** supports more precise control options. You can easy to set state drawable, just to use one attribute.

## Explosive Adjuster
**Adjuster** is be designed to insert some options in the drawing process of the **SuperTextView**. It has very important sense. e.g. The **DefaultAdjuster** can auto adjust text size before the text be draw. Of course, you can use it to do any thing.

**If you want to use Adjuster, you must to invoke `SuperTextView.setAutoAdjust(true)`. Of course, you can invoke `SuperTextView.setAutoAdjust(false)` to stop it at any time. You should invoke these method carefully. Because, once you invoke the `SuperTextView.setAutoAdjust(true)`, but didn't set your Adjuster before, the DefaultAdjuster will be used immediately.Until you set yourself Adjuster.**

### Intervene Drawing
To implement a Adjuster, you need to extends SuperTextView.Adjuster，and implement `adjust(SuperTextView v, Canvas canvas)` method. Adjuster.adjust() will be invoke whenever the draw happened, that means you can intervene the drawing process in the outside.

```
public class YourAdjuster extends SuperTextView.Adjuster {

  @Override
  protected void adjust(SuperTextView v, Canvas canvas) {
    //do your business。
  }

}
```
**Attention, if you start animation, you must be very careful to write the code in the adjuster(). Because the animation will be draw 60fps/s. That means, this method will be invoked 60 times in a second！So, do not to create any new object in this method. Otherwise, your app will be get a big lag！Because it will cause【Memory Thrashing】, and GC occur frequently. About the detail reason, you can see my this two articles:**
- [【Android Memory Thrashing : http://www.jianshu.com/p/69e6f894c698】](http://www.jianshu.com/p/69e6f894c698)
- [【Two chart to tell you why your app lags? : http://www.jianshu.com/p/df4d5ec779c8】](http://www.jianshu.com/p/df4d5ec779c8)


### Response Touch Event

If you override the `onTouch(SuperTextView v, MotionEvent event)` method of the Adjuster, you will get the touch events of the **SuperTextView**. It's very important to get a series of touch events of **SuperTextView** to handle. And you must return true in the `onTouch()`, Otherwise you will just get a  ACTION_DOWN event, not a flow of events.

```
public class YourAdjuster extends SuperTextView.Adjuster {

  @Override
  protected void adjust(SuperTextView v, Canvas canvas) {
    //do your business。
  }

  @Override
  public boolean onTouch(SuperTextView v, MotionEvent event) {
    //you can get the touch event.
    //If want to get a series of touch event, you must return true here.
  }

}
```

### So Amazing Effect

Because the **SuperTextView** the build-in animation driven, you can use Adjuster to implement the unbelievable effect. All the things you need to do is invoke `startAnim()`and `stopAnim()` to start or stop animation after your Adjuster write down.

![link](http://ogemdlrap.bkt.clouddn.com/SuperTextView.gif)

As you can see, these beautiful effect is be implemented by Adjuster. This **pull plugin** design, makes you can use a new Adjuster in the **SuperTextView** at any time. You just need to create a new Adjuster, then invoke `setAdjuster()`.

`@Alex_Cin` hopes to see the Ripple Effect, so in the `RippleAdjuster.java`, I've shown how to use Adjuster with Animation Driven to implement the Ripple Effect. [【RippleAdjuster.java link：https://github.com/chenBingX/SuperTextView/blob/master/app/src/main/java/com/coorchice/supertextview/SuperTextView/Adjuster/RippleAdjuster.java】](https://github.com/chenBingX/SuperTextView/blob/master/app/src/main/java/com/coorchice/supertextview/SuperTextView/Adjuster/RippleAdjuster.java)

See, you can implement your Ripple Effect.

### Set the hierarchy of Adjuster
**Adjuster** is sweet designed the hierarchy function. You invoke `Adjuster.setOpportunity(Opportunity opportunity)` to set the hierarchy of your Adjuster in the **SuperTextView**.

In the **SuperTextView**, the hierarchy is from bottom to top is divided into：Background Hierarchy、Drawable Hierarchy、Text Hierarchy. You can use Opportunity to set the hierarchy of your Adjuster to that you want layer.

```
public enum Opportunity {
      BEFORE_DRAWABLE, //between backgournd layer and drawable layer
      BEFORE_TEXT,     //between drawable layer and text layer
      AT_LAST          //The top layer
}
```
Opportunity chart.

![image](http://ogemdlrap.bkt.clouddn.com/Opportunity.png)

The default value is `Opportunity.BEFORE_TEXT`. Like the second chart.

In fact, **SuperTextView** like a canvas, and you can draw your creative on it. It makes you forces on the creation, and you never need to write these useless code.

# How To Use?

> - If you like the **SuperTextView**, please give me a **star**！Thank you!
> - I always writing "Dry Goods", if you want to continuous attention to me, you can go to  [【My Personal Homepage】](http://www.jianshu.com/u/cfec7d70bbec), and give me a followed. Let's drive～


## Method 1
Add these code to your **build.gradle**:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    compile 'com.github.chenBingX:SuperTextView:v1.1'
}
```


## Method 2
You can Clone my [【Github repositories : https://github.com/chenBingX/SuperTextView】](https://github.com/chenBingX/SuperTextView), find **SuperTextView.java** and **attrs.xml** in the library package, then copy them to your project.

Now, you can begin to use the **SuperTextView** immediately.

# License
Copyright (C) 2017 CoorChice <icechen_@outlook.com>

Licensed under the Apache License, Version 2.0 (the "License");you may not use this file except
in compliance with the License. You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License
is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
or implied. See the License for the specific language governing permissions and limitations under
the License.


