package org.osgl.util;

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

import org.osgl.$;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 23/10/13
 * Time: 8:51 PM
 * To change this template use File | Settings | File Templates.
 */
class CompositeIterator<T> extends StatefulIterator<T> {
    private final Iterator<? extends T> left_;
    private final Iterator<? extends T> right_;
    private volatile boolean leftOver_;

    CompositeIterator(Iterator<? extends T> i1, Iterator<? extends T> i2) {
        E.NPE(i1, i2);
        left_ = i1;
        right_ = i2;
    }

    @Override
    protected $.Option<T> getCurrent() {
        if (leftOver_) {
            if (right_.hasNext()) {
                return $.some(right_.next());
            } else {
                return $.none();
            }
        } else {
            if (left_.hasNext()) {
                return $.some(left_.next());
            } else {
                leftOver_ = true;
                return getCurrent();
            }
        }
    }
}
