package org.osgl.issues;

import org.junit.Test;
import org.osgl.$;
import org.osgl.TestBase;

/**
 * Test https://github.com/osglworks/java-tool/issues/19
 */
public class Gh19 extends TestBase {

    static class Bar {
        int n;
        Bar (int n) {
            this.n = n;
        }
    }

    static class Foo {
        Bar[] bars;

        Foo(Bar ... bars) {
            this.bars = bars;
        }
    }

    @Test
    public void test() {
        Foo foo = new Foo(new Bar(1), new Bar(2));
        eq(2, $.getProperty(foo, "bars.1.n"));
    }

}
