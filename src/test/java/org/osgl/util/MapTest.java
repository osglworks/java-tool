package org.osgl.util;

import org.junit.Test;
import org.osgl.TestBase;

import java.io.*;
import java.util.Map;

public class MapTest extends TestBase {

    @Test
    public void testSerialize() throws Exception {
        Map map = C.map("foo", 1, "bar", 0);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(map);

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        Map map2 = (Map)ois.readObject();
        eq(map, map2);
    }
}
