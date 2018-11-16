package benchmark;

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

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import org.junit.Test;
import org.osgl.BenchmarkBase;
import org.osgl.util.S;

@BenchmarkOptions(warmupRounds = 100 * 100, benchmarkRounds = 100 * 100 * 100 * 5)
public class PadLeadingZeroBenchmark extends BenchmarkBase {

    private static final int NUM = 329717635;

    @Test
    public void osgl() {
        S.padLeadingZero(NUM, 12);
    }

    @Test
    public void format() {
        String.format("%012d", NUM);
    }

}
