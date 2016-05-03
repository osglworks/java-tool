package org.osgl.util;

import java.math.BigDecimal;

public class BigDecimalValueObjectCodec implements ValueObject.Codec<BigDecimal> {
    @Override
    public Class<BigDecimal> targetClass() {
        return BigDecimal.class;
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
