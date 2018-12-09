package org.osgl.util.converter;

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

import com.alibaba.fastjson.JSONArray;
import org.osgl.*;
import org.osgl.util.S;
import org.w3c.dom.Document;

public class JsonArrayToXmlDocument extends Lang.TypeConverter<JSONArray, Document> {
    @Override
    public Document convert(JSONArray array) {
        return JsonXmlConvertHint.convert(array, OsglConfig.xmlRootTag(), OsglConfig.xmlListItemTag());
    }

    @Override
    public Document convert(JSONArray arrray, Object hint) {
        if (hint instanceof JsonXmlConvertHint) {
            JsonXmlConvertHint jsonXmlConvertHint = $.cast(hint);
            return JsonXmlConvertHint.convert(arrray, jsonXmlConvertHint.rootTag, jsonXmlConvertHint.listItemTag);
        }
        String rootTag = OsglConfig.xmlRootTag();
        if (hint instanceof String && !S.string(hint).isEmpty()) {
            rootTag = ((String) hint).trim();
        }
        return JsonXmlConvertHint.convert(arrray, rootTag, OsglConfig.xmlListItemTag());
    }

}
