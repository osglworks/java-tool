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

public abstract class ContextLocalBase<T> extends ContextLocal.InitialValueProvider<T> implements ContextLocal<T>  {
    private InitialValueProvider<T> iv;

    protected ContextLocalBase() {}

    protected ContextLocalBase(InitialValueProvider<T> ivp) {
        iv = ivp;
    }

    @Override
    public T initialValue() {
        if (null == iv) return null;
        return iv.initialValue();
    }
}
