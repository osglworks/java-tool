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

import org.osgl.exception.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

/**
 * Utility class to throw common exceptions
 */
public class E {

    E() {}

    /**
     * Throws out an {@link InvalidStateException}.
     */
    public static InvalidStateException invalidState() {
        throw new InvalidStateException();
    }

    /**
     * Throws out an {@link InvalidStateException} with message specified.
     */
    public static InvalidStateException invalidState(String msg, Object... args) {
        throw new InvalidStateException(S.fmt(msg, args));
    }

    /**
     * Throws out an {@link InvalidStateException} when `tester` evaluated to `true`.
     *
     * @param tester
     *      when `true` then an {@link InvalidStateException} will be thrown out
     */
    public static void invalidStateIf(boolean tester) {
        if (tester) {
            invalidState();
        }
    }

    /**
     * Throws out an {@link InvalidStateException} when `tester` evaluated to `true`.
     *
     * @param tester
     *      when `true` then an {@link InvalidStateException} will be thrown out.
     * @param msg
     *      the message format template
     * @param args
     *      the message format arguments
     */
    public static void invalidStateIf(boolean tester, String msg, Object... args) {
        if (tester) {
            invalidState(msg, args);
        }
    }

    /**
     * Throws {@link NullPointerException} if `o1` is `null`.
     *
     * @param o1
     *      the object to be evaluated.
     */
    public static void NPE(Object o1) {
        if (null == o1) {
            throw new NullPointerException();
        }
    }

    /**
     * Throws out {@link NullPointerException} if `o1` or `o2` is `null`.
     * @param o1
     *      the first object to be evaluated
     * @param o2
     *      the second object to be evaluated
     */
    public static void NPE(Object o1, Object o2) {
        if (null == o1 || null == o2) {
            throw new NullPointerException();
        }
    }

    /**
     * Throws out {@link NullPointerException} if `o1` or `o2` or `o3` is `null`.
     * @param o1
     *      the first object to be evaluated
     * @param o2
     *      the second object to be evaluated
     * @param o3
     *      the third object to be evaluated
     */
    public static void NPE(Object o1, Object o2, Object o3) {
        if (null == o1 || null == o2 || null == o3) {
            throw new NullPointerException();
        }
    }

    /**
     * Throw out NullPointerException if any one of the passed objects is null.
     *
     * @param o1
     *      the first object to be evaluated
     * @param o2
     *      the second object to be evaluated
     * @param o3
     *      the third object to be evaluated
     * @param objects
     *      other object instances to be evaluated
     */
    public static void NPE(Object o1, Object o2, Object o3, Object... objects) {
        NPE(o1, o2, o3);
        for (Object o : objects) {
            if (null == o) {
                throw new NullPointerException();
            }
        }
    }

    /**
     * Throws out a {@link NullPointerException} when `tester` evaluated to `true`.
     * @param tester
     *      the condition to be evaluated
     */
    public static void NPE(boolean tester) {
        if (tester) {
            throw new NullPointerException();
        }
    }

    /**
     * Throws out a {@link UnexpectedException} with message specified.
     *
     * @param msg
     *      the message format pattern.
     * @param args
     *      the message format arguments
     */
    public static UnexpectedException unexpected(String msg, Object... args) {
        throw new UnexpectedException(msg, args);
    }

    /**
     * Throws out a {@link UnexpectedException} with cause specified.
     *
     * @param cause
     *      the cause of the unexpected exception.
     */
    public static UnexpectedException unexpected(Throwable cause) {
        throw new UnexpectedException(cause);
    }

    /**
     * Throws out a {@link UnexpectedException} with message and cause specified.
     *
     * @param cause
     *      the cause of the unexpected exception.
     * @param msg
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static UnexpectedException unexpected(Throwable cause, String msg, Object... args) {
        throw new UnexpectedException(cause, msg, args);
    }

    /**
     * Throws out a {@link UnexpectedException} with message and cause specified when `tester`
     * is evaluated to `true`.
     *
     * @param tester
     *      when `true` then throw out the exception.
     * @param msg
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static void unexpectedIf(boolean tester, String msg, Object... args) {
        if (tester) {
            unexpected(msg, args);
        }
    }

    public static void unexpectedIf(boolean tester) {
        if (tester) {
            throw new UnexpectedException();
        }
    }

    /**
     * Throws out a {@link UnexpectedException} with message and cause specified when `tester`
     * is **not** evaluated to `true`.
     *
     * @param tester
     *      when `false` then throw out the exception.
     * @param msg
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static void unexpectedIfNot(boolean tester, String msg, Object... args) {
        unexpectedIf(!tester, msg, args);
    }

    public static void unexpectedIfNot(boolean tester) {
        unexpectedIf(!tester);
    }

    /**
     * Wrap the {@link IOException} into {@link UnexpectedIOException} and throw it out.
     * @param cause
     *      the {@link IOException}.
     */
    public static UnexpectedIOException ioException(IOException cause) {
        throw new UnexpectedIOException(cause);
    }

