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

import com.planet_ink.coffee_mud.Items.interfaces.BoardableShip;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.Directions;

import java.util.List;


public class Aft extends Go {
    private final String[] access = I(new String[]{"AFT"});

    public Aft() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        int direction = Directions.SOUTH;
        if ((commands != null) && (commands.size() > 1)) {
            final int nextDir = CMLib.directions().getDirectionCode(commands.get(1));
            if (nextDir == Directions.EAST)
                direction = Directions.SOUTHEAST;
            else if (nextDir == Directions.WEST)
                direction = Directions.SOUTHWEST;
        }
        if (!standIfNecessary(mob, metaFlags, true))
            return false;
        if (mob.isAttributeSet(MOB.Attrib.AUTORUN))
            CMLib.tracking().run(mob, direction, false, false, false);
        else
            CMLib.tracking().walk(mob, direction, false, false, false);
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

    @Override
    public boolean securityCheck(MOB mob) {
        return (mob == null) || (mob.isMonster()) || (mob.location() == null)
            || (mob.location() instanceof BoardableShip) || (mob.location().getArea() instanceof BoardableShip);
    }
}
