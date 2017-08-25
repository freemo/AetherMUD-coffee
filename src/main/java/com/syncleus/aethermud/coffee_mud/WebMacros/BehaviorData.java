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

import com.planet_ink.coffee_mud.Behaviors.interfaces.Behavior;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_web.interfaces.HTTPRequest;
import com.planet_ink.coffee_web.interfaces.HTTPResponse;


public class BehaviorData extends StdWebMacro {
    @Override
    public String name() {
        return "BehaviorData";
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final java.util.Map<String, String> parms = parseParms(parm);
        final String last = httpReq.getUrlParameter("BEHAVIOR");
        if (last == null)
            return " @break@";
        if (last.length() > 0) {
            final Behavior B = CMClass.getBehavior(last);
            if (B != null) {
                final StringBuffer str = new StringBuffer("");
                if (parms.containsKey("HELP")) {
                    StringBuilder s = CMLib.help().getHelpText("BEHAVIOR_" + B.ID(), null, true);
                    if (s == null)
                        s = CMLib.help().getHelpText(B.ID(), null, true);
                    int limit = 78;
                    if (parms.containsKey("LIMIT"))
                        limit = CMath.s_int(parms.get("LIMIT"));
                    str.append(helpHelp(s, limit));
                }
                String strstr = str.toString();
                if (strstr.endsWith(", "))
                    strstr = strstr.substring(0, strstr.length() - 2);
                return clearWebMacros(strstr);
            }
        }
        return "";
    }
}
