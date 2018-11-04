# 你好, SuperTextView

  [![](https://img.shields.io/badge/SuperTextView-v3.1-red.svg)](https://github.com/chenBingX/SuperTextView)    [![](https://img.shields.io/badge/License-Apache--2.0-blue.svg)](https://github.com/chenBingX/SuperTextView#license)   



#### [【SuperTextView english document】](https://github.com/chenBingX/SuperTextView/blob/master/README_EN.md)

#### [点击此处，查看《SuperTextView 开发参考文档》](https://chenbingx.github.io/SuperTextView/develop_guide.html)

#### [点击此处，查看详细的《SuperTextView API文档》](https://chenbingx.github.io/SuperTextView/SuperTextView-doc/index.html)

<img src="http://ogemdlrap.bkt.clouddn.com/SuperTextView_yuan%202.png" width=230 height=230 align=right alt="SuperTextView">

**SuperTextView** 出现的唯一目的，就是帮助 Android 开发者得心应手的构建 Android 应用。

**SuperTextView** 是一个高效的、全能的、优雅的 **Android** 控件。通过 **SuperTextView** ，你可以快速实现圆角背景，设置渐变色背景，给控件和文字描边，为控件增加状态图，添加按压时文字或背景变色效果，通过 **Adjuster** 模块快速插入操作到控件绘制过程中，展示图片，甚至可以直接从网络上下载图片展示...基本上涵盖了 **Android** 日常开发中会用到的绝大部分效果。而实现这一切的代价，仅仅是给 **SuperTextView** 设置一个属性。**SuperTextView** 可以帮助开发者高效、便捷、优雅的完成 Android 应用的开发。


![](https://gw.alicdn.com/tfs/TB1JEPSrH9YBuNjy0FgXXcxcXXa-1195-622.gif)


# 近期更新

## v3.0 - 你期待已久的SuperTextView
经过一年多的不断的聆听、思考、探索、验证， **SuperTextView** 完成了多次迭代，改善了一些问题，新增了一些功能，不断的完善，以带给开发者更好的开发体验。

如今， **SuperTextView** 已经具备了诸如圆角、边框、描边、按压变色、多状态图、圆角图、万能的 **Adjuster** 、加载网络图片等一系列的常用功能。得益于此，开发者能够轻松实现各种原本十分麻烦的效果，节省大量的开发时间，有效减少页面的复杂度，降低项目维护成本。

写代码，本应如此愉悦！

### 1. 链接云端的SuperTextView

早在几个月前，就开始有很多开发者向 **CoorChice** 建议，是否能够让 **SuperTextView** 具备加载网络图片的功能。其实这也是 **CoorChice** 很久之前就有考虑过的，但在 **SuperTextView** 的早期，完善其核心功能仍然是首要目标，所以一直没涉猎到图片相关的功能。

直到上一个大版本，**SuperTextView v2.0**，**CoorChie** 才尝试添加了图片展示的功能。这使得**SuperTextView** 可使用的范围得到了扩大，同时针对图片也推出了给图片增加描边、设置圆角、设置状态图等功能。相关使用文档可到以下链接查阅：

[【你好， SuperTextView】 - https://www.jianshu.com/p/1b91e11e441d](https://www.jianshu.com/p/1b91e11e441d)

这一次尝试，获得了开发者们不错的反响，大家对于使用 **SuperTextView** 去展示处理图片是有所期待的。上一个版本发布后，开发者们对于一个能展示网络图片的 **SuperTextView** 似乎更加的感兴趣了。

那么，现在，你所期待已久的 **SuperTextView** 在此！

![](http://ogemdlrap.bkt.clouddn.com/%E5%9B%BE%E7%89%87%E4%B8%8B%E8%BD%BD%E6%BC%94%E7%A4%BA2.gif)

#### 1.1 加载一张网络图片
显示一张网络图片，在 **SuperTextView** 中只需要如下代码：

```
SuperTextView stv_1 = (SuperTextView) findViewById(R.id.stv_1);
//填入图片Url
stv_1.setUrlImage(url);
```

效果就如上图中的第二个显示头像的例子一样。

如果你希望将网络图片作为 **SuperTextView** 的 StateDrawable 来展示的话，完全没问题。

```
//填入图片Url
stv_1.setUrlImage(url, false);
```

第二个参数为 **false** 表示网络图片将不会被作为背景充满整个 **SuperTextView**，而是作为一个状态图。当然，有关状态图的一切配置都将运用到此。就像上图中的第一个例子一样，整个布局包括图片、文字、背景都在一个 **SuperTextView** 中被处理，从网络下载的图片被作为 **StateDrawable** 放到了图中的位置。

#### 1.2 SuperTextView中图片引擎
**SuperTextView** 为了保持依赖库的纯净和尽可能小的体积，并没有内置任何的图片加载框架。所以默认情况，将使用内置的一个简易图片引擎去下载图片，确保开发者能够正常使用展示网络图片的功能。

但 **CoorChice** 仍然建议开发者根据项目的具体情况，选择一个目前正在使用的图片加载框架，设置到 **SuperTextView** 中，以用来加载图片。 **SuperTextView** 具备适配任意图片加载框架的能力。下面 **CoorChice** 将通过 Glide 和 Picasso 的例子展示如何将现有的图片框架安装到 **SuperTextView** 中。

##### 1.2.1 实现图片引擎Engine
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

##### 1.2.2 安装图片引擎Engine
实现好 **Engine** 后，下一步就是要将其安装到 **SuperTextView** 中。

**CoorChice** 建议可以在 Application的`onCreate()`中进行安装，这样当需要使用 **SuperTextView** 加载显示网络图片的时候，就能够用到三方图片框架了。

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

现在，你可以让 **SuperTextView** 使用指定的三方图片加载框架去加载图片了。

![](http://ogemdlrap.bkt.clouddn.com/%E5%9B%BE%E7%89%87%E4%B8%8B%E8%BD%BD%E5%88%97%E8%A1%A8%E6%BC%94%E7%A4%BA.gif)


### 2. 如何开始SuperTextView v3.0
```
	dependencies {
	   compile 'com.github.chenBingX:SuperTextView:v3.0.0'
	}
```
### 3. 其它
- 修复一个动画问题
- 一些优化

## v2.0 - 未来，从现在开始
**一直以来，CoorChice都心存一个设想，期待着能够打造这样一个控件：它能满足你的大部分开发需求，展示文字、图片、几何、动画、状态，让你使用一个控件就能高效的完成大部分开发工作。它是如此的强大，仿佛有心智一般，接受着你的输入，按照你的心意，呈现出叹为观止的画面。随着【SuperTextView v2.0】的到来，我们离这个设想更近了一步。现在，来和【SuperTextView v2.0】见个面吧！**

![SuperTextView v2.0](http://ogemdlrap.bkt.clouddn.com/stv_2.0_2.png)

### 图片，就是现在
在【SuperTextView v2.0】中，增加了对图片展示的支持。但不仅仅止于展示图片，它还能智能的根据你的输入将图片剪裁为你期望的形状。

![image](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202017-11-16%2001.51.33.png)

给图片加上圆角，加上边框，或者直接变成圆形，所有的一切只需要设置几个简单的属性，即刻呈现在你的眼前。

#### 展示一张图片
如何使用SuperTextView展示一张图片？只需要在xml中加上下面两句代码即可。

```
<com.coorchice.library.SuperTextView
    ...
    app:state_drawable="@drawable/avatar1"
    app:drawableAsBackground="true"
    ...
 />
```

如果你是`SuperTextView`的忠实用户的话，你会发现，原本的`state_drawable`现在可以被用来展示一张图片。

#### 给图片加上圆角
现在，你的图片呈现在了你的眼前，也许你还想对它做一些不一样的事情，比如，加个圆角，或者直接变成圆形？没问题，`SuperTextView`现在完全能胜任这样的工作。

```
<com.coorchice.library.SuperTextView
    android:layout_width="100dp"
    android:layout_height="100dp"
    ...
    app:corner="15dp"
    app:state_drawable="@drawable/avatar1"
    app:drawableAsBackground="true"
    ...
 />
```

如此简单！在原来的基础上你仅仅需要设置合理的`corner`值就行。

#### 也许，你还想要边框
有时候，你可能需要使用一个边框去包裹住你的图片，就像上面的示例那样。没错，这肯定在`SuperTextView`能力范围内。

```
<com.coorchice.library.SuperTextView
    android:layout_width="100dp"
    android:layout_height="100dp"
    ...
    app:corner="50dp"
    app:stroke_color="#F4E187"
    app:stroke_width="4dp"
    app:state_drawable="@drawable/avatar1"
    app:drawableAsBackground="true"
    ...
 />
```

`app:stroke_color` 掌控着边框的颜色，`app:stroke_width` 掌控着边框的宽度。一切如此流畅，一个有心智的控件本该如此，对吗？


### 第二个状态图
面对复杂的需求变化，【SuperTextView】为应对这种复杂性，孕育出了第二个状态图 `state_drawable2` 。


![](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202017-11-16%2001.46.23.png)

现在，CoorChice将向你展示，上图中的两种效果是如何实现的。

- **示例一**

```
<com.coorchice.library.SuperTextView
    android:layout_width="100dp"
    android:layout_height="100dp"
    ...
    app:corner="50dp"
    app:state_drawable="@drawable/avatar1"
    app:drawableAsBackground="true"
    // state_drawable2的配置由此开始
    app:isShowState2="true"
    app:state_drawable2="@drawable/recousers"
    app:state_drawable2_mode="rightTop"
    app:state_drawable2_height="20dp"
    app:state_drawable2_width="20dp"
    ...
 />
```

- **示例二**

```
<com.coorchice.library.SuperTextView
    android:layout_width="100dp"
    android:layout_height="100dp"
    ...
    // 背景图
    android:background="@drawable/avatar7"
    // drawable1的配置由此开始
    app:isShowState="true"
    app:state_drawable="@drawable/triangle"
    app:state_drawable_mode="leftTop"
    app:state_drawable_width="20dp"
    app:state_drawable_height="20dp"
    // state_drawable2的配置由此开始
    app:isShowState2="true"
    app:state_drawable2="@drawable/recousers"
    app:state_drawable2_mode="rightTop"
    app:state_drawable2_height="20dp"
    app:state_drawable2_width="20dp"
    ...
 />
```

就如你所熟悉的一样，`state_drawable2` 延续了第一代一切流畅的操作。在聪明的你合理的使用下，【SuperTextView】一定能够大放异彩！😉

### 属于Adjuster的时代
此前，`Adjuster` 的设计使得【SuperTextView】具有了灵魂，成为更聪明的控件。对绘制过程的插入，触摸事件的捕捉，使得你能轻松的从外部改变一个控件的状态。创意始于心，而行于此。

现在，【SuperTextView】能够同时承载最多3个 `Adjuster` ！也许，你的创意会更加的炫目。

![](http://ogemdlrap.bkt.clouddn.com/%E5%A4%9AAdjuster%E6%BC%94%E7%A4%BA4.gif)

在上面这个示例中，CoorChice将早起的两个【扫光】和【涟漪】特效都加入到了一个【SuperTextView】中，结果就是你看到的这样。

更多的 `Adjuster` 意味着更多的组合，更多的惊喜。在【v1.4.0】中，CoorChice同样使用了 `Adjuster` 来轻松的实现了按压变色功能。

这是 `Adjuster` 的时代，睿智的你一定可以运用它挥洒创意的。

需要注意的是⚠️，原本的 `setAdjuster(Adjuster)` 方法目前仍然被保留，但以后的版本将会被移除，你必须要尽快迁移。新的替代方法为 `addAdjuster(Adjuster)` 。

### 其它
- 修正控制Shader模式的属性 `app:shaderMode="leftToRight"` 的拼写。原来为 `app:shaderMode="leftTopRight"` 。如果你使用了该属性，在升级【SuperTextView v2.0】后请及时修正。
- 增加 `set/getPressBgColor()` 和 `set/getPressTextColor()` 用于在代码中控制按压背景色。


## v1.4.0
- 千呼万唤使出来！你想要的按压变色效果在这里！

![image](http://ogemdlrap.bkt.clouddn.com/%E6%8C%89%E5%8E%8B%E5%8F%98%E8%89%B2.gif)

只需在xml文件中设置以下两个属性就能轻松实现按压变色效果，例如上图那样的：

```
# 设置按压时的背景色
app:pressBgColor="@color/red"
# 设置按压时的文字颜色
app:pressTextColor="@color/white"
```
这个功能是依托内置一个`Adjuster`实现的，你可以看看这[]()。

CoorChice想说的是，`Adjuster`是`SuperTextView`的灵魂所在，它能够让一切创意变成可能。

- 暴露一个新的方法`getCorners()`。你可以通过它获得`SuperTextView`的所有圆角信息，有时候你真的很需要它。
- 如何使用SuperTextView v1.4？

```
dependencies {
	compile 'com.github.chenBingX:SuperTextView:v1.4'
}
```


## v1.3
- 支持随时修改动画帧率。难以置信的是，你甚至可以在动画执行过程中随时修改！当然最好不要这么做。

```
mSuperTextView.setFrameRate(30);
// 修改帧率为30帧
```
- 优化动画驱动的性能。
- 酷炫不止，渐变来袭！

![link](http://ogemdlrap.bkt.clouddn.com/%E6%B8%90%E5%8F%98%E8%89%B2.png)

一触即变，想象不至于此。艺术家，发挥你的创造力吧！

同样，渐变效果的设置支持在xml中设置，并且能够即时预览。

```
app:shaderEnable="true"
// 必须设置为true才能启用渐变功能。这意味着你可以灵活的控制这一功能。

app:shaderStartColor="@color/main_blue"
// 设置起始颜色。

app:shaderEndColor="@color/pink"
// 设置结尾颜色。

app:shaderMode="rightToLeft"
// 设置渐变模式。如上图可见，一共支持4中模式：
// topTopBottom, bottomToTop, leftToRight, rightToLeft
```
当然，这些属性也都提供了对应的`set/get`方法，供你在Java中动态改变／获取它们的值。比如：

```
mSuperTextView.setShaderStartColor(Color.RED);
```
- 现在，提供了**SuperTextView**的详尽文档，你可以到这下载查看（解压后打开目录下的`index.html`开始）：
[**SuperTextView文档：http://ogemdlrap.bkt.clouddn.com/SuperTextView%E6%96%87%E6%A1%A3%20.zip?attname=**](http://ogemdlrap.bkt.clouddn.com/SuperTextView%E6%96%87%E6%A1%A3%20.zip?attname=)

- 如何使用SuperTextView 1.3
  在你的**build.gradle**中加入：

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
- 最低支持Android 4.0，SdkVersion 14。
- 支持优雅动人的 **【链式表达式】** , eg:
```
mSuperTextView.setAdjuster(new MoveEffectAdjuster())
        .setAutoAdjust(true)
        .startAnim();
```
- 减小占用空间。

# 简介
欢迎使用**SuperTextView**，这篇文档将会向你展示如何使用这个控件来提高你构建项目的效率。
![Cover](http://ogemdlrap.bkt.clouddn.com/SuperTextView_cover0.png)

**SuperTextView**继承自TextView，它能够大量的减少布局的复杂程度，并且使得一些常见的效果变得十分容易实现且高效。同时，它内置了动画驱动，你只需要合理编写**Adjuster**，然后`startAnim()`就可以看到预期的动画效果。它仅仅是一个控件，所以你可以不费吹灰之力的在你的项目中集成使用。

# 特点
1. 你从此不必再为背景图编写和管理大量<shape>文件了。
2. 重新优化的**状态图功能**使得你能够精确的控制状态图的大小，以及在**SuperTextView**中的位置。
3. 支持设置圆角，并且能够精确的控制圆角位置。
4. 能够轻松的实现控件边框效果。
5. 支持文字描边，这使得空心文字效果成为了可能。
6. 内置动画驱动，你只需配合**Adjuster**合理的使用即可。
7. **Adjuster**的出现，使得你对控件的绘制过程具有了掌控权，良好的设计使得它能够完美的实现绝大部分你脑海中的效果。

# 使用指南
## 支持的属性
**SuperTextView**十分方便的支持在xml中直接设置属性，并且你能够立即看到效果。就像你平时使用TextView一样方便。

```
<SuperTextView
    android:layout_width="50dp"
    android:layout_height="50dp"

    //设置圆角。会同时作用于填充和边框(如果边框存在的话)。
    //如果要设置为圆形，只需要把该值设置为宽或长的1/2即可。
    app:corner="25dp"
    //设置左上角圆角
    app:left_top_corner="true"
    //设置右上角圆角
    app:right_top_corner="true"
    //设置左下角圆角
    app:left_bottom_corner="true"
    //设置右下角圆角
    app:right_bottom_corner="true"
    //设置填充颜色
    app:solid="@color/red"
    //设置边框颜色
    app:stroke_color="@color/black"
    //设置边框的宽度。
    app:stroke_width="2dp"
    //放置一个drawable在背景层上。默认居中显示。
    //并且默认大小为SuperTextView的一半。
    app:state_drawable="@drawable/emoji"
    //设置drawable的显示模式。可选值如下：
    // left、top、right、bottom、center(默认值)、
    //leftTop、rightTop、leftBottom、rightBottom、
    //fill(充满整个SuperTextView，此时会使设置drawable的大小失效)
    app:state_drawable_mode="center"
    //设置drawable的height
    app:state_drawable_height="30dp"
    //设置drawable的width
    app:state_drawable_width="30dp"
    //设置drawble相对于基础位置左边的距离
    app:state_drawable_padding_left="10dp"
    //设置drawble相对于基础位置上边的距离
    app:state_drawable_padding_top="10dp"
    // boolean类型。是否显示drawable。
    //如果你想要设置的drawable显示出来，必须设置为true。
    //当不想让它显示时，再设置为false即可。
    app:isShowState="true"
    //是否开启文字描边功能。
    //注意，启用这个模式之后通过setTextColor()设置的颜色将会被覆盖。
    //你需要通过text_fill_color来设置文字的颜色。
    app:text_stroke="true"
    // 文字的描边颜色。默认为Color.BLACK。
    app:text_stroke_color="@color/black"
    // 文字描边的宽度。
    app:text_stroke_width="1dp"
    // 文字填充的颜色。默认为Color.BLACK。
    app:text_fill_color="@color/blue"
    // boolean类型。是否启用Adjuster功能。
    //具体干什么，需要在Java中为SuperTextView实现一个Adjuster。
    //当你启用这个功能而没有实现自己的Adjuster时，
    //SuperTextView会启用默认的Adjuster。它会按照一定的规则调整文字大小。
    app:autoAdjust="true"
    />

```

以上这些属性，均可以在Java中进行动态的设置。同时也能够获得它们的值。例如：


```
mSuperTextView.setCorner(10);
mSuperTextView.getCorner();
```

### 圆形和边框
![image](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202017-04-18%2008.15.42.png)

为了实现上图效果，通常你需要编写和管理大量的<shape>文件。现在你只需要在xml或代码中对**SuperTextView**直接进行设置即可。

### 不简单的圆角
![image](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202017-04-18%2008.15.59.png)

不同于简单的圆角，**SuperTextView**支持精确的控制圆角的位置。一个、两个、三个都没问题。一切由你掌控。

### 神奇的文字描边
![image](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202017-04-18%2008.16.13.png)

文字描边从未如此简单！

### 高效的状态图
![image](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202017-04-18%2008.16.22.png)

不同于原生的Drawable，**SuperTextView**对于Drawable提供了更多精细化的控制操作。你能够轻松的指定Drawable大小以及位置，只需一个属性就能搞定。

相信你一定深有感触，想要实现上图中的效果，往往需要嵌套多层布局(一般3层吧？)。而**SuperTextView**只需一个控件，并且十分简单高效的就能实现。它能够大量的减少你的App中的布局复杂程度，减少视图树的绘制时间。

## 炸裂的Adjuster
**Adjuster**被设计用来在**SuperTextView**的绘制过程中插入一些操作。这具有非常重要的意义。比如，默认实现的**DefaultAdjuster**能够动态的调整文字的大小。当然，你可以用它来实现各种各样的效果。

**想要Adjuster生效，你必须调用`SuperTextView.setAutoAdjust(true)`来启用Adjuster功能。当然，你可以所以方便的停止，通过调用`SuperTextView.setAutoAdjust(false)`。并且，你需要注意调用顺序，因为一旦调用了`SuperTextView.setAutoAdjust(true)`，而Adjuster没有被设置的话，将会启用默认的`DefaultAdjuster`(它能够动态的调整文字大小)，直到你设置了你自己的Adjuster**

### 干预控件的绘制
实现一个Adjuster需要继承SuperTextView.Adjuster，并且实现`adjust(SuperTextView v, Canvas canvas)`方法。Adjuster.adjust()会在每次绘制过程中被调用，这意味着你能够不可思议的从外部干预控件的绘制过程。

```
public class YourAdjuster extends SuperTextView.Adjuster {

  @Override
  protected void adjust(SuperTextView v, Canvas canvas) {
    //do your business。
  }

}
```

**注意，如果开启动画，你必须十分谨慎的编写adjuster()中的代码。因为动画会以60帧/每秒的速度进行绘制。这意味着，这个方法每秒会被调用60次！所以，千万不要在这个方法中重复的创建对象，会卡爆的！原因是短时间的大量将会引起【内存抖动】，导致GC频繁发生。相关知识你可以看看我的这两篇文章：**
- [【Android内存基础——内存抖动http://www.jianshu.com/p/69e6f894c698】](http://www.jianshu.com/p/69e6f894c698)
- [【用两张图告诉你，为什么你的App会卡顿?http://www.jianshu.com/p/df4d5ec779c8】](http://www.jianshu.com/p/df4d5ec779c8)


### 响应触摸事件

如果你重载Adjuster的`onTouch(SuperTextView v, MotionEvent event)`方法，你将能够获得**SuperTextView**的触摸事件。这是重要的一点，如果你想持续的对**SuperTextView**的触摸事件进行处理，你必须使`onTouch()`返回true。否则你只能接收到一个ACTION_DOWN事件，而不是一个事件流。

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

### 如此惊艳的效果

得益于**SuperTextView**内置的动画驱动，你能够结合Adjuster来实现难以置信的动画效果。一切只需要在你合理的编写好Adjuster后，调用`startAnim()`和`stopAnim()`来启动／停止动画。

![link](http://ogemdlrap.bkt.clouddn.com/SuperTextView.gif)

如你所见，上面的效果就是通过Adjuster来实现的。并且这种**拔插式**的设计，使得你能够随时在同一个**SuperTextView**上使用新的Adjuster，你所有需要做的事情就是创建一个新的Adjuster，然后调用`setAdjuster()`。

之前`@Alex_Cin`希望看到Ripple涟漪效果，所以在`RippleAdjuster.java`中，我演示了如何使用Adjuster和动画驱动配合实现上图的Rippler涟漪效果。[【RippleAdjuster.java链接：https://github.com/chenBingX/SuperTextView/blob/master/app/src/main/java/com/coorchice/supertextview/SuperTextView/Adjuster/RippleAdjuster.java】](https://github.com/chenBingX/SuperTextView/blob/master/app/src/main/java/com/coorchice/supertextview/SuperTextView/Adjuster/RippleAdjuster.java)

看，你可以使用Adjuster实现自己的Ripple效果。

### 指定Adjuster的层级
**Adjuster**贴心的设计了控制作用层级的功能。你可以通过`Adjuster.setOpportunity(Opportunity opportunity)`来指定Adjuster的绘制层级。

在**SuperTextView**中，绘制层级被从下到上分为：背景层、Drawable层、文字层3个层级。通过Opportunity来指定你的Adjuster想要插入到那个层级间。

```
public enum Opportunity {
      BEFORE_DRAWABLE, //背景层和Drawable层之间
      BEFORE_TEXT,     //Drawable层和文字层之间
      AT_LAST          //最上层
}
```

三种类型的Opportunity示意图。

![image](http://ogemdlrap.bkt.clouddn.com/Opportunity.png)

默认值是`Opportunity.BEFORE_TEXT`。即第二张图的示例。

事实上，只要你愿意，**SuperTextView**就相当于一张画布，你可以在上面任意的挥洒你的创意。它能够让你专注于创作，而不用去在意编写那些无用麻烦的代码。

# 如何开始使用

在你的**build.gradle**中加入：

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    compile 'com.github.chenBingX:SuperTextView:v3.0.0'
}
```

现在，你可以开始使用**SuperTextView**了。

> - 如果你或你的团队正在使用 **SuperTextView**，可以通过邮箱 [`coorchice.cb@alibaba-inc.com`](coorchice.cb@alibaba-inc.com) 告知我。
> - 如果你喜欢**SuperTextView**，希望能顺手在 [**Github**](https://github.com/chenBingX/SuperTextView) 点个**star**哦！


