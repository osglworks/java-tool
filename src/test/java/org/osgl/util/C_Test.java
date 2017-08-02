package org.osgl.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class C_Test extends UtilTestBase {

    private static class Foo {
        int id;
        String name;

        public Foo(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Foo foo = (Foo) o;

            if (id != foo.id) return false;
            return name != null ? name.equals(foo.name) : foo.name == null;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }

    private static class Bar {
        int id;
        String name;
        Foo foo;

        public Bar(int id, String name, int fooId, String fooName) {
            this.id = id;
            this.name = name;
            this.foo = new Foo(fooId, fooName);
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Foo getFoo() {
            return foo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Bar bar = (Bar) o;

            if (id != bar.id) return false;
            if (!name.equals(bar.name)) return false;
            return foo.equals(bar.foo);
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + name.hashCode();
            result = 31 * result + foo.hashCode();
            return result;
        }
    }

    static Foo foo(int id, String name) {
        return new Foo(id, name);
    }

    static Bar bar(int id, String name) {
        return new Bar(id * 2, S.times(name, 2), id, name);
    }

    List<Foo> fooList = C.list(foo(0, "Zero"), foo(1, "One"));
    List<Bar> barList = C.list(bar(0, "Zero"), bar(1, "One"));

    @Test
    public void testExtract() {
        List<Foo> fooExtracted = C.extract(barList, "foo");
        eq(fooList, fooExtracted);

        List<Integer> fooIdList = C.extract(barList, "foo.id");
        eq(C.list(0, 1), fooIdList);
    }

    @Test
    public void testWrapJdkMap() {
        Map<String, Integer> jdkMap = new HashMap<>();
        jdkMap.put("abc", 3);
        jdkMap.put("ab", 2);
        C.Map<String, Integer> osglMap = C.map(jdkMap);
        eq(3, osglMap.get("abc"));
    }

}
