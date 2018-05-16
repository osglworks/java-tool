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
import cn.hutool.core.bean.BeanUtil;
import com.baidu.unbiz.easymapper.Mapper;
import com.baidu.unbiz.easymapper.MapperFactory;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.Clock;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.modelmapper.ModelMapper;
import org.osgl.$;
import org.osgl.BenchmarkBase;

@BenchmarkOptions(warmupRounds = 100 * 100 * 5, benchmarkRounds = 100 * 100 * 100, clock = Clock.NANO_TIME)
public class ShallowCopyBenchmark extends BenchmarkBase {

    private static Foo source = new Foo();
    private static Foo target = new Foo();
    private static Foo clone;
    private static ModelMapper modelMapper = new ModelMapper();
    private static Mapper easyMapper;

    @BeforeClass
    public static void init() {
        easyMapper = MapperFactory.getCopyByRefMapper();
        easyMapper.mapClass(Foo.class, Foo.class).register();
    }


    @Test
    public void osgl() {
        //for (int i = 0; i < 100 * 100 * 100 * 10; ++i)
        $.copy(source).to(target);
    }

    @Test
    public void commonsBeanUtils() throws Exception {
        BeanUtils.copyProperties(target, source);
    }

    @Test
    public void commonsPropertyUtils() throws Exception {
        PropertyUtils.copyProperties(target, source);
    }

    @Test
    public void easyMapper() {
        easyMapper.map(source, target);
    }

    @Test
    public void modelMapper() {
        modelMapper.map(source, target);
    }

    @Test
    public void hutool() {
        BeanUtil.copyProperties(source, target);
    }

    @Test
    public void javaClone() throws Exception {
        clone = source.clone();
    }


}
