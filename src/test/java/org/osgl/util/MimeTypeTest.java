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

}
