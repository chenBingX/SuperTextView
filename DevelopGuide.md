# SuperTextView 开发参考文档

<img src="https://raw.githubusercontent.com/chenBingX/img/master/stv/SuperTextViewyuan.png" width=230 height=230 align=right alt="SuperTextView">

  一直以来 **SuperTextView** 的使命，就是帮助 Android 开发者得心应手的构建 Android 应用。

  **SuperTextView** 是一个高效的、全能的 **Android** 控件。通过 **SuperTextView** ，你可以快速实现圆角背景，设置渐变色背景，给控件和文字描边，为控件增加状态图，添加按压时文字或背景变色效果，通过 **Adjuster** 模块快速插入操作到控件绘制过程中，展示图片，甚至可以直接从网络上下载图片展示...基本上涵盖了 **Android** 日常开发中会用到的绝大部分效果。而实现这一切的代价，仅仅是给 **SuperTextView** 设置一个属性。**SuperTextView** 可以帮助开发者高效、便捷、优雅的完成需求开发。

  本篇文档将详细的讲解目前 **SuperTextView** 的每一个功能，以便开发者能够快速上手。

# 1. 获取 SuperTextView


  - Gradle下添加依赖：

  ```
  dependencies {
  compile 'com.github.chenBingX:SuperTextView:v3.1.1'
  }
  ```

  - 项目地址

  [https://github.com/chenBingX/SuperTextView](https://github.com/chenBingX/SuperTextView)

  **注：关注项目以优先获得最新版本。**

# 2. SuperTextView 属性预览

  **SuperTextView** 的绝大部分属性均支持在xml中配置，设置后即预览到效果。

  ```
<SuperTextView

  //设置圆角。会同时作用于填充和边框(如果边框存在的话)。
  //如果要设置为圆形，只需要把该值设置为宽或长的1/2即可。
  app:stv_corner="25dp"

  //设置左上角圆角
  app:stv_left_top_corner="true"

  //设置右上角圆角
  app:stv_right_top_corner="true"

  //设置左下角圆角
  app:stv_left_bottom_corner="true"

  //设置右下角圆角
  app:stv_right_bottom_corner="true"

  //设置填充颜色
  app:stv_solid="@color/red"

  //设置边框颜色
  app:stv_stroke_color="@color/black"

  //设置边框的宽度。
  app:stv_stroke_width="2dp"

  //放置一个drawable在背景层上。默认居中显示。
  //并且默认大小为SuperTextView的一半。
  app:stv_state_drawable="@drawable/emoji"

  //设置drawable的显示模式。可选值如下：
  // left、top、right、bottom、center(默认值)、
  //leftTop、rightTop、leftBottom、rightBottom、
  //fill(充满整个SuperTextView，此时会使设置drawable的大小失效)
  app:stv_state_drawable_mode="center"

  //设置drawable的height
  app:stv_state_drawable_height="30dp"

  //设置drawable的width
  app:stv_state_drawable_width="30dp"

  //设置drawble相对于基础位置左边的距离
  app:stv_state_drawable_padding_left="10dp"

  //设置drawble相对于基础位置上边的距离
  app:stv_state_drawable_padding_top="10dp"

  // boolean类型。是否显示drawable。
  //如果你想要设置的drawable显示出来，必须设置为true。
  //当不想让它显示时，再设置为false即可。
  app:stv_isShowState="true"

  // 是否将state_drawable作为背景图
  // 将state_drawable作为背景图可以让SuperTextView具备展示图片的能力
  // 通过调节corner、stroke等属性，可以给图片设置圆角、边框等
  app:stv_drawableAsBackground="true"

  //放置一个drawable在背景层上。默认居中显示。
  //并且默认大小为SuperTextView的一半。
  app:stv_state_drawable2="@drawable/emoji"

  //与state_drawable类似
  app:stv_state_drawable2_mode="center"

  //与state_drawable_height类似
  app:stv_state_drawable2_height="30dp"

  //与state_drawable_width类似
  app:stv_state_drawable2_width="30dp"

  //与state_drawable_padding_left类似
  app:stv_state_drawable2_padding_left="10dp"

  //与state_drawable_padding_top类似
  app:stv_state_drawable2_padding_top="10dp"

  //与isShowState类似
  app:stv_isShow2State="true"

  //是否开启文字描边功能。
  //注意，启用这个模式之后通过setTextColor()设置的颜色将会被覆盖。
  //你需要通过text_fill_color来设置文字的颜色。
  app:stv_text_stroke="true"

  // 文字的描边颜色。默认为Color.BLACK。
  app:stv_text_stroke_color="@color/black"

  // 文字描边的宽度。
  app:stv_text_stroke_width="1dp"

  // 文字填充的颜色。默认为Color.BLACK。
  app:stv_text_fill_color="@color/blue"

  // boolean类型。是否启用Adjuster功能。
  //具体干什么，需要在Java中为SuperTextView实现一个Adjuster。
  //当你启用这个功能而没有实现自己的Adjuster时，
  //SuperTextView会启用默认的Adjuster。它会按照一定的规则调整文字大小。
  app:stv_autoAdjust="true"

  // 必须设置为true才能启用渐变功能。这意味着你可以灵活的控制这一功能。
  app:stv_shaderEnable="true"

  // 设置起始颜色。
  app:stv_shaderStartColor="@color/main_blue"

  // 设置结尾颜色。
  app:stv_shaderEndColor="@color/pink"

  // 设置渐变模式。如上图可见，一共支持4中模式：
  // topTopBottom, bottomToTop, leftToRight, rightToLeft
  app:stv_shaderMode="rightToLeft"

  // 设置按压时的背景色
  app:stv_pressBgColor="@color/red"

  // 设置按压时的文字颜色
  app:stv_pressTextColor="@color/white"

  // 修改 drawable 的颜色
  app:stv_state_drawable_tint="@color/gray"

  // 修改 drawable2 的颜色
  app:stv_state_drawable2_tint="@color/red"

  // 修改 drawable 的旋转角度
  app:stv_state_drawable_rotate="90"

  // 修改 drawable2 的旋转角度
  app:stv_state_drawable2_rotate="90"

  // 是否启用渐变色文字
  app:stv_textShaderEnable="true"

  // 设置文字的起始渐变色
  app:stv_textShaderStartColor="@color/red"

  // 设置文字的结束渐变色
  app:stv_textShaderEndColor="@color/yellow"

  // 设置文字的渐变的模式
  // leftToRight：左 -> 右
  // rightToLeft：右 -> 左
  // topToBottom：上 -> 下
  // bottomToTop：下 -> 上
  app:stv_textShaderMode="leftToRight"
  />
  ```

# 3. SuperTextView Api

  [点击此处，查看详细的《SuperTextView Api文档》](https://chenbingx.github.io/SuperTextView/SuperTextView-doc/index.html)
  
# 4. 开发指南

## 4.1 SuperTextView中的层级
  在 **SuperTextView** 中，有层级划分的概念。

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/SuperTextView层级.png)

  **1.Background层**：View的`Background`背景层。

  **2.背景层**：即通过`app:stv_solid`设置的纯色背景层。在 `SuperTextView` 中通常将该层视做背景层，而不是View的`Background`。

  **3.Drawable层**：`SuperTextView`的Drawable所在的层级。如果你希望通过 `SuperTextView` 来展示图片，就是在该层展示。

  **4.文字层**：即绘制文字的层级。

  理解层级的概念对于后面将要讲述的 **Adjuster** 很有帮助。

## 4.2 设置圆角
  圆角化功能是 **SuperTextView** 最基本的功能，你可以在xml文件或者Java中进行设置。

  在xml中：

  ```
  app:stv_corner="25dp"
  ```
  在Java中：

  ```
  stv.setCorner(25);
  ```

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/圆角矩形.png)

  圆角化的设置仅对 **SuperTextView** 的【背景层】，或者将`Drawble`用于展示图片（即：配置了`app:stv_drawableAsBackground="true"`）时有效。

  如果你需要一个圆形的效果，只需要将`corner`值设置为控件最大边长度的一半即可。比如：

  ```
  android:layout_width="80dp"
  android:layout_height="80dp"
  app:stv_corner="40dp"
  app:stv_solid="#008673"
  ```

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/圆形.png)

  将一张普通的图片圆角化：

  ```
  app:stv_corner="15dp"
  app:stv_state_drawable="@drawable/avatar1"
  app:stv_drawableAsBackground="true"
  ```

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/圆角图.png)

  如果你希望实现圆形头像，或者圆角背景图的效果，那最适合不过了。

