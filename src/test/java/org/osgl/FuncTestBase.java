package org.osgl;

import org.junit.Before;
import org.junit.Ignore;
import org.osgl.util.N;
import org.osgl.util.S;

@Ignore
public class FuncTestBase extends TestBase {

    protected final double RAND_1 = N.randDouble();
    protected final double RAND_2 = N.randDouble();
    protected final double ERROR_VAL = Double.MIN_VALUE;
    protected final $.F1<Number, Number> NEGATIVE = N.F.NEGATIVE;
    protected String rs1, rs2, rs3, rs4, rs5;

    @Before
    public void setup() {
        rs1 = S.random();
        rs2 = S.random();
        rs3 = S.random();
        rs4 = S.random();
        rs5 = S.random();
    }

}
