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
package com.planet_ink.coffee_mud.Abilities.Songs;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

import java.util.HashSet;


public class Dance_Cotillon extends Dance {
    private final static String localizedName = CMLib.lang().L("Cotillon");
    protected MOB whichLast = null;

    @Override
    public String ID() {
        return "Dance_Cotillon";
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
    protected boolean HAS_QUANTITATIVE_ASPECT() {
        return false;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if ((!mob.isInCombat())
                || (mob.getGroupMembers(new HashSet<MOB>()).size() < 2))
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if ((affected == invoker()) && ((invoker()).isInCombat())) {
            if (whichLast == null)
                whichLast = invoker();
            else {
                final MOB M = (MOB) affected;
                boolean pass = false;
                boolean found = false;
                for (int i = 0; i < M.location().numInhabitants(); i++) {
                    final MOB M2 = M.location().fetchInhabitant(i);
                    if (M2 == whichLast)
                        found = true;
                    else if ((M2 != whichLast)
                        && (found)
                        && (M2.fetchEffect(ID()) != null)
                        && (M2.isInCombat())) {
                        whichLast = M2;
                        break;
                    }
                    if (i == (M.location().numInhabitants() - 1)) {
                        if (pass)
                            return true;
                        pass = true;
                        i = -1;
                    }
                }
                if ((whichLast != null)
                    && (M.isInCombat())
                    && (M.getVictim().getVictim() != whichLast)
                    && (whichLast.location().show(whichLast, null, M.getVictim(), CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> dance(s) into <O-YOUPOSS> way."))))
                    M.getVictim().setVictim(whichLast);
            }
        }
        return true;
    }

}
