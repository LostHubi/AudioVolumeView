# AudioVolumeView
安卓录音音量显示控件

## 鸣谢
[ws123:VoiceLine](https://github.com/ws123/VoiceLine)

## 预览
![预览图](https://github.com/LostHubi/AudioVolumeView/blob/master/preview_capture.png)

## 使用方式

### 引入方式
修改项目的build.gradle文件
```groovy
repositories {
    maven { 
        url "https://dl.bintray.com/loster/maven/" 
    }
}
dependencies {
    implementation "indi.hubi.viewlab:avvlib:1.0.0"
}
```

### 可设置的属性
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="avv">
        <!--矩形块颜色-->
        <attr name="rectColor" format="color"/>
        <!--移动整个控件宽度所需的时间，默认5000ms-->
        <attr name="rectDuration" format="integer"/>
        <!--矩形宽度-->
        <attr name="rectWidth" format="dimension"/>
        <!--矩形之间的间隔-->
        <attr name="rectSpace" format="dimension"/>
        <!--矩形圆角半径-->
        <attr name="rectCorner" format="dimension"/>
        <!--矩形最小高度，小于等于最小音量时，矩形显示此高度-->
        <attr name="rectInitHeight" format="dimension"/>
        <!--所输入音量的最大值-->
        <attr name="maxVolume" format="float"/>
        <!--所输入音量的最小值-->
        <attr name="minVolume" format="float"/>
        <!--移动方向-->
        <attr name="rectOrientation">
            <!-- 从右向左移动，默认值 -->
            <enum name="right_to_left" value="0"/>
            <!-- 从左向右移动 -->
            <enum name="left_to_right" value="1"/>
        </attr>
    </declare-styleable>
</resources>
```
### 代码示例
```xml
<indi.hubi.viewlab.AudioVolumeView
    android:id="@+id/avv"
    android:layout_width="match_parent"
    android:layout_height="50dp"
    app:maxVolume="100"
    app:minVolume="40"
    app:rectCorner="1dp"
    app:rectInitHeight="4dp"
    app:rectSpace="8dp"
    app:rectWidth="2dp"
    app:rectOrientation="right_to_left"
    app:rectDuration="10000"
    app:rectColor="#FFFFFF"
/>
```

### 动态效果图
 ![image](https://github.com/LostHubi/AudioVolumeView/blob/master/preview.gif)
