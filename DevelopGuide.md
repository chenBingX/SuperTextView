# SuperTextView 开发参考文档

<img src="http://ogemdlrap.bkt.clouddn.com/SuperTextView_yuan%202.png" width=130 height=130 align=right alt="SuperTextView">

从 **SuperTextView** 诞生之初，便始终坚持一个愿景，即帮助 Android 开发者快速愉悦的去构建一个应用。

**SuperTextView** 是一个高效的、全能的 **Android** 控件。通过 **SuperTextView** ，你可以快速实现圆角背景，设置渐变色背景，给控件和文字描边，为控件增加状态图，添加按压时文字或背景变色效果，通过 **Adjuster** 模块快速插入操作到控件绘制过程中，展示图片，甚至可以直接从网络上下载图片展示...基本上涵盖了 **Android** 日常开发中会用到的绝大部分效果。而实现这一切的代价，仅仅是给 **SuperTextView** 设置一个属性。**SuperTextView** 可以帮助开发者高效、便捷、优雅的完成需求开发。

# 1. 获取 SuperTextView


- Gradle下添加依赖：

```
 dependencies {
       compile 'com.github.chenBingX:SuperTextView:v3.0.0'
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

    // 是否将state_drawable作为背景图
    // 将state_drawable作为背景图可以让SuperTextView具备展示图片的能力
    // 通过调节corner、stroke等属性，可以给图片设置圆角、边框等
    app:drawableAsBackground="true"

    //放置一个drawable在背景层上。默认居中显示。
    //并且默认大小为SuperTextView的一半。
    app:state_drawable2="@drawable/emoji"

    //与state_drawable类似
    app:state_drawable2_mode="center"

    //与state_drawable_height类似
    app:state_drawable2_height="30dp"

    //与state_drawable_width类似
    app:state_drawable2_width="30dp"

   //与state_drawable_padding_left类似
    app:state_drawable2_padding_left="10dp"

    //与state_drawable_padding_top类似
    app:state_drawable2_padding_top="10dp"

   //与isShowState类似
    app:isShow2State="true"

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

    // 必须设置为true才能启用渐变功能。这意味着你可以灵活的控制这一功能。
    app:shaderEnable="true"

    // 设置起始颜色。
    app:shaderStartColor="@color/main_blue"

    // 设置结尾颜色。
    app:shaderEndColor="@color/pink"

    // 设置渐变模式。如上图可见，一共支持4中模式：
    // topTopBottom, bottomToTop, leftToRight, rightToLeft
    app:shaderMode="rightToLeft"

    // 设置按压时的背景色
    app:pressBgColor="@color/red"

    // 设置按压时的文字颜色
    app:pressTextColor="@color/white"
    />
 ```

