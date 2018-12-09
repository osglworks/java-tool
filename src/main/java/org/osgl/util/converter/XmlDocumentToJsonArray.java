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
import org.osgl.*;
import org.osgl.util.IO;
import org.osgl.util.XML;
import org.w3c.dom.Document;

import java.io.StringWriter;

public class XmlDocumentToJsonArray extends Lang.TypeConverter<Document, JSONArray> {
    @Override
    public JSONArray convert(Document document) {
        return convert(document, null);
    }

    @Override
    public JSONArray convert(Document document, Object hint) {
        String rootTag = OsglConfig.xmlRootTag();
        String listItemTag = OsglConfig.xmlListItemTag();
        if (hint instanceof JsonXmlConvertHint) {
            JsonXmlConvertHint hint1 = $.cast(hint);
            rootTag = hint1.rootTag;
            listItemTag = hint1.listItemTag;
        } else if (hint instanceof String) {
            rootTag = $.cast(hint);
        }
        return (JSONArray) XmlDocumentToJsonUtil.convert(document, rootTag, listItemTag, true);
    }

    private static void foo() {
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

    private static void wx() {
        String s = "<xml>\n" +
                "  <ToUserName><![CDATA[toUser]]></ToUserName>\n" +
                "  <FromUserName><![CDATA[fromUser]]></FromUserName>\n" +
                "  <CreateTime>`1348831860`</CreateTime>\n" +
                "  <MsgType><![CDATA[text]]></MsgType>\n" +
                "  <Content><![CDATA[this is a test]]></Content>\n" +
                "  <MsgId>`1234567890123456`</MsgId>\n" +
                "</xml>";
        Document document = XML.read(s);
        JSONObject json = $.convert(document).to(JSONObject.class);
        System.out.println(JSON.toJSONString(json, true));
    }

    public static void main(String[] args) {
        wx();
    }
}
