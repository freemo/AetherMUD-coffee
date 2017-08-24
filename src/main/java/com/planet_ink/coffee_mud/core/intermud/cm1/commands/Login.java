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
package com.planet_ink.coffee_mud.core.intermud.cm1.commands;

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.Log;
import com.planet_ink.coffee_mud.core.interfaces.PhysicalAgent;
import com.planet_ink.coffee_mud.core.intermud.cm1.RequestHandler;


public class Login extends CM1Command {
    public Login(RequestHandler req, String parameters) {
        super(req, parameters);
    }

    @Override
    public String getCommandWord() {
        return "LOGIN";
    }

    @Override
    public void run() {
        try {
            final int x = parameters.indexOf(' ');
            if (x < 0)
                req.sendMsg("[FAIL " + getHelp(req.getUser(), null, null) + "]");
            else {
                final String user = parameters.substring(0, x);
                final String pass = parameters.substring(x + 1);
                final MOB M = CMLib.players().getLoadPlayer(user);
                if ((M == null) || (M.playerStats() == null) || (!M.playerStats().matchesPassword(pass))) {
                    Thread.sleep(5000);
                    req.sendMsg("[FAIL]");
                } else {
                    req.login(M);
                    req.sendMsg("[OK]");
                }
            }
        } catch (final Exception ioe) {
            Log.errOut(className, ioe);
            req.close();
        }
    }

    @Override
    public boolean passesSecurityCheck(MOB user, PhysicalAgent target) {
        return true;
    }

    @Override
    public String getHelp(MOB user, PhysicalAgent target, String rest) {
        return "USAGE: LOGIN <CHARACTER NAME> <PASSWORD>: Logs in a new character to act as the authorizing user.";
    }
}
