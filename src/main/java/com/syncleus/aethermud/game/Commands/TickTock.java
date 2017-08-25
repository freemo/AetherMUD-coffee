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

import com.planet_ink.game.Libraries.interfaces.CMLibrary;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.CMSecurity;
import com.planet_ink.game.core.CMath;

import java.util.Enumeration;
import java.util.List;


@SuppressWarnings("rawtypes")
public class TickTock extends StdCommand {
    private final String[] access = I(new String[]{"TICKTOCK"});

    public TickTock() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        final String s = CMParms.combine(commands, 1).toLowerCase();
        try {
            if (CMath.isInteger(s)) {
                int h = CMath.s_int(s);
                if (h == 0)
                    h = 1;
                mob.tell(L("..tick..tock.."));
                mob.location().getArea().getTimeObj().tickTock(h);
                mob.location().getArea().getTimeObj().save();
            } else if (s.startsWith("clantick"))
                CMLib.clans().tickAllClans();
            else {
                for (final Enumeration e = CMLib.libraries(); e.hasMoreElements(); ) {
                    final CMLibrary lib = (CMLibrary) e.nextElement();
                    if ((lib.getServiceClient() != null) && (s.equalsIgnoreCase(lib.getServiceClient().getName()))) {
                        if (lib instanceof Runnable)
                            ((Runnable) lib).run();
                        else
                            lib.getServiceClient().tickTicker(true);
                        mob.tell(L("Done."));
                        return false;
                    }
                }
                mob.tell(L("Ticktock what?  Enter a number of mud-hours, or clanticks, or thread id."));
            }
        } catch (final Exception e) {
            mob.tell(L("Ticktock failed: @x1", e.getMessage()));
        }

        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

    @Override
    public boolean securityCheck(MOB mob) {
        return CMSecurity.isAllowed(mob, mob.location(), CMSecurity.SecFlag.TICKTOCK);
    }

}
