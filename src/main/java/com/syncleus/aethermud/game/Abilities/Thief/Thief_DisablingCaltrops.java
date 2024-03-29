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

import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;

import java.util.HashSet;


public class Thief_DisablingCaltrops extends Thief_Caltrops {
    private final static String localizedName = CMLib.lang().L("Disabling Caltrops");
    private static final String[] triggerStrings = I(new String[]{"DISABLINGCALTROPS"});

    @Override
    public String ID() {
        return "Thief_DisablingCaltrops";
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
    public String caltropTypeName() {
        return CMLib.lang().L("disabling ");
    }

    @Override
    public void spring(MOB mob) {
        if ((!invoker().mayIFight(mob))
            || (invoker().getGroupMembers(new HashSet<MOB>()).contains(mob))
            || (CMLib.dice().rollPercentage() < mob.charStats().getSave(CharStats.STAT_SAVE_TRAPS)))
            mob.location().show(mob, affected, this, CMMsg.MSG_OK_ACTION, L("<S-NAME> avoid(s) some @x1caltrops on the floor.", caltropTypeName()));
        else if (mob.curState().getMovement() > 6) {
            mob.curState().adjMovement(-CMLib.dice().roll(3 + getX1Level(mob), 6, 20), mob.maxState());
            mob.location().show(invoker(), mob, this, CMMsg.MSG_OK_ACTION, L("The @x1caltrops on the ground disable <T-NAME>", caltropTypeName()));
        }
        // does not set sprung flag -- as this trap never goes out of use
    }
}
