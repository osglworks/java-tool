package org.osgl.util;

import java.math.BigInteger;

public class BigIntegerValueObjectCodec implements ValueObject.Codec<BigInteger> {
    @Override
    public Class<BigInteger> targetClass() {
        return BigInteger.class;
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
