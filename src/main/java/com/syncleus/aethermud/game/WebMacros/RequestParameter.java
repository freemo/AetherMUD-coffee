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

import com.syncleus.aethermud.game.core.CMStrings;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.web.interfaces.HTTPRequest;
import com.syncleus.aethermud.web.interfaces.HTTPResponse;

import java.util.HashSet;


public class RequestParameter extends StdWebMacro {
    private static HashSet<String> modifiers = new HashSet<String>();

    static {
        for (final MODIFIER M : MODIFIER.values())
            modifiers.add(M.name());
    }

    @Override
    public String name() {
        return "RequestParameter";
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        String str = "";
        final java.util.Map<String, String> parms = parseParms(parm);
        for (final String key : parms.keySet()) {
            if (!modifiers.contains(key)) {
                if (httpReq.isUrlParameter(key))
                    str += httpReq.getUrlParameter(key);
            }
        }
        boolean capCase = false;
        for (final String key : parms.keySet()) {
            if (modifiers.contains(key)) {
                int num = 0;
                if (key.equals(MODIFIER.UPPERCASE.name()))
                    str = str.toUpperCase();
                else if (key.equals(MODIFIER.LOWERCASE.name()))
                    str = str.toLowerCase();
                else if (key.equals(MODIFIER.CAPITALCASE.name()))
                    capCase = true;
                else if (key.equals(MODIFIER.TRIM.name()))
                    str = str.trim();
                else if (key.equals(MODIFIER.LEFT.name())) {
                    num = CMath.s_int(parms.get(MODIFIER.LEFT.name()));
                    if ((num > 0) && (num < str.length()))
                        str = str.substring(0, num);
                } else if (key.equals(MODIFIER.AFTER.name())) {
                    num = CMath.s_int(parms.get(MODIFIER.AFTER.name()));
                    if ((num > 0) && (num < str.length()))
                        str = str.substring(num);
                } else if (key.equals(MODIFIER.RIGHT.name())) {
                    num = CMath.s_int(parms.get(MODIFIER.RIGHT.name()));
                    if ((num > 0) && (num < str.length()))
                        str = str.substring(str.length() - num);
                } else if (key.equals(MODIFIER.ELLIPSE.name())) {
                    num = CMath.s_int(parms.get(MODIFIER.ELLIPSE.name()));
                    if ((num > 0) && (num < str.length()))
                        str = str.substring(0, num) + "...";
                }
            }
        }
        if (capCase)
            str = CMStrings.capitalizeAndLower(str);
        str = clearWebMacros(str);
        return str;
    }

    private static enum MODIFIER {
        UPPERCASE,
        LOWERCASE,
        LEFT,
        RIGHT,
        ELLIPSE,
        TRIM,
        AFTER,
        CAPITALCASE
    }
}
