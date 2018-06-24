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

import org.osgl.$;
import org.osgl.Lang;

class MapPropertyHandler extends PropertyHandlerBase {

    protected final Class<?> keyType;
    protected final Class<?> valType;

    public MapPropertyHandler(Class<?> keyType, Class<?> valType) {
        this.keyType = $.requireNotNull(keyType);
        this.valType = $.requireNotNull(valType);
    }

    public MapPropertyHandler(PropertyGetter.NullValuePolicy nullValuePolicy,
                              Class<?> keyType,
                              Class<?> valType) {
        super(nullValuePolicy);
        this.keyType = $.requireNotNull(keyType);
        this.valType = $.requireNotNull(valType);
    }

    public MapPropertyHandler(Lang.Function<Class<?>, Object> objectFactory,
                              Lang.Func2<String, Class<?>, ?> stringValueResolver,
                              Class<?> keyType,
                              Class<?> valType) {
        super(objectFactory, stringValueResolver);
        this.keyType = $.requireNotNull(keyType);
        this.valType = $.requireNotNull(valType);
    }

    public MapPropertyHandler(Lang.Function<Class<?>, Object> objectFactory,
                              Lang.Func2<String, Class<?>, ?> stringValueResolver,
                              PropertyGetter.NullValuePolicy nullValuePolicy,
                              Class<?> keyType,
                              Class<?> valType) {
        super(objectFactory, stringValueResolver, nullValuePolicy);
        this.keyType = $.requireNotNull(keyType);
        this.valType = $.requireNotNull(valType);
    }

    protected Object keyFrom(Object index) {
        if (keyType.isAssignableFrom(index.getClass())) {
            return index;
        } else {
            return stringValueResolver.apply(S.string(index), keyType);
        }
    }
}
