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
package com.syncleus.aethermud.game.Abilities.Fighter;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMStrings;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Fighter_SizeOpponent extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Opponent Knowledge");
    private static final String[] triggerStrings = I(new String[]{"SIZEUP", "OPPONENT"});

    @Override
    public String ID() {
        return "Fighter_SizeOpponent";
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
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_COMBATLORE;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        // now see if it worked
        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_LOOK | (auto ? CMMsg.MASK_ALWAYS : 0), L("<S-NAME> size(s) up <T-NAMESELF> with <S-HIS-HER> eyes."));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final StringBuffer buf = new StringBuffer(L("@x1 looks to have @x2 out of @x3 hit points.\n\r", target.name(mob), "" + target.curState().getHitPoints(), "" + target.maxState().getHitPoints()));
                buf.append(L("@x1 looks like @x2 is @x3 and is @x4.", target.charStats().HeShe(), target.charStats().heshe(), CMStrings.removeColors(CMLib.combat().fightingProwessStr(target)), CMStrings.removeColors(CMLib.combat().armorStr(target))));
                mob.tell(buf.toString());
            }
        } else
            return beneficialVisualFizzle(mob, target, L("<S-NAME> size(s) up <T-NAMESELF> with <S-HIS-HER> eyes, but look(s) confused."));

        // return whether it worked
        return success;
    }
}
