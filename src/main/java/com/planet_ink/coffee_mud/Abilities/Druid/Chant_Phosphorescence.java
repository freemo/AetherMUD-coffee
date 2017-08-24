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

import java.util.List;


public class Chant_Phosphorescence extends Chant {
    private final static String localizedName = CMLib.lang().L("Phosphorescence");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Phosphorescence)");

    @Override
    public String ID() {
        return "Chant_Phosphorescence";
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
        return Ability.ACODE_CHANT | Ability.DOMAIN_NATURELORE;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        if (!(affected instanceof Room))
            affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_LIGHTSOURCE);
        if (CMLib.flags().isInDark(affected))
            affectableStats.setDisposition(affectableStats.disposition() - PhyStats.IS_DARK);
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        final Room room = ((MOB) affected).location();
        if (canBeUninvoked())
            room.show(mob, null, CMMsg.MSG_OK_VISUAL, L("The phosphorescent glow around <S-NAME> dims."));
        super.unInvoke();
        room.recoverRoomStats();
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (!CMLib.flags().canBeSeenBy(mob.location(), mob))
                return super.castingQuality(mob, target, Ability.QUALITY_BENEFICIAL_SELF);
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        MOB target = mob;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;

        if (target.fetchEffect(this.ID()) != null) {
            target.tell(L("You are already phosphorescent."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (!success) {
            return beneficialWordsFizzle(mob, mob.location(), L("<S-NAME> chant(s) for phosphorescence, but fail(s)."));
        }

        final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> begin(s) to glow!") : L("^S<S-NAME> chant(s), causing <S-HIM-HER> skin to become phosphorescent!^?"));
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            beneficialAffect(mob, target, asLevel, 0);
            target.location().recoverRoomStats(); // attempt to handle followers
        }

        return success;
    }
}
