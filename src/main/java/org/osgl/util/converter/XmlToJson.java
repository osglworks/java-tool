package org.osgl.util.converter;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2021 OSGL (Open Source General Library)
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
import org.osgl.Lang;
import org.osgl.util.S;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class XmlToJson extends Lang.TypeConverter<Document, JSONObject> {
    
    public static final int HINT_MERGE_MAP = 0x00001000;
    
    public static final String INNER_VALUE = "innerValue";

    @Override
    public JSONObject convert(Document document) {
        return convert(document, (Object)0);
    }

    @Override
    public JSONObject convert(Document document, Object hint) {
        Node root = rootNode(document);
        if (null == root) {
            return new JSONObject();
        }
        int theHint = hint instanceof Integer ? (int)hint : 0;
        return convert(root.getChildNodes(), new JSONObject(), theHint);
    }

    private JSONObject convert(NodeList nodeList, JSONObject bag, int hint) {
        int len = nodeList.getLength();
        for (int i = 0; i < len; ++i) {
            Node node = nodeList.item(i);
            merge(node, bag, hint);
        }
        return bag;
    }

    private void merge(Node node, JSONObject bag, int hint) {
        String nodeName = nameOf(node);
        Object existing = bag.get(nodeName);
        Object innerValue = convertNodeInner(node, hint);
        Object merged = merge(innerValue, existing, hint);
        if (null != merged) {
            bag.put(nodeName, merged);
        }
    }

    private Object convertNodeInner(Node node, int hint) {
        NodeList children = node.getChildNodes();
        NamedNodeMap attributes = node.getAttributes();
        boolean hasChild = null != children && children.getLength() > 0;
        boolean hasAttribute = null != attributes && attributes.getLength() > 0;
        if (!hasChild && !hasAttribute) {
            return null;
        }
        JSONObject convertedAttributes = convert(attributes);
        Object convertedChildren = convert(children, hint);
        if (null == convertedChildren) {
            return convertedAttributes;
        }
        if ($.bool(convertedAttributes)) {
            if (convertedChildren instanceof JSONObject) {
                convertedAttributes.putAll((JSONObject) convertedChildren);
            } else {
                convertedAttributes.put(INNER_VALUE, convertedChildren);
            }
            return convertedAttributes;
        }
        return convertedChildren;
    }

    private Object convert(NodeList children, int hint) {
        int len = null == children ? 0 : children.getLength();
        if (0 == len) {
            return null;
        }
        Object prev = null;
        for (int i = 0; i < len; ++i) {
            Object cur = convert(children.item(i), hint);
            prev = merge(cur, prev, hint);
        }
        return prev;
    }

    private Object convert(Node node, int hint) {
        switch (node.getNodeType()) {
            case Node.TEXT_NODE:
            case Node.CDATA_SECTION_NODE:
                return convert(node.getNodeValue());
            case Node.ELEMENT_NODE:
                JSONObject bag = new JSONObject();
                bag.put(node.getNodeName(), convertNodeInner(node, hint));
                return bag;
            default:
                return null;
        }
    }

    private JSONObject convert(NamedNodeMap attributes) {
        int len = null == attributes ? 0 : attributes.getLength();
        if (0 == len) {
            return null;
        }
        JSONObject bag = new JSONObject();
        for (int i = 0; i < len; ++i) {
            Node attribute = attributes.item(i);
            bag.put(attribute.getNodeName(), convert(attribute.getNodeValue()));
        }
        return bag;
    }

    private static Object merge(Object value, Object existing, int hint) {
        if (null == existing) {
            return value;
        } else if (null == value) {
            return existing;
        }
        if (doMergeMap(hint)) {
            if (value instanceof JSONObject) {
                if (existing instanceof JSONObject) {
                    return mergeMap((JSONObject) value, (JSONObject) existing);
                } else {
                    JSONObject map = (JSONObject) value;
                    Object innerValue = map.get(INNER_VALUE);
                    if (null == innerValue) {
                        innerValue = value;
                    } else {
                        if (innerValue instanceof JSONArray) {
                            ((JSONArray) innerValue).add(existing);
                        } else {
                            JSONArray array = new JSONArray();
                            array.add(existing);
                            array.add(innerValue);
                            innerValue = array;
                        }
                    }
                    if (innerValue instanceof JSONObject) {
                        map.putAll((JSONObject) innerValue);
                    } else {
                        map.put(INNER_VALUE, innerValue);
                    }
                    return map;
                }
            } else {
                if (existing instanceof JSONObject) {
                    JSONObject map = (JSONObject) existing;
                    Object innerValue = map.get(INNER_VALUE);
                    if (null == innerValue) {
                        innerValue = value;
                    } else {
                        if (innerValue instanceof JSONArray) {
                            ((JSONArray) innerValue).add(value);
                        } else {
                            JSONArray array = new JSONArray();
                            array.add(innerValue);
                            array.add(value);
                            innerValue = array;
                        }
                    }
                    if (innerValue instanceof JSONObject) {
                        map.putAll((JSONObject) innerValue);
                    } else {
                        map.put(INNER_VALUE, innerValue);
                    }
                    return map;
                } else {
                    if (existing instanceof JSONArray) {
                        ((JSONArray) existing).add(value);
                        return existing;
                    } else {
                        JSONArray array = new JSONArray();
                        array.add(existing);
                        array.add(value);
                        return array;
                    }
                }
            }
        } else {
            if (existing instanceof List) {
                ((List) existing).add(value);
                return existing;
            }
            List list = new JSONArray();
            list.add(existing);
            list.add(value);
            return list;
        }
    }

    private static JSONObject mergeMap(JSONObject value, JSONObject existing) {
        for (Map.Entry<String, Object> entry: value.entrySet()) {
            String key = entry.getKey();
            Object valueValue = entry.getValue();
            Object existingValue = existing.get(key);
            if (null != existingValue) {
                existing.put(key, merge(valueValue, existingValue, HINT_MERGE_MAP));
            } else {
                existing.put(key, valueValue);
            }
        }
        return existing;
    }

    private static String nameOf(Node node) {
        String name = node.getLocalName();
        if (null == name) {
            name = node.getNodeName();
        }
        return name;
    }

    private static Object convert(String s) {
        if (null == s) {
            return null;
        }
        s = s.trim();
        if ("".equals(s)) {
            return null;
        }
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
        } else if (isNumeric(s)) {
            return Double.parseDouble(s);
        } else {
            return s;
        }
    }

    private static boolean isNumeric(String s) {
        int len = s.length();
        if (len == 0) {
            return false;
        }
        int dotCount = 0;
        for (int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            if (dotCount < 1 && c == '.') {
                dotCount++;
                continue;
            }
            if (!isNumeric(c)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNumeric(char c) {
        return (c >= '0' && c <= '9');
    }

    private static Node rootNode(Document document) {
        NodeList children = document.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; --i) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                return node;
            }
        }
        return null;
    }

    public static boolean doMergeMap(int hint) {
        return (hint & HINT_MERGE_MAP) != 0;
    }

}