    /**
     * Throws out an {@link UnexpectedIOException} with error message specified.
     * @param msg
     *      the error message format pattern
     * @param args
     *      the error message format arguments
     */
    public static UnexpectedIOException ioException(String msg, Object... args) {
        throw new UnexpectedIOException(msg, args);
    }

    /**
     * Wrap the {@link SQLException} into {@link UnexpectedSqlException} and throw it out.
     * @param cause
     *      the {@link SQLException}.
     */
    public static UnexpectedSqlException sqlException(SQLException cause) {
        throw new UnexpectedSqlException(cause);
    }

    /**
     * Wrap the {@link UnsupportedEncodingException} into an {@link UnexpectedEncodingException}
     * and throw it out.
     *
     * @param cause
     *      the {@link UnsupportedEncodingException}.
     */
    public static UnexpectedEncodingException encodingException(UnsupportedEncodingException cause) {
        throw new UnexpectedEncodingException(cause);
    }

    /**
     * Throws out a {@link ConfigurationException} with message specified.
     * @param message
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static ConfigurationException invalidConfiguration(String message, Object... args) {
        throw new ConfigurationException(message, args);
    }

    /**
     * Throws out a {@link ConfigurationException} with cause and message specified.
     * @param cause
     *      the cause of the configuration error.
     * @param message
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static ConfigurationException invalidConfiguration(Throwable cause, String message, Object... args) {
        throw new ConfigurationException(cause, message, args);
    }

    /**
     * Throws out a {@link ConfigurationException} with message specified if `tester` evaluated to `true`.
     * @param tester
     *      when `true` then throw out the exception
     * @param msg
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static void invalidConfigurationIf(boolean tester, String msg, Object... args) {
        if (tester) {
            invalidConfiguration(msg, args);
        }
    }

    /**
     * Throws out a {@link ConfigurationException} with message specified if `tester` evaluated to `false`.
     * @param tester
     *      when `false` then throw out the exception
     * @param msg
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static void invalidConfigurationIfNot(boolean tester, String msg, Object... args) {
        if (!tester) {
            invalidConfiguration(msg, args);
        }
    }

    /**
     * Throws out a {@link ToBeImplemented} with message `to be implemented`.
     */
    public static ToBeImplemented tbd() {
        throw new ToBeImplemented("to be implemented");
    }

    /**
     * Throws out a {@link ToBeImplemented} with `feature` specified.
     *
     * The error message will be `"${feature} to be implemented"`
     * @param feature
     *      the feature name
     */
    public static ToBeImplemented tbd(String feature) {
        throw new ToBeImplemented("%s to be implemented", feature);
    }

    /**
     * Throws out an {@link InvalidArgException}.
     */
    public static InvalidArgException invalidArg() {
        throw new InvalidArgException();
    }

    /**
     * Throws out an {@link InvalidArgException} when `tester` is `true`.
     * @param tester
     *      when `true` then throws out the exception
     */
    public static void invalidArgIf(boolean tester) {
        if (tester) {
            throw new InvalidArgException();
        }
    }

    /**
     * Throws out an {@link InvalidArgException} when `tester` is `false`.
     * @param tester
     *      when `false` then throws out the exception
     */
    public static void invalidArgIfNot(boolean tester) {
        if (!tester) {
            throw new InvalidArgException();
        }
    }

    /**
     * Throws out an {@link InvalidArgException} with error message specified.
     *
     * @param msg
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static InvalidArgException invalidArg(String msg, Object... args) {
        throw new InvalidArgException(msg, args);
    }

    /**
     * Throws out an {@link InvalidArgException} with error message specified
     * when `tester` is `true`.
     *
     * @param tester
     *      when `true` then throws out the exception.
     * @param msg
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static void invalidArgIf(boolean tester, String msg, Object... args) {
        if (tester) {
            throw invalidArg(msg, args);
        }
    }

    /**
     * Throws out an {@link InvalidArgException} with error message specified
     * when `tester` is `false`.
     *
     * @param tester
     *      when `false` then throws out the exception.
     * @param msg
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static void invalidArgIfNot(boolean tester, String msg, Object... args) {
        if (!tester) {
            throw invalidArg(msg, args);
        }
    }

    /**
     * Throws out an {@link InvalidRangeException}.
     */
    public static InvalidRangeException invalidRange() {
        throw new InvalidRangeException();
    }

