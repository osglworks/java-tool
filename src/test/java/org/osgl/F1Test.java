package org.osgl;

import org.junit.Before;
import org.junit.Test;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.S;

public class F1Test extends TestBase {

    private $.F1<String, String> toLowerCase = new $.F1<String, String>() {
        @Override
        public String apply(String s) throws NotAppliedException, $.Break {
            return s.toLowerCase();
        }
    };

    private $.F1<String, String> toUpperCase = new $.F1<String, String>() {
        @Override
        public String apply(String s) throws NotAppliedException, $.Break {
            return s.toUpperCase();
        }
    };

    private $.F1<String, String> inCaseFailed = new $.F1<String, String>() {
        @Override
        public String apply(String s) throws NotAppliedException, $.Break {
            return "failed";
        }
    };

    private String randStr;
    private int randStrLen;

    @Before
    public void prepare() {
        randStr = newRandStr();
        randStrLen = randStr.length();
    }

    @Test(expected = NotAppliedException.class)
    public void byDefaultInverseShallThrowOutNotAppliedException() {
        toLowerCase.inverse();
    }

    @Test
    public void elseShallNotBeCalledNormally() {
        eq("foo", toLowerCase.applyOrElse("Foo", inCaseFailed));
        eq("foo", toLowerCase.orElse(inCaseFailed).apply("Foo"));
    }

    @Test
    public void elseShallBeCalledOnException() {
        eq("failed", toLowerCase.applyOrElse(null, inCaseFailed));
        eq("failed", toLowerCase.orElse(inCaseFailed).apply(null));
    }

    @Test
    public void testCurry() {
        eq(toLowerCase.apply(randStr), toLowerCase.curry(randStr).apply());
    }

    @Test
    public void testChainedStyleAndThen() {
        eq(randStrLen, toLowerCase.andThen(S.F.LENGTH).apply(randStr));
    }

    @Test
    public void lastFunctionWinForListStyleAndThen() {
        String s = "SomeWords";
        String expected = s.toUpperCase();
        eq(expected, toLowerCase.andThen(toUpperCase).apply(s));
    }

    @Test
    public void testComposeWithFunc0() {
        eq(randStrLen, S.F.LENGTH.compose(toLowerCase.curry(randStr)).apply());
    }

    @Test
    public void composeWithFunc1ShallBeReversedAndThen() {
        eq(S.F.LENGTH.compose(toLowerCase).apply(randStr), toLowerCase.andThen(S.F.LENGTH).apply(randStr));
    }

    @Test
    public void testComposeWithFunc2() {
        String randStr2 = newRandStr();
        $.Func2<String, String, String> concat = new $.Func2<String, String, String>() {
            @Override
            public String apply(String s, String s2) throws NotAppliedException, $.Break {
                return s + s2;
            }
        };
        eq(randStrLen + randStr2.length(), S.F.LENGTH.compose(concat).apply(randStr, randStr2));
    }

    @Test
    public void testComposeWithFunc3() {
        String randStr2 = newRandStr(), randStr3 = newRandStr();
        $.Func3<String, String, String, String> concat = new $.Func3<String, String, String, String>() {
            @Override
            public String apply(String s, String s2, String s3) throws NotAppliedException, $.Break {
                return s + s2 + s3;
            }
        };
        int expectedLen = randStrLen + randStr2.length() + randStr3.length();
        eq(expectedLen, S.F.LENGTH.compose(concat).apply(randStr, randStr2, randStr3));
    }

    @Test
    public void testComposeWithFunc4() {
        String randStr2 = newRandStr(), randStr3 = newRandStr(), randStr4 = newRandStr();
        $.Func4<String, String, String, String, String> concat = new $.Func4<String, String, String, String, String>() {
            @Override
            public String apply(String s, String s2, String s3, String s4) throws NotAppliedException, $.Break {
                return s + s2 + s3 + s4;
            }
        };
        int expectedLen = randStrLen + randStr2.length() + randStr3.length() + randStr4.length();
        eq(expectedLen, S.F.LENGTH.compose(concat).apply(randStr, randStr2, randStr3, randStr4));
    }

    @Test
    public void testComposeWithFunc5() {
        String randStr2 = newRandStr(), randStr3 = newRandStr(),
               randStr4 = newRandStr(), randStr5 = newRandStr();
        $.Func5<String, String, String, String, String, String> concat;
        concat = new $.Func5<String, String, String, String, String, String>() {
            @Override
            public String apply(String s, String s2, String s3, String s4, String s5)
            throws NotAppliedException, $.Break {
                return s + s2 + s3 + s4 + s5;
            }
        };
        int expectedLen = randStrLen + randStr2.length()
                + randStr3.length() + randStr4.length() + randStr5.length();
        eq(expectedLen, S.F.LENGTH.compose(concat).apply(randStr, randStr2, randStr3, randStr4, randStr5));
    }

    @Test
    public void liftedFunctionShallNotBeDefinedInCaseNotApplied() {
        yes(toLowerCase.lift().apply(null).notDefined());
    }

    @Test
    public void liftedFunctionShallBeDefinedInNormalCase() {
        yes(toLowerCase.lift().apply(randStr).isDefined());
    }
}
