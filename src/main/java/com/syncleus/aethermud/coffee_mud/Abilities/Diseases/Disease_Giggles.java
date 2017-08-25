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
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;


public class Disease_Giggles extends Disease {
    private final static String localizedName = CMLib.lang().L("Contagious Giggles");
    private final static String localizedStaticDisplay = CMLib.lang().L("(The Giggles)");

    @Override
    public String ID() {
        return "Disease_Giggles";
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
        return 15;
    }

    @Override
    protected int DISEASE_DELAY() {
        return 3;
    }

    @Override
    protected String DISEASE_DONE() {
        return L("You feel more serious.");
    }

    @Override
    protected String DISEASE_START() {
        return L("^G<S-NAME> start(s) giggling.^?");
    }

    @Override
    protected String DISEASE_AFFECT() {
        return L("<S-NAME> giggle(s) and laugh(s) uncontrollably.");
    }

    @Override
    public int spreadBitmap() {
        return DiseaseAffect.SPREAD_PROXIMITY;
    }

    @Override
    public int difficultyLevel() {
        return 3;
    }

    @Override
    public boolean isMalicious() {
        return false;
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
        if ((!mob.amDead()) && ((--diseaseTick) <= 0)) {
            diseaseTick = DISEASE_DELAY();
            final CMMsg msg = CMClass.getMsg(mob, null, this, CMMsg.MSG_NOISE, DISEASE_AFFECT());
            if ((mob.location() != null) && (mob.location().okMessage(mob, msg)))
                mob.location().send(mob, msg);
            catchIt(mob);
            return true;
        }
        return true;
    }
}
