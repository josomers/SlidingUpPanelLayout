package com.slidinguppanel;

import android.content.Context;
import android.view.View;

import com.slidinguppanel.SlidingUpPanelLayout;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: josomers
 * Date: 22/07/13
 * Time: 16:48
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SlidingUpPanelLayoutTest {

    private Context mContext;

    @Before
    public void setUp() {
        mContext = Robolectric.getShadowApplication().getApplicationContext();
    }

    @Test
    public void testInitialConfiguration() {
        SlidingUpPanelLayout slidingUpPanelLayout = new SlidingUpPanelLayout(mContext);
        assertNotNull(slidingUpPanelLayout);
        assertFalse(slidingUpPanelLayout.isExpanded());
        assertTrue(slidingUpPanelLayout.isSlideable());
        assertTrue(slidingUpPanelLayout.getVisibility() == View.VISIBLE);
        assertTrue(slidingUpPanelLayout.getMaxContentHeightRatio() == 2 / 3f);
        assertTrue(slidingUpPanelLayout.getContentHeight() == Integer.MAX_VALUE);
        assertFalse(slidingUpPanelLayout.isInterceptingPanelEvents());
        assertTrue(slidingUpPanelLayout.getDraggerHeight() == 40);
        assertTrue(slidingUpPanelLayout.getTopDistance(0) == 0);
    }

    @Test
    public void testCustomDraggerConfiguration() {
        SlidingUpPanelLayout slidingUpPanelLayout = new SlidingUpPanelLayout(mContext);
        slidingUpPanelLayout.setDraggerView(new View(mContext), 300);
        assertEquals(slidingUpPanelLayout.getDraggerHeight(), 300);
    }

    /**
     * Currently it is not possible to pass a test for expanding the Sliding Up Panel.
     * The method smoothSlideViewTo is not calling the methods (onViewPositionChanged) needed in the DragHelperCallback
     * setting the mSlideOffset to the right value.
     */
    @Ignore
    @Test
    public void testExpand() {
    }

    /**
     * Currently it is not possible to pass a test for collapsing the Sliding Up Panel.
     * The method smoothSlideViewTo is not calling the methods (onViewPositionChanged) needed in the DragHelperCallback
     * setting the mSlideOffset to the right value.
     */
    @Ignore
    @Test
    public void testCollapse() {
    }

}
