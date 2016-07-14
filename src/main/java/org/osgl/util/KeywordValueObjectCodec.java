package org.osgl.util;

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
