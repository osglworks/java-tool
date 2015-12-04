package org.osgl;

import org.junit.Test;
import org.osgl.util.C;
import org.osgl.util.N;
import org.osgl.util.S;

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
        eq(s1, $.eval(foo, "s"));
        eq(s2, $.eval(foo, "bar.s"));
        eq(i, $.eval(foo, "i"));
        eq(false, $.eval(foo, "bar/b"));
    }

}
