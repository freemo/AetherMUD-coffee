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
package com.planet_ink.game.Abilities.Songs;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;


public class Dance_Macabre extends Dance {
    private final static String localizedName = CMLib.lang().L("Macabre");
    protected boolean activated = false;

    @Override
    public String ID() {
        return "Dance_Macabre";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    protected String danceOf() {
        return name() + " Dance";
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (activated) {
            affectableStats.setDamage(affectableStats.damage() + (adjustedLevel(invoker(), 0) / 2));
            affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() + (adjustedLevel(invoker(), 0) * 3));
        } else if ((affected instanceof MOB)
            && (((MOB) affected).isInCombat())
            && (((MOB) affected).getVictim().isInCombat())
            && (((MOB) affected).getVictim() != affected)) {
            affectableStats.setDamage(affectableStats.damage() + (adjustedLevel(invoker(), 0) / 4));
            affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() + adjustedLevel(invoker(), 0));
        }
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (CMLib.flags().isHidden(affected)) {
            if (!activated) {
                activated = true;
                affected.recoverPhyStats();
            }
        } else if (activated) {
            activated = false;
            affected.recoverPhyStats();
        }
        return super.tick(ticking, tickID);
    }

}
