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
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_ArcanePossession extends Spell {

    private final static String localizedName = CMLib.lang().L("Arcane Possession");
    protected MOB owner = null;

    @Override
    public String ID() {
        return "Spell_ArcanePossession";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    protected int canAffectCode() {
        return CAN_ITEMS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_ITEMS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ABJURATION;
    }

    @Override
    public boolean okMessage(Environmental host, CMMsg msg) {
        if (!super.okMessage(host, msg))
            return false;
        if ((msg.tool() == affected)
            && (msg.sourceMinor() == CMMsg.TYP_SELL)) {
            unInvoke();
            if (affected != null)
                affected.delEffect(this);
        } else if (msg.amITarget(affected)
            && (owner != null)
            && ((msg.targetMinor() == CMMsg.TYP_WEAR)
            || (msg.targetMinor() == CMMsg.TYP_HOLD)
            || (msg.targetMinor() == CMMsg.TYP_WIELD))
            && (msg.source() != owner)) {
            msg.source().location().show(msg.source(), null, affected, CMMsg.MSG_OK_ACTION, L("<O-NAME> flashes and flies out of <S-HIS-HER> hands!"));
            CMLib.commands().postDrop(msg.source(), affected, true, false, false);
            return false;
        }
        return true;
    }

    @Override
    public void executeMsg(Environmental host, CMMsg msg) {
        super.executeMsg(host, msg);
        if ((affected instanceof Item) && (text().length() > 0)) {
            final Item I = (Item) affected;
            if ((owner == null) && (I.owner() != null)
                && (I.owner() instanceof MOB)
                && (I.owner().Name().equals(text())))
                owner = (MOB) I.owner();
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Item target = getTarget(mob, null, givenTarget, commands, Wearable.FILTER_ANY);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> hold(s) a <T-NAMESELF> tightly and cast(s) a spell.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                mob.location().show(mob, target, CMMsg.MSG_OK_VISUAL, L("<T-NAME> glows slightly!"));
                setMiscText(mob.Name());
                beneficialAffect(mob, target, asLevel, 0);
                target.recoverPhyStats();
                mob.recoverPhyStats();
            }

        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> hold(s) <T-NAMESELF> tightly and whisper(s), but fail(s) to cast a spell."));

        // return whether it worked
        return success;
    }
}
