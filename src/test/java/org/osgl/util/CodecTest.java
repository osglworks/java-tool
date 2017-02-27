package org.osgl.util;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.osgl.TestBase;

public class CodecTest extends TestBase {

    @Test
    public void test() {
        String s = S.random(20);
        byte[] ba = s.getBytes(Charsets.UTF_8);
        String hexStr = Codec.byteToHexString(ba);
        eq(String.valueOf(Hex.encodeHex(ba, false)), hexStr);
        eq(s, new String(Codec.hexStringToByte(hexStr)));
    }

}
