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
package com.planet_ink.game.Libraries.mcppkgs;

import com.planet_ink.game.Common.interfaces.Session;
import com.planet_ink.game.Libraries.interfaces.ProtocolLibrary.MCPPackage;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.Log;
import com.planet_ink.game.core.Resources;

import java.util.List;
import java.util.Map;


public class MCPMooSimpleEditPackage implements MCPPackage {

    @Override
    public String packageName() {
        return "dns-org-mud-moo-simpleedit";
    }

    @Override
    public float minVersion() {
        return (float) 1.0;
    }

    @Override
    public float maxVersion() {
        return (float) 1.0;
    }

    @Override
    public void executePackage(Session session, String command, Map<String, float[]> clientSupported, Map<String, String> variables) {
        if (command.equalsIgnoreCase("dns-org-mud-moo-simpleedit-set")) {
            String content = variables.get("content");
            if (content == null)
                return;
            List<String> strs = Resources.getFileLineVector(new StringBuffer(content));
            content = CMParms.combineWith(strs, "%0D");
            if (session != null)
                session.setFakeInput(content);
        } else {
            Log.errOut("Unknown MCP simpleedit command: " + command);
        }
    }
}
