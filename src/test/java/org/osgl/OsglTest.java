package org.osgl;

import org.junit.Test;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.C;
import org.osgl.util.N;
import org.osgl.util.S;

import java.io.Serializable;

/**
 * Created by luog on 4/04/14.
 */
public class OsglTest extends TestBase {

    @Test
    public void testRandom() {
        C.Range<Integer> r = C.range(10, 100);
        for (int i = 0; i < 100; ++i) {
            int n = $.random(r);
            yes(n >= 10);
            yes(n < 100);
        }
    }

    @Test
    public void testPredicateOr() {
        C.List<String> l = C.list("a.xml", "b.html", "c.txt", "d.txt");
        l = l.filter(S.F.endsWith(".xml").or(S.F.endsWith(".html")));
        yes(l.contains("a.xml"));
        yes(l.contains("b.html"));
        no(l.contains("c.txt"));
        no(l.contains("d.txt"));
    }

    public class Bar {
        String s;
        private boolean b;
        Bar(String s, boolean b) {
            this.s = s;
            this.b = b;
        }
        public String s() {
            return s;
        }
        public boolean isB() {
            return b;
        }
    }

    public class Foo {
        private String s;
        private int i;
        private Bar bar;
        Foo(String s1, int i, String s2, boolean b) {
            s = s1;
            this.i = i;
            bar = new Bar(s2, b);
        }
        public String getS() {
            return s;
        }
    }

    @Test
    public void testEval() {
        String s1 = S.random();
        String s2 = S.random();
        int i = N.randInt();
        boolean b = false;
        Foo foo = new Foo(s1, i, s2, b);
        eq(s1, $.getProperty(foo, "s"));
        eq(s2, $.getProperty(foo, "bar.s"));
        eq(i, $.getProperty(foo, "i"));
        eq(false, $.getProperty(foo, "bar/b"));
    }

    @Test
    public void testEvalWithCache() {
        final C.Map<String, Serializable> map = C.newMap();
        Osgl.F1<String, Serializable> getter = new Osgl.F1<String, Serializable>() {
            @Override
            public Serializable apply(String s) throws NotAppliedException, Osgl.Break {
                return map.get(s);
            }
        };
        Osgl.F2<String, Serializable, Object> setter = new Osgl.F2<String, Serializable, Object>() {
            @Override
            public Object apply(String s, Serializable serializable) throws NotAppliedException, Osgl.Break {
                map.put(s, serializable);
                return null;
            }
        };
        Osgl.T2<? extends Osgl.Function<String, Serializable>, ? extends Osgl.Func2<String, Serializable, ?>> cache = Osgl.T2(getter, setter);

        String s1 = S.random();
        String s2 = S.random();
        int i = N.randInt();
        boolean b = false;
        Foo foo = new Foo(s1, i, s2, b);

        eq(s1, $.getProperty(cache, foo, "s"));
        eq(s2, $.getProperty(cache, foo, "bar.s"));
        eq(i, $.getProperty(cache, foo, "i"));
        eq(false, $.getProperty(cache, foo, "bar/b"));

        eq(s1, $.getProperty(cache, foo, "s"));
        eq(s2, $.getProperty(cache, foo, "bar.s"));
        eq(i, $.getProperty(cache, foo, "i"));
        eq(false, $.getProperty(cache, foo, "bar/b"));
    }

}
