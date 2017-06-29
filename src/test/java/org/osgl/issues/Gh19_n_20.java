package org.osgl.issues;

import org.junit.Before;
import org.junit.Test;
import org.osgl.$;
import org.osgl.TestBase;
import org.osgl.util.C;

import java.util.List;

/**
 * Test https://github.com/osglworks/java-tool/issues/19
 */
public class Gh19_n_20 extends TestBase {

    static class Bar {
        int n;
        Bar (int n) {
            this.n = n;
        }
    }

    static class Foo {
        Bar[] bars;
        List<Bar> barList;
        Foo(Bar ... bars) {
            this.bars = bars;
            this.barList = C.listOf(bars);
        }
    }

    protected Foo foo;

    @Before
    public void prepare() {
        foo = new Foo(new Bar(1), new Bar(2));
    }

    @Test
    public void test19() {
        eq(2, $.getProperty(foo, "bars.1.n"));
    }

    @Test
    public void test20() {
        eq(1, $.getProperty(foo, "barList.n"));
        eq(1, $.getProperty(foo, "bars.n"));
    }

}
