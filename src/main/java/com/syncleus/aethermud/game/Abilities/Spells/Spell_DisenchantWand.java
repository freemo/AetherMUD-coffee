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
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.Wand;
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Spell_DisenchantWand extends Spell {

    private final static String localizedName = CMLib.lang().L("Disenchant Wand");

    @Override
    public String ID() {
        return "Spell_DisenchantWand";
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
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_EVOCATION;
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

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if ((success)
            && (target instanceof Wand)
            && (((Wand) target).usesRemaining() > 0)
            && (((Wand) target).getSpell() != null)) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> hold(s) <T-NAMESELF> and cast(s) a spell.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                ((Wand) target).setSpell(null);
                ((Wand) target).setUsesRemaining(0);
                mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL, L("<T-NAME> fades and becomes dull!"));
                if ((target.basePhyStats().disposition() & PhyStats.IS_BONUS) == PhyStats.IS_BONUS)
                    target.basePhyStats().setDisposition(target.basePhyStats().disposition() - PhyStats.IS_BONUS);
                target.recoverPhyStats();
            }

        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> hold(s) <T-NAMESELF> and whisper(s), but nothing happens."));

        // return whether it worked
        return success;
    }
}
