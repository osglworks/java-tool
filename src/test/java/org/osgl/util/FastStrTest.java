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

public class FastStrTest extends StrTestBase<FastStr> {
    @Override
    protected FastStr copyOf(String s) {
        return FastStr.of(s);
    }

    @Override
    protected FastStr empty() {
        return FastStr.EMPTY_STR;
    }

    @Test
    public void testRevertBeginPointer() {
        final String s = "http://abc.com:8038/xyz/123";
        char[] buf = Unsafe.bufOf(s);
        FastStr fs = FastStr.unsafeOf(s);
        fs = fs.afterFirst("://").afterFirst('/');
        ceq(fs, "xyz/123");
        FastStr fs0 = fs.prepend('/');
        ceq(fs0, "/xyz/123");
        fs0 = fs.prepend("/");
        ceq(fs0, "/xyz/123");
    }

    @Test
    public void testTrimOnBeginAndEndPointerSet() {
        String s = "123 Hello world! 123";
        int sz = s.length();
        FastStr fs = FastStr.unsafeOf(s).substr(3, sz - 3);
        ceq(" Hello world! ", fs);
        ceq("Hello world!", fs.trim());
    }

    @Test
    public void testReplaceChar() {
        String s = "C:\\Users\\luog.IKARI\\Google Drive\\XYZ Research\\Client Docs\\7. CRM templates.pdf";
        FastStr fs = FastStr.unsafeOf(s);
        fs = fs.replace('\\', '/');
        eq(fs.toString(), "C:/Users/luog.IKARI/Google Drive/XYZ Research/Client Docs/7. CRM templates.pdf");
    }

    @Test
    public void testReplaceCharInSubStr() {
        String s = "C:\\Users\\luog.IKARI\\Google Drive\\XYZ Research\\Client Docs\\7. CRM templates.pdf";
        FastStr fs = FastStr.unsafeOf(s).afterFirst("C:\\Users\\luog.IKARI\\Google Drive").beforeLast(".");
        fs = fs.replace('\\', '/');
        eq(fs.toString(), "/XYZ Research/Client Docs/7. CRM templates");
    }

    @Test
    public void testConstructFastStrFromSubStr() {
        String s = "abcde";
        String subStr = s.substring(1, 4);
        FastStr fastStr = FastStr.unsafeOf(subStr);
        eq(fastStr.toString(), subStr);
    }

    @Test
    public void testConstructFromByteArray() throws Exception {
        String s = S.random();
        byte[] ba = s.getBytes("utf-8");
        FastStr fs = FastStr.of(ba, "utf-8");
        ceq(fs, s);
    }

    @Test
    public void testSplit() {
        FastStr s = FastStr.of("a=1&&b=2");
        C.List<FastStr> list = s.split("&");
        eq(3, list.size());
        ceq("a=1", list.get(0));
        ceq("", list.get(1));
        ceq("b=2", list.get(2));
    }

    @Test
    public void testCopy() {
        char[] ca = new char[]{'a', 'b', 'c'};
        FastStr fs = FastStr.unsafeOf(ca);
        FastStr copy = fs.copy();
        ceq("abc", copy);
        ca[1] = 'x';
        ceq("axc", fs);
        ceq("abc", copy);
    }

    @Test
    public void ghIssue7() {
        FastStr s1 = FastStr.of("12345678/{user}").substr(10);
        FastStr s2 = FastStr.of("12345678/exists").substr(10);
        no(s2.equals(s1));
    }

}
