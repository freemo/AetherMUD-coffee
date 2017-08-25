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
package com.planet_ink.game.Abilities.Fighter;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Fighter_Rescue extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Rescue");
    private static final String[] triggerStrings = I(new String[]{"RESCUE", "RES"});

    @Override
    public String ID() {
        return "Fighter_Rescue";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_OTHERS;
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
        return Ability.ACODE_SKILL | Ability.DOMAIN_MARTIALLORE;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT;
    }

    @Override
    public long flags() {
        return FLAG_AGGROFYING;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (mob == null)
            return false;
        final MOB imfighting = mob.getVictim();
        MOB target = null;

        if ((commands.size() == 0)
            && (imfighting != null)
            && (imfighting != mob)
            && (imfighting.getVictim() != null)
            && (imfighting.getVictim() != mob))
            target = imfighting.getVictim();

        if (target == null)
            target = getTarget(mob, commands, givenTarget);

        if (target == null)
            return false;
        final MOB monster = target.getVictim();

        if ((target.amDead()) || (monster == null) || (monster.amDead())) {
            mob.tell(L("@x1 isn't fighting anyone!", target.charStats().HeShe()));
            return false;
        }

        if (monster.getVictim() == mob) {
            mob.tell(L("You are already taking the blows from @x1.", monster.name()));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        String str = null;
        if (success) {
            str = L("^F^<FIGHT^><S-NAME> rescue(s) <T-NAMESELF>!^</FIGHT^>^?");
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_NOISYMOVEMENT, str);
            CMLib.color().fixSourceFightColor(msg);
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                monster.setVictim(mob);
            }
        } else {
            str = L("<S-NAME> attempt(s) to rescue <T-NAMESELF>, but fail(s).");
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_NOISYMOVEMENT, str);
            if (mob.location().okMessage(mob, msg))
                mob.location().send(mob, msg);
        }

        return success;
    }

}
