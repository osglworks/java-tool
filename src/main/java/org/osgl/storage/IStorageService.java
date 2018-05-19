/* 
 * Copyright (C) 2013 The Java Storage project
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
package org.osgl.storage;

/*-
 * #%L
 * Java Storage Service
 * %%
 * Copyright (C) 2013 - 2017 OSGL (Open Source General Library)
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
import org.osgl.exception.UnexpectedIOException;
import osgl.version.Version;

import java.util.Map;


/**
 * Provide persistent service for {@link ISObject}
 */
public interface IStorageService {

    Version VERSION = ISObject.VERSION;

    String DEFAULT = "default";
    String CONF_KEY_GEN = "storage.keygen";
    String CONF_CONTEXT_PATH = "storage.context";
    String CONF_ID = "storage.id";

    /**
     * Return the ID of the service.
     *
     * @return the service id
     */
    String id();

    /**
     * alias of {@link #getContextPath()}
     *
     * @return the context path
     */
    String contextPath();

    /**
     * Retrieve the stuff from the storage by key
     * <p>
     * If file cannot be find by key, then <code>null</code> is returned
     *
     * @param key
     * @return the file associated with key or null if not found
     */
    ISObject get(String key);

    /**
     * Returns lazy SObject with attributes set. This method is useful when user
     * application choose to store attributes and key of SObject into database, and
     * lazy load SObject content later one by calling {@link #loadContent(ISObject)}
     *
     * @param key
     * @param attrs
     * @return a sobject with meta attributes and key only. content of the sobject is not loaded
     */
    ISObject getLazy(String key, Map<String, String> attrs);

    /**
     * Force retrieving the stuff with content from storage without regarding to the configuration
     *
     * @param key
     * @return the storage stuff
     * @see #getFull(String)
     * @deprecated
     */
    ISObject forceGet(String key);

    /**
     * Force retrieving the stuff with content from storage without regarding to the configuration
     *
     * @param key
     * @return the storage stuff
     * @see #loadContent(ISObject)
     */
    ISObject getFull(String key);


    /**
     * Load content of an sobject. If the content is already loaded, then
     * the object should be returned directly
     *
     * @param sobj
     * @return the sobject with content presented
     */
    ISObject loadContent(ISObject sobj);

    /**
     * Update the stuff in the storage. If the existing file cannot be find
     * in the storage then it will be added.
     *
     * @param key
     * @param stuff
     * @return The new SObject representing the persistent data
     */
    ISObject put(String key, ISObject stuff) throws UnexpectedIOException;

    /**
     * Report if a {@link ISObject storage object} is managed by this service
     * @param sobj the sobject
     * @return `true` if the sobject is managed by this service
     */
    boolean isManaged(ISObject sobj);

    /**
     * Remove the file from the storage by key and return it to caller.
     *
     * @param key
     */
    void remove(String key);

    /**
     * Return the context path. A context path is the path from where
     * all the storage should happen. By default context path
     * is ""
     *
     * @return the context path
     */
    String getContextPath();

    /**
     * Return the static web endpoint configured
     * @return the configured static web endpoint
     */
    String getStaticWebEndpoint();

    /**
     * Return the URL to access a stored resource by key
     *
     * @param key
     * @return the URL
     */
    String getUrl(String key);

    String getKey(String key);

    String getKey();

    /**
     * Returns a storage service whose root is a sub folder of this storage service
     *
     * @param path the path to sub folder
     * @return the new storage service instance as described above
     */
    IStorageService subFolder(String path);

    public static class f {
        public static $.F0<Void> put(final String key, final ISObject stuff, final IStorageService ss) {
            return put().curry(key, stuff, ss);
        }

        public static $.F3<String, ISObject, IStorageService, Void> put() {
            return new $.F3<String, ISObject, IStorageService, Void>() {
                @Override
                public Void apply(String s, ISObject isObject, IStorageService iStorageService) {
                    iStorageService.put(s, isObject);
                    return null;
                }
            };
        }

        public static $.F0<ISObject> get(final String key, IStorageService ss) {
            return get().curry(key, ss);
        }

        public static $.F2<String, IStorageService, ISObject> get() {
            return new $.F2<String, IStorageService, ISObject>() {
                @Override
                public ISObject apply(String key, IStorageService ss) {
                    return ss.get(key);
                }
            };
        }

        public static $.F0<Void> remove(final String key, IStorageService ss) {
            return remove().curry(key, ss);
        }

        public static $.F2<String, IStorageService, Void> remove() {
            return new $.F2<String, IStorageService, Void>() {
                @Override
                public Void apply(String s, IStorageService ss) {
                    ss.remove(s);
                    return null;
                }
            };
        }
    }
}
