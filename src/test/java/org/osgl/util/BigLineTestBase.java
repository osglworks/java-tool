package org.osgl.util;

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

import org.junit.After;
import org.junit.Before;
import org.osgl.TestBase;
import org.osgl.logging.LogManager;
import org.osgl.logging.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class BigLineTestBase extends TestBase {

    protected static final Logger LOGGER = LogManager.get(BigLineTestBase.class);

    protected int lines;
    private File testFile;
    protected BigLines bigLines;

    BigLineTestBase(int lines) {
        this.lines = lines;
    }

    @Before
    public void prepareTestFile() throws IOException {
        testFile = File.createTempFile("big-lines-", ".txt");
        LOGGER.info("test file: %s", testFile.getAbsoluteFile());
        FileWriter fw = new FileWriter(testFile);
        PrintWriter pw = new PrintWriter(fw);
        for (int i = 0; i < lines; ++i) {
            pw.println(i);
        }
        IO.close(fw);
        bigLines = new BigLines(testFile);
    }

    @After
    public void clearTestFile() throws IOException {
        if (!testFile.delete()) {
            testFile.deleteOnExit();
        }
    }


}
