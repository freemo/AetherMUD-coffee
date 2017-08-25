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

import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.web.interfaces.HTTPRequest;
import com.syncleus.aethermud.web.interfaces.HTTPResponse;


public class ComponentData extends StdWebMacro {
    @Override
    public String name() {
        return "ComponentData";
    }

    @Override
    public boolean isAdminMacro() {
        return true;
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final java.util.Map<String, String> parms = parseParms(parm);
        final String last = httpReq.getUrlParameter("COMPONENT");
        final StringBuilder str = new StringBuilder("");
        if (parms.containsKey("DESC")) {
            str.append(CMLib.ableComponents().getAbilityComponentDesc(null, last));
        }
        String strstr = str.toString();
        if (strstr.endsWith(", "))
            strstr = strstr.substring(0, strstr.length() - 2);
        return clearWebMacros(strstr);
    }
}
