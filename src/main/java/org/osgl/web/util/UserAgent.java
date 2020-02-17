package org.osgl.web.util;

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

import org.omg.CORBA.UNKNOWN;
import org.osgl.util.S;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class UserAgent {

    public final static UserAgent UNKNOWN = new UserAgent();

    public static enum OS {
        MAC_OS, IOS, WIN32, WIN64, LINUX, DROID, SYMBIAN, BLACKBERRY, J2ME, SUN_OS, BOT, UNKNOWN
    }
    private OS os_ = null;
    public OS getOS() {
        return os_;
    }

    public static enum Device {
        IPHONE,
        IPAD,
        IPOD,
        DROID,
        DROID_TABLET,
        BLACKBERRY,
        SONYERICSSON,
        NOKIA,
        PC,
        MOBILE,
        BOT,
        UNKNOWN
    }

    private Device device_ = null;
    public Device getDevice() {
        return device_;
    }
    public final boolean is(Device device) {
        return (device_ == device);
    }

    public final boolean isMobile() {
        final Device[] da = {
            Device.IPHONE,
            Device.IPOD,
            Device.DROID,
            Device.BLACKBERRY,
            Device.SONYERICSSON,
            Device.NOKIA
        };
        for (Device d: da) {
            if (device_ == d) return true;
        }
        return false;
    }
    
    public final boolean isTablet() {
        final Device[] da = {
            Device.IPAD,
            Device.DROID_TABLET
        };
        for (Device d: da) {
            if (device_ == d) return true;
        }
        return false;
    }

    public static enum Browser {
        IE_6, IE_7, IE_8, IE_9, IE_10, IE_11, EDGE,
        CHROME, SAFARI, FIREFOX_3, FIREFOX, OPERA, UCWEB, BOT, UNKNOWN
    }

    private Browser browser_ = Browser.UNKNOWN;
    public final Browser getBrowser() {
        return browser_;
    }

    public final boolean isIE678() {
        Browser b = browser_;
        return Browser.IE_6 == b || Browser.IE_7 == b || Browser.IE_8 == b;
    }

    public final boolean isIE9Down() {
        Browser b = browser_;
        return Browser.IE_8 == b || Browser.IE_9 == b || Browser.IE_6 == b || Browser.IE_7 == b;
    }

    public final boolean isIE9Up() {
        Browser b = browser_;
        return Browser.IE_9 == b || Browser.IE_10 == b || Browser.IE_11 == b;
    }

    public final boolean isIE10Up() {
        Browser b = browser_;
        return Browser.IE_10 == b || Browser.IE_11 == b;
    }

    public final boolean isIE11Up() {
        Browser b = browser_;
        return Browser.IE_11 == b;
    }

    public final boolean isEdge() {
        Browser b = browser_;
        return Browser.EDGE == b;
    }

    public final boolean isIE() {
        return browser_.name().contains("IE");
    }

    public final boolean isFirefox3() {
        return browser_ == Browser.FIREFOX_3;
    }

    public final boolean isFirefox4Up() {
        return browser_ == Browser.FIREFOX && browser_ != Browser.FIREFOX_3;
    }

    public final boolean isFirefox() {
        return browser_.name().contains("FIREFOX");
    }

    public final boolean isOpera() {
        return browser_ == Browser.OPERA;
    }
    
    public final boolean isWebKit() {
        return str_.contains("WebKit");
    }
    
    public final boolean isSafari() {
        return browser_ == Browser.SAFARI;
    }

    public final boolean isChrome() {
        return browser_ == Browser.CHROME;
    }

    public final boolean  isUCWeb() {
        return browser_ == Browser.UCWEB;
    }

    private String str_;

    @Override
    public final String toString() {
        return str_;
    }

    private static Map<String, UserAgent> cache_ = new HashMap<String, UserAgent>();
    public static UserAgent parse(String userAgent) {
        if (S.empty(userAgent)) {
            return UserAgent.UNKNOWN;
        }
        UserAgent ua = cache_.get(userAgent);
        if (null != ua) return ua;
        ua = new UserAgent(userAgent);
        cache_.put(userAgent, ua);
        return ua;
    }

    /**
     * Construct the instance from http header: user-agent
     * @param userAgent
     */
    private UserAgent(String userAgent) {
        this();
        parse_(userAgent);
        str_ = userAgent;
    }

    private UserAgent() {
        os_ = OS.UNKNOWN;
        device_ = Device.UNKNOWN;
        browser_ = Browser.UNKNOWN;
        str_ = "";
    }

    private static enum P {
        /*
         * Note the sequence of the enum DOSE matter!
         */
        J2ME(Pattern.compile(".*(MIDP|J2ME|CLDC).*"), Device.MOBILE, null, OS.J2ME),
        UCWEB(Pattern.compile(".*UCWEB.*"), Device.MOBILE, Browser.UCWEB, null),
        WIN32(Pattern.compile(".*(Windows|W32).*"), Device.PC, null, OS.WIN32),
        WIN64(Pattern.compile(".*(WOW64|Win64).*"), Device.PC, null, OS.WIN64),
        LINUX(Pattern.compile(".*Linux.*"), null, null, OS.LINUX),
        MAC(Pattern.compile(".*Mac OS.*"), Device.PC, null, OS.MAC_OS),
        SOS(Pattern.compile(".*SunOS.*"), Device.PC, null, OS.SUN_OS),
        IPHONE(Pattern.compile(".*iPhone.*"), Device.IPHONE, Browser.SAFARI, OS.IOS),
        IPAD(Pattern.compile(".*iPad.*"), Device.IPAD, Browser.SAFARI, OS.IOS),
        IPOD(Pattern.compile(".*iPod.*"), Device.IPOD, Browser.SAFARI, OS.IOS),
        DROID_TABLET(Pattern.compile(".*Android.*"), Device.DROID_TABLET, null, OS.DROID),
        DROID_MOBILE(Pattern.compile(".*Android.*Mobile.*"), Device.DROID, null, OS.DROID),
        BLACKBERRY(Pattern.compile(".*BlackBerry.*"), Device.BLACKBERRY, null, OS.BLACKBERRY),
        SYMBIAN(Pattern.compile(".*Symbian.*", Pattern.CASE_INSENSITIVE), null, null, OS.SYMBIAN),
        SONYERICSSON(Pattern.compile(".*SonyEricsson.*"), Device.SONYERICSSON, null, null),
        NOKIA(Pattern.compile(".*Nokia.*", Pattern.CASE_INSENSITIVE), Device.NOKIA, null, null),
        IE6(Pattern.compile(".*MSIE\\s+[6]\\.0.*"), Device.PC, Browser.IE_6, null),
        IE7(Pattern.compile(".*MSIE\\s+[7]\\.0.*"), Device.PC, Browser.IE_7, null),
        IE8(Pattern.compile(".*MSIE\\s+[8]\\.0.*"), Device.PC, Browser.IE_8, null),
        IE9(Pattern.compile(".*MSIE\\s+(9)\\.0.*"), Device.PC, Browser.IE_9, null),
        IE10(Pattern.compile(".*MSIE\\s+(10)\\.0.*"), null, Browser.IE_10, null),
        IE11(Pattern.compile(".*Windows\\s+NT.+rv:(11|12)\\.0.*"), Device.PC, Browser.IE_11, null),
        FIREFOX(Pattern.compile(".*Firefox.*"), null, Browser.FIREFOX, null),
        FIREFOX3(Pattern.compile(".*Firefox/3.*"), null, Browser.FIREFOX_3, null),
        SAFARI(Pattern.compile(".*Safari.*"), null, Browser.SAFARI, null),
        CHROME(Pattern.compile(".*Chrome.*"), null, Browser.CHROME, null),
        EDGE(Pattern.compile(".*\\s+Edg\\/.*"), null, Browser.EDGE, null),
        OPERA(Pattern.compile(".*Opera.*"), null, Browser.OPERA, null),
        BOT(Pattern.compile(".*(Googlebot|msn-bot|msnbot|Bot|bot|Baiduspider|SeznamBot|facebookexternalhit).*", Pattern.CASE_INSENSITIVE), Device.BOT, Browser.BOT, OS.BOT);

        private final Pattern p_;
        private Device d_ = Device.UNKNOWN;
        private Browser b_;
        private OS o_ = OS.UNKNOWN;
        P(Pattern pattern, Device device, Browser browser, OS os) {
            p_ = pattern;
            d_ = device;
            b_ = browser;
            o_ = os;
        }
        boolean matches(String ua) {
            return p_.matcher(ua).matches();
        }
        void test(String str, UserAgent ua) {
            if (matches(str)) {
                if (null != d_) {
                    ua.device_ = d_;
                }

                if (null != b_) {
                    ua.browser_ = b_;
                }

                if (null != o_) {
                    ua.os_ = o_;
                }
            }
        }
    }

    private void parse_(String userAgent) {
        for (P p: P.values()) {
            p.test(userAgent, this);
        }
    }

    public static final String KEY = "__ua__";
    /**
     * Use valueOf instead
     * @param userAgent the useragent String
     * @return the {@code UserAgent} instance
     */
    @Deprecated
    public static final UserAgent set(String userAgent) {
        return valueOf(userAgent);
    }
    public static final UserAgent valueOf(String userAgent) {
        return UserAgent.parse(userAgent);
    }

    public static void main(String[] args) {
        String s = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.20 (KHTML, like Gecko) Chrome/11.0.669.0 Safari/534.20";
        UserAgent ua = valueOf(s);
        assert_(ua.getBrowser() == Browser.CHROME, "1");

        s = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.3; Zune 4.0)";
        ua = valueOf(s);
        assert_(!ua.is(Device.IPHONE), "4");
        assert_(ua.getBrowser() == Browser.IE_8, "4");

        s = "Mozilla/5.0 (Linux; U; Android 3.0; en-us; Xoom Build/HRI39) AppleWebKit/534.13 (KHTML, like Gecko) Version/4.0 Safari/534.13";
        ua = valueOf(s);
        assert_(ua.is(Device.DROID_TABLET), "2");
        assert_(ua.getBrowser() == Browser.SAFARI, "3");

        s = "Mozilla/5.0 (X11; U; Linux x86_64; fr; rv:1.9.2.3) Gecko/20100403 Fedora/3.6.3-4.fc13 Firefox/3.6.3";
        ua = valueOf(s);
        assert_(ua.isFirefox3(), "firefox 3");
        
        s = "Mozilla/5.0 (Linux; Android 4.1.1; C1504 Build/11.3.A.0.47) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.94 Mobile Safari/537.36";
        ua = valueOf(s);
        assert_(ua.is(Device.DROID), "droid mobile");
        assert_(ua.isMobile(), "mobile");
        
        s = "Mozilla/5.0 (Linux; Android 4.0.3; GT-P5110 Build/IML74K) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.123 Safari/537.22";
        ua = valueOf(s);
        assert_(ua.is(Device.DROID_TABLET), "droid tablet");
        assert_(ua.isTablet(), "tablet");

        s = "Mozilla/5.0 (Windows NT 6.1; Trident/7.0; rv:11.0) like Gecko";
        ua = valueOf(s);
        assert_(ua.isIE10Up(), "IE 10");

        s = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.74 Safari/537.36 Edg/79.0.309.43";
        ua = valueOf(s);
        assert_(ua.isEdge(), "Edge");
        assert_(ua.is(Device.PC), "pc");

        System.out.println("success!");
    }

    private static void assert_(boolean b, String reason) {
        if (!b) throw new RuntimeException("assert failed: " + reason);
    }
}
