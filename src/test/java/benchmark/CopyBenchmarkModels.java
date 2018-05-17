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

import org.osgl.util.C;
import org.osgl.util.N;

import java.io.Serializable;
import java.util.Map;

public class CopyBenchmarkModels {

    public static class Bar implements Cloneable, Serializable {
        private boolean flag = false;
        private double d = N.randDouble();
        private int[] ia = {N.randInt(), 0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public double getD() {
            return d;
        }

        public void setD(double d) {
            this.d = d;
        }

        public int[] getIa() {
            return ia;
        }

        public void setIa(int[] ia) {
            this.ia = ia;
        }

        @Override
        public Bar clone() throws CloneNotSupportedException {
            return (Bar) super.clone();
        }
    }

    public static class Foo implements Cloneable, Serializable {
        private int id = 273;
        private String name = "ABCDEFGHIJK";
        private Map<String, Bar> map = C.newMap("a", new Bar(), "b", new Bar());

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, Bar> getMap() {
            return map;
        }

        public void setMap(Map<String, Bar> map) {
            this.map = map;
        }

        @Override
        public Foo clone() throws CloneNotSupportedException {
            return (Foo) super.clone();
        }
    }

}
