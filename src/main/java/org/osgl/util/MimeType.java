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

import org.osgl.$;

import java.util.*;

public final class MimeType {

    private static Map<String, MimeType> indexByFileExtension = new HashMap<>();
    private static Map<String, MimeType> indexByContentType = new HashMap<>();
    private static Map<String, Trait> traitMap = new HashMap<>();

    public enum Trait {
        archive, audio, excel, image, pdf, powerpoint, text, video, word, xls, xlsx;
        public boolean test(MimeType mimeType) {
            return mimeType.test(this);
        }
    }

    private String fileExtension;
    private String type;
    private EnumSet<Trait> traits = EnumSet.noneOf(Trait.class);

    private MimeType(String fileExtension, String type, List<Trait> traitList) {
        this.fileExtension = fileExtension;
        this.type = type;
        this.traits.addAll(traitList);
    }

    @Override
    public String toString() {
        return type;
    }

    public String fileExtension() {
        return fileExtension;
    }

    public String type() {
        return type;
    }

    public boolean test(Trait trait) {
        return traits.contains(trait);
    }

    public boolean test(String s) {
        if (fileExtension.equalsIgnoreCase(s)) {
            return true;
        }
        if (type.equalsIgnoreCase(s)) {
            return true;
        }
        Trait trait = traitMap.get(s);
        return null != trait;
    }

    public static MimeType findByFileExtension(String fileExtension) {
        return indexByFileExtension.get(fileExtension.trim().toLowerCase());
    }

    public static MimeType findByContentType(String contentType) {
        return indexByContentType.get(contentType.trim().toLowerCase());
    }

    public static List<MimeType> filterByTrait(Trait trait) {
        List<MimeType> mimeTypes = new ArrayList<>();
        for (MimeType mimeType : allMimeTypes()) {
            if (mimeType.test(trait)) {
                mimeTypes.add(mimeType);
            }
        }
        return mimeTypes;
    }

    public static Collection<MimeType> allMimeTypes() {
        return indexByFileExtension.values();
    }

    /**
     * Return a content type string corresponding to a given file extension suffix.
     *
     * If there is no MimeType corresponding to the file extension, then returns the file
     * extension string directly.
     *
     * @param fileExtension
     *      file extension suffix
     * @return
     *      A content type string corresponding to the file extension suffix
     *      or the file extension suffix itself if no corresponding mimetype found.
     */
    public static String typeOfSuffix(String fileExtension) {
        MimeType mimeType = indexByFileExtension.get(fileExtension);
        return null == mimeType ? fileExtension : mimeType.type;
    }

    static {
        init();
    }

    private static void init() {
        for (Trait trait : traitMap.values()) {
            traitMap.put(trait.name(), trait);
        }
        List<String> lines = IO.read(MimeType.class.getResource("/org/osgl/mime-types2.properties")).toLines();
        for (String line : lines) {
            S.Pair pair = S.binarySplit(line, '=');
            String fileExtension = pair.left();
            C.List<String> traits = C.newList();
            String type = pair.right();
            if (type.contains("|")) {
                pair = S.binarySplit(type, '|');
                type = pair.left();
                traits.addAll(S.fastSplit(pair.right(), ","));
            }
            String prefix = S.cut(type).before("/");
            Trait trait = traitMap.get(prefix);
            if (null != trait) {
                traits.add(trait.name());
            }
            MimeType mimeType = new MimeType(fileExtension, type, traits.map(new $.Transformer<String, Trait>() {
                @Override
                public Trait transform(String s) {
                    return Trait.valueOf(s);
                }
            }));
            indexByFileExtension.put(fileExtension, mimeType);
            indexByContentType.put(type, mimeType);
        }
    }
}
