package org.osgl.concurrent;

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

import org.osgl.$;

/**
 * Provide access to context local bag. E.g JDKThreadLocal
 */
public interface ContextLocal<T> {

    /**
     * Returns the value in the current context's copy of this
     * context-local variable.  If the variable has no value for the
     * current context, it is first initialized to the value returned
     * by an invocation of the {@link #initialValue} method.
     *
     * @return the current thread's value of this thread-local
     */
    T get();

    /**
     * Sets the current context's copy of this context-local variable
     * to the specified value.  Most subclasses will have no need to
     * override this method, relying solely on the {@link #initialValue}
     * method to set the values of context-locals.
     *
     * @param value the value to be stored in the current context's copy of
     *        this context-local.
     */
    void set(T value);

    /**
     * Removes the current context's value for this context-local
     * variable.  If this context-local variable is subsequently
     * {@linkplain #get read} by the current thread, its value will be
     * reinitialized by invoking its {@link #initialValue} method,
     * unless its value is {@linkplain #set set} by the current context
     * in the interim.  This may result in multiple invocations of the
     * <tt>initialValue</tt> method in the current context.
     */
    void remove();

    /**
     * Returns the current context's "initial value" for this
     * context-local variable.  This method will be invoked the first
     * time a context accesses the variable with the {@link #get}
     * method, unless the context previously invoked the {@link #set}
     * method, in which case the <tt>initialValue</tt> method will not
     * be invoked for the context.  Normally, this method is invoked at
     * most once per context, but it may be invoked again in case of
     * subsequent invocations of {@link #remove} followed by {@link #get}.
     *
     * @return the initial value for this context-local
     */
    T initialValue();

    public abstract static class InitialValueProvider<T> extends $.F0<T> {
        @Override
        public T apply() {
            return initialValue();
        }

        public abstract T initialValue();
    }

    public static final String CONF_CONTEXT_LOCAL_FACTORY = "osgl.context_local.factory";

    public static interface Factory {
        <T> ContextLocal<T> create();
        <T> ContextLocal<T> create(InitialValueProvider<T> ivp);

        public static enum Predefined {
            ;
            public static Factory JDKThreadLocalFactory = new Factory() {
                @Override
                public <T> ContextLocal<T> create() {
                    return new JDKThreadLocal<T>();
                }

                @Override
                public <T> ContextLocal<T> create(InitialValueProvider<T> ivp) {
                    return new JDKThreadLocal<T>(ivp);
                }
            };

            public static Factory defaultFactory() {
                return JDKThreadLocalFactory;
            }
        }
    }

}
