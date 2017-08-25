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
package com.planet_ink.game.WebMacros;

import com.planet_ink.game.Areas.interfaces.Area;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.SpaceObject;
import com.planet_ink.web.interfaces.HTTPRequest;
import com.planet_ink.web.interfaces.HTTPResponse;

import java.util.Enumeration;


public class AreaNext extends StdWebMacro {
    @Override
    public String name() {
        return "AreaNext";
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final java.util.Map<String, String> parms = parseParms(parm);
        String last = httpReq.getUrlParameter("AREA");
        if (parms.containsKey("RESET")) {
            if (last != null)
                httpReq.removeUrlParameter("AREA");
            return "";
        }
        final boolean all = parms.containsKey("SPACE") || parms.containsKey("ALL");
        String lastID = "";
        for (final Enumeration<Area> a = CMLib.map().areas(); a.hasMoreElements(); ) {
            final Area A = a.nextElement();
            if ((!(A instanceof SpaceObject)) || all) {
                if ((last == null) || ((last.length() > 0) && (last.equals(lastID)) && (!A.Name().equals(lastID)))) {
                    httpReq.addFakeUrlParameter("AREA", A.Name());
                    if ((!CMLib.flags().isHidden(A)) && (!CMath.bset(A.flags(), Area.FLAG_INSTANCE_CHILD)))
                        return "";
                    last = A.Name();
                }
                lastID = A.Name();
            }
        }
        httpReq.addFakeUrlParameter("AREA", "");
        if (parms.containsKey("EMPTYOK"))
            return "<!--EMPTY-->";
        return " @break@";
    }

}
