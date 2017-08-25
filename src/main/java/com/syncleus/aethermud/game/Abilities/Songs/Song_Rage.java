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
package com.syncleus.aethermud.game.Abilities.Songs;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;


public class Song_Rage extends Song {
    private final static String localizedName = CMLib.lang().L("Rage");

    @Override
    public String ID() {
        return "Song_Rage";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (invoker == null)
            return;
        if (affected == invoker)
            return;
        affectableStats.setDamage(affectableStats.damage() + (int) Math.round(CMath.div(affectableStats.damage(), 2.0 + CMath.mul(0.25, super.getXLEVELLevel(invoker())))));
        affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() - (int) Math.round(CMath.div(affectableStats.attackAdjustment(), 6.0 + CMath.mul(0.5, super.getXLEVELLevel(invoker())))));
        affectableStats.setArmor(affectableStats.armor() + super.adjustedLevel(invoker(), 0));
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if (msg.amISource(invoker))
            return true;
        if (msg.sourceMinor() != CMMsg.TYP_FLEE)
            return true;
        if (msg.source().fetchEffect(this.ID()) == null)
            return true;

        msg.source().tell(L("You are too enraged to flee."));
        return false;
    }

}
