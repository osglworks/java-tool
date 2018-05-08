package org.osgl.exception;

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

import org.osgl.$;

public class MappingException extends UnexpectedException {

    private Object source;
    private Object target;

    public MappingException(Object source, Object target, String message, Object... messageArgs) {
        super(message, messageArgs);
        this.source = $.requireNotNull(source);
        this.target = $.requireNotNull(target);
    }

    public MappingException(Object source, Object target, Throwable cause, String message, Object... messageArgs) {
        super(cause, message, messageArgs);
        this.source = $.requireNotNull(source);
        this.target = $.requireNotNull(target);
    }

    public Object getSource() {
        return source;
    }

    public Object getTarget() {
        return target;
    }
}
