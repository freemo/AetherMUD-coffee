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
package com.planet_ink.coffee_mud.Common.interfaces;

import com.planet_ink.coffee_mud.core.interfaces.CMObject;

/**
 * Tattoos are arbitrary markers or flags.  They server no other
 * purpose other than to be checked by other things for their existence.
 * They can automatically expire by setting a tick down, or a number of
 * ticks to live.
 */
public interface Tattoo extends Cloneable, CMObject, CMCommon {

    /**
     * Set the tattoo name
     * @param name the tattoo name
     * @return this
     */
    public Tattoo set(String name);

    /**
     * Set the tatoo name and tick-down
     * @param name the tatoo name
     * @param down the tick down life span
     * @return this
     */
    public Tattoo set(String name, int down);

    /**
     * Returns the current tick-down
     * @return the tickDown
     */
    public int getTickDown();

    /**
     * Reduces the tick down by one and returns the new value
     * @return the new tick down
     */
    public int tickDown();

    /**
     * Returns the tattoo Name
     * @return the tattooName
     */
    public String getTattooName();

    /**
     * Parse a new tattoo object from the
     * coded data, of the form:
     * TATOONAME
     * or
     * NUMBER TATTOONAME
     *
     * @param tattooCode coded data
     * @return this tattoo
     */
    public Tattoo parse(String tattooCode);
}
