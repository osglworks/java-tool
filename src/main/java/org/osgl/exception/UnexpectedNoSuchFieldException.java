package org.osgl.exception;

/*-
 * #%L
 * Java Tool
 * %%
 * Copyright (C) 2014 - 2017 OSGL (Open Source General Library)
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

/**
 * A `RuntimeException` version of JDK's {@link NoSuchFieldException}
 */
public class UnexpectedNoSuchFieldException extends UnexpectedException {
    public UnexpectedNoSuchFieldException(NoSuchFieldException cause) {
        super(cause.getCause(), cause.getMessage());
    }

    public UnexpectedNoSuchFieldException(NoSuchMethodException cause, String message, Object... args) {
        super(cause.getCause(), message, args);
    }
}