## 4.3 控制每一个圆角
  默认情况下，对 **SuperTextView** 设置`corner`会对控件的4个角都有效。当然也可以单独指定那些角需要圆角化。


  在Xml

  ```
  //设置左上角圆角
  app:stv_left_top_corner="true"

  //设置右上角圆角
  app:stv_right_top_corner="true"

  //设置左下角圆角
  app:stv_left_bottom_corner="true"

  //设置右下角圆角
  app:stv_right_bottom_corner="true"
  ```

  在Java中

  ```
  //设置左上角圆角
  stv.setLeftTopCornerEnable( boolean);
  // 设置左下角圆角
  stv.setLeftBottomCornerEnable() boolean);
  //设置右上角圆角
  stv.setRightTopCornerEnable() boolean);
  //设置右下角圆角
  stv.setRightBottomCornerEnable() boolean);
  ```


![](https://raw.githubusercontent.com/chenBingX/img/master/stv/屏幕快照 2017-04-18 08.15.59.png)

  需要注意的时候，一旦指定了任何一个圆角，`app:stv_corner` 将不再对4个角都有效了，你需要一个一个的去设置。

## 4.4 边框

  **SuperTextView** 可以通过简单的配置给控件加上边框，实际开发中十分的便捷。

  ```
  app:stv_solid="#78C3ED"
  app:stv_stroke_color="#5166ED"
  app:stv_stroke_width="5dp"
  ```

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/屏幕快照 2017-04-18 08.15.42.png)

  只需要设置`app:stv_stroke_width`大于0即开启了边框功能，如果没有设置`app:stv_stroke_color`，会有默认的黑色边框。边框的圆角化也会受到`corner`属性的影响。

  边框的效果同样能够在展示图片的时候有效。

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/圆形图片加边框.png)

