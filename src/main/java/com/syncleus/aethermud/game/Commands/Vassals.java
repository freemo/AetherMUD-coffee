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
package com.planet_ink.game.Commands;

import com.planet_ink.game.Libraries.interfaces.PlayerLibrary;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMStrings;

import java.util.List;


public class Vassals extends StdCommand {
    private final String[] access = I(new String[]{"VASSALS"});

    public Vassals() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        mob.tell(L("The following players are in your service:"));
        List<PlayerLibrary.ThinPlayer> players = CMLib.database().vassals(mob.Name());
        final StringBuilder str = new StringBuilder("");
        str.append("[");
        str.append(CMStrings.padRight(L("Race"), 8) + " ");
        str.append(CMStrings.padRight(L("Class"), 10) + " ");
        str.append(CMStrings.padRight(L("Lvl"), 4) + " ");
        str.append(CMStrings.padRight(L("Exp/Lvl"), 17));
        str.append(L("] Character name\n\r"));
        for (PlayerLibrary.ThinPlayer tM : players) {
            final MOB M = CMLib.players().getPlayer(tM.name());
            if (M == null) {
                str.append("[");
                str.append(CMStrings.padRight(tM.race(), 8) + " ");
                str.append(CMStrings.padRight(tM.charClass(), 10) + " ");
                str.append(CMStrings.padRight(Integer.toString(tM.level()), 4) + " ");
                str.append(CMStrings.padRight(tM.exp() + "/" + tM.expLvl(), 17));
                str.append("] " + CMStrings.padRight(tM.name(), 15));
                str.append("\n\r");
            } else {
                str.append("[");
                str.append(CMStrings.padRight(M.charStats().getMyRace().name(), 8) + " ");
                str.append(CMStrings.padRight(M.charStats().getCurrentClass().name(M.charStats().getCurrentClassLevel()), 10) + " ");
                str.append(CMStrings.padRight("" + M.phyStats().level(), 4) + " ");
                str.append(CMStrings.padRight(M.getExperience() + "/" + M.getExpNextLevel(), 17));
                str.append("] " + CMStrings.padRight(M.name(), 15));
                str.append("\n\r");
            }
        }
        mob.tell(str.toString());
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
