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

import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_web.interfaces.HTTPRequest;
import com.planet_ink.coffee_web.interfaces.HTTPResponse;


public class JournalName extends StdWebMacro {
    @Override
    public String name() {
        return "JournalName";
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final String last = httpReq.getUrlParameter("JOURNAL");
        final java.util.Map<String, String> parms = parseParms(parm);
        if (last == null)
            return " @break@";
        if (last.length() > 0) {
            final boolean webify = parms.containsKey("WEBCOLOR");
            final boolean decolor = parms.containsKey("NOCOLOR");
            StringBuffer lastBuf = new StringBuffer(last);
            if (webify)
                lastBuf = super.colorwebifyOnly(lastBuf);
            if (decolor)
                lastBuf = new StringBuffer(CMStrings.removeColors(lastBuf.toString()));
            return clearWebMacros(lastBuf.toString());
        }
        return "";
    }
}
