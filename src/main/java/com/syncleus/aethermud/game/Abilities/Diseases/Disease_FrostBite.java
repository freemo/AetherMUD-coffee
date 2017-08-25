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
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Disease_FrostBite extends Disease {
    private final static String localizedName = CMLib.lang().L("Frost Bite");
    public int[] limbsAffectable = {Race.BODY_EAR, Race.BODY_ANTENEA, Race.BODY_FOOT, Race.BODY_HAND, Race.BODY_NOSE};
    private String where = "feet";

    @Override
    public String ID() {
        return "Disease_FrostBite";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return L("(Frost bitten " + where + ")");
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
    public int difficultyLevel() {
        return 1;
    }

    @Override
    protected int DISEASE_TICKS() {
        return (CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY) / 2);
    }

    @Override
    protected int DISEASE_DELAY() {
        return 50;
    }

    @Override
    protected String DISEASE_DONE() {
        if (tickDown > 0)
            return L("Your frost bite heals.");
        return L("Your frost bite has cost you dearly.");
    }

    @Override
    protected String DISEASE_START() {
        return L("^G<S-NAME> <S-IS-ARE> getting frost bite.^?");
    }

    @Override
    protected String DISEASE_AFFECT() {
        return "";
    }

    @Override
    public int abilityCode() {
        return 0;
    }

    @Override
    public void unInvoke() {
        if ((affected instanceof MOB) && (tickDown <= 0)) {
            final MOB mob = (MOB) affected;
            final Ability A = CMClass.getAbility("Amputation");
            if (A != null) {
                super.unInvoke();
                A.invoke(mob, CMParms.parse(where), mob, true, 0);
                mob.recoverCharStats();
                mob.recoverPhyStats();
                mob.recoverMaxState();
            } else
                super.unInvoke();
        } else
            super.unInvoke();
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        if (affected == null)
            return;
        if (where == null) {
            final Vector<Integer> choices = new Vector<Integer>();
            for (final int element : limbsAffectable) {
                if (affected.charStats().getBodyPart(element) > 0)
                    choices.addElement(Integer.valueOf(element));
            }
            if (choices.size() <= 0) {
                where = "nowhere";
                unInvoke();
            } else
                where = Race.BODYPARTSTR[choices.elementAt(CMLib.dice().roll(1, choices.size(), -1)).intValue()];
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        where = null;
        return super.invoke(mob, commands, givenTarget, auto, asLevel);
    }
}
