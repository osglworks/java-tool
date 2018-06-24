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

abstract class PropertyHandlerBase implements PropertyHandler {
    protected Lang.Function<Class<?>, Object> objectFactory;
    protected Lang.Func2<String, Class<?>, ?> stringValueResolver;
    protected PropertyGetter.NullValuePolicy nullValuePolicy;

    PropertyHandlerBase() {
        this(SimpleObjectFactory.INSTANCE, SimpleStringValueResolver.INSTANCE);
    }

    PropertyHandlerBase(PropertyGetter.NullValuePolicy nullValuePolicy) {
        this(SimpleObjectFactory.INSTANCE, SimpleStringValueResolver.INSTANCE, nullValuePolicy);
    }

    PropertyHandlerBase(Lang.Function<Class<?>, Object> objectFactory, Lang.Func2<String, Class<?>, ?> stringValueResolver) {
        setObjectFactory(objectFactory);
        setStringValueResolver(stringValueResolver);
        setNullValuePolicy(PropertyGetter.NullValuePolicy.RETURN_NULL);
    }

    PropertyHandlerBase(Lang.Function<Class<?>, Object> objectFactory,
                        Lang.Func2<String, Class<?>, ?> stringValueResolver,
                        PropertyGetter.NullValuePolicy nullValuePolicy) {
        setObjectFactory(objectFactory);
        setStringValueResolver(stringValueResolver);
        if (null == nullValuePolicy) {
            nullValuePolicy = PropertyGetter.NullValuePolicy.RETURN_NULL;
        }
        setNullValuePolicy(nullValuePolicy);
    }

    @Override
    public void setObjectFactory(Lang.Function<Class<?>, Object> factory) {
        this.objectFactory = $.requireNotNull(factory);
    }

    @Override
    public void setStringValueResolver(Lang.Func2<String, Class<?>, ?> stringValueResolver) {
        this.stringValueResolver = $.requireNotNull(stringValueResolver);
    }

    public void setNullValuePolicy(PropertyGetter.NullValuePolicy nvp) {
        this.nullValuePolicy = $.requireNotNull(nvp);
    }
}
