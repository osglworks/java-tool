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
import org.osgl.util.S;

import java.security.Key;

public class Gh118 extends TestBase {

    public static class Source {
        String id = S.random();
    }

    public static class Target {
        Key id;
        String sid;
    }

    @Test
    public void test() {
        Source source = new Source();
        Target target = $.copy(source).map("id").to("sid").to(Target.class);
        eq(source.id, target.sid);
        isNull(target.id);
    }


}
