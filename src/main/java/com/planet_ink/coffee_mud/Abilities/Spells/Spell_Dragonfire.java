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
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;
import java.util.Set;


public class Spell_Dragonfire extends Spell {

    private final static String localizedName = CMLib.lang().L("Dragonfire");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Dragonfire)");

    @Override
    public String ID() {
        return "Spell_Dragonfire";
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
        return adjustedMaxInvokerRange(3);
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_EVOCATION;
    }

    @Override
    public long flags() {
        return Ability.FLAG_FIREBASED;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Set<MOB> h = properTargets(mob, givenTarget, auto);
        if (h == null) {
            mob.tell(L("There doesn't appear to be anyone here worth burning."));
            return false;
        }

        if (!CMLib.flags().canBreatheHere(mob, mob.location())) {
            mob.tell(L("You can't breathe!"));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {

            if (mob.location().show(mob, null, this, verbalCastCode(mob, null, auto), L(auto ? "A blast of flames erupt!" : "^S<S-NAME> blast(s) flames from <S-HIS-HER> mouth!^?") + CMLib.protocol().msp("fireball.wav", 40))) {
                for (final Object element : h) {
                    final MOB target = (MOB) element;

                    final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), null);
                    final CMMsg msg2 = CMClass.getMsg(mob, target, this, CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_FIRE | (auto ? CMMsg.MASK_ALWAYS : 0), null);
                    if ((mob.location().okMessage(mob, msg)) && ((mob.location().okMessage(mob, msg2)))) {
                        mob.location().send(mob, msg);
                        mob.location().send(mob, msg2);
                        invoker = mob;

                        final int maxDie = adjustedLevel(mob, asLevel) + (2 * super.getX1Level(mob));
                        int damage = CMLib.dice().roll(maxDie, 4, maxDie);
                        if ((msg.value() > 0) || (msg2.value() > 0))
                            damage = (int) Math.round(CMath.div(damage, 2.0));
                        CMLib.combat().postDamage(mob, target, this, damage, CMMsg.MASK_ALWAYS | CMMsg.TYP_FIRE, Weapon.TYPE_BURNING, L("The dragonfire <DAMAGE> <T-NAME>!"));
                    }
                }
            }
        } else
            return maliciousFizzle(mob, null, L("<S-NAME> puff(s) smoke from <S-HIS-HER> mouth."));

        // return whether it worked
        return success;
    }
}
