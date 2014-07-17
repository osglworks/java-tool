package org.osgl;

import org.junit.Test;
import org.osgl.util.C;
import org.osgl.util.S;

/**
 * Created by luog on 4/04/14.
 */
public class OsglTest extends TestBase {

    @Test
    public void testRandom() {
        C.Range<Integer> r = C.range(10, 100);
        for (int i = 0; i < 100; ++i) {
            int n = _.random(r);
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

}
