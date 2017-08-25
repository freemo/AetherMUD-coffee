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

import com.planet_ink.game.core.interfaces.Physical;


/**
 * This interface denotes an ability that also incidentally is capable
 * of mending objects, usually items or mobs.
 */
public interface MendingSkill extends Ability {
    /**
     * Returns whether this skill can mend the given thing.
     * @param item the item to check
     * @return true or false, depending upon if this skill will do the trick.
     */
    public boolean supportsMending(Physical item);
}
