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
package com.syncleus.aethermud.game.core.intermud.i3.packets;

import com.syncleus.aethermud.game.core.intermud.i3.server.I3Server;

import java.util.Vector;

/**
 * Copyright (c) 2010-2017 Bo Zimmerman
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
@SuppressWarnings("rawtypes")
public class ShutdownPacket extends Packet {
    public ShutdownPacket() {
        super();
        type = Packet.SHUTDOWN;
    }

    public ShutdownPacket(Vector v) {
        super(v);
        type = Packet.SHUTDOWN;
        target_mud = Intermud.getNameServer().name;
    }

    @Override
    public void send() throws InvalidPacketException {
        super.send();
    }

    @Override
    public String toString() {
        return "({\"shutdown\",5,\"" + I3Server.getMudName() + "\",0,\"" + target_mud + "\",0,0,})";
    }
}
