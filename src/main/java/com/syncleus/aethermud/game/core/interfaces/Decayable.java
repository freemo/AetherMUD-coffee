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
package com.planet_ink.game.core.interfaces;

/**
 * A Drinkable object containing its own liquid material type, and liquid capacity management.
 * @author Bo Zimmerman
 *
 */
public interface Decayable extends Environmental {
    /**
     * The time, in milliseconds, when this will rot.  0=never
     * @see Decayable#setDecayTime(long)
     * @return the time in milliseconds when this will rot. 0=never
     */
    public long decayTime();

    /**
     * Sets the time, in milliseconds, when this will rot.  0=never
     * @param time in milliseconds, when this will rot.  0=never
     * @see Decayable#decayTime()
     */
    public void setDecayTime(long time);
}
