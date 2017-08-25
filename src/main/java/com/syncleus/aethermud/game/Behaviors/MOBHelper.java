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
package com.planet_ink.game.Behaviors;

import com.planet_ink.game.Abilities.interfaces.DiseaseAffect;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Environmental;


public class MOBHelper extends StdBehavior {
    @Override
    public String ID() {
        return "MOBHelper";
    }

    @Override
    public String accountForYourself() {
        return "friend protecting";
    }

    @Override
    public void executeMsg(Environmental affecting, CMMsg msg) {
        super.executeMsg(affecting, msg);
        if ((msg.target() == null) || (!(msg.target() instanceof MOB)))
            return;
        final MOB attacker = msg.source();
        final MOB monster = (MOB) affecting;
        final MOB victim = (MOB) msg.target();

        if ((attacker != monster)
            && (victim != monster)
            && (attacker != victim)
            && (!monster.isInCombat())
            && (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))
            && (CMLib.flags().canBeSeenBy(attacker, monster))
            && (CMLib.flags().canBeSeenBy(victim, monster))
            && ((!(msg.tool() instanceof DiseaseAffect)) || (((DiseaseAffect) msg.tool()).isMalicious()))
            && (victim.isMonster()))
            Aggressive.startFight(monster, attacker, true, false, null);
    }
}
