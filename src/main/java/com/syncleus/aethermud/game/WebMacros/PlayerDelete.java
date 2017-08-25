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

import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.Log;
import com.planet_ink.web.interfaces.HTTPRequest;
import com.planet_ink.web.interfaces.HTTPResponse;


public class PlayerDelete extends StdWebMacro {
    @Override
    public String name() {
        return "PlayerDelete";
    }

    @Override
    public boolean isAdminMacro() {
        return true;
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        if (!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
            return CMProps.getVar(CMProps.Str.MUDSTATUS);

        final String last = httpReq.getUrlParameter("PLAYER");
        if (last == null)
            return " @break@";
        final MOB M = CMLib.players().getLoadPlayer(last);
        if (M == null)
            return " @break@";

        CMLib.players().obliteratePlayer(M, true, true);
        Log.sysOut("PlayerDelete", "Someone destroyed the user " + M.Name() + ".");
        return "";
    }
}
