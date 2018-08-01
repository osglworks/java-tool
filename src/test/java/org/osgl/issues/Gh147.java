package org.osgl.issues;

import org.junit.Test;
import org.osgl.$;
import org.osgl.TestBase;

import java.util.HashMap;
import java.util.Map;

public class Gh147 extends TestBase {
    @Test
    public void test() {
        Map<String, Object> src = new HashMap<>();
        src.put("a", 1);
        Map tgt = $.map(src).map("a").to("b").to(Map.class);
        eq(1, tgt.get("b"));
    }
}
