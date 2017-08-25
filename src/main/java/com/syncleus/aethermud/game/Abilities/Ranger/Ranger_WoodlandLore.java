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
package com.syncleus.aethermud.game.Abilities.Ranger;

import com.syncleus.aethermud.game.Abilities.StdAbility;
import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;


public class Ranger_WoodlandLore extends StdAbility {
    private final static String localizedName = CMLib.lang().L("Woodland Lore");

    @Override
    public String ID() {
        return "Ranger_WoodlandLore";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public boolean isAutoInvoked() {
        return true;
    }

    @Override
    public boolean canBeUninvoked() {
        return false;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_NATURELORE;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if ((invoker == null) && (affected instanceof MOB))
            invoker = (MOB) affected;
        if (CMLib.flags().isInWilderness(invoker)) {
            final int xlvl = super.getXLEVELLevel(invoker());
            affectableStats.setDamage(affectableStats.damage() + 5 + xlvl);
            affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() + 5 + (2 * xlvl));
            affectableStats.setArmor(affectableStats.armor() - 10 - (2 * xlvl));
        }
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && (msg.source() == affected)
            && (msg.target() instanceof MOB)
            && (CMLib.dice().roll(1, 10, 0) == 1)
            && (CMLib.flags().isInWilderness(affected)))
            helpProficiency(msg.source(), 0);
        return super.okMessage(myHost, msg);
    }
}
