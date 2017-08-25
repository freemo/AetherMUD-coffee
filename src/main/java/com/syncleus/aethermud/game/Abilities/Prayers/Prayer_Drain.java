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
package com.planet_ink.game.Abilities.Prayers;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.Weapon;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_Drain extends Prayer {
    private final static String localizedName = CMLib.lang().L("Drain");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Drain)");

    @Override
    public String ID() {
        return "Prayer_Drain";
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
        return Ability.ACODE_PRAYER | Ability.DOMAIN_VEXING;
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
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_UNDEAD | (auto ? CMMsg.MASK_ALWAYS : 0), null);
            final CMMsg msg2 = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> reach(es) at <T-NAMESELF>, @x1!^?", prayingWord(mob)));
            if ((mob.location().okMessage(mob, msg)) && (mob.location().okMessage(mob, msg2))) {
                mob.location().send(mob, msg2);
                mob.location().send(mob, msg);
                if ((msg.value() <= 0) && (msg2.value() <= 0)) {
                    final int damage = CMLib.dice().roll(1, adjustedLevel(mob, asLevel), 0);
                    CMLib.combat().postDamage(mob, target, this, damage, CMMsg.MASK_ALWAYS | CMMsg.TYP_UNDEAD, Weapon.TYPE_BURSTING, auto ? L("<T-NAME> shudder(s) in a draining magical wake.") : L("The draining grasp <DAMAGE> <T-NAME>."));
                    if (mob != target)
                        CMLib.combat().postHealing(mob, mob, this, damage, CMMsg.MASK_ALWAYS | CMMsg.TYP_CAST_SPELL, null);
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> reach(es) for <T-NAMESELF>, @x1, but the spell fades.", prayingWord(mob)));

        // return whether it worked
        return success;
    }
}
