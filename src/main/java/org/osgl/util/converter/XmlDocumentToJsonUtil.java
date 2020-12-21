package org.osgl.util.converter;

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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.E;
import org.osgl.util.S;
import org.w3c.dom.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class XmlDocumentToJsonUtil {

    static Object convert(Document document, String rootTag, String listItemTag, boolean array) {
        NodeList list = document.getChildNodes();
        if (0 == list.getLength()) {
            return array ? new JSONArray() : new JSONObject();
        }
        Node root = list.item(0);
        return convert(root.getChildNodes(), listItemTag);
    }

    static Object convert(Node node, String listItemTag) {
        switch (node.getNodeType()) {
            case Node.CDATA_SECTION_NODE:
            case Node.TEXT_NODE:
                return convert(node.getTextContent());
            case Node.ELEMENT_NODE:
                Object ret = convert(node.getChildNodes(), listItemTag);
                if (ret instanceof JSONObject) {
                    ((JSONObject) ret).putAll(convertAttributes(node));
                } else {
                    ((JSONArray) ret).add(C.Map("_attributes", convertAttributes(node)));
                }
                return ret;
            default:
                return null;
        }
    }

    static Map<String, String> convertAttributes(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        if (null == attributes) return C.EMPTY_MAP;
        Map<String, String> ret = new HashMap<>();
        for (int i = attributes.getLength() - 1; i >= 0; --i) {
            Node attribute = attributes.item(i);
            ret.put(attribute.getNodeName(), attribute.getNodeValue());
        }
        return ret;
    }

    private static Object convert(NodeList list, String listItemTag) {
        int size = list.getLength();
        if (1 == size) {
            Node node = list.item(0);
            short nodeType = node.getNodeType();
            if (nodeType == Node.TEXT_NODE || nodeType == Node.CDATA_SECTION_NODE) {
                return convert(node.getTextContent());
            }
        }
        JSONObject json = new JSONObject();
        List array = null;
        boolean retArray = false;
        for (int i = 0; i < size; ++i) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            String name = nameOf(node);
            if (listItemTag.equals(name)) {
                retArray = true;
                if (null == array) {
                    array = new JSONArray();
                }
                array.add(convert(node, listItemTag));
            } else if (json.containsKey(name)) {
                Object o = json.get(name);
                if (o instanceof List) {
                    array = (List)o;
                } else {
                    array = new JSONArray();
                    array.add(o);
                }
                array.add(convert(node, listItemTag));
                json.put(name, array);
            } else {
                json.put(name, convert(node, listItemTag));
            }
        }
        return retArray ? array : json;
    }

    static Object convert(String s) {
        if ("true".equals(s)) {
            return Boolean.TRUE;
        } else if ("false".equals(s)) {
            return Boolean.FALSE;
        } else if (S.isInt(s)) {
            if (9 >= s.length()) {
                return Integer.parseInt(s);
            } else if (19 >= s.length()) {
                long l = Long.parseLong(s);
                if (l <= Integer.MAX_VALUE) {
                    return (int)l;
                }
                return l;
            } else {
                try {
                    return Long.parseLong(s);
                } catch (NumberFormatException e) {
                    return new BigInteger(s);
                }
            }
        } else {
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                return s;
            }
        }
    }

    static String nameOf(Node node) {
        String name = node.getLocalName();
        if (null == name) {
            name = node.getNodeName();
        }
        return name;
    }

}
