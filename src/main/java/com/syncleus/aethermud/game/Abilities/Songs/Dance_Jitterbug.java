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
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;


public class Dance_Jitterbug extends Dance {
    private final static String localizedName = CMLib.lang().L("Jitterbug");

    @Override
    public String ID() {
        return "Dance_Jitterbug";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (invoker == null)
            return;
        affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() - adjustedLevel(invoker(), 0));
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        if (invoker == null)
            return;
        affectableStats.setStat(CharStats.STAT_DEXTERITY, (int) Math.round(CMath.div(affectableStats.getStat(CharStats.STAT_DEXTERITY), 3.0)));
        final int bonus = adjustedLevel(invoker(), 0) * 2;
        affectableStats.setStat(CharStats.STAT_SAVE_ACID, affectableStats.getStat(CharStats.STAT_SAVE_ACID) + bonus);
        affectableStats.setStat(CharStats.STAT_SAVE_COLD, affectableStats.getStat(CharStats.STAT_SAVE_COLD) + bonus);
        affectableStats.setStat(CharStats.STAT_SAVE_ELECTRIC, affectableStats.getStat(CharStats.STAT_SAVE_ELECTRIC) + bonus);
        affectableStats.setStat(CharStats.STAT_SAVE_FIRE, affectableStats.getStat(CharStats.STAT_SAVE_FIRE) + bonus);
        affectableStats.setStat(CharStats.STAT_SAVE_GAS, affectableStats.getStat(CharStats.STAT_SAVE_GAS) + bonus);
    }
}
