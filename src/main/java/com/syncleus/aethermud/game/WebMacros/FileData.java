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

import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMFile;
import com.syncleus.aethermud.game.core.exceptions.HTTPServerException;
import com.syncleus.aethermud.web.http.MIMEType;
import com.syncleus.aethermud.web.interfaces.HTTPRequest;
import com.syncleus.aethermud.web.interfaces.HTTPResponse;


public class FileData extends StdWebMacro {
    @Override
    public String name() {
        return "FileData";
    }

    @Override
    public boolean isAWebPath() {
        return true;
    }

    @Override
    public boolean preferBinary() {
        return true;
    }

    public String getFilename(HTTPRequest httpReq, String filename) {
        final String path = httpReq.getUrlParameter("PATH");
        if (path == null)
            return filename;
        final String file = httpReq.getUrlParameter("FILE");
        if (file == null)
            return filename;
        return path + "/" + file;
    }

    @Override
    public byte[] runBinaryMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) throws HTTPServerException {
        String filename = getFilename(httpReq, "");
        if (filename == null)
            filename = "FileData";
        final int x = filename.lastIndexOf('/');
        if ((x >= 0) && (x < filename.length() - 1))
            filename = filename.substring(x + 1);
        final MIMEType mimeType = MIMEType.All.getMIMEType(filename);
        if (mimeType != null)
            httpResp.setHeader("Content-Type", mimeType.getType());
        httpResp.setHeader("Content-Disposition", "attachment; filename=" + filename);

        if (filename.length() == 0)
            return null;
        final MOB M = Authenticate.getAuthenticatedMob(httpReq);
        if (M == null)
            return null;
        final CMFile F = new CMFile(getFilename(httpReq, ""), M);
        if ((!F.exists()) || (!F.canRead()))
            return null;
        return F.raw();
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) throws HTTPServerException {
        return "[Unimplemented string method!]";
    }
}
