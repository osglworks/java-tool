package org.osgl.util;

import org.junit.Test;
import org.osgl.TestBase;

public class MimeTypeTest extends TestBase {

    @Test
    public void test() {
        MimeType mimeType = MimeType.findByFileExtension("pdf");
        yes(null != mimeType && mimeType.test(MimeType.Trait.pdf));

        mimeType = MimeType.findByFileExtension("bz2");
        yes(null != mimeType && mimeType.test(MimeType.Trait.archive));
    }

}
