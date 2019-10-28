package org.osgl.storage;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2019 OSGL (Open Source General Library)
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
import org.osgl.storage.impl.SObject;

import java.io.CharArrayReader;
import java.io.InputStream;
import java.io.Reader;

public class ProbeBinaryTest extends TestBase {

    @Test
    public void asciiShallNotBeBinary() {
        char[] ca = {'a', 'b', 'c'};
        Reader reader = new CharArrayReader(ca);
        no(SObject.of($.convert(reader).to(InputStream.class)).isBinary());
    }

    @Test
    public void binaryShallBeBinary() {
        char[] ca = {'a', 'b', 'c', 0};
        Reader reader = new CharArrayReader(ca);
        yes(SObject.of($.convert(reader).to(InputStream.class)).isBinary());
    }

}
