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
package com.syncleus.aethermud.game.Abilities.Poisons;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;


public class Poison_Liquor extends Poison_Alcohol {
    private final static String localizedName = CMLib.lang().L("Liquor");
    private static final String[] triggerStrings = I(new String[]{"LIQUORUP"});

    @Override
    public String ID() {
        return "Poison_Liquor";
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
    public int classificationCode() {
        return Ability.ACODE_POISON;
    }

    @Override
    protected int alchoholContribution() {
        return 2;
    }

    @Override
    protected int level() {
        return 2;
    }

    @Override
    public void unInvoke() {
        int drunkness = this.drunkness;
        MOB mob = null;
        if (affected instanceof MOB) {
            mob = (MOB) affected;
            if ((CMLib.dice().rollPercentage() < (drunkness * 10)) && (!((MOB) affected).isMonster())) {
                final Ability A = CMClass.getAbility("Disease_Migraines");
                if (A != null)
                    A.invoke(mob, mob, true, 0);
            }
            CMLib.commands().postStand(mob, true);
        }
        super.unInvoke();
        if ((mob != null) && (!mob.isInCombat()) && (drunkness > 0))
            mob.location().show(mob, null, CMMsg.MSG_SLEEP, L("<S-NAME> curl(s) up on the ground and fall(s) asleep."));
    }
}
