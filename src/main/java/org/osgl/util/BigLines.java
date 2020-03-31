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

import org.osgl.OsglConfig;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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

    public abstract static class LineReader {

        public abstract void read(String line, int lineNo);
        public abstract void batchFinished();
    }

    private File file;

    private volatile Integer lines;
    private String firstLine;
    private boolean iterateFirstLine;

    public BigLines(File file) {
        E.illegalArgumentIfNot(file.exists() && file.isFile() && file.canRead(), "file must exists and be a readable file: " + file);
        this.file = file;
    }

    public String getName() {
        return file.getName();
    }

    public boolean isEmpty() {
        return 0 == lines();
    }

    public String firstLine() {
        if (null == lines) {
            synchronized (this) {
                if (null == lines) {
                    if (lines() > 0) {
                        firstLine = fetch(0);
                    }
                }
            }
        }
        return firstLine;
    }

    public int lines() {
        if (null == lines) {
            synchronized (this) {
                if (null == lines) {
                    lines = countLines();
                }
            }
        }
        return lines;
    }

    public void setIterateFirstLine(boolean flag) {
        this.iterateFirstLine = flag;
    }

    /**
     * Returns first 5 lines including header line.
     */
    public List<String> preview() {
        return preview(5, false);
    }

    /**
     * Returns first `limit` lines including header line.
     *
     * @param limit
     *         the number of lines to be returned
     * @return the first `limit` lines
     */
    public List<String> preview(int limit) {
        return preview(limit, false);
    }

    /**
     * Returns first `limit` lines.
     *
     * @param limit
     *         the number of lines to be returned
     * @param noHeaderLine
     *         if `false` then header line will be excluded in the return list
     * @return the first `limit` lines.
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
     *         specify the line to be returned.
     * @return the line as described above.
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
     *         the start line number (`0` based)
     * @param limit
     *         the number of lines to be returned.
     * @return a number of lines as specified.
     */
    public List<String> fetch(int offset, int limit) {
        return fetch(offset, limit, new ArrayList<String>(limit));
    }

    private List<String> fetch(int offset, int limit, List<String> buf) {
        buf.clear();
        if (isEmpty()) {
            return buf;
        }
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
        try {
            for (int i = 0; i < limit; ++i) {
                String line = reader.readLine();
                if (null == line) {
                    break;
                }
                buf.add(line);
            }
        } catch (IOException e) {
            throw E.ioException(e);
        }
        return buf;
    }

    public List<String> fetchAround(int lineNumber, int before, int after) {
        int offset = lineNumber - before;
        int limit = after - before;
        return fetch(offset, limit);
    }

    public List<String> cherrypick(int[] index) {
        if (index.length < 1) {
            return C.list();
        }
        Arrays.sort(index);
        int len = index.length;
        BufferedReader reader = IO.buffered(IO.reader(file));
        List<String> lines = new ArrayList<>();
        try {
            int max = index[len - 1] + 1;
            for (int i = 0; i < max; ++i) {
                String line = reader.readLine();
                if (null == line) {
                    break;
                }
                if (Arrays.binarySearch(index, i) > -1) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            throw E.ioException(e);
        }
        return lines;
    }

    public List<String> sampling(int number) {
        E.illegalArgumentIf(number < 1, "sample number must be positive integer");
        if (number > 1100) {
            number = 1100;
        }
        int[] index = new int[number];
        Random r = ThreadLocalRandom.current();
        int max = (lines > (long) Integer.MAX_VALUE) ? Integer.MAX_VALUE : lines.intValue();
        for (int i = 0; i < number; ++i) {
            index[i] = 1 + r.nextInt(max - 1);
        }
        return cherrypick(index);
    }

    public void accept(LineReader lineReader) {
        if (lines < 100 * 100 * 10) {
            int lineNo = 0;
            BufferedReader reader = IO.buffered(IO.reader(file));
            int max = lines;
            try {
                for (int i = 0; i < max; ++i) {
                    String line = reader.readLine();
                    if (null == line) {
                        break;
                    }
                    if (0 == i && !iterateFirstLine) {
                        continue;
                    }
                    lineReader.read(line, lineNo++);
                }
                lineReader.batchFinished();
            } catch (IOException e) {
                throw E.ioException(e);
            }
        } else {
            int threads = ((lines / 100 * 100 * 10) + 1);
            threads = Math.min(threads, 20);

            Integer gap = (lines / threads) + 1;
            List<Thread> threadStore = new ArrayList<>();
            for (int i = 0; i < threads; ++i) {
                Thread t = new ReadThread(i * gap, gap, lineReader);
                threadStore.add(t);
                t.start();
            }
            for (Thread t : threadStore) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw E.unexpected(e);
                }
            }
        }
    }

    private class ReadThread extends Thread {

        private Integer offset;
        private Integer limit;
        private LineReader lineReader;

        public ReadThread(Integer offset, Integer limit, LineReader lineReader) {
            this.offset = offset;
            this.limit = limit;
            this.lineReader = lineReader;
        }

        @Override
        public void run() {
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
            try {
                int start = 0;
                int lineNo = start;
                for (int i = start; i < limit; ++i) {
                    String line = reader.readLine();
                    if (null == line) {
                        break;
                    }
                    if (0 == offset && 0 == i && !iterateFirstLine) {
                        continue;
                    }
                    lineReader.read(line, lineNo++);
                }
                lineReader.batchFinished();
            } catch (IOException e) {
                throw E.ioException(e);
            }
        }
    }

    class BigLinesIterator implements Iterator<String> {
        private int bufSize;
        private List<String> buf;
        private int offset;
        private int bufCursor;

        BigLinesIterator(int bufSize) {
            this.bufSize = bufSize;
            this.buf = fetch(offset, bufSize);
            this.offset = bufSize;
        }

        @Override
        public boolean hasNext() {
            return (offset - bufSize + bufCursor) < lines();
        }

        @Override
        public String next() {
            if (bufSize <= bufCursor) {
                fetch(this.offset, this.bufSize, this.buf);
                this.offset += this.bufSize;
                bufCursor = 0;
            }
            return buf.get(bufCursor++);
        }

        @Override
        public void remove() {
            throw E.unsupport();
        }
    }

    /**
     * This method is deprecated. Please use BigLines as an `Iterable` directly.
     */
    @Deprecated
    public Iterable<String> asIterable(int bufSize) {
        return this;
    }

    @Override
    public Iterator<String> iterator() {
        final BufferedReader br = IO.buffered(IO.reader(file));
        Iterator<String> iter = new Iterator<String>() {
            String nextLine = null;

            @Override
            public boolean hasNext() {
                if (nextLine != null) {
                    return true;
                } else {
                    try {
                        nextLine = br.readLine();
                        return (nextLine != null);
                    } catch (IOException e) {
                        throw E.ioException(e);
                    }
                }
            }

            @Override
            public String next() {
                if (nextLine != null || hasNext()) {
                    String line = nextLine;
                    nextLine = null;
                    return line;
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void remove() {
                throw E.unsupport();
            }
        };

        return iter;
    }

    // see https://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java
    private int countLines() {
        InputStream is = IO.buffered(IO.inputStream(file));
        try {
            byte[] c = new byte[1024];

            int readChars = is.read(c);
            if (readChars == -1) {
                // bail out if nothing to read
                return 0;
            }

            // make it easy for the optimizer to tune this loop
            int count = 0;
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

            return count;
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
