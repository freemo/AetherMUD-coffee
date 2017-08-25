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
package com.syncleus.aethermud.game.Abilities.Thief;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Thief_SilentOpen extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Silent Open");
    private static final String[] triggerStrings = I(new String[]{"SILENTOPEN", "SOPEN"});
    public int code = 0;

    @Override
    public String ID() {
        return "Thief_SilentOpen";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_ITEMS | Ability.CAN_EXITS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_STEALTHY;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT | USAGE_MANA;
    }

    @Override
    public int abilityCode() {
        return code;
    }

    @Override
    public void setAbilityCode(int newCode) {
        code = newCode;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if ((commands.size() < 1) && (givenTarget == null)) {
            mob.tell(L("What would you like to open?"));
            return false;
        }
        final Environmental item = super.getAnyTarget(mob, commands, givenTarget, Wearable.FILTER_UNWORNONLY);
        if (item == null)
            return false;
        if ((item instanceof MOB)
            || (item instanceof Area)
            || (item instanceof Room)) {
            mob.tell(L("You can't open that!"));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, item, this, CMMsg.MSG_THIEF_ACT, L("<S-NAME> open(s) <T-NAME>."), CMMsg.NO_EFFECT, null, CMMsg.NO_EFFECT, null);
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                CMLib.commands().postOpen(mob, item, true);
            }
        } else {
            beneficialVisualFizzle(mob, item, L("<S-NAME> attempt(s) to open <T-NAME> quietly, but fail(s)."));
            CMLib.commands().postOpen(mob, item, false);
        }
        return success;
    }
}
