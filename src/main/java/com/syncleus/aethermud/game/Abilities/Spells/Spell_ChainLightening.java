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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;


public class Spell_ChainLightening extends Spell {

    private final static String localizedName = CMLib.lang().L("Chain Lightning");

    @Override
    public String ID() {
        return "Spell_ChainLightening";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int maxRange() {
        return adjustedMaxInvokerRange(2);
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
        return Ability.FLAG_AIRBASED;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        Set<MOB> h = properTargets(mob, givenTarget, auto);
        if (h == null)
            h = new HashSet<MOB>();

        final Set<MOB> myGroup = mob.getGroupMembers(new HashSet<MOB>());
        final Vector<MOB> targets = new Vector<MOB>(h);
        for (final MOB element : h)
            targets.addElement(element);
        for (final MOB element : myGroup) {
            final MOB M = element;
            if ((M != mob) && (!targets.contains(M)))
                targets.addElement(M);
        }
        if (!targets.contains(mob))
            targets.addElement(mob);

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final int maxDie = (adjustedLevel(mob, asLevel) + (2 * super.getX1Level(mob))) / 2;
        int damage = CMLib.dice().roll(maxDie, 8, maxDie);

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            if (mob.location().show(mob, null, this, verbalCastCode(mob, null, auto), L(auto ? "A thunderous crack of lightning erupts!" : "^S<S-NAME> invoke(s) a thunderous crack of lightning.^?") + CMLib.protocol().msp("lightning.wav", 40))) {
                while (damage > 0) {
                    final int oldDamage = damage;
                    for (int i = 0; i < targets.size(); i++) {
                        final MOB target = targets.elementAt(i);
                        if (target.amDead() || (target.location() != mob.location())) {
                            int count = 0;
                            for (int i2 = 0; i2 < targets.size(); i2++) {
                                final MOB M2 = targets.elementAt(i2);
                                if ((!M2.amDead())
                                    && (mob.location() != null)
                                    && (mob.location().isInhabitant(M2))
                                    && (M2.location() == mob.location()))
                                    count++;
                            }
                            if (count < 2)
                                return true;
                            continue;
                        }

                        final boolean oldAuto = auto;
                        if ((target == mob) || (myGroup.contains(target)))
                            auto = true;
                        final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), null);
                        final CMMsg msg2 = CMClass.getMsg(mob, target, this, CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_ELECTRIC | (auto ? CMMsg.MASK_ALWAYS : 0), null);
                        auto = oldAuto;
                        if ((mob.location().okMessage(mob, msg)) && ((mob.location().okMessage(mob, msg2)))) {
                            mob.location().send(mob, msg);
                            mob.location().send(mob, msg2);
                            invoker = mob;

                            int dmg = damage;
                            if ((msg.value() > 0) || (msg2.value() > 0) || myGroup.contains(target) || (mob == target))
                                dmg = (int) Math.round(CMath.div(dmg, 2.0));
                            if (target.location() == mob.location()) {
                                CMLib.combat().postDamage(mob, target, this, dmg, CMMsg.MASK_ALWAYS | CMMsg.TYP_ELECTRIC, Weapon.TYPE_STRIKING, L("The bolt <DAMAGE> <T-NAME>!"));
                                damage = (int) Math.round(CMath.div(damage, 2.0));
                                if (damage < 5) {
                                    damage = 0;
                                    break;
                                }
                            }
                        }
                    }
                    if (oldDamage == damage)
                        break;
                }
            }
        } else
            return maliciousFizzle(mob, null, L("<S-NAME> attempt(s) to invoke a ferocious spell, but the spell fizzles."));

        // return whether it worked
        return success;
    }
}
