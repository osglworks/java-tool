package org.osgl.util;

import com.alibaba.fastjson.JSON;

public class KVCodec extends StringValueResolver<KV> implements ValueObject.Codec<KV> {

    public static final KVCodec INSTANCE = new KVCodec();

    @Override
    public Class<KV> targetClass() {
        return KV.class;
    }

    @Override
    public KV parse(String s) {
        return JSON.parseObject(s, KV.class);
    }

    @Override
    public String toString(KV o) {
        return JSON.toJSONString(o);
    }

    @Override
    public String toJSONString(KV o) {
        return toString(o);
    }

    @Override
    public KV resolve(String value) {
        return parse(value);
    }
}
