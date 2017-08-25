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

import com.planet_ink.game.Behaviors.interfaces.Behavior;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.coffee_web.interfaces.HTTPRequest;
import com.planet_ink.coffee_web.interfaces.HTTPResponse;

import java.util.Enumeration;


@SuppressWarnings("rawtypes")
public class BehaviorNext extends StdWebMacro {
    @Override
    public String name() {
        return "BehaviorNext";
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final java.util.Map<String, String> parms = parseParms(parm);
        final String last = httpReq.getUrlParameter("BEHAVIOR");
        if (parms.containsKey("RESET")) {
            if (last != null)
                httpReq.removeUrlParameter("BEHAVIOR");
            return "";
        }
        String lastID = "";
        for (final Enumeration b = CMClass.behaviors(); b.hasMoreElements(); ) {
            final Behavior B = (Behavior) b.nextElement();
            if ((last == null) || ((last.length() > 0) && (last.equals(lastID)) && (!B.ID().equals(lastID)))) {
                httpReq.addFakeUrlParameter("BEHAVIOR", B.ID());
                return "";
            }
            lastID = B.ID();
        }
        httpReq.addFakeUrlParameter("BEHAVIOR", "");
        if (parms.containsKey("EMPTYOK"))
            return "<!--EMPTY-->";
        return " @break@";
    }

}
