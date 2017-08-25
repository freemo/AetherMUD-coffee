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
package com.syncleus.aethermud.game.Abilities.Traps;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Exits.interfaces.Exit;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.Directions;
import com.syncleus.aethermud.game.core.collections.Pair;
import com.syncleus.aethermud.game.core.collections.PairVector;
import com.syncleus.aethermud.game.core.interfaces.CMObject;
import com.syncleus.aethermud.game.core.interfaces.Environmental;

import java.util.Iterator;


public class Trap_ExitRoom extends Trap_Trap {
    private final static String localizedName = CMLib.lang().L("Exit Trap");
    public PairVector<MOB, Integer> safeDirs = new PairVector<MOB, Integer>();

    @Override
    public String ID() {
        return "Trap_ExitRoom";
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

    protected boolean mayNotLeave() {
        return true;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public CMObject copyOf() {
        final Trap_ExitRoom obj = (Trap_ExitRoom) super.copyOf();
        if (safeDirs == null)
            obj.safeDirs = null;
        else
            obj.safeDirs = (PairVector) safeDirs.clone();
        return obj;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (sprung)
            return super.okMessage(myHost, msg);
        if (!super.okMessage(myHost, msg))
            return false;

        if (msg.amITarget(affected) && (affected instanceof Room) && (msg.tool() instanceof Exit)) {
            final Room room = (Room) affected;
            if ((msg.targetMinor() == CMMsg.TYP_LEAVE) || (msg.targetMinor() == CMMsg.TYP_FLEE)) {
                final int movingInDir = CMLib.map().getExitDir(room, (Exit) msg.tool());
                if ((movingInDir != Directions.DOWN) && (movingInDir != Directions.UP)) {
                    synchronized (safeDirs) {
                        for (final Iterator<Pair<MOB, Integer>> i = safeDirs.iterator(); i.hasNext(); ) {
                            final Pair<MOB, Integer> p = i.next();
                            if (p.first == msg.source()) {
                                i.remove();
                                if (movingInDir == p.second.intValue())
                                    return true;
                                spring(msg.source());
                                return !mayNotLeave();
                            }
                        }
                    }
                }
            } else if (msg.targetMinor() == CMMsg.TYP_ENTER) {
                final int movingInDir = CMLib.map().getExitDir((Room) affected, (Exit) msg.tool());
                if ((movingInDir != Directions.DOWN) && (movingInDir != Directions.UP)) {
                    synchronized (safeDirs) {
                        final int dex = safeDirs.indexOf(msg.source());
                        if (dex >= 0)
                            safeDirs.remove(dex);
                        while (safeDirs.size() > room.numInhabitants() + 1)
                            safeDirs.remove(0);
                        safeDirs.add(new Pair<MOB, Integer>(msg.source(), Integer.valueOf(movingInDir)));
                    }
                }
            }
        }
        return true;
    }
}
