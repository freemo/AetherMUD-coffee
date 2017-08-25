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
package com.syncleus.aethermud.game.Items.interfaces;

/**
 * Class for ammunition items used in weapons that require ammunition
 * @see AmmunitionWeapon
 * @author Bo Zimmerman
 */
public interface Ammunition extends Item {
    /**
     * The type/class of ammunition.  This must match the ammunition type of the weapon
     * in order to be used.  This can be an arbitrary string.
     * @see AmmunitionWeapon#ammunitionType()
     * @return the ammunition type string
     */
    public String ammunitionType();

    /**
     * Set the type/class of ammunition.  This must match the ammunition type of the weapon
     * in order to be used.  This can be an arbitrary string.
     * @see AmmunitionWeapon#setAmmunitionType(String)
     * @param type the ammunition type string
     */
    public void setAmmunitionType(String type);

    /**
     * The amount of Units of ammunition represented by this Ammunition item.  If this is,
     * for example, a bunch of arrows, or a clip of bullets, how many are in the clip or
     * bunch.
     * @return the number of units of ammunition
     */
    public int ammunitionRemaining();

    /**
     * Sets the amount of Units of ammunition represented by this Ammunition item.  If this is,
     * for example, a bunch of arrows, or a clip of bullets, how many are in the clip or
     * bunch.
     * @param amount the number of units of ammunition
     */
    public void setAmmoRemaining(int amount);
}
