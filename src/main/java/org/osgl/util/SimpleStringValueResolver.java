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

import org.osgl.Lang;
import org.osgl.exception.NotAppliedException;

import java.util.Map;

public class SimpleStringValueResolver extends Lang.F2<String, Class<?>, Object> {

    public static final SimpleStringValueResolver INSTANCE = new SimpleStringValueResolver();

    protected Map<Class, StringValueResolver> resolvers = C.newMap();

    public SimpleStringValueResolver() {
        registerPredefinedResolvers();
    }

    @Override
    public Object apply(String s, Class<?> aClass) throws NotAppliedException, Lang.Break {
        StringValueResolver r = resolvers.get(aClass);
        if (null != r) {
            return r.resolve(s);
        }
        if (null != s && Enum.class.isAssignableFrom(aClass)) {
            return Enum.valueOf(((Class<Enum>) aClass), s);
        }
        return null;
    }

    private void registerPredefinedResolvers() {
        resolvers.putAll(StringValueResolver.predefined());
    }
}
