package org.osgl.util;

public class KeywordValueObjectCodec implements ValueObject.Codec<Keyword> {
    @Override
    public Class<Keyword> targetClass() {
        return Keyword.class;
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
