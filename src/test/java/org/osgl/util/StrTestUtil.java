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

import org.junit.Before;
import org.junit.Ignore;
import org.osgl.OsglToolTestBase;

@Ignore
public abstract class StrTestUtil<T extends StrBase<T>> extends OsglToolTestBase {
    protected T aaa;
    protected T abc;
    protected T abc2;
    protected T zabcd;
    protected T 码农码代码戏码农;
    protected T 农码代;
    protected T midLength;
    protected T longStr;
    protected T empty = empty();

    private T _aaa = copyOf("aaa");
    private T _abc = copyOf("abc");
    private T _zabcd = copyOf("zabcd");
    private T _abc2 = _zabcd.substr(1, 4);
    private T _mid = copyOf(S.random(22));
    private T _long = copyOf(S.random(5000));
    private T _码农码代码戏码农 = copyOf("码农码代码戏码农");
    private T _农码代 = _码农码代码戏码农.substr(1, 4);

    protected static void ceq(CharSequence c1, CharSequence c2) {
        eq(c1.toString(), c2.toString());
    }

    protected abstract T copyOf(String s);

    protected abstract T empty();

    @Before
    public void prepare() {
        aaa = _aaa;
        abc = _abc;
        midLength = _mid;
        longStr = _long;
        abc2 = _abc2;
        zabcd = _zabcd;
        码农码代码戏码农 = _码农码代码戏码农;
        农码代 = _农码代;
        eq(abc2.toString(), "abc");
    }

}
