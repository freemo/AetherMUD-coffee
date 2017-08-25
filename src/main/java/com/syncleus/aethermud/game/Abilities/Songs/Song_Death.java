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


public class Song_Death extends Song {
    private final static String localizedName = CMLib.lang().L("Death");

    @Override
    public String ID() {
        return "Song_Death";
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
    protected int getXMAXRANGELevel(MOB mob) {
        return 0;
    } // people are complaining about multi-room death

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

        final int hpLoss = (int) Math.round(Math.floor(mob.curState().getHitPoints() * (0.07 + (0.02 * (1 + super.getXLEVELLevel(invoker()))))));
        CMLib.combat().postDamage(invoker, mob, this, hpLoss, CMMsg.MASK_ALWAYS | CMMsg.TYP_UNDEAD, Weapon.TYPE_BURSTING, L("^SThe painful song <DAMAGE> <T-NAME>!^?"));
        return true;
    }

}
