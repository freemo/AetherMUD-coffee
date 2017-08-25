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
public class MudAuthRequest extends Packet {
    public MudAuthRequest() {
        super();
        type = Packet.MAUTH_REQUEST;
        target_mud = I3Server.getMudName();
    }

    public MudAuthRequest(Vector v) {
        super(v);
        type = Packet.MAUTH_REQUEST;
        target_mud = (String) v.elementAt(4);
    }

    public MudAuthRequest(String target_mud) {
        super();
        type = Packet.MAUTH_REQUEST;
        this.target_mud = target_mud;
    }

    @Override
    public void send() throws InvalidPacketException {
        super.send();
    }

    @Override
    public String toString() {
        return "({\"auth-mud-req\",5,\"" + I3Server.getMudName() + "\",0,\"" + target_mud + "\",0,})";
    }
}
