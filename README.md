# CircleMenu

CircleMenu is a simple, elegant menu with a circular layout.

# Setup

Add this to your app build.gradle:

```gradle
compile 'com.github.imangazalievm:circlemenu:2.0.1'
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

        <com.imangazaliev.circlemenu.MenuButton
            android:id="@+id/favorite"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:colorNormal="#2196F3"
            app:colorPressed="#1E88E5"
            app:icon="@drawable/ic_favorite"
            />

        <com.imangazaliev.circlemenu.MenuButton
            android:id="@+id/search"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:colorNormal="#4CAF50"
            app:colorPressed="#43A047"
            app:icon="@drawable/ic_search"/>

        <com.imangazaliev.circlemenu.MenuButton
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

## License

The MIT License

Copyright (c) 2016 Mahach Imangazaliev 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
