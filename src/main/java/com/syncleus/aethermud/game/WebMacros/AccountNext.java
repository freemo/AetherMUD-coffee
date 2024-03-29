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

import com.syncleus.aethermud.game.Common.interfaces.PlayerAccount;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.web.interfaces.HTTPRequest;
import com.syncleus.aethermud.web.interfaces.HTTPResponse;

import java.util.Enumeration;


public class AccountNext extends StdWebMacro {
    @Override
    public String name() {
        return "AccountNext";
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        if (!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
            return CMProps.getVar(CMProps.Str.MUDSTATUS);

        final java.util.Map<String, String> parms = parseParms(parm);
        final String last = httpReq.getUrlParameter("ACCOUNT");
        if (parms.containsKey("RESET")) {
            if (last != null)
                httpReq.removeUrlParameter("ACCOUNT");
            return "";
        }
        String lastID = "";
        String sort = httpReq.getUrlParameter("SORTBY");
        if (sort == null)
            sort = "";
        final Enumeration<PlayerAccount> pe = CMLib.players().accounts(sort, httpReq.getRequestObjects());
        for (; pe.hasMoreElements(); ) {
            final PlayerAccount account = pe.nextElement();
            if ((last == null) || ((last.length() > 0) && (last.equals(lastID)) && (!account.getAccountName().equals(lastID)))) {
                httpReq.addFakeUrlParameter("ACCOUNT", account.getAccountName());
                return "";
            }
            lastID = account.getAccountName();
        }
        httpReq.addFakeUrlParameter("ACCOUNT", "");
        if (parms.containsKey("EMPTYOK"))
            return "<!--EMPTY-->";
        return " @break@";
    }

}
