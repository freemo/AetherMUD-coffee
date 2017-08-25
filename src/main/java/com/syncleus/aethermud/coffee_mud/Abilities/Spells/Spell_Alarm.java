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
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;


public class Spell_Alarm extends Spell {

    private final static String localizedName = CMLib.lang().L("Alarm");
    Room myRoomContainer = null;
    boolean waitingForLook = false;

    @Override
    public String ID() {
        return "Spell_Alarm";
    }

    @Override
    public String name() {
        return localizedName;
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
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ENCHANTMENT;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);

        if ((affected == null) || (invoker == null)) {
            unInvoke();
            return;
        }

        myRoomContainer = msg.source().location();
        if (msg.source() == invoker)
            return;

        if (msg.amITarget(affected)) {
            myRoomContainer.showHappens(CMMsg.MSG_NOISE, L("A HORRENDOUS ALARM GOES OFF, WHICH SEEMS TO BE COMING FROM @x1!!!", affected.name().toUpperCase()));
            invoker.tell(L("The alarm on your @x1 has gone off.", affected.name()));
            unInvoke();
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Physical target = getTarget(mob, mob.location(), givenTarget, commands, Wearable.FILTER_UNWORNONLY);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> glow(s) faintly for a short time.") : L("^S<S-NAME> touch(es) <T-NAMESELF> very lightly.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                myRoomContainer = mob.location();
                beneficialAffect(mob, target, asLevel, 0);
            }

        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> speak(s) and touch(es) <T-NAMESELF> very lightly, but the spell fizzles."));

        // return whether it worked
        return success;
    }
}
