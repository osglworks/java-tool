package org.osgl.issues.g1;

import org.osgl.util.S;

import java.util.Random;

/**
 * Created by luog on 17/03/14.
 */
public class Zee extends FooBase {
    int i;
    public Zee() {
        super("zee-" + S.random(5));
        i = new Random().nextInt(10000);
    }
}
