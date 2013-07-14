package org.osgl.util;

import com.osgl.TestBase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 17/06/13
 * Time: 6:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class SetTest extends TestBase {

    protected C.Set<Integer> ro;
    protected C.Set<Integer> rw;
    
    @Before
    public void setUp() {
        ro = C.set(5, 4, 3, 2, 1, 1);
        yes(ro.ro());
        eq(ro.size(), 5);
        rw = C.newSet(5, 4, 3, 2, 1, 2);
        no(rw.ro());
        eq(rw.size(), 5);
    }
    
    @Test
    public void testKeepReadOnly() {
        C.Set<Integer> s = ro.map(N.f.dbl());
        yes(s.readOnly());

        s = rw.map(N.f.dbl());
        no(s.readOnly());
    }
    
    @Test
    public void testWithout() {
        C.Set<Integer> l = ro.without(7, 6, 3);
        eq(l.size(), 4);
        no(l.contains(3));
    }
    
    @Test
    public void testIntersect() {
        C.Set<Integer> l = ro.intersect(7, 6, 3);
        eq(l.size(), 1);
        yes(l.contains(3));
    }
    
    @Test
    public void testMap() {
        C.Set<String> sl = C.set("chengdu,sydney".split(","));
        sl = sl.map(S.f.TO_UPPERCASE);
        yes(sl.contains("CHENGDU") && sl.contains("SYDNEY"));
    }
    
    @Test
    public void testReduce() {
        eq(ro.reduce(N.f.aggregate(Integer.class)), 15);
        eq(ro.reduce(10, N.f.aggregate(Integer.class)), 25);
    }
    
    public static void main(String[] args) {
        run(SetTest.class);
    }
    
}
