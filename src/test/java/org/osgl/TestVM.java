package org.osgl;

import org.osgl.util.VM;

public class TestVM {
    public static void main(String[] args) {
        System.out.println(VM.INFO);
        System.out.println(VM.IS_64);
        System.out.println(VM.SPEC_VERSION);
        System.out.println($.JAVA_VERSION);
        System.out.println($.IS_SERVER);
    }
}