## 4.5 文字描边

  使用 **SuperTextView** 能够很轻松的实现文字描边的功能。

  ```
  # 开启文字描边功能
  app:stv_text_stroke="true"
  # 设置文字填充颜色
  app:stv_text_fill_color="@color/white"
  # 设置文字描边颜色
  app:stv_text_stroke_color="#461B50"
  # 设置文字描边宽度
  app:stv_text_stroke_width="1dp"
  ```

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/文字描边.png)

  开启文字描边的功能后，文字颜色只能够通过`app:stv_text_fill_color`来设置，不要使用`android:textColor` ！

  如果想要实现空心文字的效果，只需要将`app:stv_text_fill_color`设置为透明色，或者与背景色相同即可。

  ![](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202018-05-18%20%E4%B8%8B%E5%8D%884.57.56.png)

## 4.6 状态图

  **SuperTextView** 自带状态图功能，即Drawable、Drawable2，能够展示两个状态图。

  与系统的原生TextView的Drawable不同，**SuperTextView** 的状态图能够准确的控制位置和大小。

  ```
  # boolean类型。是否开启状态图1的功能。
  app:stv_isShowState="true"

  # 设置状态图1的图片
  app:stv_state_drawable="@drawable/emoji"

  # 设置状态图1的显示模式，决定了状态图1的基础位置
  # 可选值如下：
  # left、top、right、bottom、center(默认值)、
  # leftTop、rightTop、leftBottom、rightBottom、
  # fill(充满整个SuperTextView，此时会使设置drawable的大小失效)
  app:stv_state_drawable_mode="center"

  # 设置状态图1的height
  app:stv_state_drawable_height="30dp"

  # 设置状态图1的width
  app:stv_state_drawable_width="30dp"

  # 设置状态图1相对于基础位置的左边的距离
  app:stv_state_drawable_padding_left="10dp"

  # 设置状态图1相对于基础位置上边的距离
  app:stv_state_drawable_padding_top="10dp"


  # boolean类型。是否开启状态图2的功能。
  app:stv_isShow2State="true"

  # 设置状态图2的图片
  app:stv_state_drawable2="@drawable/emoji"

  # 设置状态图2的显示模式，决定了状态图2的基础位置
  # 可选值如下：
  # left、top、right、bottom、center(默认值)、
  # leftTop、rightTop、leftBottom、rightBottom、
  # fill(充满整个SuperTextView，此时会使设置drawable的大小失效)
  app:stv_state_drawable2_mode="center"

  # 设置状态图2的height
  app:stv_state_drawable2_height="30dp"

  # 设置状态图2的width
  app:stv_state_drawable2_width="30dp"

  # 设置状态图2相对于基础位置的左边的距离
  app:stv_state_drawable2_padding_left="10dp"

  # 设置状态图2相对于基础位置上边的距离
  app:stv_state_drawable2_padding_top="10dp"

  ```

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/屏幕快照 2017-04-18 08.16.22.png)

  上图是一些使用一个状态图实现的功能，可以看到，你可以轻松准确的控制状态图的位置和大小。

  现在，看看两个状态图能够干什么。

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/屏幕快照 2017-11-16 01.46.23.png)

  - **示例一**

  ```
<com.coorchice.library.SuperTextView
android:layout_width="100dp"
android:layout_height="100dp"
  ...
  app:stv_corner="50dp"
  app:stv_state_drawable="@drawable/avatar1"
  # 将状态图1作为控件背景，以展示图片
  app:stv_drawableAsBackground="true"
  # state_drawable2的配置由此开始
  app:stv_isShowState2="true"
  app:stv_state_drawable2="@drawable/recousers"
  app:stv_state_drawable2_mode="rightTop"
  app:stv_state_drawable2_height="20dp"
  app:stv_state_drawable2_width="20dp"
  ...
  />
  ```

  - **示例二**

  ```
<com.coorchice.library.SuperTextView
android:layout_width="100dp"
android:layout_height="100dp"
  ...
  # 背景图
  android:background="@drawable/avatar7"
  # drawable1的配置由此开始
  app:stv_isShowState="true"
  app:stv_state_drawable="@drawable/triangle"
  app:stv_state_drawable_mode="leftTop"
  app:stv_state_drawable_width="20dp"
  app:stv_state_drawable_height="20dp"
  # state_drawable2的配置由此开始
  app:stv_isShowState2="true"
  app:stv_state_drawable2="@drawable/recousers"
  app:stv_state_drawable2_mode="rightTop"
  app:stv_state_drawable2_height="20dp"
  app:stv_state_drawable2_width="20dp"
  ...
  />
  ```

