package org.osgl.issues.g1;

import org.junit.Test;
import org.osgl.TestBase;
import org.osgl.util.C;

import java.util.List;

/**
 * Created by luog on 17/03/14.
 */
public class TestIssue1 extends TestBase {
    @Test
    public void test() {
        List<Foo> foos = C.newList();
        foos.add(new Bar());
        foos.add(new Zee());

        C.List<Foo> l = C.list(foos);
        yes(l.size() == 2);
        yes(l.is(C.Feature.READONLY));
    }
}
