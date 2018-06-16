package org.osgl.util;

import org.junit.Test;
import org.osgl.TestBase;

public class StringTokenSetTest extends TestBase {

    @Test
    public void testEmptySet() {
        StringTokenSet set = new StringTokenSet();
        yes(set.isEmpty());
        eq(S.EMPTY_ARRAY, set.toArray());
    }

    @Test
    public void testSingleElementSet() {
        StringTokenSet set = new StringTokenSet("abc");
        no(set.isEmpty());
        eq(1, set.size());
        yes(set.contains("abc"));
        yes(set.containsAll(C.set("abc")));
        no(set.containsAll(C.set("abc", "xyz")));
        eq(new String[]{"abc"}, set.toArray());
    }

    @Test
    public void testMultipleElementSet() {
        StringTokenSet set = new StringTokenSet("abc,xyz");
        no(set.isEmpty());
        eq(2, set.size());
        yes(set.contains("abc"));
        yes(set.containsAll(C.set("abc")));
        yes(set.containsAll(C.set("abc", "xyz")));
        eq(new String[]{"abc", "xyz"}, set.toArray());
    }

    @Test
    public void testAdd() {
        StringTokenSet set = new StringTokenSet();
        set.add("abc");
        eq(1, set.size());
        eq(new String[]{"abc"}, set.toArray());
        set.add("xyz");
        eq(2, set.size());
        eq(new String[]{"abc", "xyz"}, set.toArray());
    }

    @Test
    public void testRemove() {
        StringTokenSet set = new StringTokenSet("abc,xyz,mmm");
        no(set.remove(new Object()));
        yes(set.remove("xyz"));
        eq(2, set.size());
        eq(new String[]{"abc","mmm"}, set.toArray(new String[2]));
        yes(set.remove("abc"));
        eq(1, set.size());
        eq(new String[]{"mmm", null}, set.toArray(new String[2]));
        yes(set.remove("mmm"));
        yes(set.isEmpty());
        eq(S.EMPTY_ARRAY, set.toArray());
    }

    @Test
    public void testHashCodeAndEquality() {
        StringTokenSet set1 = new StringTokenSet("abc,xyz,mmm");
        StringTokenSet set2 = new StringTokenSet("xyz,abc,mmm");
        eq(set1, set2);
        eq(set1.hashCode(), set2.hashCode());
        StringTokenSet set3 = new StringTokenSet("xyz,abc,mmm3");
        ne(set1, set3);
    }

}
