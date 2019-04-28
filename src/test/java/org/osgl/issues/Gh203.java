package org.osgl.issues;

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
