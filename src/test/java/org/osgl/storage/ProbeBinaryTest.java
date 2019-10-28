package org.osgl.storage;

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
