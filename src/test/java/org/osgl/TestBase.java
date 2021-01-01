package org.osgl;

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

import org.junit.runner.JUnitCore;
import org.osgl.util.IO;
import org.osgl.util.S;

import java.util.Random;

/**
 * The test base case
 */
public abstract class TestBase extends osgl.ut.TestBase {

    protected static void run(Class<? extends TestBase> cls) {
        new JUnitCore().run(cls);
    }
    
    protected static void println(String tmpl, Object... args) {
        System.out.println(String.format(tmpl, args));
    }

    protected static String newRandStr() {
        return S.random(new Random().nextInt(30) + 15);
    }

    protected String loadFileAsString(String path) {
        return IO.read(getClass().getClassLoader().getResource(path)).toString();
    }
}
