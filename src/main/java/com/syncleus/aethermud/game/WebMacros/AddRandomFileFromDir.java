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

import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.Log;
import com.syncleus.aethermud.web.http.HTTPException;
import com.syncleus.aethermud.web.interfaces.HTTPRequest;
import com.syncleus.aethermud.web.interfaces.HTTPResponse;

import java.io.File;
import java.util.Vector;


public class AddRandomFileFromDir extends StdWebMacro {
    @Override
    public String name() {
        return "AddRandomFileFromDir";
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final java.util.Map<String, String> parms = parseParms(parm);
        if ((parms == null) || (parms.size() == 0))
            return "";
        final StringBuffer buf = new StringBuffer("");
        final Vector<String> fileList = new Vector<String>();
        boolean LINKONLY = false;
        for (final String val : parms.values()) {
            if (val.equalsIgnoreCase("LINKONLY"))
                LINKONLY = true;
        }
        for (String filePath : parms.values()) {
            if (filePath.equalsIgnoreCase("LINKONLY"))
                continue;
            final File directory = grabFile(httpReq, filePath);
            if ((!filePath.endsWith("/")) && (!filePath.endsWith("/")))
                filePath += "/";
            if ((directory != null) && (directory.canRead()) && (directory.isDirectory())) {
                final String[] list = directory.list();
                for (final String element : list)
                    fileList.addElement(filePath + element);
            } else
                Log.sysOut("AddRFDir", "Directory error: " + filePath);
        }
        if (fileList.size() == 0)
            return buf.toString();

        try {
            if (LINKONLY)
                buf.append(fileList.elementAt(CMLib.dice().roll(1, fileList.size(), -1)));
            else
                buf.append(new String(getHTTPFileData(httpReq, fileList.elementAt(CMLib.dice().roll(1, fileList.size(), -1)))));
        } catch (final HTTPException e) {
            Log.warnOut("Failed " + name() + " " + fileList.elementAt(CMLib.dice().roll(1, fileList.size(), -1)));
        }
        return buf.toString();
    }
}