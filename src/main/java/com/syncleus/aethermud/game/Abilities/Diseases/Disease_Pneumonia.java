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
package com.syncleus.aethermud.game.Abilities.Diseases;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Abilities.interfaces.DiseaseAffect;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharState;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Disease_Pneumonia extends Disease {
    private final static String localizedName = CMLib.lang().L("Pneumonia");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Pneumonia)");

    @Override
    public String ID() {
        return "Disease_Pneumonia";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public boolean putInCommandlist() {
        return false;
    }

    @Override
    protected int DISEASE_TICKS() {
        return 38;
    }

    @Override
    protected int DISEASE_DELAY() {
        return 3;
    }

    @Override
    protected String DISEASE_DONE() {
        return L("Your pneumonia clears up.");
    }

    @Override
    protected String DISEASE_START() {
        return L("^G<S-NAME> come(s) down with pneumonia.^?");
    }

    @Override
    protected String DISEASE_AFFECT() {
        return L("<S-NAME> shake(s) feverishly.");
    }

    @Override
    public int spreadBitmap() {
        return DiseaseAffect.SPREAD_CONSUMPTION | DiseaseAffect.SPREAD_PROXIMITY | DiseaseAffect.SPREAD_CONTACT | DiseaseAffect.SPREAD_STD;
    }

    @Override
    public int difficultyLevel() {
        return 3;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (affected == null)
            return false;
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if ((!mob.amDead()) && ((--diseaseTick) <= 0)) {
            MOB diseaser = invoker;
            if (diseaser == null)
                diseaser = mob;
            diseaseTick = DISEASE_DELAY();
            mob.location().show(mob, null, CMMsg.MSG_QUIETMOVEMENT, DISEASE_AFFECT());
            final int damage = CMLib.dice().roll(4, mob.phyStats().level() + 1, 1);
            CMLib.combat().postDamage(diseaser, mob, this, damage, CMMsg.MASK_ALWAYS | CMMsg.TYP_DISEASE, -1, null);
            final Disease_Cold A = (Disease_Cold) CMClass.getAbility("Disease_Cold");
            A.catchIt(mob);
            if (CMLib.dice().rollPercentage() == 1) {
                final Ability A2 = CMClass.getAbility("Disease_Fever");
                if (A2 != null)
                    A2.invoke(diseaser, mob, true, 0);
            }
            return true;
        }
        return true;
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        if (affected == null)
            return;
        affectableStats.setStat(CharStats.STAT_CONSTITUTION, affectableStats.getStat(CharStats.STAT_CONSTITUTION) - 8);
        affectableStats.setStat(CharStats.STAT_STRENGTH, affectableStats.getStat(CharStats.STAT_STRENGTH) - 10);
        if (affectableStats.getStat(CharStats.STAT_CONSTITUTION) <= 0)
            affectableStats.setStat(CharStats.STAT_CONSTITUTION, 1);
        if (affectableStats.getStat(CharStats.STAT_STRENGTH) <= 0)
            affectableStats.setStat(CharStats.STAT_STRENGTH, 1);
    }

    @Override
    public void affectCharState(MOB affected, CharState affectableState) {
        if (affected == null)
            return;
        affectableState.setMovement(10);
        affectableState.setMana(affectableState.getMana() - (affectableState.getMana() / 2));
        affectableState.setHitPoints(affectableState.getHitPoints() - (affected.phyStats().level() * 2));
    }
}
