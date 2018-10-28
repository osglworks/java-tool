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

import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import org.osgl.Lang;
import org.osgl.util.S;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.lang.reflect.Array;
import java.util.*;

public class JsonObjectToXmlDocument extends Lang.TypeConverter<JSONObject, Document> {
    @Override
    public Document convert(JSONObject json) {
        Node root;
        DocumentImpl doc = new DocumentImpl();
        int sz = json.size();
        if (sz == 0) {
            return doc;
        } else {
            root = doc.createElement("root");
            doc.appendChild(root);
            append(root, json, "root", doc);
        }
        return doc;
    }

    private void append(Node parent, Object value, String key, Document doc) {
        if (null == value) {
            return;
        }
        if (value instanceof Map) {
            Map<String, Object> map = (Map) value;
            append(parent, map, doc);
        } else if (value instanceof List) {
            List list = (List) value;
            append(parent, list, key, doc);
        } else {
            if (value.getClass().isArray()) {
                List list = new ArrayList();
                int len = Array.getLength(value);
                for (int i = 0; i < len; ++i) {
                    list.add(Array.get(value, i));
                }
                append(parent, list, key, doc);
            } else {
                Node node = doc.createElement(key);
                parent.appendChild(node);
                node.appendChild(doc.createTextNode(S.string(value)));
            }
        }
    }

    private void append(Node parent, Map<String, Object> map, Document doc) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            append(parent, entry.getValue(), entry.getKey(), doc);
        }
    }

    private void append(Node parent, List list, String key, Document doc) {
        for (Object o: list) {
            Node node = doc.createElement(key);
            append(node, o, key, doc);
            parent.appendChild(node);
        }
    }
}