## 4.7 渐变效果

  **SuperTextView** 支持通过配置简单的属性实现渐变色效果。


  ```
  # 必须设置为true才能启用渐变功能。这意味着你可以灵活的控制这一功能。
  app:stv_shaderEnable="true"

  # 设置起始颜色
  app:stv_shaderStartColor="@color/main_blue"

  # 设置结尾颜色。
  app:stv_shaderEndColor="@color/pink"

  # 设置渐变模式。如上图可见，一共支持4中模式：
  # topTopBottom, bottomToTop, leftToRight, rightToLeft
  app:stv_shaderMode="rightToLeft"

  ```

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/渐变色.png)

  这些属性也支持在Java中设置。

  ```
  // 开启渐变色功能
  stv.setShaderEnable(true);
  // 设置起始颜色
  stv.setShaderStartColor(Color.BLUE);
  // 设置结束颜色
  stv.setShaderEndColor(Color.RED);
  // 设置模式
  stv.setShaderMode(SuperTextView.ShaderMode.LEFT_TO_RIGHT);
  ```

  渐变模式共有4种：

  ```
  public static enum ShaderMode {
  /**
  * 从上到下
  */
  TOP_TO_BOTTOM(0),
  /**
  * 从下到上
  */
  BOTTOM_TO_TOP(1),
  /**
  * 从左到右
  */
  LEFT_TO_RIGHT(2),
  /**
  * 从右到左
  */
  RIGHT_TO_LEFT(3);
  }
  ```

## 4.8 按压变色

  **SuperTextView** 能够快速的支持按压变色效果，就像下图一样，文字和背景色都支持。


![](https://raw.githubusercontent.com/chenBingX/img/master/stv/按压变色.gif)


  只需配置简单的属性：

  ```
  # 设置按压时的背景色
  app:stv_pressBgColor="@color/red"
  # 设置按压时的文字颜色
  app:stv_pressTextColor="@color/white"
  ```

  在Java中：

  ```
  // 设置按压背景变色
  stv.setPressBgColor(Color.RED);
  // 取消按压文字变色
  stv.setPressTextColor(-99);
  ```

  - 如果要取消按压背景变色，只需设置`PressBgColor`为透明色，`Color.TRANSPARENT`。
  - 如果要取消按压文字变色，只需设置`PressTextColor`为-99。


## 4.9 展示图片
### 4.9.1 展示本地图片

  前面有提到过，**SuperTextView** 可以通过状态图1变成一个`ImageView`。在设置好状态图1后，只需开启图片展示功能即可。

  ```
<com.coorchice.library.SuperTextView
  ...
  app:stv_state_drawable="@drawable/avatar1"
  # 开启图片展示功能
  app:stv_drawableAsBackground="true"
  ...
  />
  ```

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/展示本地图片.png)

  需要注意的是，当将状态图1用于展示图片后，状态图1将不再具备状态图的功能，直到你关闭了图片展示功能，通过`stv.setDrawableAsBackground(false)`。

