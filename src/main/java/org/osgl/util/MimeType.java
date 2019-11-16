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
        archive, audio, css, csv, doc, docx, excel, image, javascript, json, pdf,
        powerpoint, ppt, pptx, problem, text, video, word, xls, xlsx, xml, yaml;
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

    /**
     * Create an new MimeType with traits and type of this MimeType instance and associate
     * it with a fileExtension.
     *
     * If the fileExtension is already registered, then an {@link IllegalArgumentException}
     * will be thrown out.
     *
     * @param fileExtension the file extension to be associated with the new MimeType instance
     * @return the new MimeType instance.
     */
    public MimeType createAlias(String fileExtension) {
        MimeType mimeType = indexByFileExtension.get(fileExtension);
        E.illegalArgumentIf(null != mimeType, "file extension already reig");
        mimeType = newInstance(fileExtension);
        indexByFileExtension.put(fileExtension, mimeType);
        return mimeType;
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

    private MimeType() {}
    private MimeType newInstance(String fileExtension) {
        MimeType newInstance = new MimeType();
        newInstance.fileExtension = S.requireNotBlank(fileExtension);
        newInstance.type = this.type;
        newInstance.traits = this.traits;
        return newInstance;
    }

    static {
        init();
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

    private static void init() {
        for (Trait trait : Trait.values()) {
            traitMap.put(trait.name(), trait);
        }
        List<String> lines = IO.read(MimeType.class.getResource("/org/osgl/mime-types2.properties")).toLines();
        load(lines);
    }

    private static void load(List<String> lines) {
        List<String> lowPriorityLines = new ArrayList<>();
        for (String line : lines) {
            boolean parsed = parse(line, true);
            if (!parsed) {
                lowPriorityLines.add(line);
            }
        }
        for (String line : lowPriorityLines) {
            parse(line, false);
        }
    }

    private static boolean parse(String line, boolean first) {
        if (line.startsWith("#")) {
            return true;
        }
        boolean hasDecoration = line.contains("+");
        if (hasDecoration && first) {
            return false;
        }
        S.Pair pair = S.binarySplit(line, '=');
        String fileExtension = pair.left();
        if (fileExtension.contains(".")) {
            // process content type alias
            fileExtension = S.cut(fileExtension).beforeFirst(".");
            MimeType mimeType = indexByFileExtension.get(fileExtension);
            E.illegalStateIf(null == mimeType, "error parsing line: " + line);
            indexByContentType.put(pair.right(), mimeType);
            return true;
        }
        C.List<String> traits = C.newList();
        String type = pair.right();
        if (type.contains("|")) {
            pair = S.binarySplit(type, '|');
            type = pair.left();
            traits.addAll(S.fastSplit(pair.right(), ","));
        }
        pair = S.binarySplit(type, '/');
        String prefix = pair.left();
        String suffix = pair.right();
        Trait trait = traitMap.get(prefix);
        if (null != trait) {
            traits.add(trait.name());
        }
        // treat the case like `problem+json`
        if (hasDecoration) {
            pair = S.binarySplit(suffix, '+');
            String decorator = pair.left(); // e.g. problem
            trait = traitMap.get(decorator);
            if (null != trait) {
                traits.add(trait.name());
            }
            String realType = pair.right();
            String originalType = S.concat(prefix, "/", realType);
            MimeType mimeType = indexByContentType.get(originalType);
            if (null != mimeType) {
                for (Trait element : mimeType.traits) {
                    traits.add(element.name());
                }
            } else {
                trait = traitMap.get(realType);
                if (null != trait) {
                    traits.add(trait.name());
                }
            }
        } else {
            trait = traitMap.get(suffix);
            if (null != trait) {
                traits.add(trait.name());
            }
        }
        MimeType mimeType = indexByContentType.get(type);
        if (null == mimeType) {
            mimeType = new MimeType(fileExtension, type, traits.map(new $.Transformer<String, Trait>() {
                @Override
                public Trait transform(String s) {
                    return Trait.valueOf(s);
                }
            }));
            if (mimeType.test(Trait.xls) || mimeType.test(Trait.xlsx)) {
                mimeType.traits.add(Trait.excel);
            } else if (mimeType.test(Trait.ppt) || mimeType.test(Trait.pptx)) {
                mimeType.traits.add(Trait.powerpoint);
            } else if (mimeType.test(Trait.doc) || mimeType.test(Trait.docx)) {
                mimeType.traits.add(Trait.word);
            } else if (mimeType.test(Trait.xml)) {
                mimeType.traits.add(Trait.text);
            }
            indexByContentType.put(type, mimeType);
        } else if (S.neq(fileExtension, mimeType.fileExtension)) {
            mimeType = mimeType.newInstance(fileExtension);
        }
        indexByFileExtension.put(fileExtension, mimeType);
        return true;
    }
}
