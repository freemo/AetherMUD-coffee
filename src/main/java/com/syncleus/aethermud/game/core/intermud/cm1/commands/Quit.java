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
package com.syncleus.aethermud.game.core.intermud.cm1.commands;

import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.Log;
import com.syncleus.aethermud.game.core.interfaces.PhysicalAgent;
import com.syncleus.aethermud.game.core.intermud.cm1.RequestHandler;


public class Quit extends CM1Command {
    public Quit(RequestHandler req, String parameters) {
        super(req, parameters);
    }

    @Override
    public String getCommandWord() {
        return "QUIT";
    }

    @Override
    public void run() {
        try {
            req.sendMsg("[OK]");
            req.close();
        } catch (final java.io.IOException ioe) {
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
        return "USAGE: QUIT: Ends the current user session and disconnects the user.";
    }
}