# 3. SuperTextView Api

 [点击此处，查看详细的《SuperTextView Api文档》](https://chenbingx.github.io/SuperTextView/SuperTextView-doc/index.html)

# 4. 开发指南

## 4.1 SuperTextView中的层级
在 **SuperTextView** 中，有层级划分的概念。

![](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202018-05-18%20%E4%B8%8B%E5%8D%883.45.04.png)

**1.Background层**：View的`Background`背景层。

**2.背景层**：即通过`app:solid`设置的纯色背景层。在 `SuperTextView` 中通常将该层视做背景层，而不是View的`Background`。

**3.Drawable层**：`SuperTextView`的Drawable所在的层级。如果你希望通过 `SuperTextView` 来展示图片，就是在该层展示。

**4.文字层**：即绘制文字的层级。

理解层级的概念对于后面将要讲述的 **Adjuster** 很有帮助。

## 4.2 设置圆角
圆角化功能是 **SuperTextView** 最基本的功能，你可以在xml文件或者Java中进行设置。

在xml中：

```
app:corner="25dp"
```
在Java中：

```
stv.setCorner(25);
```

 ![](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202018-05-18%20%E4%B8%8B%E5%8D%8812.57.49.png)

圆角化的设置仅对 **SuperTextView** 的【背景层】，或者将`Drawble`用于展示图片（即：配置了`app:drawableAsBackground="true"`）时有效。

如果你需要一个圆形的效果，只需要将`corner`值设置为控件最大边长度的一半即可。比如：

```
android:layout_width="80dp"
android:layout_height="80dp"
app:corner="40dp"
app:solid="#008673"
```

![](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202018-05-18%20%E4%B8%8B%E5%8D%883.59.41.png)

将一张普通的图片圆角化：

```
app:corner="15dp"
app:state_drawable="@drawable/avatar1"
app:drawableAsBackground="true"
```

![](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202018-05-18%20%E4%B8%8B%E5%8D%884.02.42.png)

如果你希望实现圆形头像，或者圆角背景图的效果，那最适合不过了。

# 4.3 逐个控制圆角
 默认情况下，对 **SuperTextView** 设置`corner`会对控件的4个角都有效。当然也可以单独指定那些角需要圆角化。


 在Xml

 ```
//设置左上角圆角
app:left_top_corner="true"

//设置右上角圆角
app:right_top_corner="true"

//设置左下角圆角
app:left_bottom_corner="true"

//设置右下角圆角
app:right_bottom_corner="true"
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


 ![image](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202017-04-18%2008.15.59.png)

 需要注意的时候，一旦指定了任何一个圆角，`app:corner` 将不再对4个角都有效了，你需要一个一个的去设置。

# 4.4 边框

 **SuperTextView** 可以通过简单的配置给控件加上边框，实际开发中十分的便捷。

 ```
 app:solid="#78C3ED"
 app:stroke_color="#5166ED"
 app:stroke_width="5dp"
 ```

 ![](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202018-05-18%20%E4%B8%8B%E5%8D%884.46.13.png)

 只需要设置`app:stroke_width`大于0即开启了边框功能，如果没有设置`app:stroke_color`，会有默认的黑色边框。边框的圆角化也会受到`corner`属性的影响。

 边框的效果同样能够在展示图片的时候有效。

 ![](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202018-05-18%20%E4%B8%8B%E5%8D%884.49.48.png)

# 4.5 文字描边

 使用 **SuperTextView** 能够很轻松的实现文字描边的功能。

 ```
 # 开启文字描边功能
 app:text_stroke="true"
 # 设置文字填充颜色
 app:text_fill_color="@color/white"
 # 设置文字描边颜色
 app:text_stroke_color="#461B50"
 # 设置文字描边宽度
 app:text_stroke_width="1dp"
 ```

![](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202018-05-18%20%E4%B8%8B%E5%8D%884.53.07.png)

开启文字描边的功能后，文字颜色只能够通过`app:text_fill_color`来设置，不要使用`android:textColor` ！

如果想要实现空心文字的效果，只需要将`app:text_fill_color`设置为透明色，或者与背景色相同即可。

![](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202018-05-18%20%E4%B8%8B%E5%8D%884.57.56.png)

# 4.6 状态图

**SuperTextView** 自带状态图功能，即Drawable、Drawable2，能够展示两个状态图。

与系统的原生TextView的Drawable不同，**SuperTextView** 的状态图能够准确的控制位置和大小。

```
# boolean类型。是否开启状态图1的功能。
app:isShowState="true"

# 设置状态图1的图片
app:state_drawable="@drawable/emoji"

# 设置状态图1的显示模式，决定了状态图1的基础位置
# 可选值如下：
# left、top、right、bottom、center(默认值)、
# leftTop、rightTop、leftBottom、rightBottom、
# fill(充满整个SuperTextView，此时会使设置drawable的大小失效)
app:state_drawable_mode="center"

# 设置状态图1的height
app:state_drawable_height="30dp"

# 设置状态图1的width
app:state_drawable_width="30dp"

# 设置状态图1相对于基础位置的左边的距离
app:state_drawable_padding_left="10dp"

# 设置状态图1相对于基础位置上边的距离
app:state_drawable_padding_top="10dp"


# boolean类型。是否开启状态图2的功能。
app:isShow2State="true"

# 设置状态图2的图片
app:state_drawable2="@drawable/emoji"

# 设置状态图2的显示模式，决定了状态图2的基础位置
# 可选值如下：
# left、top、right、bottom、center(默认值)、
# leftTop、rightTop、leftBottom、rightBottom、
# fill(充满整个SuperTextView，此时会使设置drawable的大小失效)
app:state_drawable2_mode="center"

# 设置状态图2的height
app:state_drawable2_height="30dp"

# 设置状态图2的width
app:state_drawable2_width="30dp"

# 设置状态图2相对于基础位置的左边的距离
app:state_drawable2_padding_left="10dp"

# 设置状态图2相对于基础位置上边的距离
app:state_drawable2_padding_top="10dp"

```

![image](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202017-04-18%2008.16.22.png)

上图是一些使用一个状态图实现的功能，可以看到，你可以轻松准确的控制状态图的位置和大小。

现在，看看两个状态图能够干什么。

![](http://ogemdlrap.bkt.clouddn.com/%E5%B1%8F%E5%B9%95%E5%BF%AB%E7%85%A7%202017-11-16%2001.46.23.png)

- **示例一**

```
<com.coorchice.library.SuperTextView
    android:layout_width="100dp"
    android:layout_height="100dp"
    ...
    app:corner="50dp"
    app:state_drawable="@drawable/avatar1"
    # 将状态图1作为控件背景，以展示图片
    app:drawableAsBackground="true"
    # state_drawable2的配置由此开始
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
    # 背景图
    android:background="@drawable/avatar7"
    # drawable1的配置由此开始
    app:isShowState="true"
    app:state_drawable="@drawable/triangle"
    app:state_drawable_mode="leftTop"
    app:state_drawable_width="20dp"
    app:state_drawable_height="20dp"
    # state_drawable2的配置由此开始
    app:isShowState2="true"
    app:state_drawable2="@drawable/recousers"
    app:state_drawable2_mode="rightTop"
    app:state_drawable2_height="20dp"
    app:state_drawable2_width="20dp"
    ...
 />
```

# 4.7 渐变效果

**SuperTextView** 支持通过配置简单的属性实现渐变色效果。


```
# 必须设置为true才能启用渐变功能。这意味着你可以灵活的控制这一功能。
app:shaderEnable="true"

# 设置起始颜色
app:shaderStartColor="@color/main_blue"

# 设置结尾颜色。
app:shaderEndColor="@color/pink"

# 设置渐变模式。如上图可见，一共支持4中模式：
# topTopBottom, bottomToTop, leftToRight, rightToLeft
app:shaderMode="rightToLeft"

```

![link](http://ogemdlrap.bkt.clouddn.com/%E6%B8%90%E5%8F%98%E8%89%B2.png)

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


![image](http://ogemdlrap.bkt.clouddn.com/%E6%8C%89%E5%8E%8B%E5%8F%98%E8%89%B2.gif)


只需配置简单的属性：

```
# 设置按压时的背景色
app:pressBgColor="@color/red"
# 设置按压时的文字颜色
app:pressTextColor="@color/white"
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


