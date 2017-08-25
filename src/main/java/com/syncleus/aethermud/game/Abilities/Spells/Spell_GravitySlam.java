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
package com.planet_ink.game.Abilities.Spells;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.Weapon;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Spell_GravitySlam extends Spell {

    private final static String localizedName = CMLib.lang().L("Gravity Slam");

    @Override
    public String ID() {
        return "Spell_GravitySlam";
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
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ALTERATION;
    }

    @Override
    public long flags() {
        return Ability.FLAG_MOVING;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        // now see if it worked
        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, somanticCastCode(mob, target, auto), L(auto ? "" : "^S<S-NAME> incant(s) and point(s) at <T-NAMESELF>!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                invoker = mob;

                int damage = 0;
                int maxDie = (int) Math.round((adjustedLevel(mob, asLevel) + (2.0 * super.getX1Level(mob))) / 2.0);
                if (!CMLib.flags().isInFlight(target))
                    maxDie = maxDie / 2;
                final Room R = mob.location();
                if (CMLib.flags().isUnderWateryRoom(R))
                    maxDie = maxDie / 6;
                if (CMLib.flags().isWaterySurfaceRoom(R))
                    maxDie = maxDie / 4;

                damage += CMLib.dice().roll(maxDie, 20, 6 + maxDie);
                if (msg.value() > 0)
                    damage = (int) Math.round(CMath.div(damage, 2.0));
                if (!CMLib.flags().isInFlight(target))
                    mob.location().show(target, null, CMMsg.MSG_OK_ACTION, L("<S-NAME> <S-IS-ARE> hurled up into the air and **SLAMMED** back down!"));
                else
                    mob.location().show(target, null, CMMsg.MSG_OK_ACTION, L("<S-NAME> <S-IS-ARE> hurled even higher into the air and **SLAMMED** back down!"));

                if (target.location() == mob.location())
                    CMLib.combat().postDamage(mob, target, this, damage, CMMsg.MASK_ALWAYS | CMMsg.TYP_JUSTICE, Weapon.TYPE_BASHING, L("The fall <DAMAGE> <T-NAME>!"));
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> incant(s) and point(s) at <T-NAMESELF>, but flub(s) the spell."));

        // return whether it worked
        return success;
    }
}
