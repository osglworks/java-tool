package org.osgl.util;

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
