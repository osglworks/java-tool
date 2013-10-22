package org.osgl.util;

import org.osgl.TestBase;
import org.junit.Test;

import java.util.List;

/**
 * Test Collection Utils
 */
public class CTest extends TestBase {
    
    @Test
    public void testHead() {
        List<Integer> l = C0.list(1, 2, 3, 4, 5);
        l = C0.head(l, 3);
        eq(C0.list(1, 2, 3), l);
        
        l = C0.head(l, -2);
        eq(C0.list(3), l);
    }
    
    @Test
    public void testTail() {
        List<Integer> l = C0.list(1, 2, 3, 4, 5, 6);
        l = C0.tail(l, 3);
        eq(C0.list(4, 5, 6), l);
        
        l = C0.tail(l, -2);
        eq(C0.list(4), l);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testList() {
        C0.List<Integer> l = C0.list(1, 2, 3, 4, 5);
        yes(l.readOnly());
        l.remove(2);
    }
    
    @Test
    public void testNewList() {
        C0.List<Integer> l = C0.newList(1, 2, 3, 4, 5);
        no(l.readOnly());
        l.remove(2);
        eq(l.size(), 4);
        eq(l.get(2), 4);
    }
    
    @Test
    public void testCompact() {
<<<<<<< HEAD
        C0.List<String> l = C0.list("A", null, "C1", "D");
=======
        C0.List<String> l = C0.list("A", null, "C", "D");
>>>>>>> 34987d8de8a48f906bf7a9033b2e3546226e12e0
        eq(l.size(), 4);
        eq(l.compact().size(), 3);
    }

    public static void main(String[] args) {
        run(CTest.class);
    }
}
