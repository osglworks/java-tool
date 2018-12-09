package org.osgl.util.converter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import org.osgl.util.S;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.lang.reflect.Array;
import java.util.*;

public class JsonXmlConvertHint {
    public String rootTag;
    public String listItemTag;

    public JsonXmlConvertHint() {
        this.rootTag = "xml";
        this.listItemTag = "_item";
    }

    public JsonXmlConvertHint(String rootTag) {
        this(rootTag, "_item");
    }

    public JsonXmlConvertHint(String rootTag, String listItemTag) {
        this.rootTag = S.requireNotBlank(rootTag).trim();
        this.listItemTag = listItemTag;
    }

    static Document convert(JSONObject json, String rootTag, String listItemTag) {
        Node root;
        DocumentImpl doc = new DocumentImpl();
        int sz = json.size();
        if (sz == 0) {
            return doc;
        } else {
            root = doc.createElement(rootTag);
            doc.appendChild(root);
            append(root, json, listItemTag, doc);
        }
        return doc;
    }

    static Document convert(JSONArray jsonArray, String rootTag, String listItemTag) {
        Node root;
        DocumentImpl doc = new DocumentImpl();
        int sz = jsonArray.size();
        if (sz == 0) {
            return doc;
        } else {
            root = doc.createElement(rootTag);
            doc.appendChild(root);
            append(root, jsonArray, null, listItemTag, doc);
        }
        return doc;
    }

    static void append(Node parent, Object value, String key, String listItemTag, Document doc) {
        if (null == value) {
            return;
        }
        Node node = null == key ? parent : doc.createElement(key);
        if (parent != node) {
            parent.appendChild(node);
        }
        if (value instanceof Map) {
            Map<String, Object> map = (Map) value;
            append(node, map, listItemTag, doc);
        } else if (value instanceof List) {
            List list = (List) value;
            for (Object o : list) {
                if (null != listItemTag) {
                    Node listNode = doc.createElement(listItemTag);
                    node.appendChild(listNode);
                    append(listNode, o, null, listItemTag, doc);
                } else {
                    append(node, o, null, listItemTag, doc);
                }
            }
        } else {
            if (value.getClass().isArray()) {
                List list = new ArrayList();
                int len = Array.getLength(value);
                for (int i = 0; i < len; ++i) {
                    list.add(Array.get(value, i));
                }
                append(parent, list, key, listItemTag, doc);
            } else {
                node.appendChild(doc.createTextNode(S.string(value)));
            }
        }
    }

    static void append(Node parent, Map<String, Object> map, String listItemTag, Document doc) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            append(parent, entry.getValue(), entry.getKey(), listItemTag, doc);
        }
    }

    static void append(Node parent, List list, String key, String listItemTag, Document doc) {
        for (Object o: list) {
            Node node = doc.createElement(null == key ? listItemTag : key);
            append(node, o, key, listItemTag, doc);
            parent.appendChild(node);
        }
    }

}
