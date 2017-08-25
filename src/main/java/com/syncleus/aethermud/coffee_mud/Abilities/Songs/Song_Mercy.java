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
package com.planet_ink.coffee_mud.Abilities.Songs;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

import java.util.List;


public class Song_Mercy extends Song {
    private final static String localizedName = CMLib.lang().L("Mercy");
    protected Room lastRoom = null;
    protected int count = 3;

    @Override
    public String ID() {
        return "Song_Mercy";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_OTHERS;
    }

    @Override
    protected boolean HAS_QUANTITATIVE_ASPECT() {
        return false;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (!(affected instanceof MOB))
            return true;
        final MOB mob = (MOB) affected;
        if (mob.location() != lastRoom) {
            count = 3;
            lastRoom = mob.location();
        } else
            count--;
        return true;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        final MOB mob = (MOB) affected;
        if (((msg.targetMajor() & CMMsg.MASK_MALICIOUS) > 0)
            && (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))
            && (mob.location() != null)
            && ((msg.amITarget(mob)))
            && ((count > 0) || (lastRoom == null) || (lastRoom != mob.location()))) {
            final MOB target = (MOB) msg.target();
            if ((!target.isInCombat())
                && (mob.location() == target.location())
                && (msg.source().getVictim() != target)) {
                msg.source().tell(L("You feel like showing @x1 mercy right now.", target.name(msg.source())));
                if (target.getVictim() == msg.source()) {
                    target.makePeace(true);
                    target.setVictim(null);
                }
                return false;
            }

        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        count = 3;
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        count = 3;
        return true;
    }
}
