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
package com.planet_ink.coffee_mud.Abilities.Druid;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.TimeClock;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;


public class Chant_TideMoon extends Chant {
    private final static String localizedName = CMLib.lang().L("Tide Moon");
    protected int abilityCode = 0;

    @Override
    public String ID() {
        return "Chant_TideMoon";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    public int abilityCode() {
        return abilityCode;
    }

    @Override
    public void setAbilityCode(int newCode) {
        this.abilityCode = newCode;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_MOONALTERING;
    }

    @Override
    public long flags() {
        return Ability.FLAG_TIDEALTERING;
    }

    @Override
    protected int canAffectCode() {
        return CAN_AREAS | CAN_ROOMS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    protected int overrideMana() {
        return Ability.COST_ALL - 51;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (mob.location().getArea().getTimeObj().getTODCode() != TimeClock.TimeOfDay.NIGHT) {
            mob.tell(L("This chant can only be done at night."));
            return false;
        }

        if (!mob.location().getArea().getClimateObj().canSeeTheMoon(mob.location(), null)) {
            mob.tell(L("You can't see the moon from here."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> chant(s) to the moon, and it begins to change shape.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final Area mobA = mob.location().getArea();
                if (mobA != null) {
                    Ability A = mobA.fetchEffect(ID());
                    if (A == null)
                        A = this.beneficialAffect(mob, mobA, asLevel, 0);
                    if (A != null) {
                        mob.location().showHappens(CMMsg.MSG_OK_VISUAL, L("The moon begins pushing and pulling on the tides in new ways!"));
                        A.setAbilityCode(A.abilityCode() + 1);
                        mob.tell(L(mobA.getTimeObj().getTidePhase(mob.location()).getDesc()));
                    }
                }
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> chant(s) to the moon, but the magic fades"));

        // return whether it worked
        return success;
    }
}
