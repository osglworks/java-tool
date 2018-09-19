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

import java.io.*;
import java.util.*;

/**
 * A help class provide utilities that read through text file with big
 * number of lines.
 *
 * It supports:
 *
 * 1. preview the first line
 * 2. get line numbers
 * 3. skip lines
 * 4. fetch certain number of lines
 */
public class BigLines implements Iterable<String> {

    private File file;

    private volatile Long lines;
    private String firstLine;

    public BigLines(File file) {
        E.illegalArgumentIfNot(file.exists() && file.isFile() && file.canRead(), "file must exists and be a readable file: " + file);
        this.file = file;
        this.firstLine = fetch(0);
    }

    public boolean isEmpty() {
        return null == firstLine;
    }

    public String firstLine() {
        return firstLine;
    }

    public long lines() {
        if (null == lines) {
            synchronized (this) {
                if (null == lines) {
                    lines = countLines();
                }
            }
        }
        return lines;
    }

    /**
     * Returns first 5 lines including header line.
     */
    public List<String> preview() {
        return preview(5, false);
    }

    /**
     * Returns first `limit` lines including header line.
     * @param limit
     *      the number of lines to be returned
     * @return
     *      the first `limit` lines
     */
    public List<String> preview(int limit) {
        return preview(limit, false);
    }

    /**
     * Returns first `limit` lines.
     *
     * @param limit
     *      the number of lines to be returned
     * @param noHeaderLine
     *      if `false` then header line will be excluded in the return list
     * @return
     *      the first `limit` lines.
     */
    public List<String> preview(int limit, boolean noHeaderLine) {
        E.illegalArgumentIf(limit < 1, "limit must be positive integer");
        return fetch(noHeaderLine ? 1 : 0, limit);
    }

    /**
     * Returns the line specified by `lineNumber`.
     *
     * Note the `lineNumber` starts with `0`.
     *
     * @param lineNumber
     *      specify the line to be returned.
     * @return
     *      the line as described above.
     */
    public String fetch(int lineNumber) {
        E.illegalArgumentIf(lineNumber < 0, "line number must not be negative number: " + lineNumber);
        E.illegalArgumentIf(lineNumber >= lines(), "line number is out of range: " + lineNumber);
        List<String> list = fetch(lineNumber, 1);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Returns a number of lines specified by start position `offset` and `limit`.
     *
     * @param offset
     *      the start line number (`0` based)
     * @param limit
     *      the number of lines to be returned.
     * @return
     *      a number of lines as specified.
     */
    public List<String> fetch(int offset, int limit) {
        E.illegalArgumentIf(offset < 0, "offset must not be negative number");
        E.illegalArgumentIf(offset >= lines(), "offset is out of range: " + offset);
        E.illegalArgumentIf(limit < 1, "limit must be at least 1");
        BufferedReader reader = IO.buffered(IO.reader(file));
        try {
            for (int i = 0; i < offset; ++i) {
                if (null == reader.readLine()) {
                    break;
                }
            }
        } catch (IOException e) {
            throw E.ioException(e);
        }
        List<String> lines = new ArrayList<>();
        try {
            for (int i = 0; i < limit; ++i) {
                String line = reader.readLine();
                if (null == line) {
                    break;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            throw E.ioException(e);
        }
        return lines;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {

            int cursor;

            @Override
            public boolean hasNext() {
                return cursor < lines();
            }

            @Override
            public String next() {
                return fetch(cursor++);
            }

            @Override
            public void remove() {
                throw E.unsupport();
            }
        };
    }

    // see https://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java
    private long countLines() {
        InputStream is = IO.buffered(IO.inputStream(file));
        try {
            byte[] c = new byte[1024];

            int readChars = is.read(c);
            if (readChars == -1) {
                // bail out if nothing to read
                return 0;
            }

            // make it easy for the optimizer to tune this loop
            long count = 0;
            while (readChars == 1024) {
                for (int i = 0; i < 1024; ) {
                    if (c[i++] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            // count remaining characters
            while (readChars != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            return count == 0 ? 1 : count;
        } catch (IOException e) {
            throw E.ioException(e);
        } finally {
            IO.close(is);
        }
    }

    public static void main(String[] args) {
        BigLines bigLines = new BigLines(new File("/tmp/1.csv"));
        System.out.println(bigLines.lines());

        System.out.println(bigLines.firstLine());

        List<String> lines = bigLines.fetch(555554, 2);
        System.out.println(S.join("\n", lines));

        bigLines = new BigLines(new File("/tmp/2.txt"));
        System.out.println(bigLines.lines());
        for (String line : bigLines) {
            System.out.println(line);
        }
    }
}
