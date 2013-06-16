package com.greenlaw1110.util;

import com.greenlaw110.TestBase;
import com.greenlaw110.util.C;
import com.greenlaw110.util.F;
import org.junit.Test;

import java.util.List;

/**
 * Test Collection Utils
 */
public class CTest extends TestBase{
    
    @Test
    public void testHead() {
        List<Integer> l = C.list(1, 2, 3, 4, 5);
        l = C.head(l, 3);
        eq(C.list(1, 2, 3), l);
        
        l = C.head(l, -2);
        eq(C.list(3), l);
    }
    
    @Test
    public void testTail() {
        List<Integer> l = C.list(1, 2, 3, 4, 5, 6);
        l = C.tail(l, 3);
        eq(C.list(4, 5, 6), l);
        
        l = C.tail(l, -2);
        eq(C.list(4), l);
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testList() {
        F.List<Integer> l = C.list(1, 2, 3, 4, 5);
        yes(l.readonly());
        l.remove(2);
    }
    
    @Test
    public void testNewList() {
        F.List<Integer> l = C.newList(1, 2, 3, 4, 5);
        no(l.readonly());
        l.remove(2);
        eq(l.size(), 4);
        eq(l.get(2), 4);
    }
    
    @Test
    public void testCompact() {
        F.List<String> l = C.list("A", null, "C", "D");
        eq(l.size(), 4);
        eq(l.compact().size(), 3);
    }

    public static void main(String[] args) {
        run(CTest.class);
    }
}
