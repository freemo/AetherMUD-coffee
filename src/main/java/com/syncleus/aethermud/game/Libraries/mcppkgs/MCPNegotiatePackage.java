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
package com.syncleus.aethermud.game.Libraries.mcppkgs;

import com.syncleus.aethermud.game.Common.interfaces.Session;
import com.syncleus.aethermud.game.Libraries.interfaces.ProtocolLibrary.MCPPackage;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.Log;

import java.util.Map;


public class MCPNegotiatePackage implements MCPPackage {

    @Override
    public String packageName() {
        return "mcp-negotiate";
    }

    @Override
    public float minVersion() {
        return (float) 1.0;
    }

    @Override
    public float maxVersion() {
        return (float) 2.0;
    }

    @Override
    public void executePackage(Session session, String command, Map<String, float[]> clientSupported, Map<String, String> variables) {
        if (command.equalsIgnoreCase("mcp-negotiate-end")) {
            // nothing to do, really
            return;
        } else if (command.equalsIgnoreCase("mcp-negotiate-can")) {
            String packageName = variables.get("package");
            String minVersion = variables.get("min-version");
            String maxVersion = variables.get("max-version");
            if (packageName != null) {
                clientSupported.put(packageName, new float[]{CMath.s_float(minVersion), CMath.s_float(maxVersion)});
            } else
                Log.errOut("MCP negotiate-can without package name!");
        } else
            Log.errOut("Unknown MCP negotiate command: " + command);
    }
}
