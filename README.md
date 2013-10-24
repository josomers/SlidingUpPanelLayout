Android Sliding Up Panel
=========================


![SlidingUpPanelLayout](https://raw.github.com/josomers/SlidingUpPanelLayout/master/slidinguppanel.png)

Usage
-----------
To use the layout, simply include `com.slidinguppanel.SlidingUpPanelLayout` as the root element in your activity Layout. Make sure that it has two children. The first child is your main layout. The second child is your layout for the sliding up panel. Both children should have width and height set to `match_parent`. For more information, please refer to the sample code.

```xml
<com.slidinguppanel.SlidingUpPanelLayout
    android:id="@+id/sliding_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent" >

    <TextView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center"
        android:text="Main Content"
        android:textSize="16sp" />

    <TextView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center|top"
        android:text="The Awesome Sliding Up Panel"
        android:textSize="16sp" />
</com.slidinguppanel.SlidingUpPanelLayout>
```
For smooth interaction with the ActionBar, make sure that `windowActionBarOverlay` is set to `true` in your styles:
```xml
<style name="AppTheme">
    <item name="android:windowActionBarOverlay">true</item>
</style>
```

Additional Features
-----------
* `setDraggerView(View draggerView, int height)`    : Restric the drag area of the sliding panel to a specific view and height. Otherwise, the whole panel will be slidable and it will intercept all clicks.
* `setDraggerHeight(int height)`                    : Change the dragger height
* `setShadowDrawable(Drawable drawable)`            : Change the dragger shadow. No shadow displayed by default.
* `setShadowDrawable(Drawable drawable, int height)`: Change the dragger shadow with a custom height. No shadow displayed by default.
* `setContentHeight(int contentHeight)`             : Change the panel's content height to a custom one
* `setMaxContentHeightRatio(float ratio)`           : When using a custom height you can define the max height ratio of the view. 2f/3 by default, use 1.0f for full screen height.
* `collapse()` or `expand()`                        : Expand/Collapse the sliding panel
* `setInterceptingPanelEvents(boolean interceptTap)`: When using a custom dragger view, it might be usefull to make some views in the dragger clickable.

Requirements
-----------
* Install the support-v4-r13.jar to your own artifact repo as it is not available in the Maven repo's.
* Tested on Android 2.2+

Licence
-----------
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this work except in compliance with the License.
You may obtain a copy of the License in the LICENSE file, or at:

  [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Credits
-----------
* All credits goes to the [Umano Team](http://umanoapp.com): for the idea and the basic, but awesome implementation of this layout. You can see the original here: [AndroidSlidingUpPanel](https://github.com/umano/AndroidSlidingUpPanel/)
* This code is heavily based on the opened-sourced [SlidingPaneLayout](http://developer.android.com/reference/android/support/v4/widget/SlidingPaneLayout.html) component from the r13 of the Android Support Library. Thanks Android team!

=======

[![Build Status](https://secure.travis-ci.org/josomers/AndroidSlidingUpPanel.png)](https://travis-ci.org/josomers/AndroidSlidingUpPanel)
