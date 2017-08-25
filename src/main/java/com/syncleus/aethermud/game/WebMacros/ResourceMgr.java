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

import com.syncleus.aethermud.game.core.Resources;
import com.syncleus.aethermud.web.interfaces.HTTPRequest;
import com.syncleus.aethermud.web.interfaces.HTTPResponse;

import java.util.Iterator;


public class ResourceMgr extends StdWebMacro {
    @Override
    public String name() {
        return "ResourceMgr";
    }

    @Override
    public boolean isAdminMacro() {
        return true;
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final java.util.Map<String, String> parms = parseParms(parm);
        final String last = httpReq.getUrlParameter("RESOURCE");
        if (parms.containsKey("RESET")) {
            if (last != null)
                httpReq.removeUrlParameter("RESOURCE");
            return "";
        } else if (parms.containsKey("NEXT")) {
            String lastID = "";
            for (final Iterator<String> k = Resources.findResourceKeys(""); k.hasNext(); ) {
                final String key = k.next();
                if ((last == null) || ((last.length() > 0) && (last.equals(lastID)) && (!key.equals(lastID)))) {
                    httpReq.addFakeUrlParameter("RESOURCE", key);
                    return "";
                }
                lastID = key;
            }
            httpReq.addFakeUrlParameter("RESOURCE", "");
            if (parms.containsKey("EMPTYOK"))
                return "<!--EMPTY-->";
            return " @break@";
        } else if (parms.containsKey("DELETE")) {
            final String key = httpReq.getUrlParameter("RESOURCE");
            if ((key != null) && (Resources.getResource(key) != null)) {
                Resources.removeResource(key);
                return "Resource '" + key + "' deleted.";
            }
            return "<!--EMPTY-->";
        } else if (last != null)
            return last;
        return "<!--EMPTY-->";
    }

}
