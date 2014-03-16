package org.osgl.issues.g1;

/**
 * Created by luog on 17/03/14.
 */
public class FooBase implements Foo {

    private String nm;

    FooBase(String name) {
        nm = name;
    }

    @Override
    public String name() {
        return nm;
    }
}
