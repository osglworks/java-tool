package org.osgl.util;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2017 OSGL (Open Source General Library)
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.osgl.$;
import org.osgl.TestBase;
import org.osgl.util.converter.XmlToJson;
import org.w3c.dom.Document;

public class XMLTest extends TestBase {

    @Test
    public void testConvertValueOnly() {
        String s = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><xml>x</xml>";
        Document doc = XML.read(s);
        JSONObject json = $.convert(doc).to(JSONObject.class);
        yes(json.isEmpty());
    }

    @Test
    public void testConvertXmlToJsonSimple() {
        String s = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><xml><name>x</name><id>5</id></xml>";
        Document doc = XML.read(s);
        JSONObject json = $.convert(doc).to(JSONObject.class);
        eq("x", json.getString("name"));
        eq(5, json.getInteger("id"));
    }

    @Test
    public void testConvertXmlToJsonWithAttributes() {
        String s = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><xml><id len=\"3\">5</id></xml>";
        Document doc = XML.read(s);
        JSONObject json = $.convert(doc).to(JSONObject.class);
        JSONObject id = json.getJSONObject("id");
        eq(3, id.getInteger("len"));
        eq(5, id.getInteger(XmlToJson.INNER_VALUE));
    }

    @Test
    public void testConvertXmlToJsonEhCacheConfigMergeMap() {
        String s = loadFileAsString("ehcache.xml");
        Document doc = XML.read(s);
        JSONObject json = $.convert(doc).hint(XmlToJson.HINT_MERGE_MAP).to(JSONObject.class);
        notNull(json);
        JSONObject cache = json.getJSONObject("cache");
        eq(8, cache.size());
        JSONArray timeToLiveSeconds = cache.getJSONArray("timeToLiveSeconds");
        eq(45, timeToLiveSeconds.size());
        eq(20, timeToLiveSeconds.getInteger(0));
        eq("charityCache", cache.getJSONArray("name").getString(0));
        JSONObject persistence = cache.getJSONObject("persistence");
        JSONArray strategy = persistence.getJSONArray("strategy");
        eq(45, strategy.size());
        eq("none", strategy.getString(0));
    }

    @Test
    public void testConvertXmlToJsonEhCacheConfigNoMerge() {
        String s = loadFileAsString("ehcache.xml");
        Document doc = XML.read(s);
        JSONObject json = $.convert(doc).to(JSONObject.class);
        notNull(json);
        JSONArray caches = json.getJSONArray("cache");
        eq(45, caches.size());
        JSONObject obj = caches.getJSONObject(0);
        eq(8, obj.size());
        eq(20, obj.getInteger("timeToLiveSeconds"));
        no(obj.getBoolean("eternal"));
        eq("off", obj.getString("transactionalMode"));
        JSONObject persistence = obj.getJSONObject("persistence");
        notNull(persistence);
        eq("none", persistence.getString("strategy"));
    }

    @Test
    public void testCase1_1() {
        test("1_1");
    }

    @Test
    public void testCase1_2() {
        test("1_2");
    }

    @Test
    public void testCase1_3() {
        test("1_3");
    }

    @Test
    public void testCase2_1() {
        test("2_1");
    }

    @Test
    public void testCase3_1() {
        test("3_1");
    }

    private void test(String caseName) {
        test(caseName, 0);
        test(caseName, XmlToJson.HINT_MERGE_MAP);
    }

    private void test(String caseName, int hint) {
        String xmlFile = "xml_json/" + caseName + ".xml";
        String jsonFile = "xml_json/" + caseName + suffix(hint) + ".json";
        Document doc = XML.read(loadFileAsString(xmlFile));
        JSONObject json = JSON.parseObject(loadFileAsString(jsonFile));
        eq(json, $.convert(doc).hint(hint).to(JSONObject.class));
    }

    private static String suffix(int hint) {
        return XmlToJson.doMergeMap(hint) ? "_merge_map" : "";
    }
}
