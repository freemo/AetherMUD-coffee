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
package com.planet_ink.coffee_mud.Abilities.Druid;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

import java.util.List;


public class Chant_DeepDarkness extends Chant {
    private final static String localizedName = CMLib.lang().L("Deep Darkness");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Deep Darkness spell)");

    @Override
    public String ID() {
        return "Chant_DeepDarkness";
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
        return Ability.ACODE_CHANT | Ability.DOMAIN_DEEPMAGIC;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    protected int canAffectCode() {
        return CAN_ROOMS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_ROOMS;
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (affected == null)
            return;
        if (!(affected instanceof Room))
            return;
        final Room room = (Room) affected;
        super.unInvoke();
        if (canBeUninvoked()) {
            room.recoverRoomStats();
            room.recoverRoomStats();
            room.showHappens(CMMsg.MSG_OK_VISUAL, L("The deep darkness starts to lift."));
        }
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((tickID == Tickable.TICKID_SPELL_AFFECT)
            && (affected instanceof Room)
            && (affected.fetchEffect(ID()) == this)
            && (affected.fetchEffect(affected.numEffects() - 1) != this)) {
            affected.delEffect(this);
            affected.addEffect(this);
            ((Room) affected).recoverRoomStats();
            ((Room) affected).recoverRoomStats();
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (CMLib.flags().isGlowing(affected))
            affectableStats.setDisposition(affectableStats.disposition() - PhyStats.IS_GLOWING);
        if (CMLib.flags().isLightSource(affected))
            affectableStats.setDisposition(affectableStats.disposition() - PhyStats.IS_LIGHTSOURCE);
        affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_DARK);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final Physical target = mob.location();

        if (target.fetchEffect(this.ID()) != null) {
            mob.tell(mob, null, null, L("Deep Darkness is already been here!"));
            return false;
        }

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {

            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), L((auto ? "D" : "^S<S-NAME> chant(s) deeply and d") + "arkness descends.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, mob.location(), asLevel, 0);
                mob.location().recoverRoomStats();
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> chant(s) deeply, but nothing happens."));

        // return whether it worked
        return success;
    }
}
