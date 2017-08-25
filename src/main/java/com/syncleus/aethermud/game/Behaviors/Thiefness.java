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

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Behaviors.interfaces.Behavior;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMSecurity;
import com.syncleus.aethermud.game.core.interfaces.PhysicalAgent;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.HashSet;
import java.util.Vector;


public class Thiefness extends CombatAbilities {
    protected int tickDown = 0;

    @Override
    public String ID() {
        return "Thiefness";
    }

    @Override
    public long flags() {
        return Behavior.FLAG_TROUBLEMAKING;
    }

    @Override
    public String accountForYourself() {
        return "thiefliness";
    }

    @Override
    public void startBehavior(PhysicalAgent forMe) {
        super.startBehavior(forMe);
        if (!(forMe instanceof MOB))
            return;
        final MOB mob = (MOB) forMe;
        combatMode = COMBAT_RANDOM;
        makeClass(mob, getParmsMinusCombatMode(), "Thief");
        newCharacter(mob);
        //%%%%%att,armor,damage,hp,mana,move
        if ((preCastSet == Integer.MAX_VALUE) || (preCastSet <= 0)) {
            setCombatStats(mob, 0, 10, 15, -15, -15, -15, true);
            setCharStats(mob);
        }
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        super.tick(ticking, tickID);

        if (tickID != Tickable.TICKID_MOB)
            return true;
        if (!canActAtAll(ticking))
            return true;
        if (!(ticking instanceof MOB))
            return true;
        final MOB mob = (MOB) ticking;
        if ((--tickDown) <= 0)
            if ((CMLib.dice().rollPercentage() < 10) && (mob.location() != null)) {
                tickDown = 2;
                MOB victim = null;
                if (mob.isInCombat())
                    victim = mob.getVictim();
                else
                    for (int i = 0; i < mob.location().numInhabitants(); i++) {
                        final MOB potentialVictim = mob.location().fetchInhabitant(i);
                        if ((potentialVictim != null)
                            && (potentialVictim != mob)
                            && (!potentialVictim.isMonster())
                            && (CMLib.flags().canBeSeenBy(potentialVictim, mob))
                            && (!potentialVictim.getGroupMembers(new HashSet<MOB>()).contains(mob))
                            && (!mob.getGroupMembers(new HashSet<MOB>()).contains(potentialVictim)))
                            victim = potentialVictim;
                    }
                if ((victim != null)
                    && (!CMSecurity.isAllowed(victim, victim.location(), CMSecurity.SecFlag.CMDROOMS))
                    && (!CMSecurity.isAllowed(victim, victim.location(), CMSecurity.SecFlag.ORDER))) {
                    final Vector<String> V = new Vector<String>();
                    final Ability A = mob.fetchAbility((CMLib.dice().rollPercentage() > 50) ? (mob.isInCombat() ? "Thief_Mug" : "Thief_Steal") : "Thief_Swipe");
                    if (A != null) {
                        if (!A.ID().equalsIgnoreCase("Thief_Swipe")) {
                            Item I = null;
                            for (int i = 0; i < victim.numItems(); i++) {
                                final Item potentialI = victim.getItem(i);
                                if ((potentialI != null)
                                    && (potentialI.amWearingAt(Wearable.IN_INVENTORY))
                                    && (CMLib.flags().canBeSeenBy(potentialI, mob)))
                                    I = potentialI;
                            }
                            if (I != null)
                                V.addElement(I.ID());
                        }
                        if (!A.ID().equalsIgnoreCase("Thief_Mug"))
                            V.addElement(victim.name());
                        A.setProficiency(CMLib.dice().roll(1, 50, A.adjustedLevel(mob, 0) * 15));
                        A.invoke(mob, V, null, false, 0);
                    }
                }
            }
        return true;
    }
}
