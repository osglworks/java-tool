package test;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.osgl.$;
import org.osgl.util.C;
import org.osgl.util.XML;
import org.w3c.dom.Document;

public class Main {


    public static void main1(String[] args) throws Exception {
        String s = "## <a name=\"intro\"></a>1. 介绍";
        System.out.println(s.indexOf("<"));
        System.out.println(s.charAt(2));
        System.out.println(s.substring(12));
    }

    public static void main(String[] args) {
        JSONArray array = new JSONArray();
        array.add(C.Map("foo", 1));
        JSON json = array;
        Document document = $.convert(json).to(Document.class);
        System.out.println(XML.toString(document));
    }


}
