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
package com.syncleus.aethermud.game.WebMacros;

import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.web.interfaces.HTTPRequest;
import com.syncleus.aethermud.web.interfaces.HTTPResponse;
import com.syncleus.aethermud.web.util.CWThread;


public class ChannelNext extends StdWebMacro {
    @Override
    public String name() {
        return "ChannelNext";
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final java.util.Map<String, String> parms = parseParms(parm);
        String last = httpReq.getUrlParameter("CHANNEL");
        if (parms.containsKey("RESET")) {
            if (last != null)
                httpReq.removeUrlParameter("CHANNEL");
            return "";
        }
        final MOB mob = Authenticate.getAuthenticatedMob(httpReq);
        boolean allChannels = false;
        if ((Thread.currentThread() instanceof CWThread)
            && CMath.s_bool(((CWThread) Thread.currentThread()).getConfig().getMiscProp("ADMIN"))
            && parms.containsKey("ALLCHANNELS"))
            allChannels = true;
        String lastID = "";
        for (int i = 0; i < CMLib.channels().getNumChannels(); i++) {
            final String name = CMLib.channels().getChannel(i).name();
            if ((last == null)
                || ((last.length() > 0) && (last.equals(lastID)) && (!name.equals(lastID)))) {
                if (allChannels || ((mob != null) && (CMLib.channels().mayReadThisChannel(mob, i, true)))) {
                    httpReq.addFakeUrlParameter("CHANNEL", name);
                    return "";
                }
                last = name;
            }
            lastID = name;
        }
        httpReq.addFakeUrlParameter("CHANNEL", "");
        if (parms.containsKey("EMPTYOK"))
            return "<!--EMPTY-->";
        return " @break@";
    }

}
