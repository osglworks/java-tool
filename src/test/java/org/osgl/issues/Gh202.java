package org.osgl.issues;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.osgl.$;
import org.osgl.TestBase;
import org.osgl.util.C;
import org.osgl.util.S;

import java.util.Map;

public class Gh202 extends TestBase {

    public static class Foo {
        public String id;
    }

    public static class Bar {
        public String id;
        public Foo foo;
    }

    public static class Bean {
        public Map<String, Object> map;
        public String name;
        public Bar bar;
    }

    @Test
    public void test2() {
        Foo foo = new Foo();
        foo.id = "foo";

        Bar bar = new Bar();
        bar.id = "bar";
        bar.foo = foo;

        Bean bean = new Bean();
        bean.name = "bean";
        bean.map = C.Map("foo", bar);
        bean.bar = bar;

        Bean target = $.deepCopy(bean).filter("bar").to(Bean.class);
        isNull(target.name);
        isNull(target.map);

        Bar tgtBar = target.bar;
        notNull(tgtBar);
        eq("bar", tgtBar.id);

        Foo tgtFoo = tgtBar.foo;
        notNull(tgtFoo);
        eq("foo", tgtFoo.id);
    }

    @Test
    public void test() {
        Bean bean = new Bean();
        Map<String, Object> innerMap = C.Map("count", 1);
        bean.map = C.Map("foo", "bar", "inner", innerMap);
        bean.name = S.random();

        Bean target = $.deepCopy(bean).filter("map").to(Bean.class);
        Map<String, Object> innerMap2 = $.cast(target.map.get("inner"));
        notNull(innerMap2);
        eq(1, innerMap2.get("count"));
    }

}
