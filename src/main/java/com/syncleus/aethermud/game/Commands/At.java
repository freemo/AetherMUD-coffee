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

import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.CMSecurity;
import com.planet_ink.game.core.collections.XVector;

import java.util.List;


public class At extends StdCommand {
    private final String[] access = I(new String[]{"AT"});

    public At() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        commands.remove(0);
        if (commands.size() == 0) {
            mob.tell(L("At where do what?"));
            return false;
        }
        final String cmd = commands.get(0);
        commands.remove(0);
        Room room = CMLib.map().getRoom(cmd.toString());
        if (room == null)
            room = CMLib.map().findWorldRoomLiberally(mob, cmd, "APMIR", 100, 120000);
        if (room == null) {
            if (CMSecurity.isAllowedAnywhere(mob, CMSecurity.SecFlag.AT))
                mob.tell(L("At where? Try a Room ID, player name, area name, or room text!"));
            else
                mob.tell(L("You aren't powerful enough to do that."));
            return false;
        }
        if (!CMSecurity.isAllowed(mob, room, CMSecurity.SecFlag.AT)) {
            mob.tell(L("You aren't powerful enough to do that there."));
            return false;
        }
        final Room R = mob.location();
        if (R != room)
            room.bringMobHere(mob, false);
        mob.doCommand(new XVector<String>(CMParms.toStringArray(commands)), metaFlags);
        if (mob.location() != R)
            R.bringMobHere(mob, false);
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

    @Override
    public boolean securityCheck(MOB mob) {
        return CMSecurity.isAllowedAnywhere(mob, CMSecurity.SecFlag.AT);
    }

}
