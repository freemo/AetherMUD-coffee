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
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_web.interfaces.HTTPRequest;
import com.planet_ink.coffee_web.interfaces.HTTPResponse;

import java.util.Enumeration;


@SuppressWarnings("rawtypes")
public class BaseCharClassName extends StdWebMacro {
    @Override
    public String name() {
        return "BaseCharClassName";
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final String last = httpReq.getUrlParameter("BASECLASS");
        if (last == null)
            return " @break@";
        if (last.length() > 0) {
            final java.util.Map<String, String> parms = parseParms(parm);
            CharClass C = CMClass.getCharClass(last);
            if (C != null) {
                if (parms.containsKey("PLURAL"))
                    return clearWebMacros(CMLib.english().makePlural(C.name()));
                else
                    return clearWebMacros(C.name());
            }
            for (final Enumeration e = CMClass.charClasses(); e.hasMoreElements(); ) {
                C = (CharClass) e.nextElement();
                if (C.baseClass().equalsIgnoreCase(last)) {
                    if (parms.containsKey("PLURAL"))
                        return clearWebMacros(CMLib.english().makePlural(C.baseClass()));
                    else
                        return clearWebMacros(C.baseClass());
                }
            }
        }
        return "";
    }
}
