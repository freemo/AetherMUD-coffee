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
package com.syncleus.aethermud.game.Abilities.Common;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class SlaveTrading extends CommonSkill {
    private final static String localizedName = CMLib.lang().L("Slave Trading");
    private static final String[] triggerStrings = I(new String[]{"SLAVETRADING", "SLAVETRADE", "SLAVESELL", "SSELL"});

    @Override
    public String ID() {
        return "SlaveTrading";
    }

    @Override
    public String name() {
        return localizedName;
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
        return Ability.ACODE_COMMON_SKILL | Ability.DOMAIN_CRIMINAL;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        commands.add(0, "SELL");
        final Environmental shopkeeper = CMLib.english().parseShopkeeper(mob, commands, "Sell whom to whom?");
        if (shopkeeper == null)
            return false;
        if (commands.size() == 0) {
            commonTell(mob, L("Sell whom?"));
            return false;
        }

        final String str = CMParms.combine(commands, 0);
        final MOB M = mob.location().fetchInhabitant(str);
        if (M != null) {
            if (!CMLib.flags().canBeSeenBy(M, mob)) {
                commonTell(mob, L("You don't see anyone called '@x1' here.", str));
                return false;
            }
            if (!M.isMonster()) {
                commonTell(mob, M, null, L("You can't sell <T-NAME> as a slave."));
                return false;
            }
            if (CMLib.flags().isAnimalIntelligence(M)) {
                commonTell(mob, M, null, L("You can't sell <T-NAME> as a slave.  Animals are not slaves."));
                return false;
            }

            final Ability oldEnslaveA = M.fetchEffect("Skill_Enslave");
            if ((oldEnslaveA == null) || (!oldEnslaveA.text().equals(mob.Name()))) {
                commonTell(mob, M, null, L("<T-NAME> do(es)n't seem to be your slave."));
                return false;
            }
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        if (proficiencyCheck(mob, 0, auto)) {
            final CMMsg msg = CMClass.getMsg(mob, shopkeeper, M, CMMsg.MSG_SELL, L("<S-NAME> sell(s) <O-NAME> to <T-NAME>."));
            if (mob.location().okMessage(mob, msg))
                mob.location().send(mob, msg);
        } else
            beneficialWordsFizzle(mob, shopkeeper, L("<S-NAME> <S-IS-ARE>n't able to strike a deal with <T-NAME>."));
        return true;
    }
}
