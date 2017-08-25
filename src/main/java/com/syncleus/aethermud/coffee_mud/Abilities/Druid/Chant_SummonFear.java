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
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;
import java.util.Set;


public class Chant_SummonFear extends Chant {
    private final static String localizedName = CMLib.lang().L("Summon Fear");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Afraid)");

    @Override
    public String ID() {
        return "Chant_SummonFear";
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
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_ENDURING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public int maxRange() {
        return adjustedMaxInvokerRange(1);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            final Set<MOB> h = properTargets(mob, target, false);
            if (h == null)
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Set<MOB> h = properTargets(mob, givenTarget, auto);
        if (h == null) {
            mob.tell(L("There doesn't appear to be anyone here worth scaring."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            for (final Object element : h) {
                final MOB target = (MOB) element;

                final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L("^S<S-NAME> frighten(s) <T-NAMESELF> with <S-HIS-HER> chant.^?"));
                final CMMsg msg2 = CMClass.getMsg(mob, target, this, verbalCastMask(mob, target, auto) | CMMsg.TYP_MIND, null);
                if ((mob.location().okMessage(mob, msg)) && ((mob.location().okMessage(mob, msg2)))) {
                    mob.location().send(mob, msg);
                    if (msg.value() <= 0) {
                        mob.location().send(mob, msg2);
                        if (msg2.value() <= 0) {
                            invoker = mob;
                            CMLib.commands().postFlee(target, "");
                        }
                    }
                }
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> chant(s) in a frightening way, but the magic fades."));

        // return whether it worked
        return success;
    }
}
