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

import java.math.BigDecimal;

public class BigDecimalValueObjectCodec extends StringValueResolver<BigDecimal> implements ValueObject.Codec<BigDecimal> {

    public static final BigDecimalValueObjectCodec INSTANCE = new BigDecimalValueObjectCodec();

    @Override
    public Class<BigDecimal> targetClass() {
        return BigDecimal.class;
    }

    @Override
    public BigDecimal resolve(String value) {
        return parse(value);
    }

    @Override
    public BigDecimal parse(String s) {
        return new BigDecimal(s);
    }

    @Override
    public String toString(BigDecimal o) {
        return o.toString();
    }

    @Override
    public String toJSONString(BigDecimal o) {
        return o.toString();
    }

}
