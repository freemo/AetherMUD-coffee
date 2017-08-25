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
package com.syncleus.aethermud.game.Abilities.Archon;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PlayerStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.Log;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Archon_Stinkify extends ArchonSkill {
    private final static String localizedName = CMLib.lang().L("Stinkify");
    private static final String[] triggerStrings = I(new String[]{"STINKIFY"});

    @Override
    public String ID() {
        return "Archon_Stinkify";
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
        return CAN_MOBS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_ARCHON;
    }

    @Override
    public int maxRange() {
        return adjustedMaxInvokerRange(1);
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = getTargetAnywhere(mob, commands, givenTarget, false, true, true);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MASK_MOVE | CMMsg.TYP_JUSTICE | (auto ? CMMsg.MASK_ALWAYS : 0), auto ? L("A stink cloud surrounds <T-NAME>!") : L("^F<S-NAME> stinkif(ys) <T-NAMESELF>.^?"));
            CMLib.color().fixSourceFightColor(msg);
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (target.playerStats() != null) {
                    mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> <S-IS-ARE> stinkier!"));
                    target.playerStats().adjHygiene(PlayerStats.HYGIENE_DELIMIT + 1);
                    Log.sysOut("Stinkify", mob.Name() + " stinkied " + target.name() + ".");
                } else
                    mob.tell(mob, target, null, L("<T-NAME> is a mob.  Try a player."));
            }
        } else
            return beneficialVisualFizzle(mob, target, L("<S-NAME> attempt(s) to stinkify <T-NAMESELF>, but fail(s)."));
        return success;
    }
}
