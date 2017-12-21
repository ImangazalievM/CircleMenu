# CircleMenu

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-CircleMenu-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5361)
![minSdkVersion 21](https://img.shields.io/badge/minSdkVersion-15-blue.svg)
[ ![Download](https://api.bintray.com/packages/imangazaliev/maven/circlemenu/images/download.svg) ](https://bintray.com/imangazaliev/maven/circlemenu/_latestVersion)

<img src="https://github.com/ImangazalievM/CircleMenu/blob/master/art/splash.png" width="100%">

CircleMenu is a simple, elegant menu with a circular layout.

<img src="https://github.com/ImangazalievM/CircleMenu/blob/master/art/preview.gif" width="33%">

# Setup

Add this to your app build.gradle:

```gradle
compile 'com.github.imangazalievm:circlemenu:1.1.1'
```

# Usage

Add to your layout xml-file:

```xml
<com.imangazaliev.circlemenu.CircleMenu
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        android:id="@+id/circleMenu"
        >

        <com.imangazaliev.circlemenu.CircleMenuButton
            android:id="@+id/favorite"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:colorNormal="#2196F3"
            app:colorPressed="#1E88E5"
            app:icon="@drawable/ic_favorite"
            />

        <com.imangazaliev.circlemenu.CircleMenuButton
            android:id="@+id/search"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:colorNormal="#4CAF50"
            app:colorPressed="#43A047"
            app:icon="@drawable/ic_search"/>

        <com.imangazaliev.circlemenu.CircleMenuButton
            android:id="@+id/alert"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:colorNormal="#F44336"
            app:colorPressed="#E53935"
            app:icon="@drawable/ic_alert"/>

</com.imangazaliev.circlemenu.CircleMenu>
```
Set OnItemClickListener for handling menu items clicks:

```java
CircleMenu circleMenu = (CircleMenu) findViewById(R.id.circleMenu);
circleMenu.setOnItemClickListener(new CircleMenu.OnItemClickListener() {
    @Override
    public void onItemClick(MenuButton menuButton) {

    }
});
```
Set OnStateUpdateListener for handling open/close actions

```java
circleMenu.setStateUpdateListener(new CircleMenu.OnStateUpdateListener() {
    @Override
    public void onMenuExpanded() {

    }

    @Override
    public void onMenuCollapsed() {

    }
});
```

Also you can enable hints, that shows in Toast by long pressing a button

```xml
<com.imangazaliev.circlemenu.CircleMenu
        ...
        app:hintsEnabled="true">

        <com.imangazaliev.circlemenu.CircleMenuButton
                   ...
                   app:hintText="Place"/>

        ...

</com.imangazaliev.circlemenu.CircleMenu>
```

### If you want to use multiple checks


<p align="center">
  <img src="etc/circle-menu-checked.gif" height="500" alt="progress image view" />
</p>

```xml
<com.imangazaliev.circlemenu.CircleMenu
        ...
        app:multiple_check="true"
        app:center_drawable="@drawable/ic_add_alert_black_24dp"
        app:confirmation_center_drawable="@drawable/ic_send_red">

        <!-- Add your menu button items here -->

</com.imangazaliev.circlemenu.CircleMenu>
```

### Animated border to multiple checks

<p align="center">
  <img src="etc/check-border.gif" height="500" alt="progress image view" />
</p>

```xml
<com.imangazaliev.circlemenu.CircleMenu
        ...
        app:multiple_check="true"
        app:border_check="true"
        app:alpha_check="false"
        app:center_drawable="@drawable/ic_add_alert_black_24dp"
        app:confirmation_center_drawable="@drawable/ic_send_red">

        <!-- Add your menu button items here -->

</com.imangazaliev.circlemenu.CircleMenu>
```

### Multiple checks attributes on Circle Menu

* `app:multiple_check` : Enables the multiple_check selection (Default: `false`)
* `app:border_check="true"`: Enable animation border
* `app:alpha_check="false"`: Enable animation alpha
* `app:center_drawable` : Path to your custom graphic resource
* `app:confirmation_center_drawable` : Path to your custom confirmation button graphic resource

### Attributes on Circle Menu Button

* `app:enable_border` : Enable animation border
* `app:full_drawable` : When you want to draw in full circle


## License

The MIT License

Copyright (c) 2016 Mahach Imangazaliev

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
