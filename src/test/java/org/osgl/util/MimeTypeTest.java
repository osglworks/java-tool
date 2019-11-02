package org.osgl.util;

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
import org.osgl.TestBase;
import org.osgl.util.MimeType.Trait;

import static org.osgl.util.MimeType.Trait.*;
import static org.osgl.util.MimeType.findByContentType;
import static org.osgl.util.MimeType.findByFileExtension;

public class MimeTypeTest extends TestBase {

    @Test
    public void test() {
        MimeType mimeType = findByFileExtension("pdf");
        yes(null != mimeType && mimeType.test(pdf));

        mimeType = findByFileExtension("bz2");
        yes(null != mimeType && mimeType.test(archive));

        mimeType = findByContentType("text/plain");
        yes(mimeType.test(text));

        mimeType = findByFileExtension("xlsx");
        yes(mimeType.test(excel));
        yes(mimeType.test(xlsx));
        no(mimeType.test(xls));

        mimeType = findByFileExtension("pptx");
        yes(mimeType.test(powerpoint));
        yes(mimeType.test(pptx));
        no(mimeType.test(ppt));

        mimeType = findByContentType("application/javascript");
        yes(mimeType.test(text));
    }

    @Test
    public void testTxt() {
        eq("txt", findByFileExtension("txt").fileExtension());
    }

    @Test
    public void test215() {
        MimeType yml = findByFileExtension("yml");
        yes(null != yml && yml.test(Trait.yaml));
        MimeType yaml = findByFileExtension("yaml");
        same(yml.type(), yaml.type());

        MimeType yml2 = findByContentType("application/x-yaml");
        same(yml2, yaml);
    }

    @Test
    public void test216() {
        MimeType ejson = findByContentType("application/problem+json");
        MimeType json = findByContentType("application/json");
        ne(json, ejson);
        eq("application/problem+json", ejson.type());
        yes(ejson.test(problem));
        yes(ejson.test(Trait.json));
        no(json.test(problem));

        MimeType exml = findByContentType("application/problem+xml");
        MimeType xml = findByContentType("text/xml");
        ne(xml, exml);
        eq("application/problem+xml", exml.type());
        yes(exml.test(problem));
        yes(exml.test(Trait.xml));
        no(xml.test(problem));

        MimeType eyaml = findByContentType("application/problem+yaml");
        MimeType yaml = findByContentType("text/vnd.yaml");
        ne(yaml, eyaml);
        eq("application/problem+yaml", eyaml.type());
        yes(eyaml.test(problem));
        yes(eyaml.test(Trait.yaml));
        no(yaml.test(problem));
    }

}
