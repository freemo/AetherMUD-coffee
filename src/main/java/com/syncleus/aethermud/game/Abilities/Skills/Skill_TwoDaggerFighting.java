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
package com.syncleus.aethermud.game.Abilities.Skills;

import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Skill_TwoDaggerFighting extends Skill_TwoWeaponFighting {
    private final static String localizedName = CMLib.lang().L("Two Dagger Fighting");

    @Override
    public String ID() {
        return "Skill_TwoDaggerFighting";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        if (affected instanceof MOB) {
            final MOB mob = (MOB) affected;
            if (mob.isInCombat()) {
                final Weapon W1 = getFirstWeapon(mob);
                final Weapon W2 = getSecondWeapon(mob);
                if ((W1 != null) && (W2 != null)
                    && (W1.weaponClassification() == Weapon.CLASS_DAGGER)
                    && (W2.weaponClassification() == Weapon.CLASS_DAGGER)) {
                    final int xlvl = super.getXLEVELLevel(invoker());
                    final boolean adjustOnly = mob.fetchEffect("Skill_TwoWeaponFighting") != null;
                    if (!adjustOnly)
                        affectableStats.setSpeed(affectableStats.speed() + 1.0 + (0.1 * xlvl));
                    else {
                        affectableStats.setSpeed(affectableStats.speed() + (0.1 * xlvl));
                        affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() + (affectableStats.attackAdjustment() / (5 + xlvl)));
                        affectableStats.setDamage(affectableStats.damage() + (affectableStats.damage() / (20 + xlvl)));
                    }
                }
            }
        }
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((tickID == Tickable.TICKID_MOB) && (affected instanceof MOB)) {
            if (((MOB) affected).fetchEffect("Skill_TwoWeaponFighting") != null)
                return true;
        }
        return super.tick(ticking, tickID);
    }
}
