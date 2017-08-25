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
package com.syncleus.aethermud.game.Behaviors;

import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class PlayerHelper extends StdBehavior {
    @Override
    public String ID() {
        return "PlayerHelper";
    }

    @Override
    public String accountForYourself() {
        return "protectiveness of heroes";
    }

    @Override
    public void executeMsg(Environmental affecting, CMMsg msg) {
        super.executeMsg(affecting, msg);
        if ((msg.target() == null) || (!(msg.target() instanceof MOB)))
            return;
        final MOB mob = msg.source();
        final MOB monster;
        if (affecting instanceof MOB)
            monster = (MOB) affecting;
        else if ((affecting instanceof Item) && (((Item) affecting).owner() instanceof MOB))
            monster = (MOB) ((Item) affecting).owner();
        else
            return;
        final MOB target = (MOB) msg.target();

        if ((mob != monster)
            && (target != monster)
            && (mob != target)
            && (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))
            && (!monster.isInCombat())
            && (CMLib.flags().canBeSeenBy(mob, monster))
            && (CMLib.flags().canBeSeenBy(target, monster))
            && (!target.isMonster()))
            Aggressive.startFight(monster, mob, false, false, null);
    }
}
