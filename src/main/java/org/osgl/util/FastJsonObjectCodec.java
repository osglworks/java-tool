package org.osgl.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class FastJsonObjectCodec extends StringValueResolver<JSONObject> implements ValueObject.Codec<JSONObject> {

    public static final FastJsonObjectCodec INSTANCE = new FastJsonObjectCodec();

    @Override
    public Class<JSONObject> targetClass() {
        return JSONObject.class;
    }

    @Override
    public JSONObject parse(String s) {
        return JSON.parseObject(s, JSONObject.class);
    }

    @Override
    public String toString(JSONObject o) {
        return JSON.toJSONString(o);
    }

    @Override
    public String toJSONString(JSONObject o) {
        return toString(o);
    }

    @Override
    public JSONObject resolve(String value) {
        return parse(value);
    }
}
