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
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;


public class Spell_Frost extends Spell {

    private final static String localizedName = CMLib.lang().L("Frost");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Frost)");

    @Override
    public String ID() {
        return "Spell_Frost";
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
        return Ability.FLAG_WATERBASED;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;
        Room R = CMLib.map().roomLocation(target);
        if (R == null)
            R = mob.location();

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        // now see if it worked
        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, somanticCastCode(mob, target, auto), L((auto ? "A " : "^S<S-NAME> incant(s) and point(s) at <T-NAMESELF>. A ") + "blast of frost erupts!^?") + CMLib.protocol().msp("spelldam1.wav", 40));
            final CMMsg msg2 = CMClass.getMsg(mob, target, this, CMMsg.MSK_CAST_MALICIOUS_SOMANTIC | CMMsg.TYP_COLD | (auto ? CMMsg.MASK_ALWAYS : 0), null);
            if ((R.okMessage(mob, msg)) && (R.okMessage(mob, msg2))) {
                R.send(mob, msg);
                invoker = mob;

                int damage = 0;
                final int maxDie = (Math.min(adjustedLevel(mob, asLevel), 50) + (2 * super.getX1Level(mob))) / 4;
                damage += CMLib.dice().roll(maxDie, 6, 5);
                R.send(mob, msg2);
                if ((msg2.value() > 0) || (msg.value() > 0))
                    damage = (int) Math.round(CMath.div(damage, 2.0));

                if (target.location() == R)
                    CMLib.combat().postDamage(mob, target, this, damage, CMMsg.MASK_ALWAYS | CMMsg.TYP_COLD, Weapon.TYPE_FROSTING, L("The frost <DAMAGE> <T-NAME>!"));
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> incant(s) and point(s) at <T-NAMESELF>, but flub(s) the spell."));

        // return whether it worked
        return success;
    }
}
