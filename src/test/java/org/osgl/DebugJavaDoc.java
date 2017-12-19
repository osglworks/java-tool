package org.osgl;

public class DebugJavaDoc {
    public static void main(String[] args) {
        String[] sa = {"@options", "@packages"};
        com.sun.tools.javadoc.Main.execute(sa);
    }
}