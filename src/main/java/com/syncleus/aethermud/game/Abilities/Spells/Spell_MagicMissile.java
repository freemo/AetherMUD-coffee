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
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_MagicMissile extends Spell {

    private final static String localizedName = CMLib.lang().L("Magic Missile");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Magic Missile spell)");

    @Override
    public String ID() {
        return "Spell_MagicMissile";
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
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_CONJURATION;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final int numMissiles = Math.min((int) Math.round(Math.floor(CMath.div(adjustedLevel(mob, asLevel), 5)) + 1), 8);
            final Room R = target.location();
            for (int i = 0; (i < numMissiles) && (target.location() == R); i++) {
                final CMMsg msg = CMClass.getMsg(mob, target, this, somanticCastCode(mob, target, auto), (i == 0) ? L((auto ? "A magic missile appears hurling full speed at <T-NAME>!" : "^S<S-NAME> point(s) at <T-NAMESELF>, shooting forth a magic missile!^?") + CMLib.protocol().msp("spelldam2.wav", 40)) : null);
                if (mob.location().okMessage(mob, msg)) {
                    mob.location().send(mob, msg);
                    if (msg.value() <= 0) {
                        final int damage = CMLib.dice().roll(1, 7 + super.getXLEVELLevel(mob), 7 + super.getXLEVELLevel(mob) / numMissiles);
                        if (target.location() == mob.location())
                            CMLib.combat().postDamage(mob, target, this, damage, CMMsg.MASK_ALWAYS | CMMsg.TYP_CAST_SPELL, Weapon.TYPE_BURSTING, ((i == 0) ? "^SThe missile " : "^SAnother missile ") + "<DAMAGE> <T-NAME>!^?");
                    }
                }
                if (target.amDead()) {
                    target = this.getTarget(mob, commands, givenTarget, true, false);
                    if (target == null)
                        break;
                    if (target.amDead())
                        break;
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> point(s) at <T-NAMESELF>, but fizzle(s) the spell."));

        // return whether it worked
        return success;
    }
}
