/* 
 * Copyright (C) 2013 The Java Tool project
 * Gelin Luo <greenlaw110(at)gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
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
 * Indicate a computation exceeds the implied range
 *
 * @see IndexOutOfBoundsException
 */
public class InvalidRangeException extends UnexpectedException {

    public InvalidRangeException() {
    }

    public InvalidRangeException(String message){
        super(message);
    }

    public InvalidRangeException(String message, Object... args){
        super(message, args);
    }

    public InvalidRangeException(Throwable cause){
        super(cause);
    }

    /**
     * Convert to corresponding JDK exception. Warning, since there are synchronized method execution
     * please beware of the performance issue when calling this method
     * @return the JDK {@link IndexOutOfBoundsException} converted from this excpetion instance
     */
    public IndexOutOfBoundsException asJDKException() {
        IndexOutOfBoundsException e = new IndexOutOfBoundsException(getMessage()) {
            @Override
            public synchronized Throwable fillInStackTrace() {
                return this;
            }
        };
        e.setStackTrace(getStackTrace());
        return e;
    }
}
