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

import com.syncleus.aethermud.game.core.CMath;
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
public class FingerReply extends Packet {
    public String visible_name = "";
    public String title = "";
    public String real_name = "";
    public String e_mail = "";
    public String loginout_time = "";
    public String idle_time = "";
    public String ip_time = "";
    public String extra = "";

    public FingerReply() {
        super();
        type = Packet.FINGER_REPLY;
    }

    public FingerReply(String to_whom, String mud) {
        super();
        type = Packet.FINGER_REPLY;
        target_mud = mud;
        target_name = to_whom;
    }

    public FingerReply(Vector v) throws InvalidPacketException {
        super(v);
        try {
            type = Packet.FINGER_REPLY;
            try {
                visible_name = v.elementAt(6).toString();
                title = v.elementAt(7).toString();
                real_name = v.elementAt(8).toString();
                e_mail = v.elementAt(9).toString();
                loginout_time = v.elementAt(10).toString();
                idle_time = v.elementAt(11).toString();
                ip_time = v.elementAt(12).toString();
                extra = v.elementAt(13).toString();
            } catch (final Exception e) {
            }
        } catch (final ClassCastException e) {
            throw new InvalidPacketException();
        }
    }

    @Override
    public void send() throws InvalidPacketException {
        super.send();
    }

    @Override
    public String toString() {
        String cmd = "({\"finger-reply\",5,\"" + I3Server.getMudName() +
            "\",0,\"" + target_mud + "\",\"" + target_name + "\",";
        final String[] responses = {visible_name, title, real_name, e_mail,
            loginout_time, idle_time, ip_time, extra};
        for (final String nom : responses) {
            if (nom.length() == 0)
                cmd += "0,";
            else if (CMath.isNumber(nom))
                cmd += "" + nom + ",";
            else
                cmd += "\"" + nom + "\",";
        }
        cmd += "})";
        return cmd;

    }
}
