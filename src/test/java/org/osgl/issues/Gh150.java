package org.osgl.issues;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2018 OSGL (Open Source General Library)
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
import org.osgl.$;
import org.osgl.TestBase;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Gh150 extends TestBase {

    public static class Foo {
        BigInteger a;
        Bar bar = new Bar();
        Foo init() {
            bar.init();
            a = new BigInteger("1001");
            return this;
        }
    }

    public static class Bar {
        BigDecimal b;
        void init() {
            b = new BigDecimal("1.01");
        }
    }

    @Test
    public void testToMap() {
        Foo src = new Foo().init();
        Foo tgt = $.deepCopy(src).to(Foo.class);
        eq(src.a, tgt.a);
        eq(src.bar.b, tgt.bar.b);
    }

}
