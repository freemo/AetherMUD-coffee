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
package com.syncleus.aethermud.game.core.collections;

import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;

public class ReverseEnumeration<K> implements Enumeration<K> {
    private final List<K> set;
    private int index;

    public ReverseEnumeration(List<K> eset) {
        set = eset;
        index = set.size();
        hasMoreElements();
    }

    @Override
    public boolean hasMoreElements() {
        while (index > set.size())
            index--;
        return (index > 0);
    }

    @Override
    public K nextElement() {
        if (!hasMoreElements())
            throw new NoSuchElementException();
        index--;
        return set.get(index);
    }
}
