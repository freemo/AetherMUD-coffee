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
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_KnowPain extends Spell {

    private final static String localizedName = CMLib.lang().L("Know Pain");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Spell_KnowPain)");

    @Override
    public String ID() {
        return "Spell_KnowPain";
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
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_DIVINATION;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        // now see if it worked
        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L(auto ? "A painful memory erupts!" : "^S<S-NAME> invoke(s) <T-YOUPOSS> most painful memories."));
            final CMMsg msg2 = CMClass.getMsg(mob, target, this, CMMsg.MSK_CAST_MALICIOUS_VERBAL | CMMsg.TYP_MIND | (auto ? CMMsg.MASK_ALWAYS : 0), null);
            if ((mob.location().okMessage(mob, msg)) && (mob.location().okMessage(mob, msg2))) {
                mob.location().send(mob, msg);
                invoker = mob;

                int damage = 0;
                final int maxDie = (target.phyStats().level() + adjustedLevel(mob, asLevel)) / 2;
                damage += CMLib.dice().roll(maxDie, 6, target.charStats().getStat(CharStats.STAT_INTELLIGENCE));
                mob.location().send(mob, msg2);
                if ((msg2.value() > 0) || (msg.value() > 0))
                    damage = (int) Math.round(CMath.div(damage, 2.0));

                if (target.location() == mob.location())
                    CMLib.combat().postDamage(mob, target, this, damage, CMMsg.MASK_ALWAYS | CMMsg.TYP_GAS, Weapon.TYPE_BURSTING, L("The horrible memory <DAMAGE> <T-NAME>!"));
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> incant(s) at <T-NAMESELF>, but flub(s) the spell."));

        // return whether it worked
        return success;
    }
}
