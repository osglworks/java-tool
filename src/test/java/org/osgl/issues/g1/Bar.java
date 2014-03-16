package org.osgl.issues.g1;

import org.osgl.util.S;

/**
 * Created by luog on 17/03/14.
 */
public class Bar extends FooBase {
    String id;
    public Bar() {
        super("bar-" + S.random(3));
        id = S.random(10);
    }
}
