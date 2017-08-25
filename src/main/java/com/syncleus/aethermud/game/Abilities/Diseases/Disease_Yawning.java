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
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Disease_Yawning extends Disease {
    private final static String localizedName = CMLib.lang().L("Yawning");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Yawning)");

    @Override
    public String ID() {
        return "Disease_Yawning";
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
        return 30;
    }

    @Override
    protected int DISEASE_DELAY() {
        return 3;
    }

    @Override
    protected String DISEASE_DONE() {
        return L("You stop yawning.");
    }

    @Override
    protected String DISEASE_START() {
        return L("^G<S-NAME> seem(s) really tired.^?");
    }

    @Override
    protected String DISEASE_AFFECT() {
        return L("<S-NAME> stretch(es) and yawn(s).");
    }

    @Override
    protected boolean DISEASE_REQSEE() {
        return true;
    }

    @Override
    public boolean isMalicious() {
        return false;
    }

    @Override
    public int spreadBitmap() {
        return DiseaseAffect.SPREAD_PROXIMITY;
    }

    @Override
    public int difficultyLevel() {
        return 0;
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
        MOB diseaser = invoker;
        if (diseaser == null)
            diseaser = mob;
        if ((getTickDownRemaining() == 1)
            && (!mob.amDead())
            && (!CMLib.flags().isSleeping(mob))
            && (CMLib.dice().rollPercentage() > mob.charStats().getSave(CharStats.STAT_SAVE_DISEASE))) {
            mob.delEffect(this);
            final Ability A = CMClass.getAbility("Disease_Yawning");
            A.invoke(diseaser, mob, true, 0);
        } else if ((!mob.amDead())
            && ((--diseaseTick) <= 0)
            && (!CMLib.flags().isSleeping(mob))) {
            diseaseTick = DISEASE_DELAY();
            final CMMsg msg = CMClass.getMsg(mob, null, this, CMMsg.MSG_NOISE, DISEASE_AFFECT() + CMLib.protocol().msp("yawn.wav", 40));
            if ((mob.location() != null) && (mob.location().okMessage(mob, msg)))
                mob.location().send(mob, msg);
            catchIt(mob);
            return true;
        }
        return true;
    }
}
