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
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;


public class Spell_Dismissal extends Spell {

    private final static String localizedName = CMLib.lang().L("Dismissal");

    @Override
    public String ID() {
        return "Spell_Dismissal";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_CONJURATION;
    }

    @Override
    public long flags() {
        return Ability.FLAG_MOVING | Ability.FLAG_TRANSPORTING;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if ((target instanceof MOB)
            && (((MOB) target).amFollowing() == mob)
            && (((MOB) target).isMonster()))
            return Ability.QUALITY_INDIFFERENT;
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        boolean success = false;
        if (target.getStartRoom() == null) {
            int levelDiff = target.phyStats().level() - (mob.phyStats().level() + (2 * getXLEVELLevel(mob)));
            if (levelDiff < 0)
                levelDiff = 0;
            success = proficiencyCheck(mob, -(levelDiff * 5), auto);
        } else
            success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MASK_MOVE | somanticCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> point(s) at <T-NAMESELF> and utter(s) a dismissive spell!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    if (target.getStartRoom() == null)
                        target.destroy();
                    else {
                        mob.location().show(mob, target, CMMsg.MSG_OK_ACTION, L("<T-NAME> vanish(es) in dismissal!"));
                        target.getStartRoom().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> appear(s)!"));
                        target.getStartRoom().bringMobHere(target, false);
                        CMLib.commands().postLook(target, true);
                    }
                    mob.location().recoverRoomStats();
                }

            }

        } else
            maliciousFizzle(mob, target, L("<S-NAME> point(s) at <T-NAMESELF> and utter(s) a dismissive but fizzled spell!"));

        // return whether it worked
        return success;
    }
}