    /**
     * Throws out an {@link InvalidRangeException} when `tester`
     * is `true`.
     *
     * @param tester
     *      when `true` then throw out the exception.
     */
    public static void invalidRangeIf(boolean tester) {
        if (tester) {
            throw invalidRange();
        }
    }

    /**
     * Throws out an {@link InvalidRangeException} when `tester`
     * is `false`.
     *
     * @param tester
     *      when `false` then throw out the exception.
     */
    public static void invalidRangeIfNot(boolean tester) {
        if (!tester) {
            throw invalidRange();
        }
    }

    /**
     * Throws out an {@link InvalidRangeException} with error message specified.
     * @param msg
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static InvalidRangeException invalidRange(String msg, Object... args) {
        throw new InvalidRangeException(msg, args);
    }

    /**
     * Throws out an {@link InvalidRangeException} with error message specified
     * when `tester` is `true`.
     *
     * @param tester
     *      when `true` then throw out the exception
     * @param msg
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static void invalidRangeIf(boolean tester, String msg, Object... args) {
        if (tester) {
            throw invalidRange(msg, args);
        }
    }

    /**
     * Throws out an {@link InvalidRangeException} with error message specified
     * when `tester` is `false`.
     *
     * @param tester
     *      when `false` then throw out the exception
     * @param msg
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static void invalidRangeIfNot(boolean tester, String msg, Object... args) {
        if (!tester) {
            throw new InvalidRangeException(msg, args);
        }
    }

    /**
     * Throws out an {@link NullPointerException} when `tester` is `true`.
     *
     * @param tester
     *      when `true` then throw out the exception.
     */
    public static void npeIf(boolean tester) {
        if (tester) {
            throw new NullPointerException();
        }
    }

    /**
     * Throws out an {@link NullPointerException} when `tester` is `false`.
     * 
     * @param tester
     *      when `false` then throw out the exception.
     */
    public static void npeIfNot(boolean tester) {
        if (!tester) {
            throw new NullPointerException();
        }
    }

    /**
     * Throws out an {@link UnsupportedException}.
     */
    public static UnsupportedException unsupport() {
        throw new UnsupportedException();
    }

    /**
     * Throws out an {@link UnsupportedException} with error message specified.
     * @param msg
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static UnsupportedException unsupport(String msg, Object... args) {
        throw new UnsupportedException(msg, args);
    }

    /**
     * Throws out an {@link UnsupportedException} if `tester` is `true`.
     *
     * @param tester
     *      when `true` then throw out the exception.
     */
    public static void unsupportedIf(boolean tester) {
        if (tester) {
            throw unsupport();
        }
    }

    /**
     * Throws out an {@link UnsupportedException} if `tester` is `false`.
     *
     * @param tester
     *      when `false` then throw out the exception.
     */
    public static void unsupportedIfNot(boolean tester) {
        if (!tester) {
            throw unsupport();
        }
    }

    /**
     * Throws out an {@link UnsupportedException} with error message specified
     * if `tester` is `true`.
     *
     * @param tester
     *      when `true` then throw out the exception.
     * @param msg
     *      the error message.
     */
    public static void unsupportedIf(boolean tester, String msg) {
        if (tester) {
            throw unsupport(msg);
        }
    }

    /**
     * Throws out an {@link UnsupportedException} with error message specified
     * if `tester` is `false`.
     *
     * @param tester
     *      when `false` then throw out the exception.
     * @param msg
     *      the error message.
     */
    public static void unsupportedIfNot(boolean tester, String msg) {
        if (!tester) {
            throw unsupport(msg);
        }
    }

