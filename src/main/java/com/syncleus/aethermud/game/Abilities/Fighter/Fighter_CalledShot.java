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

import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.Weapon;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;


public class Fighter_CalledShot extends Fighter_CalledStrike {
    private final static String localizedName = CMLib.lang().L("Called Shot");
    private static final String[] triggerStrings = I(new String[]{"CALLEDSHOT"});

    @Override
    public String ID() {
        return "Fighter_CalledShot";
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
    protected boolean prereqs(MOB mob, boolean quiet) {
        if (mob.isInCombat() && (mob.rangeToTarget() == 0)) {
            if (!quiet)
                mob.tell(L("You are too close to perform a called shot!"));
            return false;
        }

        final Item w = mob.fetchWieldedItem();
        if ((w == null) || (!(w instanceof Weapon))) {
            if (!quiet)
                mob.tell(L("You need a weapon to perform a called shot!"));
            return false;
        }
        final Weapon wp = (Weapon) w;
        if ((wp.weaponClassification() != Weapon.CLASS_RANGED) && (wp.weaponClassification() != Weapon.CLASS_THROWN)) {
            if (!quiet)
                mob.tell(L("You cannot shoot with @x1!", wp.name()));
            return false;
        }
        return true;
    }
}
