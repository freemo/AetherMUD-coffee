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

import com.syncleus.aethermud.game.Commands.interfaces.Command;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.web.interfaces.HTTPRequest;
import com.syncleus.aethermud.web.interfaces.HTTPResponse;


public class SystemFunction extends StdWebMacro {
    @Override
    public String name() {
        return "SystemFunction";
    }

    @Override
    public boolean isAdminMacro() {
        return true;
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final java.util.Map<String, String> parms = parseParms(parm);
        if (parms.get("ANNOUNCE") != null) {
            final String s = httpReq.getUrlParameter("TEXT");
            if ((s != null) && (s.length() > 0)) {
                final MOB M = ((MOB) CMClass.sampleMOB().copyOf());
                final Command C = CMClass.getCommand("Announce");
                try {
                    C.execute(M, CMParms.parse("all " + s.trim()), 0);
                } catch (final Exception e) {
                }
            }
        }
        if (parms.get("SHUTDOWN") != null) {
            //com.syncleus.aethermud.game.application.MUD.globalShutdown(null,(parms.get("RESTART")==null),null);
            return "";
        }
        return "";
    }
}
