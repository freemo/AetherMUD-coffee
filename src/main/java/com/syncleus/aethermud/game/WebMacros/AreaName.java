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

import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.web.interfaces.HTTPRequest;
import com.syncleus.aethermud.web.interfaces.HTTPResponse;


public class AreaName extends StdWebMacro {
    @Override
    public String name() {
        return "AreaName";
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final String last = httpReq.getUrlParameter("AREA");
        if (last == null)
            return " @break@";
        if (last.length() > 0) {
            final Area A = MUDGrinder.getAreaObject(last);
            if (A != null) {
                final java.util.Map<String, String> parms = parseParms(parm);
                String name = A.Name();
                if (parms.containsKey("UNDERSCORE"))
                    name = name.replace(' ', '_');
                if (parms.containsKey("UPPERCASE"))
                    name = name.toUpperCase();
                return clearWebMacros(name);
            }
        }
        return "";
    }
}
