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
package com.syncleus.aethermud.game.core.interfaces;

import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Locales.interfaces.Room;


/**
 * An object which is permitted to monitor game events in AetherMud.
 * @author Bo Zimmerman
 */
public interface MsgMonitor {
    /**
     * The general message event monitor for the object.  The messages
     * have already been through an approval process, so this method is
     * called only to see the final execution of the meaning of the
     * message.
     * @param room the room the message was sent to
     * @param msg the CMMsg that needs to be executed
     * @see com.syncleus.aethermud.game.Common.interfaces.CMMsg
     */
    public void monitorMsg(Room room, CMMsg msg);
}
