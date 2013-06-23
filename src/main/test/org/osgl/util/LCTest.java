package org.osgl.util;

import com.osgl.TestBase;
import org.junit.Test;

import java.util.List;


/**
 * Test {@link ListComprehension}
 */
public class LCTest extends TestBase {
    
    @Test
    public void testMap() {
        int[] a1 = {1, 5, 8};
        int[] a2 = {1 * 2, 5 * 2, 8 * 2};
        List<Integer> l2 = C.listp(a2);
        List<Integer> l = C.lc(a1).map(Integer.class, N.f.dbl()).asList();
        eq(l, l2); 
    }
    
    @Test
    public void testChainedMaps() {
        int[] a1 = {0, 1, 20};
        String s1 = "1,2,3";
        String s = S.join(",", C.lc(a1).map(N.f.mul(10), _.f.toStr(), S.f.size()).asList());
        eq(s1, s);
    }
    
    @Test
    public void testAll() {
        int[] a1 = {0, 1, 2};
        yes(C.lc(a1).all(_.f.gt(-1)));
        no(C.lc(a1).all(_.f.gt(0)));
    }
    
    @Test
    public void testAny() {
        int[] a1 = {0, 1, 2};
        yes(C.lc(a1).any(_.f.gt(0)));
        yes(C.lc(a1).any(_.f.gt(0)));
        no(C.lc(a1).any(_.f.lt(0)));
    }
    
    @Test
    public void testDigest() {
        int[] a1 = {0, 1, 2};
        
        int[] a2 = {1, 2};
        C.List<Integer> l2 = C.listp(a2);

        eq(l2, C.lc(a1).filter(_.f.gt(0)).asList());
        
        C.List<String> l = C.list("Aristotle", "Plato", "Socrates", "Pythagoras");
        C.List<String> l0 = C.list("Plato", "Pythagoras");
        eq(l0, l.filter(S.f.startsWith("P")));
    }
    
    @Test
    public void testReduce() {
        int[] a1 = {0, 1, 2};
        eq(3, C.lc(a1).reduce(0, N.f.aggregate(Integer.class)));
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
        final List l = C.list(r);
        final ListComprehension<Integer> lc = C.lc(r);
        int sum = lc.reduce(0, N.f.aggregate(Integer.class));
        int sum0 = _aggregate(l);
        //C.list(sum, sum0).println();
        eq(sum, sum0);

        long ts = System.currentTimeMillis();
        int times = 10000;
        for (int i = 0; i < times; ++i) {
            _aggregate(l);
        }
//        _.times(new F.F0<Integer>() {
//            @Override
//            public Integer run() {
//                return _aggregate(l);  //To change body of implemented methods use File | Settings | File Templates.
//            }
//        }, times);
        
        long t0 = System.currentTimeMillis() - ts;
        ts = System.currentTimeMillis();
        _.times(new F.F0<Integer>(){
            @Override
            public Integer run() {
                return lc.reduce(N.f.aggregate(Integer.class));
            }
        }, times);
        long t1 = System.currentTimeMillis() - ts;
        
//        ts = System.currentTimeMillis();
//        _.times(new F.F0<Integer>(){
//            @Override
//            public Integer run() {
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
        eq(1, C.lc(a1).first(_.f.gt(0)));
    }

    @Test
    public void testAndOr() {
        C.List<Integer> c1 = C.list(1, 2, 3);
        eq(true, c1.lc().and(_.f.gt(0)));
        eq(true, c1.lc().or(_.f.gt(0)));
        
        eq(false, c1.lc().and(_.f.gt(1)));
        eq(true, c1.lc().or(_.f.gt(1)));

        eq(false, c1.lc().and(_.f.gt(4)));
        eq(false, c1.lc().or(_.f.gt(4)));
    }
    

    @Test
    public void testAllAny() {
        C.List<Integer> c1 = C.list(1, 2, 3);
        eq(true, c1.lc().all(_.f.gt(0)));
        eq(true, c1.lc().any(_.f.gt(0)));

        eq(false, c1.lc().all(_.f.gt(2)));
        eq(true, c1.lc().any(_.f.gt(2)));

        eq(false, c1.lc().all(_.f.gt(4)));
        eq(false, c1.lc().any(_.f.gt(4)));
    }
    
    @Test
    public void testFoo() {
        final ListComprehension<Integer> lc = C.lc(Range.valueOf(1, 10001));
        eq(true, lc.or(_.f.gt(9999)));
        eq(true, lc.any(_.f.gt(9999)));
        
        long ts = _.ts();
        _.times(new F.F0(){
            @Override
            public Object run() {
                return lc.or(_.f.gt(1));
            }
        }, 10000);
        long t1 = _.ts() - ts;
        ts = _.ts();
        
        _.times(new F.F0(){
            @Override
            public Object run() {
                return lc.any(_.f.gt(1));
            }
        }, 10000);
        long t2 = _.ts() - ts;
        
        println("t1: %s, t2: %s", t1, t2);
    }

    public static void main(String[] args) {
        run(LCTest.class);
    }
}
