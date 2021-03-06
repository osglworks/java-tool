package org.osgl.util;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2019 OSGL (Open Source General Library)
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultSetConverter {

    public static <T> List<T> convert(ResultSet rs, Class<T> listElementType) {
        return convert(rs, listElementType, null);
    }

    public static <T> List<T> convert(ResultSet rs, Class<T> listElementType, Map<String, String> specialMaps) {
        try {
            ResultSetRecordConverter<T> rsrc = new ResultSetRecordConverter<>(rs, listElementType, specialMaps);
            List<T> list = new ArrayList<>();
            while (rs.next()) {
                T record = rsrc.doConvert();
                list.add(record);
            }
            return list;
        } catch (SQLException e) {
            throw E.sqlException(e);
        }
    }

}
