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
package com.planet_ink.coffee_mud.Abilities.Spells;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;
import java.util.Random;


public class Spell_Siphon extends Spell {
    private final static String localizedName = CMLib.lang().L("Siphon");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Siphon spell)");
    private static Random randomizer = null;

    public Spell_Siphon() {
        super();
        if (randomizer == null)
            randomizer = new Random(System.currentTimeMillis());
    }

    @Override
    public String ID() {
        return "Spell_Siphon";
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
    public int maxRange() {
        return adjustedMaxInvokerRange(1);
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ENCHANTMENT;
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
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> feel(s) a thirst for the energy of others.") : L("^S<S-NAME> invoke(s) an area deprived of energy around <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> attempt(s) to invoke an energy thirst, but fail(s)."));

        return success;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        super.unInvoke();

        mob.tell(L("You no longer feel a thirst for the energy of others."));
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;

        if ((msg.amITarget(mob))
            && (!msg.amISource(mob))
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && ((msg.value()) > 0)
            && (msg.tool() instanceof Weapon)
            && (CMLib.dice().rollPercentage() > 50)
            && (msg.source().curState().getMana() > 0)) {
            final MOB sourceM = msg.source();
            final CMMsg msg2 = CMClass.getMsg(mob, sourceM, null, CMMsg.MSG_QUIETMOVEMENT, L("<S-NAME> siphon(s) mana from <T-NAME>!"));
            if (mob.location().okMessage(mob, msg2)) {
                final int maxManaRestore = 3;
                final int curSourceMana = sourceM.curState().getMana();
                int manaDrain = 0;
                if (maxManaRestore <= curSourceMana) {
                    manaDrain = maxManaRestore;
                } else {
                    manaDrain = curSourceMana;
                }
                mob.curState().adjMana(manaDrain, mob.maxState());
                sourceM.curState().adjMana(manaDrain * -1, sourceM.maxState());
                mob.location().send(mob, msg2);
            }
        }
        return super.okMessage(myHost, msg);
    }
}
