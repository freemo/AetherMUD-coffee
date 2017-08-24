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
package com.planet_ink.coffee_mud.Commands;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.CMath;

import java.util.Enumeration;
import java.util.List;


public class Weather extends StdCommand {
    private final String[] access = I(new String[]{"WEATHER"});

    public Weather() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        final Room room = mob.location();
        if (room == null)
            return false;
        if ((commands.size() > 1) && ((room.domainType() & Room.INDOORS) == 0) && (commands.get(1).equalsIgnoreCase("WORLD"))) {
            final StringBuffer tellMe = new StringBuffer("");
            for (final Enumeration<Area> a = CMLib.map().areas(); a.hasMoreElements(); ) {
                final Area A = a.nextElement();
                if ((CMLib.flags().canAccess(mob, A))
                    && (!CMath.bset(A.flags(), Area.FLAG_INSTANCE_CHILD)))
                    tellMe.append(CMStrings.padRight(A.name(), 20) + ": " + A.getClimateObj().weatherDescription(room) + "\n\r");
            }
            mob.tell(tellMe.toString());
            return false;
        }
        mob.tell(room.getArea().getClimateObj().weatherDescription(room));
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
