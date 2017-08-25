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
package com.syncleus.aethermud.game.Commands;

import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.collections.XVector;

import java.util.List;
import java.util.Vector;


public class NoFollow extends Follow {
    private final String[] access = I(new String[]{"NOFOLLOW", "NOFOL"});

    public NoFollow() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        Vector<String> origCmds = new XVector<String>(commands);
        if (commands.size() > 1) {
            if (commands.get(0).equalsIgnoreCase("UNFOLLOW")) {
                unfollow(mob, ((commands.size() > 1) && (commands.get(1).equalsIgnoreCase("QUIETLY"))));
                return false;
            }
            final String name = CMParms.combine(commands, 1);
            if (name.equalsIgnoreCase("ALL")) {
                if (mob.numFollowers() == 0)
                    CMLib.commands().doCommandFail(mob, origCmds, L("No one is following you!"));
                else
                    unfollow(mob, ((commands.size() > 1) && (commands.get(1).equalsIgnoreCase("QUIETLY"))));
                return false;
            }
            MOB M = mob.fetchFollower(name);
            if ((M == null) && (mob.location() != null)) {
                M = mob.location().fetchInhabitant(name);
                if (M != null)
                    CMLib.commands().doCommandFail(mob, origCmds, L("@x1 is not following you!", M.name(mob)));
                else
                    CMLib.commands().doCommandFail(mob, origCmds, L("There is noone here called '@x1' following you!", name));
                return false;
            }
            if ((mob.location() != null) && (M != null) && (M.amFollowing() == mob)) {
                nofollow(M, true, false);
                return true;
            }
            CMLib.commands().doCommandFail(mob, origCmds, L("There is noone called '@x1' following you!", name));
            return false;
        }
        if (!mob.isAttributeSet(MOB.Attrib.NOFOLLOW)) {
            mob.setAttribute(MOB.Attrib.NOFOLLOW, true);
            //unfollow(mob,false);
            mob.tell(L("You are no longer accepting new followers."));
        } else {
            mob.setAttribute(MOB.Attrib.NOFOLLOW, false);
            mob.tell(L("You are now accepting new followers."));
        }
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
