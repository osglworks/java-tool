package org.osgl.issues;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2019 OSGL (Open Source General Library)
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

import org.junit.Test;
import org.osgl.TestBase;
import org.osgl.util.XML;
import org.w3c.dom.Document;

public class Gh203 extends TestBase {

    @Test
    public void test() {
        String s = "<xml><ToUserName><![CDATA[gh_703c945c39bf]]></ToUserName> <FromUserName><![CDATA[oi-Xb5qca0FuQcsIdZcQhFVfyQvI]]></FromUserName> <CreateTime>1555907601</CreateTime> <MsgType><![CDATA[voice]]></MsgType> <MediaId><![CDATA[xfm3BIdKtzCI8hZc_vERjggM6sI3nFYgNbK3hNL6686kDm3pE_v3ElfXmXwCME4l]]></MediaId> <Format><![CDATA[amr]]></Format> <MsgId>6682572261892816896</MsgId> <Recognition><![CDATA[今天天气不错。]]></Recognition> </xml>";
        Document doc = XML.read(s);
        eq("oi-Xb5qca0FuQcsIdZcQhFVfyQvI", doc.getElementsByTagName("FromUserName").item(0).getTextContent());
    }

}
