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

/**
 * Implement {@link org.osgl.concurrent.ContextLocal} using
 * JDK ThreadLocal
 */
public class JDKThreadLocal<T> extends ContextLocalBase<T> implements ContextLocal<T> {

    private volatile ThreadLocal<T> tl;

    public JDKThreadLocal() {}

    public JDKThreadLocal(InitialValueProvider<T> ivp) {
        super(ivp);
    }

    private ThreadLocal<T> tl() {
        if (null == tl) {
            synchronized (this) {
                if (null == tl) {
                    final JDKThreadLocal<T> me = this;
                    tl = new ThreadLocal<T>() {
                        @Override
                        protected T initialValue() {
                            return me.initialValue();
                        }
                    };
                }
            }
        }
        return tl;
    }

    @Override
    public T get() {
        return tl().get();
    }

    @Override
    public void set(T value) {
        tl().set(value);
    }

    @Override
    public void remove() {
        tl().remove();
    }

}
