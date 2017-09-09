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
import org.osgl.Osgl;

class ListPropertyHandler extends PropertyHandlerBase {

    protected final Class<?> itemType;

    ListPropertyHandler(Class<?> itemType) {
        this.itemType = $.notNull(itemType);
    }

    ListPropertyHandler(PropertyGetter.NullValuePolicy nullValuePolicy, Class<?> itemType) {
        super(nullValuePolicy);
        this.itemType = $.notNull(itemType);
    }

    ListPropertyHandler(Osgl.Function<Class<?>, Object> objectFactory,
                        Osgl.Func2<String, Class<?>, ?> stringValueResolver,
                        Class<?> itemType) {
        super(objectFactory, stringValueResolver);
        this.itemType = $.notNull(itemType);
    }

    ListPropertyHandler(Osgl.Function<Class<?>, Object> objectFactory,
                        Osgl.Func2<String, Class<?>, ?> stringValueResolver,
                        PropertyGetter.NullValuePolicy nullValuePolicy,
                        Class<?> itemType) {
        super(objectFactory, stringValueResolver, nullValuePolicy);
        this.itemType = $.notNull(itemType);
    }

}
