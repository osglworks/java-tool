/* 
 * Copyright (C) 2013 The Java Tool project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.osgl.util;

import org.osgl.exception.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Utility class to throw common exceptions
 */
public class E {

    /**
     * Throw out {@link IllegalStateException}
     */
    public static InvalidStateException invalidState() {
        throw new InvalidStateException();
    }

    /**
     * throw out {@link IllegalStateException}
     * @param msg message template
     * @param args message arguments
     */
    public static InvalidStateException invalidState(String msg, Object... args) {
        throw new InvalidStateException(S.fmt(msg, args));
    }
    
    public static void invalidStateIf(boolean tester) {
        if (tester) {
            invalidState();
        }
    }

    public static void invalidStateIf(boolean tester, String msg, Object... args) {
        if (tester) {
            invalidState(msg, args);
        }
    }

    public static void NPE(Object o1) {
        if (null == o1) {
            throw new NullPointerException();
        }
    }

    public static void NPE(Object o1, Object o2) {
        if (null == o1 || null == o2) {
            throw new NullPointerException();
        }
    }

    public static void NPE(Object o1, Object o2, Object o3) {
        if (null == o1 || null == o2 || null == o3) {
            throw new NullPointerException();
        }
    }

    /**
     * Throw out NullPointerException if any one of the passed objects is null
     * 
     * @param objects the object instances to be tested
     */
    public static void NPE(Object o1, Object o2, Object o3, Object... objects) {
        NPE(o1, o2, o3);
        for (Object o : objects) {
            if (null == o) {
                throw new NullPointerException();
            }
        }
    }

    public static void NPE(boolean tester) {
        if (tester) {
            throw new NullPointerException();
        }
    }

    /**
     * Throw out {@link org.osgl.exception.UnexpectedException}
     * 
     * @param msg the message template
     * @param args the message arguments
     */
    public static UnexpectedException unexpected(String msg, Object... args) {
        throw new UnexpectedException(msg, args);
    }
    
    /**
     * Throw out {@link org.osgl.exception.UnexpectedException}
     * 
     * @param cause 
     */
    public static UnexpectedException unexpected(Throwable cause) {
        throw new UnexpectedException(cause);
    }

    /**
     * Throw out {@link org.osgl.exception.UnexpectedException}
     * 
     * @param cause 
     * @param msg the message template
     * @param args the message arguments
     */
    public static UnexpectedException unexpected(Throwable cause, String msg, Object... args) {
        throw new UnexpectedException(cause, msg, args);
    }

    public static void unexpectedIf(boolean tester, String msg, Object... args) {
        if (tester) {
            unexpected(msg, args);
        }
    }


    /**
     * Throw out {@link org.osgl.exception.UnexpectedIOException}
     * 
     * @param cause the IOException caused this unexpected IO issue
     */
    public static UnexpectedIOException ioException(IOException cause) {
        throw new UnexpectedIOException(cause);
    }

    public static UnexpectedIOException ioException(String msg, Object... args) {
        throw new UnexpectedIOException(msg, args);
    }
    
    /**
     * Throw out {@link org.osgl.exception.UnexpectedEncodingException}
     * 
     * @param cause the UnsupportedEncodingException caused this unexpected encoding issue
     */
    public static UnexpectedEncodingException encodingException(UnsupportedEncodingException cause) {
        throw new UnexpectedEncodingException(cause);
    }

    public static ConfigurationException invalidConfiguration(Throwable cause, String message, Object... args) {
        throw new ConfigurationException(cause, message, args);
    }

    public static ConfigurationException invalidConfiguration(String message, Object... args) {
        throw new ConfigurationException(message, args);
    }

    public static void invalidConfigurationIf(boolean tester, String msg, Object... args) {
        if (tester) {
            invalidConfiguration(msg, args);
        }
    }
    
    public static UnsupportedException tbd() {
        throw new UnsupportedException("to be implemented");
    }
    
    public static UnsupportedException unsupport() {
        throw new UnsupportedException();
    }

    public static UnsupportedException unsupport(String msg, Object... args) {
        throw new UnsupportedException(msg, args);
    }
    
    public static InvalidArgException invalidArg() {
        throw new InvalidArgException();
    }

    public static void invalidArgIf(boolean test) {
        if (test) {
            throw new InvalidArgException();
        }
    }

    public static InvalidArgException invalidArg(String msg, Object... args) {
        throw new InvalidArgException(msg, args);
    }
    
    public static void invalidArgIf(boolean test, String msg, Object... args) {
        if (test) {
            throw new InvalidArgException(msg, args);
        }
    }

    public static InvalidRangeException invalidRange() {
        throw new InvalidRangeException();
    }

    public static void invalidRangeIf(boolean test) {
        if (test) {
            throw new InvalidRangeException();
        }
    }

    public static InvalidRangeException invalidRange(String msg, Object... args) {
        throw new InvalidRangeException(msg, args);
    }

    public static void invalidRangeIf(boolean test, String msg, Object... args) {
        if (test) {
            throw new InvalidRangeException(msg, args);
        }
    }

    public static void npeIf(boolean test) {
        if (test) {
            throw new NullPointerException();
        }
    }

    public static void unsupportedIf(boolean test) {
        if (test) {
            throw new UnsupportedOperationException();
        }
    }

    public static void illegalArgumentIf(boolean test) {
        if (test) {
            throw new IllegalArgumentException();
        }
    }

    public static void illegalStateIf(boolean test) {
        if (test) {
            throw new IllegalStateException();
        }
    }
    
}
