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

    private static Map<String, MimeType> indexByName = new HashMap<>();
    private static Map<String, MimeType> indexByContentType = new HashMap<>();
    private static Map<String, Trait> traitMap = new HashMap<>();

    public enum Trait {
        archive, audio, css, csv, doc, docx, excel, image, javascript, json, pdf,
        powerpoint, ppt, pptx, problem, text, video, word, xls, xlsx, xml, yaml;
        public boolean test(MimeType mimeType) {
            return mimeType.test(this);
        }
    }

    private String name;
    private String type;
    private EnumSet<Trait> traits = EnumSet.noneOf(Trait.class);

    private MimeType() {}

    private MimeType(String name, String type, List<Trait> traitList) {
        this.name = name.intern();
        this.type = type.intern();
        this.traits.addAll(traitList);
    }

    @Override
    public String toString() {
        return type;
    }

    /**
     * Return file extension of this MimeType.
     *
     * Note this method is obsolete, please use {@link #name() instead}
     *
     * @return file extension of this MimeType
     */
    @Deprecated
    public String fileExtension() {
        return name;
    }

    /**
     * Return name of this MimeType.
     * @return name of this MimeType
     */
    public String name() {
        return name;
    }

    public String type() {
        return type;
    }

    /**
     * Check if the mime type specified has the same {@link #type}
     * of this mime type.
     * @param mimeType the mime type to be test
     * @return `true` if the mime type has the same type with this mime type.
     */
    public boolean isSameTypeWith(MimeType mimeType) {
        return type == mimeType.type;
    }

    /**
     * Check if this mime type is same type of any one specified in the
     * var arg list.
     *
     * @param types a var arg list of mime types.
     * @return `true` if this mime type is same type of any one specified in the list.
     */
    public boolean isSameTypeWithAny(MimeType ... types) {
        for (MimeType type : types) {
            if (isSameTypeWith(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the mime type specified is an alias of this mime type.
     * Calling this method has the same effect with calling {@link #isSameType(MimeType)}.
     *
     * @param mimeType the mime type to be tested.
     * @return `true` if the mime type has the same type with this mime type.
     */
    public boolean isAlias(MimeType mimeType) {
        return type == mimeType.type;
    }

    /**
     * Create an new MimeType with traits and type of this MimeType instance and associate
     * it with an new name.
     *
     * If the name specified is already registered, then an {@link IllegalArgumentException}
     * will be thrown out.
     *
     * @param name the name to be associated with the new MimeType instance
     * @return the new MimeType instance.
     */
    public MimeType createAlias(String name) {
        MimeType mimeType = indexByName.get(name);
        E.illegalArgumentIf(null != mimeType, "name already reigistered");
        mimeType = newInstance(name);
        indexByName.put(name, mimeType);
        return mimeType;
    }

    /**
     * This method is deprecated. Please use {@link #hasTrait(Trait)} instead.
     * 
     * Check if this `MimeType` has the trait specified.
     *
     * @param trait the trait to test this mime type.
     * @return `true` if this mime type has the trait specified.
     */
    @Deprecated
    public boolean test(Trait trait) {
        return traits.contains(trait);
    }

    /**
     * Check if this `MimeType` has the trait specified.
     *
     * @param trait the trait to test this mime type.
     * @return `true` if this mime type has the trait specified.
     */
    public boolean hasTrait(Trait trait) {
        return traits.contains(trait);
    }

    /**
     * This method is deprecated. Please use {@link #matches(String)} instead
     *
     * Check if this `MimeType` matches a string specified.
     *
     * This method will
     *
     * - check if the string matches the name, if not then
     * - check if the string matches the type, if not then
     * - check if the string represent a trait and contained in this MimeType.
     *
     * @param s the string to be tested.
     * @return `true` if the `s` matches as per logic specified above.
     */
    public boolean test(String s) {
        if (name.equalsIgnoreCase(s)) {
            return true;
        }
        if (type.equalsIgnoreCase(s)) {
            return true;
        }
        Trait trait = traitMap.get(s);
        return null != trait;
    }

    /**
     * Check if this `MimeType` matches a string specified.
     *
     * This method will
     *
     * - check if the string matches the name, if not then
     * - check if the string matches the type, if not then
     * - check if the string represent a trait and contained in this MimeType.
     *
     * @param s the string to be tested.
     * @return `true` if the `s` matches as per logic specified above.
     */
    public boolean matches(String s) {
        if (name.equalsIgnoreCase(s)) {
            return true;
        }
        if (type.equalsIgnoreCase(s)) {
            return true;
        }
        Trait trait = traitMap.get(s);
        return null != trait;
    }

    private MimeType newInstance(String fileExtension) {
        MimeType newInstance = new MimeType();
        newInstance.name = fileExtension.intern();
        newInstance.type = this.type;
        newInstance.traits = this.traits;
        return newInstance;
    }

    static {
        init();
    }

    /**
     * This method is deprecated. Please use {@link #findByName(String)} instead.
     *
     * @param fileExtension the file extension.
     * @return the MimeType associated with the file extension (name)
     */
    @Deprecated
    public static MimeType findByFileExtension(String fileExtension) {
        return indexByName.get(fileExtension.trim().toLowerCase());
    }

    /**
     * Return a MimeType by name.
     * @param name the name to locate the MimeType.
     * @return the MimeType associated with the name specified.
     */
    public static MimeType findByName(String name) {
        return indexByName.get(name.trim().toLowerCase());
    }

    /**
     * Find MimeType by content type.
     * @param contentType the content type.
     * @return MimeType with the content type specified.
     */
    public static MimeType findByContentType(String contentType) {
        return indexByContentType.get(contentType.trim().toLowerCase());
    }

    /**
     * Get a list of `MimeType` with each item contains the trait specified.
     *
     * @param trait the trait to filter `MimeType` list
     * @return a list of `MimeType` matches the trait.
     */
    public static List<MimeType> filterByTrait(Trait trait) {
        List<MimeType> mimeTypes = new ArrayList<>();
        for (MimeType mimeType : allMimeTypes()) {
            if (mimeType.test(trait)) {
                mimeTypes.add(mimeType);
            }
        }
        return mimeTypes;
    }

    /**
     * Returns a collection of all managed mime types.
     * @return all managed mime types.
     */
    public static Collection<MimeType> allMimeTypes() {
        return indexByName.values();
    }

    /**
     * This method is deprecated. Please use {@link #typeOfName(String)} instead.
     *
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
    @Deprecated
    public static String typeOfSuffix(String fileExtension) {
        MimeType mimeType = indexByName.get(fileExtension);
        return null == mimeType ? fileExtension : mimeType.type;
    }

    /**
     * Return a content type string corresponding to a given name.
     *
     * If there is no MimeType corresponding to the name, then returns the name string directly.
     *
     * @param name the name
     * @return
     *      A content type string corresponding to the file extension suffix
     *      or the file extension suffix itself if no corresponding mimetype found.
     */
    public static String typeOfName(String name) {
        MimeType mimeType = indexByName.get(name);
        return null == mimeType ? name : mimeType.type;
    }

    /**
     * Register an new MimeType with name, contentType and traits.
     *
     * Note if there are existing MimeType associated with the name, the existing one will be
     * replaced.
     *
     * @param name the name of the new mime type
     * @param contentType the content type of the new mime type
     * @param traits the traits of the new mimetype
     */
    public static void registerMimeType(String name, String contentType, Trait ... traits) {
        MimeType mimeType = new MimeType(name, contentType, C.listOf(traits));
        indexByName.put(name, mimeType);
        if (!indexByContentType.containsKey(contentType)) {
            indexByContentType.put(contentType, mimeType);
        }
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
            MimeType mimeType = indexByName.get(fileExtension);
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
        } else if (S.neq(fileExtension, mimeType.name)) {
            mimeType = mimeType.newInstance(fileExtension);
        }
        indexByName.put(fileExtension, mimeType);
        return true;
    }
}
