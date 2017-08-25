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
import com.syncleus.aethermud.game.Common.interfaces.Session;
import com.syncleus.aethermud.game.Libraries.interfaces.PlayerLibrary.ThinPlayer;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.web.interfaces.HTTPRequest;
import com.syncleus.aethermud.web.interfaces.HTTPResponse;

import java.util.Enumeration;


public class NumPlayers extends StdWebMacro {
    @Override
    public String name() {
        return "NumPlayers";
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final java.util.Map<String, String> parms = parseParms(parm);
        if (parms.containsKey("ALL"))
            return "" + CMLib.sessions().getCountLocalOnline();
        if (parms.containsKey("TOTALCACHED"))
            return "" + CMLib.players().numPlayers();
        if (parms.containsKey("TOTAL")) {
            final Enumeration<ThinPlayer> pe = CMLib.players().thinPlayers("", httpReq.getRequestObjects());
            int x = 0;
            for (; pe.hasMoreElements(); pe.nextElement())
                x++;
            return "" + x;
        }
        if (parms.containsKey("ACCOUNTS")) {
            final Enumeration<PlayerAccount> pe = CMLib.players().accounts("", httpReq.getRequestObjects());
            int x = 0;
            for (; pe.hasMoreElements(); pe.nextElement())
                x++;
            return "" + x;
        }

        int numPlayers = 0;
        for (final Session S : CMLib.sessions().localOnlineIterable()) {
            if ((S.mob() != null) && (!CMLib.flags().isCloaked(S.mob())))
                numPlayers++;
        }
        return Integer.toString(numPlayers);
    }

}
