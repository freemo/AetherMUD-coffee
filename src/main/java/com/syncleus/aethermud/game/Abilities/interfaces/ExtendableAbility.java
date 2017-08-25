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
package com.planet_ink.game.Abilities.interfaces;

import com.planet_ink.game.core.interfaces.MsgListener;
import com.planet_ink.game.core.interfaces.StatsAffecting;
import com.planet_ink.game.core.interfaces.Tickable;


/**
 * An ExtendableAbility is an ability/effect object that can be more easily
 * extended in code at runtime using various methods that add internal pointers
 * to coded solutions to it's external api.
 */
public interface ExtendableAbility extends Ability {
    /**
     * Since each ability made this way will tend to be rather unique, this
     * allows the ID of the ability to be altered at runtime to make it unique.
     * @param ID the ID to change this ability into (will not add to CMClass!)
     * @return this
     */
    public ExtendableAbility setAbilityID(String ID);

    /**
     * Set the stats affector for this ability.
     * @see com.planet_ink.game.core.interfaces.StatsAffecting
     * @param code the stats affector code
     * @return this
     */
    public ExtendableAbility setStatsAffector(StatsAffecting code);

    /**
     * Set the msg listener for this ability.
     * @see com.planet_ink.game.core.interfaces.MsgListener
     * @param code the msg listener code
     * @return this
     */
    public ExtendableAbility setMsgListener(MsgListener code);

    /**
     * Set the ticker for this ability.
     * @see com.planet_ink.game.core.interfaces.Tickable
     * @param code the ticker code
     * @return this
     */
    public ExtendableAbility setTickable(Tickable code);
}
