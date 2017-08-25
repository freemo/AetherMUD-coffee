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
package com.syncleus.aethermud.game.Abilities.Spells;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.Set;


public class Spell_IceStorm extends Spell {

    private final static String localizedName = CMLib.lang().L("Ice Storm");

    @Override
    public String ID() {
        return "Spell_IceStorm";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int maxRange() {
        return adjustedMaxInvokerRange(5);
    }

    @Override
    public int minRange() {
        return 1;
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
        return Ability.FLAG_WATERBASED;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Set<MOB> h = properTargets(mob, givenTarget, auto);
        if (h == null) {
            mob.tell(L("There doesn't appear to be anyone here worth storming."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            if (mob.location().show(mob, null, this, verbalCastCode(mob, null, auto), L(auto ? "A ferocious ice storm appears!" : "^S<S-NAME> evoke(s) a ferocious ice storm!^?") + CMLib.protocol().msp("spelldam2.wav", 40))) {
                for (final Object element : h) {
                    final MOB target = (MOB) element;

                    final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), null);
                    final CMMsg msg2 = CMClass.getMsg(mob, target, this, CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_COLD | (auto ? CMMsg.MASK_ALWAYS : 0), null);
                    if ((mob.location().okMessage(mob, msg)) && ((mob.location().okMessage(mob, msg2)))) {
                        mob.location().send(mob, msg);
                        mob.location().send(mob, msg2);
                        invoker = mob;

                        final int numDice = (adjustedLevel(mob, asLevel) + (2 * super.getX1Level(mob))) / 4;
                        int damage = CMLib.dice().roll(numDice, 15, 10);
                        if ((msg.value() > 0) || (msg2.value() > 0))
                            damage = (int) Math.round(CMath.div(damage, 2.0));
                        damage = (int) Math.round(CMath.div(damage, 2.0));
                        CMLib.combat().postDamage(mob, target, this, damage, CMMsg.MASK_ALWAYS | CMMsg.TYP_COLD, Weapon.TYPE_FROSTING, L("The freezing blast <DAMAGE> <T-NAME>!"));
                        CMLib.combat().postDamage(mob, target, this, damage, CMMsg.MASK_ALWAYS | CMMsg.TYP_COLD, Weapon.TYPE_FROSTING, L("The lumps of hail <DAMAGE> <T-NAME>!@x1", CMLib.protocol().msp("hail.wav", 40)));
                    }
                }
            }
        } else
            return maliciousFizzle(mob, null, L("<S-NAME> attempt(s) to evoke an ice storm, but the spell fizzles."));

        return success;
    }
}
