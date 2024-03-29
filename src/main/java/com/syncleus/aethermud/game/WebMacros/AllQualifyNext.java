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

import com.syncleus.aethermud.game.Libraries.interfaces.AbilityMapper;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.web.interfaces.HTTPRequest;
import com.syncleus.aethermud.web.interfaces.HTTPResponse;

import java.util.Iterator;
import java.util.Map;


public class AllQualifyNext extends StdWebMacro {
    @Override
    public String name() {
        return "AllQualifyNext";
    }

    @Override
    public boolean isAdminMacro() {
        return true;
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final java.util.Map<String, String> parms = parseParms(parm);
        final String last = httpReq.getUrlParameter("ALLQUALID");
        if (parms.containsKey("RESET")) {
            if (last != null)
                httpReq.removeUrlParameter("ALLQUALID");
            return "";
        }
        String which = httpReq.getUrlParameter("ALLQUALWHICH");
        if (parms.containsKey("WHICH"))
            which = parms.get("WHICH");
        if ((which == null) || (which.length() == 0))
            which = "ALL";
        final Map<String, Map<String, AbilityMapper.AbilityMapping>> allQualMap = CMLib.ableMapper().getAllQualifiesMap(httpReq.getRequestObjects());
        final Map<String, AbilityMapper.AbilityMapping> map = allQualMap.get(which.toUpperCase().trim());
        if (map == null)
            return " @break@";

        String lastID = "";
        String abilityID;
        for (final Iterator<String> i = map.keySet().iterator(); i.hasNext(); ) {
            abilityID = i.next();
            if ((last == null) || ((last.length() > 0) && (last.equals(lastID)) && (!abilityID.equalsIgnoreCase(lastID)))) {
                httpReq.addFakeUrlParameter("ALLQUALID", abilityID);
                return "";
            }
            lastID = abilityID;
        }
        httpReq.addFakeUrlParameter("ALLQUALID", "");
        if (parms.containsKey("EMPTYOK"))
            return "<!--EMPTY-->";
        return " @break@";
    }
}
