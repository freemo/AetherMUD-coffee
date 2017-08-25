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
package com.syncleus.aethermud.game.Abilities.Properties;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Prop_Invisibility extends Property {
    protected int ticksSinceLoss = 100;

    @Override
    public String ID() {
        return "Prop_Invisibility";
    }

    @Override
    public String name() {
        return "Persistant Invisibility";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS | Ability.CAN_ITEMS | Ability.CAN_EXITS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_ADJUSTER;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        if ((msg.amISource(mob)) && (CMath.bset(msg.sourceMajor(), CMMsg.MASK_MALICIOUS))) {
            ticksSinceLoss = 0;
            mob.recoverPhyStats();
        }
        return;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (ticksSinceLoss < 9999)
            ticksSinceLoss++;
        return super.tick(ticking, tickID);
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_INVISIBLE);
        if (ticksSinceLoss > 60)
            affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_INVISIBLE);
    }
}
