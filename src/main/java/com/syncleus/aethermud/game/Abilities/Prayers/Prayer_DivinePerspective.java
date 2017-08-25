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
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.Deity;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_DivinePerspective extends Prayer {
    private final static String localizedName = CMLib.lang().L("Divine Perspective");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Perspective)");
    public String mobName = "";
    public boolean noRecurse = false;

    @Override
    public String ID() {
        return "Prayer_DivinePerspective";
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
        return Ability.ACODE_PRAYER | Ability.DOMAIN_COMMUNING;
    }

    @Override
    public long flags() {
        return Ability.FLAG_HOLY;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        if (canBeUninvoked()) {
            if (invoker != null)
                invoker.tell(L("The perspective of '@x1' fades from your mind.", mob.name(invoker)));
        }
        super.unInvoke();

    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        if (noRecurse)
            return;

        if ((affected instanceof MOB)
            && (msg.amISource((MOB) affected))
            && ((msg.sourceMinor() == CMMsg.TYP_LOOK) || (msg.sourceMinor() == CMMsg.TYP_EXAMINE))
            && (invoker != null)
            && (msg.target() != null)
            && ((invoker.location() != ((MOB) affected).location()) || (!(msg.target() instanceof Room)))) {
            noRecurse = true;
            final CMMsg newAffect = CMClass.getMsg(invoker, msg.target(), msg.sourceMinor(), null);
            msg.target().executeMsg(msg.target(), newAffect);
        } else if ((affected instanceof MOB)
            && (invoker != null)
            && (msg.source() != invoker)
            && (invoker.location() != ((MOB) affected).location())
            && (msg.othersCode() != CMMsg.NO_EFFECT)
            && (msg.othersMessage() != null)) {
            noRecurse = true;
            invoker.executeMsg(invoker, msg);
        }
        noRecurse = false;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if ((mob.getWorshipCharID().length() == 0)
            || (CMLib.map().getDeity(mob.getWorshipCharID()) == null)) {
            mob.tell(L("You must worship a god to use this prayer."));
            return false;
        }
        final Deity target = CMLib.map().getDeity(mob.getWorshipCharID());
        final Room newRoom = target.location();

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            mobName = target.Name();
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> invoke(s) the holy perspective of '@x1'.^?", mobName));
            final CMMsg msg2 = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), null);
            if ((mob.location().okMessage(mob, msg)) && ((newRoom == mob.location()) || (newRoom.okMessage(mob, msg2)))) {
                mob.location().send(mob, msg);
                if (newRoom != mob.location())
                    newRoom.send(target, msg2);
                beneficialAffect(mob, target, asLevel, 10);
            }

        } else
            beneficialVisualFizzle(mob, null, L("<S-NAME> attempt(s) to invoke the holy perspective of @x1, but fail(s).", target.Name()));

        // return whether it worked
        return success;
    }
}
