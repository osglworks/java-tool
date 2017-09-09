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

import java.math.BigInteger;

public class BigIntegerValueObjectCodec extends StringValueResolver<BigInteger> implements ValueObject.Codec<BigInteger> {

    public static final BigIntegerValueObjectCodec INSTANCE = new BigIntegerValueObjectCodec();

    @Override
    public Class<BigInteger> targetClass() {
        return BigInteger.class;
    }

    @Override
    public BigInteger resolve(String value) {
        return parse(value);
    }

    @Override
    public BigInteger parse(String s) {
        return new BigInteger(s);
    }

    @Override
    public String toString(BigInteger o) {
        return o.toString();
    }

    @Override
    public String toJSONString(BigInteger o) {
        return o.toString();
    }

}
