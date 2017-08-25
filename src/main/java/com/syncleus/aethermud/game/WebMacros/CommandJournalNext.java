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

import com.syncleus.aethermud.game.Libraries.interfaces.JournalsLibrary;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.web.interfaces.HTTPRequest;
import com.syncleus.aethermud.web.interfaces.HTTPResponse;
import com.syncleus.aethermud.web.util.CWThread;

import java.util.Enumeration;


public class CommandJournalNext extends StdWebMacro {
    @Override
    public String name() {
        return "CommandJournalNext";
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final java.util.Map<String, String> parms = parseParms(parm);
        String last = httpReq.getUrlParameter("COMMANDJOURNAL");
        if (parms.containsKey("RESET")) {
            if (last != null)
                httpReq.removeUrlParameter("COMMANDJOURNAL");
            return "";
        }
        final MOB mob = Authenticate.getAuthenticatedMob(httpReq);
        String lastID = "";
        boolean allJournals = false;
        if ((Thread.currentThread() instanceof CWThread)
            && CMath.s_bool(((CWThread) Thread.currentThread()).getConfig().getMiscProp("ADMIN"))
            && parms.containsKey("ALLCOMMANDJOURNALS"))
            allJournals = true;
        for (final Enumeration<JournalsLibrary.CommandJournal> i = CMLib.journals().commandJournals(); i.hasMoreElements(); ) {
            final JournalsLibrary.CommandJournal J = i.nextElement();
            final String name = J.NAME();
            if ((last == null)
                || ((last.length() > 0) && (last.equals(lastID)) && (!name.equals(lastID)))) {
                if (allJournals || ((mob != null) && (J.mask().length() > 0) && (!CMLib.masking().maskCheck(J.mask(), mob, true)))) {
                    httpReq.addFakeUrlParameter("COMMANDJOURNAL", name);
                    return "";
                }
                last = name;
            }
            lastID = name;
        }
        httpReq.addFakeUrlParameter("COMMANDJOURNAL", "");
        if (parms.containsKey("EMPTYOK"))
            return "<!--EMPTY-->";
        return " @break@";
    }

}
