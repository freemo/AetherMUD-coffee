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
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

/*
 Copyright 2004-2017 Bo Zimmerman

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

public class Disease_Cannibalism extends Disease {
    private final static String localizedName = CMLib.lang().L("Cannibalism");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Cannibalism)");

    @Override
    public String ID() {
        return "Disease_Cannibalism";
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
    public int difficultyLevel() {
        return 6;
    }

    @Override
    protected int DISEASE_TICKS() {
        return 999999;
    }

    @Override
    protected int DISEASE_DELAY() {
        return 100;
    }

    @Override
    protected String DISEASE_DONE() {
        if (affected instanceof MOB) {
            final MOB mob = (MOB) affected;
            return L("<S-NAME> no longer hunger for @x1 meat.", mob.charStats().raceName());
        } else {
            return L("<S-NAME> no longer hunger for your race's meat.");
        }
    }

    @Override
    protected String DISEASE_START() {
        String desiredMeat = "";
        if (affected instanceof MOB) {
            final MOB mob = (MOB) affected;
            desiredMeat = mob.charStats().raceName();
        } else {
            desiredMeat = "your race's";
        }
        return L("^G<S-NAME> hunger(s) for " + desiredMeat + " meat.^?");
    }

    @Override
    protected String DISEASE_AFFECT() {
        return "";
    }

    @Override
    public int spreadBitmap() {
        return DiseaseAffect.SPREAD_CONSUMPTION;
    }

    @Override
    public void unInvoke() {
        if (affected == null)
            return;
        if (affected instanceof MOB) {
            final MOB mob = (MOB) affected;

            super.unInvoke();
            if (canBeUninvoked())
                mob.tell(mob, null, this, DISEASE_DONE());
        } else
            super.unInvoke();
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (affected instanceof MOB) {
            final MOB source = msg.source();
            if (source == null)
                return false;
            final MOB mob = (MOB) affected;
            if (msg.targetMinor() == CMMsg.TYP_EAT) {
                final Environmental food = msg.target();
                if ((food != null) && (food.name().toLowerCase().indexOf(mob.charStats().raceName()) < 0)) {
                    final CMMsg newMessage = CMClass.getMsg(mob, null, this, CMMsg.MSG_OK_VISUAL, L("^S<S-NAME> attempt(s) to eat @x1, but can't stomach it....^?", food.Name()));
                    if (mob.location().okMessage(mob, newMessage))
                        mob.location().send(mob, newMessage);
                    return false;
                }
            }
        }
        if (affected instanceof MOB) {
            final MOB mob = (MOB) affected;
            if (msg.amITarget(mob) && (msg.tool() != null) && (msg.tool().ID().equals("Spell_Hungerless"))) {
                mob.tell(L("You don't feel any less hungry."));
                return false;
            }
        }

        return super.okMessage(myHost, msg);
    }
}
