/**
 * Copyright 2017 Syncleus, Inc.
 * with portions copyright 2004-2017 Bo Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.planet_ink.coffee_mud.core.collections;

import java.util.Enumeration;
import java.util.Iterator;

public class IteratorEnumeration<K> implements Enumeration<K> {
    private final Iterator<K> i;

    @SuppressWarnings("unchecked")
    public IteratorEnumeration(Iterator<K> i) {
        if (i == null)
            this.i = EmptyIterator.INSTANCE;
        else
            this.i = i;
        hasMoreElements();
    }

    @Override
    public boolean hasMoreElements() {
        return i.hasNext();
    }

    @Override
    public K nextElement() {
        return i.next();
    }
}
