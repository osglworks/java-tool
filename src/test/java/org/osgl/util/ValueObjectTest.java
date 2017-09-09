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

import org.junit.Test;
import org.osgl.OsglToolTestBase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ValueObjectTest extends OsglToolTestBase {

    @Test
    public void testBoolean() {
        ValueObject vo = new ValueObject(true);
        eq(vo.toString(), "true");
        eq(vo.toJSONString(), "true");
        yes(vo.booleanValue());
    }

    @Test
    public void testByte() {
        byte b = 100;
        ValueObject vo = new ValueObject(b);
        eq(vo.toString(), "100");
        eq(vo.toJSONString(), "100");
        eq(b, vo.byteValue());
    }

    @Test
    public void testChar() {
        ValueObject vo = new ValueObject('c');
        eq(vo.toString(), "c");
        eq(vo.toJSONString(), "\"c\"");
        eq('c', vo.charValue());
    }

    @Test
    public void testString() {
        String s = S.random();
        ValueObject vo = new ValueObject(s);
        eq(vo.toString(), s);
        eq(vo.toJSONString(), "\"" + s + "\"");
        eq(s, vo.stringValue());
    }

    @Test
    public void testEnum() {
        Enum e = FilteredIterator.Type.ALL;
        ValueObject vo = new ValueObject(e);
        eq(vo.toString(), e.toString());
        eq(vo.toJSONString(), "\"" + e + "\"");
        FilteredIterator.Type type = vo.enumValue();
        eq(FilteredIterator.Type.ALL, type);
    }

    @Test
    public void testShort() {
        short s = 100;
        ValueObject vo = new ValueObject(s);
        eq(vo.toString(), "100");
        eq(vo.toJSONString(), "100");
        eq(s, vo.shortValue());
    }

    @Test
    public void testInt() {
        int i = Integer.MAX_VALUE;
        ValueObject vo = new ValueObject(i);
        eq(vo.toString(), S.string(i));
        eq(vo.toJSONString(), S.string(i));
        eq(i, vo.intValue());
    }

    @Test
    public void testLong() {
        long l = Long.MAX_VALUE;
        ValueObject vo = new ValueObject(l);
        eq(vo.toString(), S.string(l));
        eq(vo.toJSONString(), S.string(l));
        eq(l, vo.longValue());
    }

    @Test
    public void testFloat() {
        float f = 10.234f;
        ValueObject vo = new ValueObject(f);
        eq(vo.toString(), "10.234");
        eq(vo.toJSONString(), "10.234");
        eq(f, vo.floatValue());
    }

    @Test
    public void testDouble() {
        double d = 2109.32143125d;
        ValueObject vo = new ValueObject(d);
        eq(vo.toString(), "2109.32143125");
        eq(vo.toJSONString(), "2109.32143125");
        eq(d, vo.doubleValue());
    }

    @Test
    public void testObject() {
        Object o = 100;
        ValueObject vo = new ValueObject(o);
        eq(vo.toString(), "100");
        eq(vo.toJSONString(), "100");

        o = null;
        vo = new ValueObject(o);
        eq(vo.toString(), "");
        eq(vo.toJSONString(), "\"\"");

        o = false;
        vo = new ValueObject(o);
        eq(vo.toString(), "false");
        eq(vo.toJSONString(), "false");
    }

    @Test
    public void testUDF() {
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        ValueObject.Codec dateCodec = new ValueObject.Codec<Date>() {
            @Override
            public Class<Date> targetClass() {
                return Date.class;
            }

            @Override
            public Date parse(String s) {
                try {
                    return df.parse(s);
                } catch (Exception e) {
                    throw E.unexpected(e);
                }
            }

            @Override
            public String toString(Date o) {
                return df.format(o);
            }

            @Override
            public String toJSONString(Date o) {
                return S.fmt("\"%s\"", df.format(o));
            }
        };
        ValueObject.register(dateCodec);
        Date date = new Date();
        ValueObject vo = new ValueObject(date);
        eq(vo.toString(), df.format(date));
        eq(vo.toJSONString(), S.fmt("\"%s\"", df.format(date)));
    }

    @Test(expected = NullPointerException.class)
    public void testIncorrectType() {
        ValueObject vo = new ValueObject(false);
        vo.intValue();
    }

    @Test
    public void testCopyConstructor() {
        ValueObject vo = new ValueObject(5);
        ValueObject copy = new ValueObject(vo);
        eq(copy, vo);
    }
}