### 4.9.2 展示网络图片

  ```
  stv.setUrlImage(url);
  ```

  在 **SuperTextView** 中，通过上面简单的一句即可将 **SuperTextView** 作为一个可以展示网络图片的ImageView。

  调用上面的方法会默认开启 **SuperTextView** 的图片展示功能，因此，此时状态图1的状态图功能会被停用。你也可以通过以下方法，使得能够从网络中下载状态图1：

  ```
  stv.setUrlImage(url, false);
  ```

  第二个参数表示关闭图片展示功能。

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/图片下载演示2.gif)

  上图中，第一个例子是从网络中下载图片用作状态图，第二个例子是用作展示图片。

  **SuperTextView** 为了保持依赖库的纯净和尽可能小的体积，并没有内置任何的图片加载框架。所以默认情况，将使用内置的一个简易图片引擎去下载图片，确保开发者能够正常使用展示网络图片的功能。

  **SuperTextView** 具备兼容任意第三方图片下载框架的能力，建议开发者根据项目的具体情况，选择一个目前正在使用的图片加载框架，设置到 **SuperTextView** 中，以用来加载图片。 下面将通过几个的例子展示如何将现有的图片框架安装到 **SuperTextView** 中。

  **第一步：实现图片引擎Engine**

  在 **SuperTextView** 中，核心的图片加载引擎被抽象成接口 **Engine** ，开发者需要根据所用的图片框架，实现一个 **Engine**。

  - **Glide图片加载框架**

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
// 主要是通过callback返回Drawable对象给SuperTextView
callback.onCompleted(resource);
}
});
}
}
```

- **Picasso图片加载框架**

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
// 主要是通过callback返回Drawable对象给SuperTextView
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

**第二步：安装图片引擎Engine**

实现好 **Engine** 后，下一步就是要将其安装到 **SuperTextView** 中。

建议可以在 Application的`onCreate()`中进行安装，这样当需要使用 **SuperTextView** 加载显示网络图片的时候，就能够用到三方图片框架了。

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

一行代码，轻松安装。

需要注意的是，任何时候，后安装的 **Engine** 实例总是会替换掉先前安装的 **Engine** 实例，即 **SuperTextView** 只允许全局存在一个 **Engine** 实例。

只需简单两步，即可完成任意三方图片加载框架的适配。

## 4.10 Adjuster

**Adjuster** 被设计用来在 **SuperTextView** 的绘制过程中插入一些操作。这具有非常重要的意义。比如，实时的改变控件的状态，制作复杂的动画效果或者交互效果。

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

通过重写 **Adjuster** 的 `adjust()` 方法，可以获取每次绘制过程中控件的`Canvas`对象，这意味着可以在绘制过程中从外部插入一些新的元素。当然，单单通过 **SuperTextView** 的实例修改其状态也是可以的。

通过重写 **Adjuster** 的 `onTouch()` 方法，可以获取每一次控件的触摸事件，如果在该方法中返回true，表明该 **Adjuster** 需要获取后续的触摸事件，同时也会使得 **SuperTextView** 在整个控件树中回去拦截触摸事件。配合 `adjust()` 可以实现一些复杂的交互效果。值得注意的是，如果在 **SuperTextView** 之前，已经有控件拦截的触摸事件，那么其中的 **Adjuster** 将无法获取到触摸事件。

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/SuperTextView.gif)

当装载 **Adjuster** 到  **SuperTextView** 之后，需要调用以下方法来开启 **Adjuster** 的功能：

```
stv.setAutoAdjust(true);
```

当然，停止 **Adjuster** 只需要设置为false即可。

### 4.10.1 如何装载Adjuster到SuperTextView

```
stv.addAdjuster(mAdjuster);
```

通过上面方法可以将一个 **Adjuster** 添加到 **SuperTextView** 中，最多支持添加3个 **Adjuster** 。超过3个的部分，将会始终覆盖最后一个 **Adjuster**。

如果你想要移除一个 **Adjuster**，通过下面方法来实现。

```
stv.removeAdjuster(index)
```

### 4.10.2 设置Adjuster的层级

前面有描述过 **SuperTextView** 的层级划分，**Adjuster** 可以通过配置，将其插入到指定的层级。

```
mAdjuster.setOpportunity(opportunity);
```

层级定义了如下几个枚举变量：

```
public enum Opportunity {
// 背景层和Drawable层之间
BEFORE_DRAWABLE,
// Drawable层和文字层之间
BEFORE_TEXT,
// 最上层
AT_LAST
}
```

分别对应如下几种场景，其中Emoji图是StateDrawable状态图，蓝色圆形就是一个 **Adjuster** ：

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/Opportunity.png)

### 4.10.3 开启动画

在 **SuperTextView** 中，可以通过以下方法触发 **SuperTextView** 的定时绘制：

```
stv.startAnim();
```

启动动画后， **SuperTextView** 将会以默认 **60fps** 的帧率进行刷新。配合 **Adjuster** 可以十分简单的实现动画效果。

当 **SuperTextView** 离开屏幕后，将会自动停止正在播放的动画，当在次进入屏幕时，又会自动启动。所以开发者可以无需担心动画在后台消耗资源。

当然，开发者也可以随时停止动画，通过调用以下方法：

```
stv.stopAnim();
```

通过以下方法，开发者可以随时修改 **SuperTextView** 的刷新频率：

```
// 每秒30帧
stv.setFrameRate(30);
```

## 4.11 修改 StateDrawable 颜色

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/着色.png)  

开发者可以轻松的改变一个图标的颜色，而不用再增加一个仅仅是颜色不同的图标到项目中。这项技术将为你的 Android 应用程序带来一次瘦身的机遇。  

```
# 修改 drawable 的颜色
app:stv_state_drawable_tint="@color/gray"

