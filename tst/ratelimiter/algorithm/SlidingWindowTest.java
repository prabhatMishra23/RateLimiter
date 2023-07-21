package ratelimiter.algorithm;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SlidingWindowTest {

    public static SlidingWindow ratelimiter;

    @Before
    public void setUp(){
        ratelimiter = new SlidingWindow("Prabhat");
    }

    @Test
    public void testIsAllowedWhenRequestComesWithinRange() throws InterruptedException {
        for(int i=0; i<3; i++){
            Assert.assertTrue(ratelimiter.is_allowed("Prabhat"));
            Assert.assertTrue(ratelimiter.is_allowed("Prabhat"));
            Assert.assertTrue(ratelimiter.is_allowed("Prabhat"));
            Thread.sleep(1);
        }
    }

    @Test
    public void testIsAllowedWhenRequestComesOutsideRange() throws InterruptedException {
        for(int i=0; i<3; i++){
            Assert.assertTrue(ratelimiter.is_allowed("Prabhat"));
            Assert.assertTrue(ratelimiter.is_allowed("Prabhat"));
            Assert.assertTrue(ratelimiter.is_allowed("Prabhat"));
            Thread.sleep(1);
        }
        Assert.assertTrue(ratelimiter.is_allowed("Prabhat"));
        Assert.assertTrue(ratelimiter.is_allowed("Prabhat"));
        Assert.assertFalse(ratelimiter.is_allowed("Prabhat"));
        Thread.sleep(2);
        Assert.assertTrue(ratelimiter.is_allowed("Prabhat"));
    }


}