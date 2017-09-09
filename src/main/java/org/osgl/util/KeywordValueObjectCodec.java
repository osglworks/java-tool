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

public class KeywordValueObjectCodec extends StringValueResolver<Keyword> implements ValueObject.Codec<Keyword> {

    public static final KeywordValueObjectCodec INSTANCE = new KeywordValueObjectCodec();

    @Override
    public Class<Keyword> targetClass() {
        return Keyword.class;
    }

    @Override
    public Keyword resolve(String value) {
        return Keyword.of(value);
    }

    @Override
    public Keyword parse(String s) {
        return Keyword.of(s);
    }

    @Override
    public String toString(Keyword o) {
        return o.toString();
    }

    @Override
    public String toJSONString(Keyword o) {
        return ValueObject.of(o.underscore()).toJSONString();
    }

}
