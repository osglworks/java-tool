package com.osgl.sample;

import org.osgl.util.C0;
import org.osgl.util.IO;
import org.osgl.util.N;

import java.util.List;

public class MappingSample {
    public static void main(String[] args) {
        List<Integer> l = C0.list(1, 2, 3);
        C0.lc(l).map(N.f.dbl(), IO.f.PRINTLN).walkthrough();
    }
}
