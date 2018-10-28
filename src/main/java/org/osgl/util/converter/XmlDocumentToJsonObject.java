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

import com.alibaba.fastjson.*;
import org.osgl.$;
import org.osgl.Lang;
import org.osgl.util.*;
import org.w3c.dom.*;

import java.io.StringWriter;
import java.util.List;

public class XmlDocumentToJsonObject extends Lang.TypeConverter<Document, JSONObject> {
    @Override
    public JSONObject convert(Document document) {
        Element element = document.getDocumentElement();
        String name = nameOf(element);
        JSONObject json = new JSONObject();
        json.put(name, convert(element));
        return json;
    }

    private Object convert(Node node) {
        switch (node.getNodeType()) {
            case Node.TEXT_NODE:
                return convert(node.getTextContent());
            case Node.ELEMENT_NODE:
                return convert(node.getChildNodes());
            default:
                return null;
        }
    }

    private Object convert(NodeList list) {
        JSONObject json = new JSONObject();
        int size = list.getLength();
        if (1 == size) {
            Node node = list.item(0);
            if (node.getNodeType() == Node.TEXT_NODE) {
                return convert(node.getTextContent());
            }
        }
        for (int i = 0; i < size; ++i) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            String name = nameOf(node);
            if (json.containsKey(name)) {
                List array;
                Object o = json.get(name);
                if (o instanceof List) {
                    array = (List)o;
                } else {
                    array = new JSONArray();
                    array.add(o);
                }
                array.add(convert(node));
                json.put(name, array);
            } else {
                json.put(name, convert(node));
            }
        }
        return json;
    }

    private Object convert(String s) {
        if ("true".equals(s)) {
            return Boolean.TRUE;
        } else if ("false".equals(s)) {
            return Boolean.FALSE;
        } else if (S.isInt(s)) {
            if (9 < s.length()) {
                return Long.parseLong(s);
            }
            return Integer.parseInt(s);
        } else if (S.isNumeric(s)) {
            return Double.parseDouble(s);
        } else {
            return s;
        }
    }

    private String nameOf(Node node) {
        String name = node.getLocalName();
        if (null == name) {
            name = node.getNodeName();
        }
        return name;
    }

    public static void main(String[] args) {
        String s = "<root>\n\t<foo>\n\t\t<name>x</name>\n<id>1</id></foo><foo><name>y</name><id>2</id></foo></root>";
        Document document = XML.read(s);
        StringWriter w = new StringWriter();
        IO.write(document).pretty().to(w);
        System.out.println(s);
        //System.out.println(w.toString());
        //System.out.println($.convert(document).hint(XML.HINT_PRETTY).toString());
        JSONObject json = $.convert(document).to(JSONObject.class);
        System.out.println(JSON.toJSONString(json, true));
        Document doc2 = $.convert(json).to(Document.class);
        System.out.println($.convert(doc2).hint(XML.HINT_PRETTY).toString());
    }
}
