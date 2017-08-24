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
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;


public class Chant_SpeedBirth extends Chant {
    private final static String localizedName = CMLib.lang().L("Speed Birth");

    @Override
    public String ID() {
        return "Chant_SpeedBirth";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_BREEDING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_OTHERS;
    }

    @Override
    protected int overrideMana() {
        return Ability.COST_ALL;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        Ability A = target.fetchEffect("Pregnancy");
        long start = 0;
        long end = 0;
        long days = 0;
        long remain = 0;
        if (A != null) {
            start = CMath.s_long(A.getStat("PREGSTART"));
            end = CMath.s_long(A.getStat("PREGEND"));
            if ((start >= 0) && (end >= 0)) {
                remain = end - System.currentTimeMillis();
                final long divisor = CMProps.getTickMillis() * CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY);
                days = remain / divisor; // down to days;
            } else
                A = null;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if ((success) && (A != null) && (remain > 0)) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> chant(s) to <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (remain <= 20000) {
                    mob.tell(L("Birth is imminent!"));
                    return true;
                } else if (days < 1) {
                    if (end > System.currentTimeMillis())
                        remain = (end - System.currentTimeMillis()) + 19999;
                } else
                    remain = remain / 2;

                A.setStat("PREGSTART", "" + (start - remain));
                A.setStat("PREGEND", "" + (end - remain));
                target.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> appear(s) even MORE pregnant!"));
            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> chant(s) to <T-NAMESELF>, but the magic fades."));

        // return whether it worked
        return success;
    }
}
