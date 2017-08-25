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

import com.syncleus.aethermud.web.interfaces.HTTPRequest;
import com.syncleus.aethermud.web.interfaces.HTTPResponse;


public class CheckReqParm extends StdWebMacro {
    @Override
    public String name() {
        return "CheckReqParm";
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final java.util.Map<String, String> parms = parseParms(parm);
        boolean finalCondition = false;
        for (String key : parms.keySet()) {
            if (key.length() == 0)
                continue;
            final String equals = parms.get(key);
            boolean not = false;
            boolean thisCondition = true;
            boolean startswith = false;
            boolean inside = false;
            boolean endswith = false;
            if (key.startsWith("||"))
                key = key.substring(2);
            if (key.startsWith("<")) {
                startswith = true;
                key = key.substring(1);
            }
            if (key.startsWith(">")) {
                endswith = true;
                key = key.substring(1);
            }
            if (key.startsWith("*")) {
                inside = true;
                key = key.substring(1);
            }

            if (key.startsWith("!")) {
                key = key.substring(1);
                not = true;
            }
            final String check = httpReq.getUrlParameter(key);
            if (not) {
                if ((check == null) && (equals.length() == 0))
                    thisCondition = false;
                else if (check == null)
                    thisCondition = true;
                else if (startswith)
                    thisCondition = !check.startsWith(equals);
                else if (endswith)
                    thisCondition = !check.endsWith(equals);
                else if (inside)
                    thisCondition = !(check.indexOf(equals) >= 0);
                else if (!check.equalsIgnoreCase(equals))
                    thisCondition = true;
                else
                    thisCondition = false;
            } else {
                if ((check == null) && (equals.length() == 0))
                    thisCondition = true;
                else if (check == null)
                    thisCondition = false;
                else if (startswith)
                    thisCondition = check.startsWith(equals);
                else if (endswith)
                    thisCondition = check.endsWith(equals);
                else if (inside)
                    thisCondition = (check.indexOf(equals) >= 0);
                else if (!check.equalsIgnoreCase(equals))
                    thisCondition = false;
                else
                    thisCondition = true;
            }
            finalCondition = finalCondition || thisCondition;
        }
        if (finalCondition)
            return "true";
        return "false";
    }
}
