<p align="center">
 <img width="200px" src="https://res.cloudinary.com/dqxo6zuw7/image/upload/v1598180618/Circle_Menu_bsq21v.png"
    align="center"
    alt="Circle Menu"
    />
 <h2 align="center">CircleMenu</h2>
 <p align="center"><b>Simple, elegant menu with a circular layout!</b></p>
</p>

<p align="center">
  <a href="https://android-arsenal.com/details/1/5361">
    <img alt="Android Arsenal" src="https://img.shields.io/badge/Android%20Arsenal-CircleMenu-brightgreen.svg?style=flat" />
  </a>
  <a href="#">
    <img alt="minSdkVersion 15" src="https://img.shields.io/badge/minSdkVersion-15-blue.svg" />
  </a>
  <a href="https://bintray.com/imangazaliev/maven/circlemenu/_latestVersion">
    <img alt="Download" src="https://api.bintray.com/packages/imangazaliev/maven/circlemenu/images/download.svg" />
  </a>
  <br />
</p>

<p align="center">
  <a href="#setup">Setup</a> •
  <a href="#preview">Preview</a> •
  <a href="#-usage">Usage</a> •
  <a href="#-license">License</a>
</p>

## 🛠Setup

Add this to your app build.gradle:

```gradle
implementation 'com.github.imangazalievm:circlemenu:3.0.0'
```

## Preview

⭕ Simple Circle Menu:

<img src="https://github.com/ImangazalievM/CircleMenu/blob/master/art/preview_simple.gif" width="50%">

⭕ Using with BottomAppBar:

<img src="https://github.com/ImangazalievM/CircleMenu/blob/master/art/preview_bottom_bar.gif" width="50%">

⭕ Using as FAB:

<img src="https://github.com/ImangazalievM/CircleMenu/blob/master/art/preview_fab.gif" width="50%">

## 💥 Usage

Add to your layout xml-file:

```xml
<com.imangazaliev.circlemenu.CircleMenu
        android:id="@+id/circleMenu"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:buttonColors="@array/colors"
        app:buttonIcons="@array/icons" />
```

Handling menu items clicks:

```kotlin
val circleMenu = findViewById<CircleMenu>(R.id.circleMenu);
circleMenu.setOnItemClickListener { menuButton ->   }
```

You can use ```open(boolean animate)``` and ```close(boolean animate)``` methods, to open and close menu programmatically:

```kotlin
circleMenu.open(true)
```

Set EventListener for handling open/close actions

```kotlin
circleMenu.setOnItemClickListener { buttonIndex -> }

circleMenu.onMenuOpenAnimationStart { }

circleMenu.onMenuOpenAnimationEnd { }

circleMenu.onMenuCloseAnimationStart { }

circleMenu.onMenuCloseAnimationEnd { }

circleMenu.onButtonClickAnimationStart { buttonIndex -> }

circleMenu.onButtonClickAnimationEnd { buttonIndex -> }
```

#### ⚙ Options

CircleMenu XML-options:

- `buttonIcons` **(required)** - icons of menu buttons
- `buttonColors` **(required)** - background colors of menu buttons
- `iconsColor` - color of buttons icons
<br><br>
- `startAngle` - start circle angle
- `maxAngle` - maximum degree of the menu arc
- `distance` - the distance between center menu and buttons (radius)
- `centerButtonColor` - background color of center menu button
- `centerButtonIconColor` - icon background color of center menu button
<br><br>
- `menuIcon` - center button icon type: `hamburger` or `plus`
- `openOnStart` - open the menu when the screen starts
- `showSelectAnimation` - show select animation when clicking on on a button or just close the menu


## 🤝 License

```
The MIT License

Copyright (c) 2016-2020 Mahach Imangazaliev

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```