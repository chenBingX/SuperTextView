# Hello, Developer！Welcome to use SuperTextView


[![](https://jitpack.io/v/chenbingx/supertextview.svg)](https://jitpack.io/#chenbingx/supertextview) [![](https://img.shields.io/badge/SuperTextView-v3.1.4-orange.svg)](https://github.com/chenBingX/SuperTextView) [![](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/chenBingX/SuperTextView) [![](https://img.shields.io/badge/API-14+-yellowgreen.svg)](https://android-arsenal.com/api?level=14#l14) [![](https://img.shields.io/badge/License-Apache--2.0-blueviolet.svg)](https://github.com/chenBingX/SuperTextView#license) [![](https://img.shields.io/badge/Author-CoorChice-blue.svg)](https://weibo.com/5406092281/profile?topnav=1&wvr=6) [![](https://img.shields.io/badge/QQ--Group-775951525-ff5722.svg)](https://jq.qq.com/?_wv=1027&k=5DIRlPm)


# **English** | [**中文**](https://github.com/chenBingX/SuperTextView/blob/master/README_CN.md)

<img src="https://raw.githubusercontent.com/chenBingX/img/master/stv/SuperTextViewyuan.png" width=150 height=150 align=right alt="SuperTextView">

Hi，Developer，Welcome to use **SuperTextView** ！Thank you and tens of thousands of Android developers for  your trust in me 😘

**SuperTextView** is a component that focuses on serving Android developers, designed to help you build amazing **Android** applications.

In the past one and a half years, **SuperTextView** has gone through many iterations and hundreds of times of **commit**, which has been widely used in various types of commercial apps, and has withstood the test of tens of millions of users. I believe that **SuperTextView** can bring you to improve the development experience, and help you build or improve a more beautiful application.

**SuperTextView** is improving the development experience for **Android** developers in a more concise way. With everyone's support, in the future, **SuperTextView** will continue to serve developers and bring more surprises to developers.

# Feature
- set rounded corners for **View**
- supports separate control of each rounded corner
- add border for **View**
- add a stroke or hollow effect to the text
- support up to 2 **Drawable** to display
- accurately control the size and position of **Drawable**
- support gradient background
- touch discoloration
- display pictures, including the net pictures
- set rounded corners for pictures
- add borders to pictures
-  **Adjuster** module can insert operation
- change the color of **Drawable**
- change the rotation of **Drawable**
- support text gradient effect
- ...

# Demo


[ 📲 click here (or scan the qr code below) to download the **Demo**](https://raw.githubusercontent.com/chenBingX/img/master/%E5%85%B6%E5%AE%83%E6%96%87%E4%BB%B6/STVDemo.apk)

<img src="https://raw.githubusercontent.com/chenBingX/img/master/stv/stv_Demo_url.png" width=150 height=150 align=center alt="SuperTextView">

|One|Two|Three|
|:---:|:---:|:---:|
|![](https://raw.githubusercontent.com/chenBingX/img/master/stv/stv演示1.gif)|![](https://raw.githubusercontent.com/chenBingX/img/master/stv/stv演示2.gif)|![](https://raw.githubusercontent.com/chenBingX/img/master/stv/stv演示3.gif)|


# Portal Area


- [【Portal】：《SuperTextView Development Reference Document》- You can learn how to use SuperTextView to build your application more efficiently](https://github.com/chenBingX/SuperTextView/wiki)

- [【Portal】：《SuperTextView API Document》— You can view all of the SuperTextView's available apis and properties](https://chenbingx.github.io/SuperTextView/SuperTextView-doc/index.html)


# Usage


Add it in you build.gradle:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    compile 'com.github.chenBingX:SuperTextView:VERSION_CODE'
}
```

[The version code of the latest release can be found here.](https://github.com/chenBingX/SuperTextView/releases)


# Update Log


![](https://raw.githubusercontent.com/chenBingX/img/master/stv/stv全集3.1.3.gif)

## v3.1.1 - Sincerity works，SuperTextView

**SuperTextView** was built to help Android developers develop Android applications more efficiently, conveniently, and elegantly.

Now the bona fides have escalated again. The new **SuperTextView** will open up more possibilities to Android developers, and, as always, **SuperTextView** will bring more efficient features.

### 1 Amazing coloring

![image](https://raw.githubusercontent.com/chenBingX/img/master/stv/着色.png)

This update to **SuperTextView** adds a magical and powerful coloring capability to **StateDrawable**. Developers can easily change the color of an icon without having to add a different color icon to the project. This technology will give your Android applications a chance to slim down.

```
# modify the drawable color
app:stv_state_drawable_tint="@color/gray"

# modify the drawable2 color
app:stv_state_drawable2_tint="@color/red"
```

With such a simple line of code, you can instantly give an picture the power to change. Any color you want is up to you, of course. All this happened without the need to introduce another picture.

In the Java code, there is a set / get function corresponding to it, so that developers can cast magic at any time, changing the color of a picture.

### 2 Seventy-two Metamorphoses;


StateDrawable's enhancements go beyond color transformations. SuperTextView has been given the ability to change StateDrawable's shape. With the same picture, developers can combine an infinite number of possibilities.

![image](https://raw.githubusercontent.com/chenBingX/img/master/stv/旋转.png)

With a few lines of code, you can transform any picture as you wish.

```
# Modify the drawable's rotation
app:stv_state_drawable_rotate="90"

# Modify the drawable's rotation
app:stv_state_drawable2_rotate="90"
```

No need for complicated code, **SuperTextView** is as simple and elegant as ever.

Similarly, in Java code, the corresponding set/get function is also provided.

This ability can effectively help developers compress the volume of Android applications to the extreme.


### 3 Wonderful is far more than this

![image](https://raw.githubusercontent.com/chenBingX/img/master/stv/文字渐变色.png)

This is the gradient text!

What **SuperTextView** offers is probably the simplest and elegant solution for implementing gradient text so far. With a simple configuration, you can achieve cool gradient text effects.

```
# Whether to enable gradient text
app:stv_textShaderEnable="true"

# Set the starting color of the text
app:stv_textShaderStartColor="@color/red"

# Set the ending color of the text
app:stv_textShaderEndColor="@color/yellow"

# Set the gradient mode of the text
# leftToRight：left -> right
# rightToLeft：right -> left
# topToBottom：top -> bottom
# bottomToTop：bottom -> top
app:stv_textShaderMode="leftToRight"
```

These properties also provide the set/get interface in Java, making it easy for developers to modify them at any time.


### 4 New apis have been opened

#### 4.1 Adjuster adds onAttach, onDetach

Adjuster added two new functions:

-  `onAttach()` : when Adjuster is set to a **SuperTextView** will be invoked.
-  `onDetach() ` : when Adjuster is removed from a **SuperTextView** will be invoked.

By rewriting these two functions in Adjuster, the developer can perform state registration, initialization, unregistration, resource release and other operations at the right time.

```
public class MyAdjuster extends SuperTextView.Adjuster{

    @Override
    protected void adjust(SuperTextView superTextView, Canvas canvas) {

    }

    @Override
    public void onAttach(SuperTextView stv) {
      // will be called when the modifier is added to a SuperTextView
    }

    @Override
    public void onDetach(SuperTextView stv) {
      // will be called when the Adjuster is removed from SuperTextView
    }
}
```

#### 4.2 Provide getAdjusterList() function

This function allows the developer to get all the **Adjusters** in a **SuperTextView**. If there is no **Adjuster** in the **SuperTextView**, it will return null.

### 5 ⚠️You must take these changes seriously

#### 5.1 Attribute with the stv_ prefix

All properties of **SuperTextView** are now prefixed with `stv_`.

This avoids conflicts with the property names that **SuperTextView** may generate when other third-party libraries are introduced by the developer.

If the developer is currently using a previous version of **SuperTextView**, then after upgrading to the new version, you need to prefix the attributes in all xml with the `stv_` prefix.

```
app:corner="10dp"
```

corner is the name of the attribute in the old version. After upgrading to the new version, you need to add the `stv_` prefix to the front and become `stv_corner`.

![image](https://raw.githubusercontent.com/chenBingX/img/master/stv/replace.png)

If the developer is using **AndroidStudio**, open the bulk replacement dialog from `Edit > Find > Replace` and follow the instructions below.

![image](https://raw.githubusercontent.com/chenBingX/img/master/stv/属性替换.png)

If only **SuperTextView** uses the same namespace (such as `app`) in the developer's project, then fortunately, you can simply replace `app:` with `app:stv_`.

#### 5.2 setAdjuster(Adjuster) has been removed

Starting with **SuperTextView** v2.0, the `setAdjuster(Adjuster)` function is marked for the state to be removed, and the new function `addAdjuster(Adjuster)` is added instead.

In the new version, the `setAdjuster(Adjuster)` function will be officially removed. If the developer has used this method before, please change it to `addAdjuster(Adjuster)`.




### 6 How to get started SuperTextView v3.1.1
```
dependencies {
	 compile 'com.github.chenBingX:SuperTextView:v3.1.1'
}
```





## v3.0 - Your long-awaited SuperTextView
Today, **SuperTextView** has a range of common features such as fillets, borders, strokes, press discoloration, multi-state diagrams, fillet plots, versatile **Adjuster**, loading the net picture, and more. Thanks to this, developers can easily achieve a variety of very cumbersome effects, save a lot of development time, effectively reduce the complexity of the page, reduce project maintenance costs.

Write the code, it should be so pleasing!

### 1. Linked Cloud SuperTextView

As early as a few months ago, many developers have suggested to **CoorChice** whether it is possible to have **SuperTextView** with the ability to load net picture. In fact, this is also **CoorChice** has been considered a long time ago, but in the early days of **SuperTextView**, perfecting its core functions is still the primary goal, so it has not been involved in image-related functions.

Until the last big version, **SuperTextView** v2.0, **CoorChie** tried to add the picture display function. This has enabled the scope of the **SuperTextView** to be expanded, as well as the ability to add strokes, rounded corners, and state diagrams to pictures. Related documentation can be found at the following link:

[【Hello， SuperTextView】 - https://www.jianshu.com/p/1b91e11e441d](https://www.jianshu.com/p/1b91e11e441d)

This time, I got a good response from the developers. Everyone is looking forward to using **SuperTextView** to display and process picture. After the last release, developers seem to be more interested in a **SuperTextView** that can display the net picture.

So, now, the long-awaited **SuperTextView** is coming back!

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/图片下载演示2.gif)

#### 1.1 Load a net picture
To display a net picture, you only need the following code in **SuperTextView**:

```
SuperTextView stv_1 = (SuperTextView) findViewById(R.id.stv_1);
// fill in the picture Url
stv_1.setUrlImage(url);
```

The effect is the same as the second example of displaying an avatar in the image above.

If you want to display the net picture as a StateDrawable of **SuperTextView**, it's fine.

```
// fill in the picture Url
stv_1.setUrlImage(url, false);
```

The second parameter is **false** to indicate that the net picture will not be filled with the entire **SuperTextView** as a background, but as a state diagram. Of course, everything about the state diagram will be used here. As in the first example above, the entire layout, including pictures, text, and background, is processed in a **SuperTextView**, and pictures downloaded from the net are placed as **StateDrawable** in the place.

#### 1.2 Image engine in SuperTextView
**SuperTextView** In order to keep the library dependent on the purity and the smallest possible size, there is no built-in image load framework. So by default, a simple image engine built-in will be used to download pictures, to ensure that developers can use the ability to display the net picture.

However, **CoorChice** still recommends that developers choose a image loading framework that is currently in use, depending on the project, and set it to **SuperTextView** to load the picture. **SuperTextView** has the ability to adapt to any image load framework. Below **CoorChice** will show you how to install an existing image framework into **SuperTextView** with the Glide and the Picasso examples.

##### 1.2.1 Implementing image engine
In **SuperTextView**, the core image loading engine is abstracted into the interface **Engine**, and the developer needs to implement a **Engine** depending on the image frame used.

- **the Glide Image load framework**

```
public class GlideEngine implements Engine {

  private Context context;

  public GlideEngine(Context context) {
        this.context = context;
  }

  @Override
  public void load(String url, final ImageEngine.Callback callback) {
        Glide.with(context).load(url).into(new SimpleTarget<GlideDrawable>() {
        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            // mainly through the callback return Drawable object to SuperTextView
            callback.onCompleted(resource);
        }
        });
    }
}
```

- **the Picasso Image load framework**

```
public class PicassoEngine implements Engine {

  private Context context;

  public PicassoEngine(Context context) {
        this.context = context;
  }

  @Override
  public void load(String url, final ImageEngine.Callback callback) {
        Picasso.with(context).load(url).into(new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            // mainly through the callback return Drawable object to SuperTextView
            callback.onCompleted(new BitmapDrawable(Resources.getSystem(), bitmap));
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    });
  }
}
```

##### 1.2.2 Install Image Engine
After implementing **Engine**, the next step is to install it into **SuperTextView**.

**CoorChice** it is recommended to install in the `onCreate()` of the application, so that when you need to use **SuperTextView** to load and display the net picture, you can use the three-party image load framwork.

```
public class STVApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    // 安装图片引擎
    ImageEngine.install(new GlideEngine(this));
    // ImageEngine.install(new PicassoEngine(this));
  }
}
```

One line of code for easy installation.

It should be noted that at any time, the post-installed **Engine** instance will always replace the previously installed **Engine** instance, ie **SuperTextView** only allows one **Engine** instance to exist globally.

Now you can have **SuperTextView** load the picture using the specified three-party image load framework.

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/图片下载列表演示.gif)


### 2. How to get started SuperTextView v3.0
```
	dependencies {
	   compile 'com.github.chenBingX:SuperTextView:v3.0.0'
	}
```
### 3. Other
- Fix an animation problem
- Some optimization



## v2.0 - The future, from now on
**All along, CoorChice has a vision and expects to be able to create such a control: it can meet most of your development needs, display text, pictures, geometry, animation, state, so that you can use a control to be efficient Complete most of the development work. It is so powerful, as if it is mentally minded, accepting your input, and presenting a stunning picture according to your mind. With the arrival of 【SuperTextView v2.0】, we are one step closer to this idea. Now, come and see 【SuperTextView v2.0】!**

![SuperTextView v2.0](https://raw.githubusercontent.com/chenBingX/img/master/stv/stv_2.0_2.png)

### 1 Now, Picture
In 【SuperTextView v2.0】, support for image display has been added. But it's not just about displaying pictures, it's also smart to crop the image to your desired shape based on your input.

![image](https://raw.githubusercontent.com/chenBingX/img/master/stv/屏幕快照%202017-11-16%2001.51.33.png)

Add a rounded corner to the picture, add a border, or turn it directly into a circle. All you need to do is set a few simple properties that are instantly visible in front of your eyes.

#### 1.1 Display Picture
How to use **SuperTextView** to display a picture?

Just add the following two lines of code to the xml.

```
<com.coorchice.library.SuperTextView
    ...
    app:stv_state_drawable="@drawable/avatar1"
    app:stv_drawableAsBackground="true"
    ...
 />
```

If you are a loyal user of `SuperTextView`, you will find that the original `state_drawable` can now be used to display a picture.

#### 1.2 Fill the picture with rounded corners
Now that your picture is in front of you, maybe you want to do something different about it, for example, add a rounded corner, or directly become a circle? No problem, `SuperTextView` is now fully qualified for this kind of work.

```
<com.coorchice.library.SuperTextView
    android:layout_width="100dp"
    android:layout_height="100dp"
    ...
    app:stv_corner="15dp"
    app:stv_state_drawable="@drawable/avatar1"
    app:stv_drawableAsBackground="true"
    ...
 />
```

So Easy! On the basis of the original you only need to set a reasonable the `corner`.

#### 1.3 Maybe you still want a border
Sometimes you may need to use a border to wrap your picture, as in the example above. That's right, this is definitely within the scope of the `SuperTextView` capability.

```
<com.coorchice.library.SuperTextView
    android:layout_width="100dp"
    android:layout_height="100dp"
    ...
    app:stv_corner="50dp"
    app:stv_stroke_color="#F4E187"
    app:stv_stroke_width="4dp"
    app:stv_state_drawable="@drawable/avatar1"
    app:stv_drawableAsBackground="true"
    ...
 />
```

`app:stv_stroke_color` controls the color of the border, and `app:stv_stroke_width` controls the width of the border. Everything is so smooth, a intelligent control should be like this, right?


### 2 Second StateDrawable
In the face of complex changes in demand, 【SuperTextView】gave birth to a second drawable to deal with  this complexity.

`state_drawable2` .


![](https://raw.githubusercontent.com/chenBingX/img/master/stv/屏幕快照%202017-11-16%2001.46.23.png)

Now, CoorChice will show you how the two effects in the above picture are implemented.

- **eg. 1**

```
<com.coorchice.library.SuperTextView
    android:layout_width="100dp"
    android:layout_height="100dp"
    ...
    app:stv_corner="50dp"
    app:stv_state_drawable="@drawable/avatar1"
    app:stv_drawableAsBackground="true"
    // The configuration of state_drawable2 starts here
    app:stv_isShowState2="true"
    app:stv_state_drawable2="@drawable/recousers"
    app:stv_state_drawable2_mode="rightTop"
    app:stv_state_drawable2_height="20dp"
    app:stv_state_drawable2_width="20dp"
    ...
 />
```

- **eg. 2**

```
<com.coorchice.library.SuperTextView
    android:layout_width="100dp"
    android:layout_height="100dp"
    ...
    // background
    android:background="@drawable/avatar7"
    // The configuration of drawable1 starts here
    app:stv_isShowState="true"
    app:stv_state_drawable="@drawable/triangle"
    app:stv_state_drawable_mode="leftTop"
    app:stv_state_drawable_width="20dp"
    app:stv_state_drawable_height="20dp"
    // The configuration of state_drawable2 starts here
    app:stv_isShowState2="true"
    app:stv_state_drawable2="@drawable/recousers"
    app:stv_state_drawable2_mode="rightTop"
    app:stv_state_drawable2_height="20dp"
    app:stv_state_drawable2_width="20dp"
    ...
 />
```

As you are familiar with, `state_drawable2` continues all the smooth operations of the first generation. Under the wise use of your smart, 【SuperTextView】 will be able to shine! 😉

### 3 The era of Adjuster
Previously, the design of `Adjuster` made 【SuperTextView】a soul and a smarter control. The insertion of the drawing process, the capture of touch events, makes it easy to change the state of a control from the outside. Creativity begins with the heart and goes here.

Now, 【SuperTextView】can carry up to 3 `Adjuster` at the same time! Maybe your creativity will be more dazzling.

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/多Adjuster演示.gif)

In the above example, CoorChice adds the two early  the【Sweep】and the【Move】effects to a 【SuperTextView】, and the result is what you see.

More `Adjuster` means more combinations and more surprises. In 【v1.4.0】, CoorChice also uses `Adjuster` to easily implement the press color change function.

This is the era of `Adjuster`, you can use it to be creative.

⚠️ Note that the original `setAdjuster(Adjuster)` method is still preserved, but future versions will be removed and you must migrate as soon as possible. The new alternative is `addAdjuster(Adjuster)`.

### 4 Other
- Fixed spelling of the property `app:stv_shaderMode="leftToRight"` that controls the Shader mode. The right is`app:stv_shaderMode="leftTopRight"` . If you use this property, please correct it after upgrading 【SuperTextView v2.0】.
- Added `set/getPressBgColor()` and `set/getPressTextColor()` to control the background color in the code.



## v1.4.0
- SuperTextView support to press to change background color  and text color.

![image](https://raw.githubusercontent.com/chenBingX/img/master/stv/按压变色.gif)

You just need to set these properties at the xml file like this:

```
# set the background color when you pressed
app:stv_pressBgColor="@color/red"
# set the text color when you pressed
app:stv_pressTextColor="@color/white"
```
- There is a new method `getCorners()`. You can get the infomation of the corners in the `SuperTextView`, sometimes you really want to use this method.
- How to use SuperTextView v1.4？

```
dependencies {
	compile 'com.github.chenBingX:SuperTextView:v1.4'
}
```
## v1.3
- Now, you can change frame rate at any time.

```
mSuperTextView.setFrameRate(30);
// set 30 fps
```
- Optimize the animation performance.
- Cool, Cool, Cool! Shader is coming！

![link](https://raw.githubusercontent.com/chenBingX/img/master/stv/渐变色.png)

A touch namely change, imagine can't be stop. Artist, play to your creativity！

You can set the Shader Effect in the xml, and you can。

```
app:stv_shaderEnable="true"
// set true to begin shader.

app:stv_shaderStartColor="@color/main_blue"
// set shader start color.

app:stv_shaderEndColor="@color/pink"
// set shader end color.

app:stv_shaderMode="rightToLeft"
// set shader mode. These are four mode：
// topTopBottom, bottomToTop, leftToRight, rightToLeft
```

Of course, these properties can be changed by `set/get` method. eg：

```
mSuperTextView.setShaderStartColor(Color.RED);
```

- Now, **SuperTextView** javadoc is provided, you can download from here（click `index.html` to begin）：
[**SuperTextView javadoc：http://ogemdlrap.bkt.clouddn.com/SuperTextView%E6%96%87%E6%A1%A3%20.zip?attname=**](http://ogemdlrap.bkt.clouddn.com/SuperTextView%E6%96%87%E6%A1%A3%20.zip?attname=)

- how to use SuperTextView 1.3
  in the **build.gradle**：

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    compile 'com.github.chenBingX:SuperTextView:v1.3'
}
```
---

## v1.1
- Support Android 4.0，SdkVersion 14.
- Support elegant fascinating **【Chain Programming】** , eg:


```
mSuperTextView.setAdjuster(new MoveEffectAdjuster())
        .setAutoAdjust(true)
        .startAnim();
```

- Reduce the library memory.

## v1.0
### 1 Attribute
**SuperTextView** properties can be set in the XML easily, and you can see the effect immediately. Just like to use TextView.

```
<SuperTextView
    android:layout_width="50dp"
    android:layout_height="50dp"

    //Set Corner.
    //If you want to get a circle, you just need to set the value of half of width.
    app:stv_corner="25dp"
    //Corner of left-top
    app:stv_left_top_corner="true"
    //Corner of right-top
    app:stv_right_top_corner="true"
    //Corner of left-bottom
    app:stv_left_bottom_corner="true"
    //Corner of right-bottom
    app:stv_right_bottom_corner="true"
    //Fill color
    app:stv_solid="@color/red"
    //Stroke color
    app:stv_stroke_color="@color/black"
    //Stroke width
    app:stv_stroke_width="2dp"
    //Set a state drawbale
    //The default size is half of the SuperTextView.
    app:stv_state_drawable="@drawable/emoji"
    //The mode of the state drawable. Optional values:
    // left、top、right、bottom、center(Default)、
    //leftTop、rightTop、leftBottom、rightBottom、
    //fill(Fill the SuperTextView. In this case, set state drawable size will not work.)
    app:stv_state_drawable_mode="center"
    //state drawable height
    app:stv_state_drawable_height="30dp"
    //state drawable width
    app:stv_state_drawable_width="30dp"
    //The padding of the left, it base on the value of state_drawable_mode.
    app:stv_state_drawable_padding_left="10dp"
    //The padding of the top, it base on the value of state_drawable_mode.
    app:stv_state_drawable_padding_top="10dp"
    //boolean. Whether to show the state drawble.
    app:stv_isShowState="true"
    //Whether to use the Stroke Text Function.
    //Attention, Once you opne this function, setTextColor() will not work.
    //That means you must to uses text_fill_color to set text color.
    app:stv_text_stroke="true"
    // Text stroke color. The default value is Color.BLACK.
    app:stv_text_stroke_color="@color/black"
    // Stroke text width.
    app:stv_text_stroke_width="1dp"
    // Stroke text color. The default value is Color.BLACK.
    app:stv_text_fill_color="@color/blue"
    //boolean. Whether to use the Adjuster Function.
    //Use this function to do what you want to do.
    //If open this function, but you haven't implemented your Adjuster, the DefaultAdjuster will be used.
    //The DefaultAdjuster can auto adjust text size.
    app:stv_autoAdjust="true"
    />

```
All the attributes can be set in the java. You can also to get their value. e.g.:

```
mSuperTextView.setCorner(10);
mSuperTextView.getCorner();
```
#### 1.1 Corner And Border
![image](https://raw.githubusercontent.com/chenBingX/img/master/stv/屏幕快照%202017-04-18%2008.15.42.png)

Usually, you have to write and manage a lot of <shape> file to implement the effect of the above chart. But now, you can easy to do this in the XML.

#### 1.2 Not Simple Corner
![image](https://raw.githubusercontent.com/chenBingX/img/master/stv/屏幕快照%202017-04-18%2008.15.59.png)

Different from general Corner, **SuperTextView** can support to precise control the location of corner. One, two , three, what ever you want.

#### 1.3 Amazing Stroke Text
![](https://raw.githubusercontent.com/chenBingX/img/master/stv/屏幕快照%202017-04-18%2008.16.13.png)

Use Stroke text is so easily！

#### 1.4 High-Efficient State Drawable
![](https://raw.githubusercontent.com/chenBingX/img/master/stv/屏幕快照%202017-04-18%2008.16.22.png)

Different from general state drawable, **SuperTextView** supports more precise control options. You can easy to set state drawable, just to use one attribute.

### 2 Explosive Adjuster
**Adjuster** is be designed to insert some options in the drawing process of the **SuperTextView**. It has very important sense. e.g. The **DefaultAdjuster** can auto adjust text size before the text be draw. Of course, you can use it to do any thing.

**If you want to use Adjuster, you must to invoke `SuperTextView.setAutoAdjust(true)`. Of course, you can invoke `SuperTextView.setAutoAdjust(false)` to stop it at any time. You should invoke these method carefully. Because, once you invoke the `SuperTextView.setAutoAdjust(true)`, but didn't set your Adjuster before, the DefaultAdjuster will be used immediately.Until you set yourself Adjuster.**

#### 2.1 Intervene Drawing
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


#### 2.2 Response Touch Event

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

#### 2.3 So Amazing Effect

Because the **SuperTextView** the build-in animation driven, you can use Adjuster to implement the unbelievable effect. All the things you need to do is invoke `startAnim()`and `stopAnim()` to start or stop animation after your Adjuster write down.

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/SuperTextView.gif)

As you can see, these beautiful effect is be implemented by Adjuster. This **pull plugin** design, makes you can use a new Adjuster in the **SuperTextView** at any time. You just need to create a new Adjuster, then invoke `setAdjuster()`.

`@Alex_Cin` hopes to see the Ripple Effect, so in the `RippleAdjuster.java`, I've shown how to use Adjuster with Animation Driven to implement the Ripple Effect. [【RippleAdjuster.java link：https://github.com/chenBingX/SuperTextView/blob/master/app/src/main/java/com/coorchice/supertextview/SuperTextView/Adjuster/RippleAdjuster.java】](https://github.com/chenBingX/SuperTextView/blob/master/app/src/main/java/com/coorchice/supertextview/SuperTextView/Adjuster/RippleAdjuster.java)

See, you can implement your Ripple Effect.

#### 2.4 Set the hierarchy of Adjuster
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

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/Opportunity.png)

The default value is `Opportunity.BEFORE_TEXT`. Like the second chart.

In fact, **SuperTextView** like a canvas, and you can draw your creative on it. It makes you forces on the creation, and you never need to write these useless code.

Now you can start using **SuperTextView**.

> - If you like [**SuperTextView**](https://github.com/chenBingX/SuperTextView), I thank you to go to the [**Github**](https://github.com/chenBingX/SuperTextView) to give me a  [**star** 🌟](https://github.com/chenBingX/SuperTextView) !

> - In addition, **CoorChice** will occasionally share dry goods on the blog platform, including **Android related technology**, **machine learning**, **algorithm**,**new technology**, And ** some insights and thoughts about personal development and improvement**. Go to [CoorChice's 【Personal Homepage】](https://juejin.im/user/57fc43b67db2a200595ffd94)  to give me a follow.

> - SuperTextView QQ Group：***775951525***

# License




    Copyright 2017 CoorChice

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

