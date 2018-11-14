package org.osgl.issues.gh181;

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

import org.osgl.util.Generics;

import java.lang.reflect.Type;
import java.util.List;

public class DaoBase<ID_TYPE, MODEL_TYPE> implements Dao<ID_TYPE, MODEL_TYPE> {

    public Type modelType;
    public Class<MODEL_TYPE> modelClass;
    public Type idType;
    public Class<ID_TYPE> idClass;

    public DaoBase() {
        exploreTypes();
    }

    private void exploreTypes() {
        List<Type> types = Generics.typeParamImplementations(getClass(), DaoBase.class);
        int sz = types.size();
        if (sz < 1) {
            return;
        }
        if (sz > 1) {
            modelType = types.get(1);
            modelClass = Generics.classOf(modelType);
        }
        idType = types.get(0);
        idClass = Generics.classOf(idType);
    }

}
