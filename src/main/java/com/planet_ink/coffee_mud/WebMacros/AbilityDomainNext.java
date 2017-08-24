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

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_web.interfaces.HTTPRequest;
import com.planet_ink.coffee_web.interfaces.HTTPResponse;


public class AbilityDomainNext extends StdWebMacro {
    @Override
    public String name() {
        return "AbilityDomainNext";
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final java.util.Map<String, String> parms = parseParms(parm);
        final String last = httpReq.getUrlParameter("DOMAIN");
        if (parms.containsKey("RESET")) {
            if (last != null)
                httpReq.removeUrlParameter("DOMAIN");
            return "";
        }
        String lastID = "";
        for (final String element : Ability.DOMAIN_DESCS) {
            final String S = element;
            if ((last == null) || ((last.length() > 0) && (last.equals(lastID)) && (!S.equals(lastID)))) {
                httpReq.addFakeUrlParameter("DOMAIN", S);
                return "";
            }
            lastID = S;
        }
        httpReq.addFakeUrlParameter("DOMAIN", "");
        if (parms.containsKey("EMPTYOK"))
            return "<!--EMPTY-->";
        return " @break@";
    }

}
