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
package com.planet_ink.coffee_mud.Exits;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;


public class Impassable extends GenExit {
    public Impassable() {
        super();
        name = "a blocked way";
        description = "It doesn't look like you can go that way.";
    }

    @Override
    public String ID() {
        return "Impassable";
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        final MOB mob = msg.source();
        if ((!msg.amITarget(this)) && (msg.tool() != this))
            return true;
        else if (msg.targetMajor(CMMsg.MASK_MOVE)) {
            mob.tell(L("You can't go that way."));
            return false;
        }
        return true;
    }
}
