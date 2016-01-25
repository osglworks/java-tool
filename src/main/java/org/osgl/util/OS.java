package org.osgl.util;

/**
 * Operating system enum
 */
public enum OS {
    WINDOWS, MAC_OS_X, LINUX, OS2, HP_UX, AIX, IRIX, SOLARIS, SUN_OS, MPE_IX, OS_390, FREEBSD, DIGITAL_UNIX, OSF1, UNKNOWN;

    private static OS os = null; static {
        String s = System.getProperty("os.name").toUpperCase();
        for (OS x: OS.values()) {
            if (s.startsWith(x.name())) {
                os = x;
                break;
            }
        }
        if (null == os) {
            if (s.startsWith("OS/2")) {
                os = OS2;
            } else if (s.startsWith("OS/390")) {
                os = OS_390;
            } else if (s.startsWith("DIGITAL UNIX")) {
                os = DIGITAL_UNIX;
            } else {
                os = UNKNOWN;
            }
        }
    }

    private String lineSeparator = System.getProperty("line.separator");
    private String pathSeparator = System.getProperty("path.separator");
    private String fileSeparator = System.getProperty("file.separator");

    public boolean isWindows() {
        return WINDOWS == this;
    }
    public boolean isMacOsX() {
        return MAC_OS_X == this;
    }
    public boolean isLinux() {
        return LINUX == this;
    }
    public boolean isUnix() {
        return UNKNOWN != this && WINDOWS != this;
    }

    public String lineSeparator() {
        return lineSeparator;
    }

    public String pathSeparator() {
        return pathSeparator;
    }

    public String fileSeparator() {
        return fileSeparator;
    }

    public static OS get() {
        return os;
    }
}
