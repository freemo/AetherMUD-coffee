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

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.MOBS.interfaces.Deity;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.web.interfaces.HTTPRequest;
import com.syncleus.aethermud.web.interfaces.HTTPResponse;


public class AbilityBlessingNext extends StdWebMacro {
    @Override
    public String name() {
        return "AbilityBlessingNext";
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        if (!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
            return " @break@";

        final java.util.Map<String, String> parms = parseParms(parm);
        final String last = httpReq.getUrlParameter("ABILITY");
        if (parms.containsKey("RESET")) {
            if (last != null)
                httpReq.removeUrlParameter("ABILITY");
            return "";
        }

        String lastID = "";
        final String deityName = httpReq.getUrlParameter("DEITY");
        Deity D = null;
        if ((deityName != null) && (deityName.length() > 0))
            D = CMLib.map().getDeity(deityName);
        if (D == null) {
            if (parms.containsKey("EMPTYOK"))
                return "<!--EMPTY-->";
            return " @break@";
        }

        for (int a = 0; a < D.numBlessings(); a++) {
            final Ability A = D.fetchBlessing(a);
            if ((last == null) || ((last.length() > 0) && (last.equals(lastID)) && (!A.ID().equals(lastID)))) {
                httpReq.addFakeUrlParameter("ABILITY", A.ID());
                return "";
            }
            lastID = A.ID();
        }
        httpReq.addFakeUrlParameter("ABILITY", "");
        if (parms.containsKey("EMPTYOK"))
            return "<!--EMPTY-->";
        return " @break@";
    }
}
