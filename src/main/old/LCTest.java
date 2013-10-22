package org.osgl.util;

import org.osgl.TestBase;
import org.junit.Test;
import org.osgl._;

import java.util.List;


/**
 * Test {@link ListComprehension}
 */
public class LCTest extends TestBase {
    
    @Test
    public void testMap() {
        int[] a1 = {1, 5, 8};
        int[] a2 = {1 * 2, 5 * 2, 8 * 2};
        List<Integer> l2 = C0.list(a2);
        List<Integer> l = C0.lc(a1).map(Integer.class, N.f.dbl()).asList();
        eq(l, l2); 
    }
    
    @Test
    public void testChainedMaps() {
        int[] a1 = {0, 1, 20};
        String s1 = "1,2,3";
        String s = S.join(",", C0.lc(a1).map(N.f.mul(10), X.f.toStr(), S.f.size()).asList());
        eq(s1, s);
    }
    
    @Test
    public void testAll() {
        int[] a1 = {0, 1, 2};
        yes(C0.lc(a1).all(X.f.gt(-1)));
        no(C0.lc(a1).all(X.f.gt(0)));
    }
    
    @Test
    public void testAny() {
        int[] a1 = {0, 1, 2};
        yes(C0.lc(a1).any(X.f.gt(0)));
        yes(C0.lc(a1).any(X.f.gt(0)));
        no(C0.lc(a1).any(X.f.lt(0)));
    }
    
    @Test
    public void testDigest() {
        int[] a1 = {0, 1, 2};
        
        int[] a2 = {1, 2};
        C0.List<Integer> l2 = C0.list(a2);

        eq(l2, C0.lc(a1).filter(X.f.gt(0)).asList());
        
        C0.List<String> l = C0.list("Aristotle", "Plato", "Socrates", "Pythagoras");
        C0.List<String> l0 = C0.list("Plato", "Pythagoras");
        eq(l0, l.filter(S.f.startsWith("P")));
    }
    
    @Test
    public void testReduce() {
        int[] a1 = {0, 1, 2};
        eq(3, C0.lc(a1).reduce(0, N.f.aggregate(Integer.class)));
    }
    
    private static int _aggregate(List<Integer> list) {
        int sum = 0;
        for (int i : list) {
            sum += i;
        }
        return sum;
    }
    
    @Test
    public void testSpeed() {
        final Range<Integer> r = Range.valueOf(0, 100);
        final List l = C0.list(r);
        final ListComprehension<Integer> lc = C0.lc(r);
        int sum = lc.reduce(0, N.f.aggregate(Integer.class));
        int sum0 = _aggregate(l);
        //C1.list(sum, sum0).println();
        eq(sum, sum0);

        long ts = System.currentTimeMillis();
        int times = 10000;
        for (int i = 0; i < times; ++i) {
            _aggregate(l);
        }
//        _.times(new F.F0<Integer>() {
//            @Override
//            public Integer apply() {
//                return _aggregate(l);  //To change body of implemented methods use File | Settings | File Templates.
//            }
//        }, times);
        
        long t0 = System.currentTimeMillis() - ts;
        ts = System.currentTimeMillis();
        X.times(new _.F0<Integer>() {
            @Override
            public Integer apply() {
                return lc.reduce(N.f.aggregate(Integer.class));
            }
        }, times);
        long t1 = System.currentTimeMillis() - ts;
        
//        ts = System.currentTimeMillis();
//        _.times(new F.F0<Integer>(){
//            @Override
//            public Integer apply() {
//                return lc.reduce(N.f.INT_AGGREGATE);
//            }
//        }, times);
//        long t2 = System.currentTimeMillis() - ts;
//        
        println("t0: %s; t1: %s", t0, t1);
    }
    
    @Test
    public void testFirst() {
        int[] a1 = {0, 1, 2};
        eq(1, C0.lc(a1).first(X.f.gt(0)));
    }

    @Test
    public void testAndOr() {
        C0.List<Integer> c1 = C0.list(1, 2, 3);
        eq(true, c1.lc().and(X.f.gt(0)));
        eq(true, c1.lc().or(X.f.gt(0)));
        
        eq(false, c1.lc().and(X.f.gt(1)));
        eq(true, c1.lc().or(X.f.gt(1)));

        eq(false, c1.lc().and(X.f.gt(4)));
        eq(false, c1.lc().or(X.f.gt(4)));
    }
    

    @Test
    public void testAllAny() {
        C0.List<Integer> c1 = C0.list(1, 2, 3);
        eq(true, c1.lc().all(X.f.gt(0)));
        eq(true, c1.lc().any(X.f.gt(0)));

        eq(false, c1.lc().all(X.f.gt(2)));
        eq(true, c1.lc().any(X.f.gt(2)));

        eq(false, c1.lc().all(X.f.gt(4)));
        eq(false, c1.lc().any(X.f.gt(4)));
    }
    
    @Test
    public void testFoo() {
        final ListComprehension<Integer> lc = C0.lc(Range.valueOf(1, 10001));
        eq(true, lc.or(X.f.gt(9999)));
        eq(true, lc.any(X.f.gt(9999)));
        
        long ts = X.ts();
        X.times(new _.F0() {
            @Override
            public Object apply() {
                return lc.or(X.f.gt(1));
            }
        }, 10000);
        long t1 = X.ts() - ts;
        ts = X.ts();
        
        X.times(new _.F0() {
            @Override
            public Object apply() {
                return lc.any(X.f.gt(1));
            }
        }, 10000);
        long t2 = X.ts() - ts;
        
        println("t1: %s, t2: %s", t1, t2);
    }

    public static void main(String[] args) {
        run(LCTest.class);
    }
}
