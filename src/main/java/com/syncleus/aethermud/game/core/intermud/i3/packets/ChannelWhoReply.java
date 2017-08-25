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
public class ChannelWhoReply extends Packet {
    public String channel = null;
    public Vector who = null;

    public ChannelWhoReply() {
        super();
        type = Packet.CHAN_WHO_REP;
    }

    public ChannelWhoReply(Vector v) throws InvalidPacketException {
        super(v);
        try {
            type = Packet.CHAN_WHO_REP;
            channel = (String) v.elementAt(6);
            channel = Intermud.getLocalChannel(channel);
            try {
                who = (Vector) v.elementAt(7);
            } catch (final Exception e) {
                who = new Vector();
            }
        } catch (final ClassCastException e) {
            throw new InvalidPacketException();
        }
    }

    @Override
    public void send() throws InvalidPacketException {
        if (channel == null || who == null) {
            throw new InvalidPacketException();
        }
        channel = Intermud.getRemoteChannel(channel);
        super.send();
    }

    @Override
    public String toString() {
        String cmd = "({\"chan-who-reply\",5,\"" + I3Server.getMudName() +
            "\",0,\"" + target_mud + "\",\"" + target_name + "\",\"" + channel + "\",({";
        int i;

        for (i = 0; i < who.size(); i++) {
            final String nom = (String) who.elementAt(0);
            cmd += "\"" + nom + "\",";
        }
        cmd += "}),})";
        return cmd;

    }
}
