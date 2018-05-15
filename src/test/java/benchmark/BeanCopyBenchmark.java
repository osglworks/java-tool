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

import com.baidu.unbiz.easymapper.MapperFactory;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;
import org.osgl.$;
import org.osgl.BenchmarkBase;
import org.osgl.util.C;
import org.osgl.util.N;
import org.osgl.util.S;

import java.util.Map;

@BenchmarkOptions(warmupRounds = 10000 * 5, benchmarkRounds = 10000 * 10 * 5)
public class BeanCopyBenchmark extends BenchmarkBase {

    public static class Bar {
        private boolean flag = $.random(true, false);
        private double d = N.randDouble();
        private int[] ia = $.copy(C.range(0, 30)).to(new int[30]);

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
    }

    public static class Foo {
        private int id = N.randInt();
        private String name = S.random();
        private Map<String, Bar> map = C.Map("a", new Bar(), "b", new Bar());

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
    }

    private Foo source = new Foo();
    private Foo target = new Foo();

    @Test
    public void testOsglCopy() {
        $.copy(source).to(target);
    }

    @Test
    public void testBeanUtilsCopy() throws Exception {
        BeanUtils.copyProperties(target, source);
    }

    //@Test
    public void testOsglDeepCopy() {
        $.deepCopy(source).to(target);
    }

    @Test
    public void testEasyMapper() {
        MapperFactory.getCopyByRefMapper().mapClass(Foo.class, Foo.class).registerAndMap(source, target);
    }
}
