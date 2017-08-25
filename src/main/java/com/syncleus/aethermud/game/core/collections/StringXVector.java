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
package com.planet_ink.game.core.collections;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@SuppressWarnings("rawtypes")
public class StringXVector extends XVector<String> {
    /**
     *
     */
    private static final long serialVersionUID = -2080154538499166336L;

    public StringXVector(List V) {
        super();
        if (V != null) {
            for (Object o : V)
                add(o.toString());
        }
    }

    public StringXVector() {
        super();
    }

    public StringXVector(int size, boolean boo) {
        super(size, boo);
    }

    public StringXVector(Object[] E) {
        super();
        if (E != null) {
            for (final Object o : E)
                add(o.toString());
        }
    }

    public StringXVector(Object E) {
        super();
        if (E != null)
            add(E.toString());
    }

    public StringXVector(Object E, Object E2) {
        this(E);
        if (E2 != null)
            add(E2.toString());
    }

    public StringXVector(Object E, Object E2, Object E3) {
        this(E, E2);
        if (E3 != null)
            add(E3.toString());
    }

    public StringXVector(Object E, Object E2, Object E3, Object E4) {
        this(E, E2, E3);
        if (E4 != null)
            add(E4.toString());
    }

    public StringXVector(Set E) {
        super();
        if (E != null) {
            for (final Object o : E)
                add(o.toString());
        }
    }

    public StringXVector(Enumeration E) {
        super();
        if (E != null) {
            for (; E.hasMoreElements(); )
                add(E.nextElement().toString());
        }
    }

    public StringXVector(Iterator E) {
        super();
        if (E != null) {
            for (; E.hasNext(); )
                add(E.next().toString());
        }
    }

}
