package com.greenlaw110.sample;

import com.greenlaw110.util.C;
import com.greenlaw110.util.IO;
import com.greenlaw110.util._;

import java.util.List;

public class MappingSample {
    public static void main(String[] args) {
        List<Integer> l = C.list(1, 2, 3);
        C.lc(l).map(_.f.dbl(), IO.f.PRINTLN).walkthrough();
    }
}
