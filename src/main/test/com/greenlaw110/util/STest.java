package com.greenlaw110.util;

import com.greenlaw110.TestBase;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 17/06/13
 * Time: 5:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class STest extends TestBase {
    @Test
    public void testAfter() {
        String s = "image/png";
        eq(S.after(s, "/"), "png");
        
        s = "morphia.storage.service";
        eq(S.after(s, "morphia."), "storage.service");
    }
    
    @Test
    public void testBefore() {
        String s = "image/png";
        eq(S.before(s, "/"), "image");
    }
    
    @Test
    public void testBuilder() {
        StringBuilder sb = S.builder();
        assertNotNull(sb);
        yes(sb.length() == 0);
        
        sb = S.builder("abc", 5, false);
        assertNotNull(sb);
        eq(sb.toString(), "abc5false");
    }
    
    @Test
    public void testFirstLast() {
        String s = "abc123";
        eq(S.first(s, 3), "abc");
        eq(S.last(s, 3), "123");
    }

    public static void main(String[] args) {
        run(STest.class);
    }
}
