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
package com.planet_ink.game.Abilities.Songs;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.Weapon;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.HashSet;


public class Dance_Flamenco extends Dance {
    private final static String localizedName = CMLib.lang().L("Flamenco");

    @Override
    public String ID() {
        return "Dance_Flamenco";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;

        final MOB mob = (MOB) affected;
        if (mob == null)
            return false;
        if (mob == invoker)
            return true;
        final MOB invoker = (invoker() != null) ? invoker() : mob;
        final int hpLoss = CMLib.dice().roll(adjustedLevel(invoker(), 0), 8, 0)
            + CMLib.dice().roll(invoker().getGroupMembers(new HashSet<MOB>()).size() - 1, 8, 0);
        CMLib.combat().postDamage(invoker, mob, this, hpLoss, CMMsg.MASK_ALWAYS | CMMsg.TYP_CAST_SPELL, Weapon.TYPE_BURSTING, L("^SThe flamenco dance <DAMAGE> <T-NAME>!^?"));
        return true;
    }

}
