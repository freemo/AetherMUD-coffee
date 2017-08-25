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
package com.planet_ink.coffee_mud.WebMacros;

import com.planet_ink.coffee_mud.Common.interfaces.AccountStats;
import com.planet_ink.coffee_mud.Libraries.interfaces.AchievementLibrary.Achievement;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_web.interfaces.HTTPRequest;
import com.planet_ink.coffee_web.interfaces.HTTPResponse;

import java.util.Enumeration;


public class AchievementNext extends StdWebMacro {
    @Override
    public String name() {
        return "AchievementNext";
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final java.util.Map<String, String> parms = parseParms(parm);
        final String last = httpReq.getUrlParameter("ACHIEVEMENT");
        if (parms.containsKey("RESET")) {
            if (last != null)
                httpReq.removeUrlParameter("ACHIEVEMENT");
            return "";
        }
        String agentStr = parms.get("AGENT");
        if (agentStr == null)
            agentStr = httpReq.getUrlParameter("AGENT");
        final AccountStats.Agent agent = ((agentStr == null) || (agentStr.length() == 0) || (!CMProps.isUsingAccountSystem())) ?
            AccountStats.Agent.PLAYER : (AccountStats.Agent) CMath.s_valueOf(AccountStats.Agent.class, agentStr.toUpperCase().trim());
        if (agent == null) {
            return " @break@";
        }

        String lastID = "";
        for (final Enumeration<Achievement> r = CMLib.achievements().achievements(agent); r.hasMoreElements(); ) {
            Achievement A = r.nextElement();
            final String title = A.getTattoo();
            if ((last == null) || ((last.length() > 0) && (last.equals(lastID)) && (!title.equals(lastID)))) {
                httpReq.addFakeUrlParameter("ACHIEVEMENT", title);
                return "";
            }
            lastID = title;
        }
        httpReq.addFakeUrlParameter("ACHIEVEMENT", "");
        if (parms.containsKey("EMPTYOK"))
            return "<!--EMPTY-->";
        return " @break@";
    }

}
