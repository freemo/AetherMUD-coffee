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
package com.planet_ink.coffee_mud.Items.interfaces;

/**
 * A False, or Fake Limb is an item that re-provides a wear location,
 * and the personal functionality that goes with that limb, to 
 * someone who is missing that limb due to amputation.  These include
 * things like peg legs.
 * @author Bo Zimmerman
 *
 */
public interface FalseLimb extends Armor {
    /**
     * Gets the racial body part code that corresponds with the
     * part of the body that this false limb replaces.
     * @see com.planet_ink.coffee_mud.Races.interfaces.Race#BODYPARTSTR
     * @return the racial body part code
     */
    public int getBodyPartCode();

    /**
     * Sets the racial body part code that corresponds with the
     * part of the body that this false limb replaces.
     * @see com.planet_ink.coffee_mud.Races.interfaces.Race#BODYPARTSTR
     * @param partNum the racial body part code
     */
    public void setBodyPartCode(int partNum);

    /**
     * Gets the single wear location that this limb both helps to provide,
     * and appears to be worn underneath other clothing.  The number
     * is a bit value, but not a mask of worn location bits.
     * @see Wearable#DEFAULT_WORN_DESCS
     * @return the single wear location bit value
     */
    public long getWearLocations();

    /**
     * Sets the single wear location that this limb both helps to provide,
     * and appears to be worn underneath other clothing.  The number
     * is a bit value, but not a mask of worn location bits.
     * @see Wearable#DEFAULT_WORN_DESCS
     * @param wearPlace  the single wear location bit value
     */
    public void setWearLocations(long wearPlace);
}
