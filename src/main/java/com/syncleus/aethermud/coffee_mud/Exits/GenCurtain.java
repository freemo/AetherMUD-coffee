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
import com.planet_ink.coffee_mud.core.interfaces.Environmental;


public class GenCurtain extends GenExit {
    protected Object passSync = new Object();
    protected boolean passThrough = false;

    public GenCurtain() {
        super();
        name = "a curtain";
        displayText = "";
        description = "An opague cloth is hanging across the doorway.";
        hasADoor = true;
        hasALock = false;
        isOpen = false;
        doorDefaultsClosed = true;
        doorDefaultsLocked = false;
        closedText = "a curtain";
        doorName = "curtain";
        closeName = "close";
        openName = "open";
    }

    @Override
    public String ID() {
        return "GenCurtain";
    }

    @Override
    public boolean isOpen() {
        return isOpen || passThrough;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (((msg.amITarget(this)) || (msg.tool() == this))
            && (msg.targetMinor() == CMMsg.TYP_ENTER)) {
            synchronized (passSync) {
                try {
                    passThrough = true;
                    if (!super.okMessage(myHost, msg))
                        return false;
                } finally {
                    passThrough = false;
                }
            }
            return true;
        }
        return super.okMessage(myHost, msg);
    }
}
