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
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;

import java.util.HashSet;


public class Thief_ImprovedCaltrops extends Thief_Caltrops {
    private final static String localizedName = CMLib.lang().L("Improved Caltrops");
    private static final String[] triggerStrings = I(new String[]{"IMPROVEDCALTROPS"});

    @Override
    public String ID() {
        return "Thief_ImprovedCaltrops";
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
        return CMLib.lang().L("improved ");
    }

    @Override
    public void spring(MOB mob) {
        final MOB invoker = (invoker() != null) ? invoker() : CMLib.map().deity();
        if ((!invoker.mayIFight(mob))
            || (invoker.getGroupMembers(new HashSet<MOB>()).contains(mob))
            || ((CMLib.dice().rollPercentage() - getXLEVELLevel(invoker())) < mob.charStats().getSave(CharStats.STAT_SAVE_TRAPS)))
            mob.location().show(mob, affected, this, CMMsg.MSG_OK_ACTION, L("<S-NAME> avoid(s) some @x1caltrops on the floor.", caltropTypeName()));
        else {
            CMLib.combat().postDamage(invoker, mob, null, CMLib.dice().roll(8, 6, 5 * adjustedLevel(invoker(), 0)),
                CMMsg.MASK_MALICIOUS | CMMsg.TYP_JUSTICE, Weapon.TYPE_PIERCING, L("The @x1caltrops on the ground <DAMAGE> <T-NAME>.", caltropTypeName()));
        }
        // does not set sprung flag -- as this trap never goes out of use
    }
}
