package com.slidinguppanel;

import com.slidinguppanel.demo.activities.MainActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertNotNull;

/**
 * User: josomers
 * Date: 11/07/13
 * Time: 12:26
 */

@RunWith(RobolectricTestRunner.class)
public class SampleTest {

    @Test
    public void testSample() {
        MainActivity mainActivity = Robolectric.buildActivity(MainActivity.class).create().get();
        assertNotNull(mainActivity); // not a real test, just added a default testcase.
    }

}
