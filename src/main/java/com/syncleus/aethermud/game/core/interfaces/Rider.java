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
 * The interface for an item or mob which ride a Rideable
 * @see com.planet_ink.game.core.interfaces.Rideable
 * @author Bo Zimmerman
 *
 */
public interface Rider extends PhysicalAgent {
    /**
     * Sets the Rideable upon which this Rider is Riding.
     * @see com.planet_ink.game.core.interfaces.Rideable
     * @param ride the Rideable to ride upon
     */
    public void setRiding(Rideable ride);

    /**
     * Returns the Rideable upon which this Rider is Riding.
     * @see com.planet_ink.game.core.interfaces.Rideable
     * @return the Rideable upon which this Rider is Riding.
     */
    public Rideable riding();
}
