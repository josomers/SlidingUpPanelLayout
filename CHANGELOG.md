Change Log
==========

Version 1.1 *(2013-10-24)*
----------------------------

 * Added Maven support with a sample and library module which makes it easy to test
 * Support for IntelliJ (Android Studio) & Eclipse
 * Refactored from `com.sothree.slidinguppaneldemo.SlidingUpPanelLayout` to a more generic name: `com.slidinguppanel.SlidingUpPanelLayout`. Inspired on the view naming conventions of `viewpagerindicator` for example.
 * Added support for a SlidingUpPanelLayout with a custom content height: `setContentHeight(int contentHeight)`
 * Added support for a SlidingUpPanellayout with a max content height, with a full height ratio (1.0f), or just a part of the screen: `setMaxContentHeightRatio(float ratio)`. Default set to a ratio of 2f/3.
 * Renamed `setPanelHeight(int height)` to `setDraggerHeight(int height)` as we have a panel with a dragger view
 * Renamed `setDragView(View dragView)` to `setDraggerView(View draggerView, int height)` where height is the height of the dragger view.
 * Simplified `collapse()` & `expand()` methods
 * Removed the SimplePanelSlideListener as it is not usefull
 * Set the DEFAULT_DRAGGER_HEIGHT to 40 (smaller than 68 as it was to high)
 * Renewed the drawScrim drawing as it was not always correct after making the view content dynamic, working with the setAlpha method on a Paint now which is much easier than swapping bits and bites around. DEFAULT_FADE_COLOR set to Color.BLACK (transparency not needed anymore)
* Enable/Disable tapping on the dragger layout (which makes it able to set buttons in it) (`setInterceptingPanelEvents(boolean interceptTap)`)
 * Added Robolectric and some very basic tests
 * Updated README.md with some new screenshots
 * Added travis build config

Version 1.0 *(2013-05-31)*
----------------------------

* Initial release.