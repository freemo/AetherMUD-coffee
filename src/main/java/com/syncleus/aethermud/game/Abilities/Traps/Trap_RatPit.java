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
package com.planet_ink.game.Abilities.Traps;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Items.interfaces.CagedAnimal;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;


public class Trap_RatPit extends Trap_SnakePit {
    private final static String localizedName = CMLib.lang().L("rat pit");

    @Override
    public String ID() {
        return "Trap_RatPit";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ROOMS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    protected int trapLevel() {
        return 12;
    }

    @Override
    public String requiresToSet() {
        return "some caged rats";
    }

    @Override
    protected Item getCagedAnimal(MOB mob) {
        if (mob == null)
            return null;
        if (mob.location() == null)
            return null;
        for (int i = 0; i < mob.location().numItems(); i++) {
            final Item I = mob.location().getItem(i);
            if (I instanceof CagedAnimal) {
                final MOB M = ((CagedAnimal) I).unCageMe();
                if ((M != null) && (M.baseCharStats().getMyRace().racialCategory().equalsIgnoreCase("Rodent")))
                    return I;
            }
        }
        return null;
    }

}
