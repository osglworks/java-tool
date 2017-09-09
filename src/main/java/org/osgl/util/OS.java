package org.osgl.util;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2017 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
