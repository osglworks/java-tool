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

import benchmark.CopyBenchmarkModels.Foo;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.dozer.DozerBeanMapper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgl.$;
import org.osgl.BenchmarkBase;

@BenchmarkOptions(warmupRounds = 100 * 100 * 10, benchmarkRounds = 100 * 100 * 50)
public class DeepCopyBenchmark extends BenchmarkBase {

    private Foo source = new Foo();
    private Foo target = new Foo();

    private static MapperFactory mapperFactory;
    private static BoundMapperFacade<Foo, Foo> orika;
    private static DozerBeanMapper dozer = new DozerBeanMapper();

    @BeforeClass
    public static void init() {
        mapperFactory = new DefaultMapperFactory.Builder().build();
        orika = mapperFactory.getMapperFacade(Foo.class, Foo.class);
    }

    @Test
    public void dozer() {
        dozer.map(source, target);
    }


    @Test
    public void osgl() {
        //for (int i = 0; i < 100 * 100 * 100; ++i)
        $.deepCopy(source).to(target);
    }

    @Test
    public void orika() {
        orika.map(source, target);
    }

}
