package org.osgl.exception;

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

import org.osgl.util.E;

import java.lang.reflect.InvocationTargetException;

/**
 * A runtime exception that is raised when exception encountered
 * invoking a method by Java reflection
 */
public class UnexpectedMethodInvocationException extends UnexpectedException {
    public UnexpectedMethodInvocationException(Throwable cause) {
        super(triage(cause));
    }

    public UnexpectedMethodInvocationException(Throwable cause, String message, Object... args) {
        super(triage(cause), message, args);
    }

    private static Throwable triage(Throwable cause) {
        E.NPE(cause);
        if (cause instanceof InvocationTargetException) {
            return ((InvocationTargetException) cause).getTargetException();
        } else {
            return cause;
        }
    }

    public static RuntimeException handle(Exception e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else if (e instanceof InvocationTargetException) {
            return handle((InvocationTargetException) e);
        } else {
            return new UnexpectedMethodInvocationException(e);
        }
    }

    public static RuntimeException handle(InvocationTargetException e) {
        Throwable cause = e.getCause();
        if (cause instanceof RuntimeException) {
            return (RuntimeException) cause;
        }
        return new UnexpectedMethodInvocationException(cause);
    }
}
