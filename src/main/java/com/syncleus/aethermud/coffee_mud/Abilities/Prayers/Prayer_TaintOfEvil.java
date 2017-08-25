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
package com.planet_ink.coffee_mud.Abilities.Prayers;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;


public class Prayer_TaintOfEvil extends Prayer {
    private final static String localizedName = CMLib.lang().L("Taint of Evil");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Taint of Evil)");

    @Override
    public String ID() {
        return "Prayer_TaintOfEvil";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return super.canBeUninvoked ? "" : localizedStaticDisplay;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_CURSING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_UNHOLY;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked())
            mob.tell(L("Your taint of evil fades."));
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if ((msg.source() == affected)
            && (msg.sourceMinor() == CMMsg.TYP_FACTIONCHANGE)
            && (msg.othersMessage() != null)
            && (msg.othersMessage().equalsIgnoreCase(CMLib.factions().AlignID()))
            && (msg.value() > 0)
            && (msg.value() < Integer.MAX_VALUE)) {
            double prof;
            if ((invoker() != affected) && (canBeUninvoked()))
                prof = super.getXLEVELLevel(invoker());
            else
                prof = 0;
            msg.setValue((int) Math.round(msg.value() * (0.5 - (prof * 0.05))));
        }
        super.executeMsg(myHost, msg);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (mob.isInCombat())
                return Ability.QUALITY_INDIFFERENT;
            if (target instanceof MOB) {
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> <T-IS-ARE> cursed with the taint of evil!") : L("^S<S-NAME> @x1 for <T-YOUPOSS> need to be tainted with evil!^?", prayWord(mob)));
            final CMMsg msg3 = CMClass.getMsg(mob, target, this, CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_MIND | (auto ? CMMsg.MASK_ALWAYS : 0), null);
            if ((mob.location().okMessage(mob, msg)) && (mob.location().okMessage(mob, msg3))) {
                mob.location().send(mob, msg);
                mob.location().send(mob, msg3);
                if ((msg.value() <= 0) && (msg3.value() <= 0))
                    maliciousAffect(mob, target, asLevel, 0, -1);
            }
        } else
            return maliciousFizzle(mob, null, L("<S-NAME> @x1 for <T-YOUPOSS> to be tainted, but nothing happens.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
