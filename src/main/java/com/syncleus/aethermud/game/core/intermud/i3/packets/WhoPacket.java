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
package com.planet_ink.game.core.intermud.i3.packets;

import com.planet_ink.game.core.intermud.i3.server.I3Server;

import java.util.Vector;

/**
 * Copyright (c) 1996 George Reese
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
public class WhoPacket extends Packet {
    public Vector who = null;

    public WhoPacket() {
        super();
        type = Packet.WHO_REQUEST;
    }

    public WhoPacket(Vector v) {
        super(v);
        if (v.size() == 6) {
            type = Packet.WHO_REQUEST;
        } else {
            type = Packet.WHO_REPLY;
            who = (Vector) v.elementAt(6);
        }
    }

    @Override
    public void send() throws InvalidPacketException {
        if (type == Packet.WHO_REPLY && who == null) {
            throw new InvalidPacketException();
        }
        super.send();
    }

    @Override
    public String toString() {
        if (type == Packet.WHO_REQUEST) {
            return "({\"who-req\",5,\"" + I3Server.getMudName() +
                "\",\"" + sender_name + "\",\"" + target_mud +
                "\",0,})";
        }
        String cmd = "({\"who-reply\",5,\"" + I3Server.getMudName() +
            "\",0,\"" + target_mud + "\",\"" + target_name + "\",({";
        int i;

        for (i = 0; i < who.size(); i++) {
            final Vector v = (Vector) who.elementAt(i);
            final String nom = (String) v.elementAt(0);
            final int idle = ((Integer) v.elementAt(1)).intValue();
            final String xtra = (String) v.elementAt(2);

            cmd += "({\"" + nom + "\"," + idle + ",\"" + xtra + "\",}),";
        }
        cmd += "}),})";
        return cmd;
    }
}
