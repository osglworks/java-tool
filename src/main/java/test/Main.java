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

import org.osgl.util.S;

public class Main {


    public static void main(String[] args) throws Exception {
        final String s = "Hello World";
        foo(s);
        System.gc();
        System.out.println("Press any key to continue");
        System.in.read();

        System.out.println(foo(s));
        System.out.println("Press any key to continue2");
        System.in.read();

        System.out.println(foo(s));
        System.out.println("Press any key to exit");
        System.in.read();
    }

    private static long foo(String s) {
        long count = 0L;
        for (int i = 0; i < 1000 * 1000; ++i) {
            if (S.is(s).startsWith("Hello")) {
                count++;
            }
        }
        return count;
    }

}
