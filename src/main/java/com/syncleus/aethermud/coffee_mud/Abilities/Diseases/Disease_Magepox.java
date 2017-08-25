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
package com.planet_ink.coffee_mud.Abilities.Diseases;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.DiseaseAffect;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharState;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;


public class Disease_Magepox extends Disease {
    private final static String localizedName = CMLib.lang().L("Magepox");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Magepox)");

    @Override
    public String ID() {
        return "Disease_Magepox";
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
        return CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY);
    }

    @Override
    protected int DISEASE_DELAY() {
        return 15;
    }

    @Override
    protected String DISEASE_DONE() {
        return L("Your magepox clears up.");
    }

    @Override
    protected String DISEASE_START() {
        return L("^G<S-NAME> come(s) down with the Magepox.^?");
    }

    @Override
    protected String DISEASE_AFFECT() {
        return L("<S-NAME> watch(es) new mystical sores appear on <S-HIS-HER> body.");
    }

    @Override
    public int spreadBitmap() {
        return DiseaseAffect.SPREAD_PROXIMITY;
    }

    @Override
    public int difficultyLevel() {
        return 9;
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
            diseaseTick = DISEASE_DELAY();
            mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, DISEASE_AFFECT());
            catchIt(mob);
            return true;
        }
        return true;
    }

    @Override
    public void affectCharState(MOB affected, CharState affectableState) {
        if (affected == null)
            return;
        int hitsLost = affected.maxState().getHitPoints() - affected.curState().getHitPoints();
        if (hitsLost < 0)
            hitsLost = 0;
        int movesLost = (affected.maxState().getMovement() - affected.curState().getMovement());
        if (movesLost < 0)
            movesLost = 0;
        final int lostMana = hitsLost + movesLost;
        affectableState.setMana(affectableState.getMana() - lostMana);
        if (affectableState.getMana() < 0)
            affectableState.setMana(0);
        if (affected.curState().getMana() > affectableState.getMana())
            affected.curState().setMana(affectableState.getMana());

    }
}
