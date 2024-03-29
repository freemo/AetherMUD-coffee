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
package com.syncleus.aethermud.game.Abilities.Druid;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.Set;


public class Chant_MeteorStrike extends Chant {
    private final static String localizedName = CMLib.lang().L("Meteor Strike");

    @Override
    public String ID() {
        return "Chant_MeteorStrike";
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
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_ROCKCONTROL;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            final Set<MOB> h = properTargets(mob, target, false);
            if (h == null)
                return Ability.QUALITY_INDIFFERENT;

            final Room R = mob.location();
            if (R != null) {
                if ((R.domainType() & Room.INDOORS) > 0)
                    return Ability.QUALITY_INDIFFERENT;
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Set<MOB> h = properTargets(mob, givenTarget, auto);
        if (h == null) {
            mob.tell(L("There doesn't appear to be anyone here worth striking at."));
            return false;
        }
        if ((mob.location().domainType() & Room.INDOORS) > 0) {
            mob.tell(L("You must be outdoors to strike with meteors."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {

            if (mob.location().show(mob, null, this, verbalCastCode(mob, null, auto), L(auto ? "A devastating meteor shower erupts!" : "^S<S-NAME> chant(s) for a devastating meteor shower!^?") + CMLib.protocol().msp("meteor.wav", 40))) {
                for (final Object element : h) {
                    final MOB target = (MOB) element;

                    final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), null);
                    if (mob.location().okMessage(mob, msg)) {
                        mob.location().send(mob, msg);
                        invoker = mob;

                        int damage = 0;
                        final int maxDie = (adjustedLevel(mob, asLevel) + (2 * super.getX1Level(mob))) / 2;
                        damage = CMLib.dice().roll(maxDie, 6, 30);
                        if (msg.value() > 0)
                            damage = (int) Math.round(CMath.div(damage, 2.0));
                        if (target.location() == mob.location())
                            CMLib.combat().postDamage(mob, target, this, damage, CMMsg.MASK_ALWAYS | CMMsg.TYP_FIRE, Weapon.TYPE_BASHING, L("The meteors <DAMAGE> <T-NAME>!"));
                    }
                }
            }
        } else
            return maliciousFizzle(mob, null, L("<S-NAME> chant(s) to the sky, but nothing happens."));

        // return whether it worked
        return success;
    }
}
