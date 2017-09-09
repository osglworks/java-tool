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

public class IntRange extends LazyRange<Integer> {

    public IntRange(int from, int to) {
        this(from, to, 1);
    }

    public IntRange(int from, int to, int stepLen) {
        super(from, to, N.F.intRangeStep(stepLen));
    }

    public N.IntRangeStep step() {
        return (N.IntRangeStep)super.step();
    }

    public int get(int id) {
        if (id < 0) {
            return step().times(-id).apply(to(), ordering);
        } else if (0 == id) {
            return from();
        } else {
            return step().times(id).apply(from(), -ordering);
        }
    }

    public static IntRange of(int from, int to) {
        return new IntRange(from, to);
    }

}
