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
    WINDOWS,
    MAC_OS() {
        @Override
        public String toString() {
            return "macOS";
        }
    },
    LINUX,
    OS_2() {
        @Override
        public String toString() {
            return "OS/2";
        }
    },
    HP_UX() {
        @Override
        public String toString() {
            return "HP-UX";
        }
    },
    AIX() {
        @Override
        public String toString() {
            return "AIX";
        }
    },
    IRIX() {
        @Override
        public String toString() {
            return "IRIX";
        }
    },
    SOLARIS,
    SUN_OS() {
        @Override
        public String toString() {
            return "SunOS";
        }
    },
    MPE_IX() {
        @Override
        public String toString() {
            return "MPE/iX";
        }
    },
    OS_390() {
        @Override
        public String toString() {
            return "OS/390";
        }
    },
    FREEBSD() {
        @Override
        public String toString() {
            return "FreeBSD";
        }
    },
    DIGITAL_UNIX() {
        @Override
        public String toString() {
            return "Digital UNIX";
        }
    },
    OSF_1() {
        @Override
        public String toString() {
            return "OSF/1";
        }
    },
    UNKNOWN;

    private static OS os = null; static {
        String s = Keyword.of(System.getProperty("os.name")).snakeCase().toUpperCase();
        for (OS x: OS.values()) {
            if (s.startsWith(x.name())) {
                os = x;
                break;
            }
        }
        if (null == os) {
            os = UNKNOWN;
        }
    }

    private String lineSeparator = System.getProperty("line.separator");
    private String pathSeparator = System.getProperty("path.separator");
    private String fileSeparator = System.getProperty("file.separator");

    public boolean isWindows() {
        return WINDOWS == this;
    }
    public boolean isMacOsX() {
        return MAC_OS == this;
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

    public String toString() {
        return Keyword.of(name()).readable();
    }

    public static OS get() {
        return os;
    }
}
