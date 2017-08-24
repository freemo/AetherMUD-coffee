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

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.interfaces.Physical;


/**
 * Magic Dust is itemized magic, like a potion or pill, except that it 
 * can be targeted at someone else, making malicious magic.
 * @author Bo Zimmerman
 *
 */
public interface MagicDust extends SpellHolder, MiscMagic {
    /**
     * This is the invocation of the magic dust method, where
     * the source and target are specified, and the
     * @param mob the spreader of the dust
     * @param target the one the dust will fall on
     */
    public void spreadIfAble(MOB mob, Physical target);
}
