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

import com.planet_ink.coffee_mud.CharClasses.interfaces.CharClass;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_web.interfaces.HTTPRequest;
import com.planet_ink.coffee_web.interfaces.HTTPResponse;

import java.util.Enumeration;


@SuppressWarnings("rawtypes")
public class ClassRaceNext extends StdWebMacro {
    @Override
    public String name() {
        return "ClassRaceNext";
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final java.util.Map<String, String> parms = parseParms(parm);
        final String cclass = httpReq.getUrlParameter("CLASS");
        if (cclass.trim().length() == 0)
            return " @break@";
        final CharClass C = CMClass.getCharClass(cclass.trim());
        if (C == null)
            return " @break";
        final String last = httpReq.getUrlParameter("RACE");
        if (parms.containsKey("RESET")) {
            if (last != null)
                httpReq.removeUrlParameter("RACE");
            return "";
        }
        String lastID = "";
        for (final Enumeration r = MobData.sortedRaces(httpReq); r.hasMoreElements(); ) {
            final Race R = (Race) r.nextElement();
            if (((CMLib.login().isAvailableRace(R))
                || (parms.containsKey("ALL")))
                && (C.isAllowedRace(R))) {
                if ((last == null) || ((last.length() > 0) && (last.equals(lastID)) && (!R.ID().equals(lastID)))) {
                    httpReq.addFakeUrlParameter("RACE", R.ID());
                    return "";
                }
                lastID = R.ID();
            }
        }
        httpReq.addFakeUrlParameter("RACE", "");
        if (parms.containsKey("EMPTYOK"))
            return "<!--EMPTY-->";
        return " @break@";
    }
}
