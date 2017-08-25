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
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class Song_PirateShanty extends Song {
    private final static String localizedName = CMLib.lang().L("Pirate Shanty");
    public Set<MOB> singers = new HashSet<MOB>();

    @Override
    public String ID() {
        return "Song_PirateShanty";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    public void executeMsg(Environmental myHost, CMMsg msg) {
        super.executeMsg(myHost, msg);
        if ((msg.sourceMinor() == CMMsg.TYP_SPEAK)
            && (msg.tool() != null)
            && (msg.tool().ID().equals(ID()))
            && (!singers.contains(msg.source()))) {
            synchronized (singers) {
                singers.add(msg.source());
            }
        }
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((!super.tick(ticking, tickID)) || (!(affected instanceof MOB)))
            return false;

        final MOB M = (MOB) affected;
        if (M == null)
            return false;
        final Room R = CMLib.map().roomLocation(M);
        if (R == null)
            return false;
        int ct = 0;
        synchronized (singers) {
            for (Iterator<MOB> i = singers.iterator(); i.hasNext(); ) {
                if (i.next().location() != R)
                    i.remove();
            }
            ct = singers.size();
        }
        if (ct > 0) {
            M.curState().adjMovement(ct * ct, M.maxState());
            M.curState().adjFatigue(-(ct * ct), M.maxState());
        }
        return true;
    }
}
