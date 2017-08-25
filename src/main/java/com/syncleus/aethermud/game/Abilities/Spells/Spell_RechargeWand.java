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
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Wand;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_RechargeWand extends Spell {

    private final static String localizedName = CMLib.lang().L("Recharge Wand");

    @Override
    public String ID() {
        return "Spell_RechargeWand";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canTargetCode() {
        return CAN_ITEMS;
    }

    @Override
    public int overrideMana() {
        return 100;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ENCHANTMENT;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Item target = getTarget(mob, mob.location(), givenTarget, commands, Wearable.FILTER_ANY);
        if (target == null)
            return false;

        if (!(target instanceof Wand)) {
            mob.tell(L("You can't recharge that."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> incant(s) at <T-NAMESELF> as sweat beads form on <S-HIS-HER> forehead.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if ((((Wand) target).usesRemaining() + 5) >= ((Wand) target).maxUses()) {
                    mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL, L("<T-NAME> glow(s) brightly then disintigrates!"));
                    target.destroy();
                } else {
                    boolean willBreak = false;
                    if ((((Wand) target).usesRemaining() + 10) >= ((Wand) target).maxUses())
                        willBreak = true;
                    ((Wand) target).setUsesRemaining(((Wand) target).usesRemaining() + 5);
                    if (!(willBreak)) {
                        mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL, L("<T-NAME> glow(s) brightly!"));
                    } else {
                        mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL, L("<T-NAME> glow(s) brightly and begins to hum.  It clearly cannot hold more magic."));
                    }
                }
            }

        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> incant(s) at <T-NAMESELF>, looking more frustrated every minute."));

        // return whether it worked
        return success;
    }
}