# 修改 drawable2 的颜色
app:stv_state_drawable2_tint="@color/red"
```

只需要如此一行简单的代码，就能瞬间赋予一张图片千变万化的能力。想要任何色彩，当然是你说了算。而这一切的发生，无需再引进另外一张图片。

在 Java 代码中，有与之对应 set/get 函数，让开发者可以在任何时候都能施展魔法，改变一张图片的色彩。


## 4.12 修改 StateDrawable 旋转角度

SuperTextView 被赋予了改变 StateDrawable 形态的能力。同样的一张图，开发者可以组合出无数种可能。  

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/旋转.png)  

只需简单的几行代码，你便可以随心所欲的变换任何一张图片。  

```
# 修改 drawable 的旋转角度
app:stv_state_drawable_rotate="90"

# 修改 drawable2 的旋转角度
app:stv_state_drawable2_rotate="90"
```

无需复杂的代码，SuperTextView 一如既往的简洁、优雅。  

同样，在 Java 代码中，也提供了对应的 set/get 函数。

这项能力，可以有效的帮助开发者将 Android 应用的体积向着极致的方向压缩。


## 4.13 设置文字渐变

![](https://raw.githubusercontent.com/chenBingX/img/master/stv/文字渐变色.png)  

这就是渐变文字！  

SuperTextView 所提供的可能是目前为止实现渐变文字最简洁、优雅的解决方案。只需要简单的配置，就能实现酷炫的渐变文字效果。

```
# 是否启用渐变色文字
app:stv_textShaderEnable="true"

# 设置文字的起始渐变色
app:stv_textShaderStartColor="@color/red"

# 设置文字的结束渐变色
app:stv_textShaderEndColor="@color/yellow"

# 设置文字的渐变的模式
# leftToRight：左 -> 右
# rightToLeft：右 -> 左
# topToBottom：上 -> 下
# bottomToTop：下 -> 上
app:stv_textShaderMode="leftToRight"
```

这些属性也在 Java 中开放了 set/get 接口，便于开发者随时动态的修改它们。


---

> - 如果你喜欢 [**SuperTextView**](https://github.com/chenBingX/SuperTextView)，希望能到 [**Github**](https://github.com/chenBingX/SuperTextView) 点个 **star** [🌟](https://github.com/chenBingX/SuperTextView) 哦！

> - **CoorChice** 会不定期的在博客平台分享干货，快进入 [CoorChice的【个人主页】](https://juejin.im/user/57fc43b67db2a200595ffd94) 关注一波吧。

**文档结束。更多例子可以Clone项目到本地学习，祝你使用愉快！**



