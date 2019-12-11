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

import org.osgl.$;
import org.osgl.OsglConfig;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.sql.Types.*;

public class ResultSetRecordConverter<T> {

    private static Map<Class, List<Field>> classMetaInfoRepo = new HashMap<>();
    private Class<T> targetType;
    private ResultSet rs;
    private ResultSetMetaData rsMeta;
    private Map<String, String> specialMaps;
    private Map<String, String> reverseSpecialMaps;
    private static Map<ResultSetMetaData, Map<String, Integer>> resultSetMetaDataColumnNameLookup = new HashMap<>();

    public ResultSetRecordConverter(ResultSet rs, Class<T> targetType, Map<String, String> specialMaps) {
        this.targetType = $.requireNotNull(targetType);
        this.specialMaps = specialMaps;
        if (null != specialMaps) {
            this.reverseSpecialMaps = C.Map(specialMaps).flipped();
        }
        try {
            this.rsMeta = rs.getMetaData();
        } catch (SQLException e) {
            throw E.sqlException(e);
        }
        this.rs = rs;
    }

    private Map<String, Integer> columnNameLookup() throws SQLException {
        Map<String, Integer> lookup = resultSetMetaDataColumnNameLookup.get(rsMeta);
        if (null == lookup) {
            lookup = new HashMap<>();
            int n = rsMeta.getColumnCount();
            for (int i = 1; i <= n; ++i) {
                lookup.put(rsMeta.getColumnLabel(i), i);
            }
            resultSetMetaDataColumnNameLookup.put(rsMeta, lookup);
        }
        return lookup;
    }

    public T doConvert() {
        try {
            T entity = (T) OsglConfig.globalInstanceFactory().apply(targetType);
            if (Map.class.isAssignableFrom(targetType)) {
                return (T) convertToMap((Map) entity);
            } else if (AdaptiveMap.class.isAssignableFrom(targetType)) {
                return (T) convertToAdaptiveMap((AdaptiveMap) entity);
            } else {
                return (T) convertToEntity(entity);
            }
        } catch (SQLException e) {
            throw E.sqlException(e);
        }
    }

    private Map convertToMap(Map map) throws SQLException {
        int n = rsMeta.getColumnCount();
        for (int i = 1; i <= n; ++i) {
            String label = rsMeta.getColumnLabel(i);
            if (null != specialMaps) {
                String newLabel = specialMaps.get(label);
                if (null != newLabel) {
                    label = newLabel;
                }
            }
            map.put(label, getFieldValue(i));
        }
        return map;
    }

    private AdaptiveMap convertToAdaptiveMap(AdaptiveMap map) throws SQLException {
        int n = rsMeta.getColumnCount();
        for (int i = 1; i <= n; ++i) {
            String label = rsMeta.getColumnLabel(i);
            if (null != specialMaps) {
                String newLabel = specialMaps.get(label);
                if (null != newLabel) {
                    label = newLabel;
                }
            }
            map.putValue(label, getFieldValue(i));
        }
        return map;
    }

    private Object convertToEntity(Object entity) throws SQLException {
        List<Field> fields = classMetaInfoRepo.get(targetType);
        if (null == fields) {
            fields = $.fieldsOf(targetType);
            classMetaInfoRepo.put(targetType, fields);
        }
        Map<String, Integer> columnNameLookup = columnNameLookup();
        for (Field f : fields) {
            Column column = f.getAnnotation(Column.class);
            String label = null != column ? column.name() : f.getName();
            if (null != reverseSpecialMaps) {
                String newLabel = reverseSpecialMaps.get(label);
                if (null != newLabel) {
                    label = newLabel;
                }
            }
            Integer n = columnNameLookup.get(label);
            if (null == n) {
                continue;
            }
            Class fieldType = f.getType();
            Object o = getFieldValue(n);
            $.setFieldValue(entity, f, $.convert(o).to(fieldType));
        }
        return entity;
    }

    private Object getFieldValue(int colId) throws SQLException {
        Object o = null;
        switch (rsMeta.getColumnType(colId)) {
            case ARRAY:
                Array array = rs.getArray(colId);
                return null == array ? null : array.getArray();
            case BIGINT:
                o = rs.getLong(colId);
                break;
            case BINARY:
                o = rs.getBytes(colId);
                break;
            case BIT:
                o = rs.getBoolean(colId);
                break;
            case BLOB:
                o = rs.getBlob(colId);
                break;
            case BOOLEAN:
                o = rs.getBoolean(colId);
                break;
            case CHAR:
                o = rs.getString(colId);
                break;
            case CLOB:
                o = rs.getClob(colId);
                break;
            case DATALINK:
                o = rs.getURL(colId);
                break;
            case DATE:
                o = rs.getDate(colId);
                break;
            case DECIMAL:
                o = rs.getBigDecimal(colId);
                break;
            case DOUBLE:
                o = rs.getDouble(colId);
                break;
            case FLOAT:
                o = rs.getFloat(colId);
                break;
            case INTEGER:
                o = rs.getInt(colId);
                break;
            case JAVA_OBJECT:
                o = rs.getObject(colId);
                break;
            case LONGVARCHAR:
            case LONGNVARCHAR:
                o = rs.getString(colId);
                break;
            case LONGVARBINARY:
                o = rs.getBytes(colId);
                break;
            case NCHAR:
                o = rs.getString(colId);
                break;
            case NCLOB:
                o = rs.getNClob(colId);
                break;
            case NULL:
                o = null;
                break;
            case NUMERIC:
                o = rs.getBigDecimal(colId);
                break;
            case NVARCHAR:
                o = rs.getString(colId);
                break;
            case REAL:
                o = rs.getFloat(colId);
                break;
            case SMALLINT:
                o = rs.getInt(colId);
                break;
            case SQLXML:
                o = rs.getSQLXML(colId);
                break;
            case TIME:
                o = rs.getTime(colId);
                break;
            case TINYINT:
                o = rs.getInt(colId);
                break;
            case VARBINARY:
                o = rs.getBytes(colId);
                break;
            case VARCHAR:
                o = rs.getString(colId);
                break;
            default:
                o = null;
        }
        if (rs.wasNull()) {
            o = null;
        }
        return o;
    }

}
