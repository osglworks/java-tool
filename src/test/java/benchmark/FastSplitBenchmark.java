package benchmark;

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

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import org.junit.Test;
import org.osgl.BenchmarkBase;
import org.osgl.util.C;
import org.osgl.util.S;
import org.osgl.util.UtilTestBase;

@BenchmarkOptions(warmupRounds = 100 * 100 * 5, benchmarkRounds = 100 * 100 * 100 * 5)
public class FastSplitBenchmark extends BenchmarkBase {

    String toBeSplited = "abc**12345**abxyd**123425**asfadfa**9$$#sdd";

    @Test
    @BenchmarkOptions(warmupRounds = 1, benchmarkRounds = 1)
    public void testStringSplit() {
        String[] sa = toBeSplited.split("\\*\\*");
        yes(sa.length == 6);
    }

    @Test
    @BenchmarkOptions(warmupRounds = 1, benchmarkRounds = 1)
    public void testFastSplit() {
        S.List sl = S.fastSplit(toBeSplited, "**");
        UtilTestBase.eq(sl, C.listOf(toBeSplited.split("\\*\\*")));
    }

    @Test
    public void benchmarkStringSplit() {
        toBeSplited.split("\\*\\*");
    }

    @Test
    public void benchmarkFastSplit() {
        S.fastSplit(toBeSplited, "**");
    }

}
