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
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_Anchor extends Spell {

    private final static String localizedName = CMLib.lang().L("Anchor");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Anchor)");

    @Override
    public String ID() {
        return "Spell_Anchor";
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
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS | CAN_ITEMS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ABJURATION;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB)) {
            super.unInvoke();
            return;
        }
        final MOB mob = (MOB) affected;
        if (canBeUninvoked())
            mob.tell(L("Your anchor has been lifted."));

        super.unInvoke();

    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if (affected == null)
            return true;

        if ((msg.tool() instanceof Ability)
            && ((affected == null)
            || ((affected instanceof Item) && (!((Item) affected).amWearingAt(Wearable.IN_INVENTORY)) && (msg.amITarget(((Item) affected).owner())))
            || ((affected instanceof MOB) && (msg.amITarget(affected))))
            && (CMath.bset(((Ability) msg.tool()).flags(), Ability.FLAG_MOVING)
            || CMath.bset(((Ability) msg.tool()).flags(), Ability.FLAG_TRANSPORTING))) {
            Room roomS = null;
            Room roomD = null;
            if (msg.target() instanceof MOB)
                roomD = ((MOB) msg.target()).location();
            else if (msg.target() instanceof Item) {
                final Item I = (Item) msg.target();
                if ((I.owner() != null) && (I.owner() instanceof MOB))
                    roomD = ((MOB) ((Item) msg.target()).owner()).location();
                else if ((I.owner() != null) && (I.owner() instanceof Room))
                    roomD = (Room) ((Item) msg.target()).owner();
            } else if (msg.target() instanceof Room)
                roomD = (Room) msg.target();

            if (msg.source().location() != null)
                roomS = msg.source().location();

            if ((roomS != null) && (roomD != null) && (roomS == roomD))
                roomD = null;

            final Ability A = (Ability) msg.tool();
            if (((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_CHANT)
                || ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_SPELL)
                || ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_PRAYER)
                || ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_SONG)) {
                if (roomS != null)
                    roomS.showHappens(CMMsg.MSG_OK_VISUAL, L("Magic energy fizzles and is absorbed into the air."));
                if (roomD != null)
                    roomD.showHappens(CMMsg.MSG_OK_VISUAL, L("Magic energy fizzles and is absorbed into the air."));
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L(auto ? "An magical anchoring field envelopes <T-NAME>!" : "^S<S-NAME> invoke(s) an anchoring field of protection around <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> attempt(s) to invoke an anchoring field, but fail(s)."));

        return success;
    }
}
