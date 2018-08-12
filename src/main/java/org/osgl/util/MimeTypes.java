package org.osgl.util;

/*-
 * #%L
 * Java Storage Service
 * %%
 * Copyright (C) 2013 - 2017 OSGL (Open Source General Library)
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

import java.io.File;
import java.util.Map;
import java.util.Properties;
import javax.activation.MimetypesFileTypeMap;

/**
 * This class is deprecated, alternative is {@link MimeType}
 */
@Deprecated
public class MimeTypes {

    public static final String BMP = "image/bmp";
    public static final String PDF = "application/pdf";
    public static final String PNG = "image/png";

    // some common types that are missing from java activation utils
    private static Map<String, String> commonMimeTypes = C.Map(
            "pdf", "application/pdf",
            "png", "image/png",
            "bmp", "image/bmp"
    );
    private static MimetypesFileTypeMap activationMimeTypes = new MimetypesFileTypeMap();
    private static Properties mimeTypes;
    static {
        // TODO - use osgl-tool built in mime types when update to osgl-tool-2.x
        final String OSGL_HTTP_MIME_TYPES = "/org/osgl/mime-types.properties";
        try {
            mimeTypes = IO.loadProperties(
                    IO.is(MimeTypes.class.getResource(OSGL_HTTP_MIME_TYPES)));
        } catch (Exception e) {
            mimeTypes = new Properties();
        }
    }

    public static String mimeType(File file) {
        return mimeType(file.getName());
    }

    public static String mimeType(String fileName) {
        String mimeType = commonMimeTypes.get(S.fileExtension(fileName));
        if (null != mimeType) {
            return mimeType;
        }
        mimeType = mimeTypes.getProperty(S.afterLast(fileName, "."));
        return null != mimeType ? mimeType : activationMimeTypes.getContentType(fileName);
    }
}
