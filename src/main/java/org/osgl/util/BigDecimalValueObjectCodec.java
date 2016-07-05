package org.osgl.util;

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