    /**
     * Throws out an {@link UnsupportedException} with error message specified
     * if `tester` is `true`.
     *
     * @param tester
     *      when `true` then throw out the exception.
     * @param msg
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static void unsupportedIf(boolean tester, String msg, Object ... args) {
        if (tester) {
            throw unsupport(msg, args);
        }
    }

    /**
     * Throws out an {@link UnsupportedException} with error message specified
     * if `tester` is `false`.
     *
     * @param tester
     *      when `false` then throw out the exception.
     * @param msg
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static void unsupportedIfNot(boolean tester, String msg, Object ... args) {
        if (!tester) {
            throw unsupport(msg, args);
        }
    }

    /**
     * Throws out an {@link IllegalArgumentException} if `tester` is `true`.
     *
     * @param tester
     *      when `true` then throw out the exception.
     */
    public static void illegalArgumentIf(boolean tester) {
        if (tester) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Throws out an {@link IllegalArgumentException} if `tester` is `false`.
     *
     * @param tester
     *      when `false` then throw out the exception.
     */
    public static void illegalArgumentIfNot(boolean tester) {
        if (!tester) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Throws out an {@link IllegalArgumentException} if `tester` is `false`.
     *
     * @param tester
     *      when `false` then throw out the exception.
     */
    public static void illegalArgumentIf(boolean tester, String msg) {
        if (tester) {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Throws out an {@link IllegalArgumentException} if `tester` is `true`.
     *
     * @param tester
     *      when `true` then throw out the exception.
     * @param msg
     *      the error message.
     */
    public static void illegalArgumentIfNot(boolean tester, String msg) {
        if (!tester) {
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Throws out an {@link IllegalArgumentException} with error message specified
     * if `tester` is `true`.
     *
     * @param tester
     *      when `true` then throw out the exception.
     * @param msg
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static void illegalArgumentIf(boolean tester, String msg, Object... args) {
        if (tester) {
            throw new IllegalArgumentException(S.fmt(msg, args));
        }
    }

    /**
     * Throws out an {@link IllegalArgumentException} with error message specified
     * if `tester` is `false`.
     *
     * @param tester
     *      when `false` then throw out the exception.
     * @param msg
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static void illegalArgumentIfNot(boolean tester, String msg, Object... args) {
        if (!tester) {
            throw new IllegalArgumentException(S.fmt(msg, args));
        }
    }

    /**
     * Throws out an {@link IllegalStateException} if `tester` is `true`.
     *
     * @param tester
     *      when `true` then throw out the exception.
     */
    public static void illegalStateIf(boolean tester) {
        if (tester) {
            throw new IllegalStateException();
        }
    }

    /**
     * Throws out an {@link IllegalStateException} if `tester` is `false`.
     *
     * @param tester
     *      when `false` then throw out the exception.
     */
    public static void illegalStateIfNot(boolean tester) {
        if (!tester) {
            throw new IllegalStateException();
        }
    }

    /**
     * Throws out an {@link IllegalStateException} with error message specified
     * if `tester` is `true`.
     *
     * @param tester
     *      when `true` then throw out the exception.
     * @param msg
     *      the error message.
     */
    public static void illegalStateIf(boolean tester, String msg) {
        if (tester) {
            throw new IllegalStateException(msg);
        }
    }

    /**
     * Throws out an {@link IllegalStateException} with error message specified
     * if `tester` is `false`.
     *
     * @param tester
     *      when `false` then throw out the exception.
     * @param msg
     *      the error message.
     */
    public static void illegalStateIfNot(boolean tester, String msg) {
        if (!tester) {
            throw new IllegalStateException(msg);
        }
    }

    /**
     * Throws out an {@link IllegalStateException} with error message specified
     * if `tester` is `true`.
     *
     * @param tester
     *      when `true` then throw out the exception.
     * @param msg
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static void illegalStateIf(boolean tester, String msg, Object... args) {
        if (tester) {
            throw new IllegalStateException(S.fmt(msg, args));
        }
    }

    /**
     * Throws out an {@link IllegalStateException} with error message specified
     * if `tester` is `false`.
     *
     * @param tester
     *      when `false` then throw out the exception.
     * @param msg
     *      the error message format pattern.
     * @param args
     *      the error message format arguments.
     */
    public static void illegalStateIfNot(boolean tester, String msg, Object... args) {
        if (!tester) {
            throw new IllegalStateException(S.fmt(msg, args));
        }
    }

    /**
     * Convert an Exception to RuntimeException
     * @param e the Exception instance
     * @return a RuntimeException instance
     */
    public static RuntimeException asRuntimeException(Exception e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return UnexpectedMethodInvocationException.triage(e);
    }

    /**
     * Returns the error stack trace of a {@link Throwable} specified as a String.
     *
     * @param t
     *      the throwable.
     * @return the stack trace of `t` the throwable as string.
     */
    public static String stackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
    
}
