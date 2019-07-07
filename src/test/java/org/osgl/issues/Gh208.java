package org.osgl.issues;

import org.junit.Test;
import org.osgl.$;
import org.osgl.TestBase;
import org.osgl.util.N;
import org.osgl.util.S;

public class Gh208 extends TestBase {
    public static class Foo {
        public String name;
        public int level;
        public boolean flag;
    }

    public static class Bar {
        public String name;
        public Foo foo;
    }

    @Test
    public void test() {
        Bar source = new Bar();
        source.name = S.random();
        Foo foo = new Foo();
        foo.name = S.random();
        foo.level = N.randInt();
        foo.flag = true;
        source.foo = foo;

        Bar target = $.deepCopy(source).filter("-foo,+foo.name").to(Bar.class);
        eq(source.name, target.name);
        Foo targetFoo = target.foo;
        notNull(targetFoo);
        eq(foo.name, targetFoo.name);
        eq(0, targetFoo.level);
        no(targetFoo.flag);
    }
}
