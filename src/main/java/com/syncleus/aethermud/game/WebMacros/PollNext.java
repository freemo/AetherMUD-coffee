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

import com.planet_ink.game.Common.interfaces.Poll;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.coffee_web.interfaces.HTTPRequest;
import com.planet_ink.coffee_web.interfaces.HTTPResponse;

import java.util.Iterator;


public class PollNext extends StdWebMacro {
    @Override
    public String name() {
        return "PollNext";
    }

    @Override
    public boolean isAdminMacro() {
        return true;
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final java.util.Map<String, String> parms = parseParms(parm);
        final String last = httpReq.getUrlParameter("POLL");
        if (parms.containsKey("RESET")) {
            if (last != null)
                httpReq.removeUrlParameter("POLL");
            return "";
        }
        String lastID = "";
        for (final Iterator<Poll> q = CMLib.polls().getPollList(); q.hasNext(); ) {
            final Poll poll = q.next();
            if ((last == null) || ((last.length() > 0) && (last.equals(lastID)) && (!poll.getName().equalsIgnoreCase(lastID)))) {
                httpReq.addFakeUrlParameter("POLL", poll.getName());
                return "";
            }
            lastID = poll.getName();
        }
        httpReq.addFakeUrlParameter("POLL", "");
        if (parms.containsKey("EMPTYOK"))
            return "<!--EMPTY-->";
        return " @break@";
    }
}
