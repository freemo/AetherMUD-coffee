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
package com.planet_ink.coffee_mud.WebMacros;

import com.planet_ink.coffee_mud.Libraries.interfaces.DatabaseEngine.PlayerData;
import com.planet_ink.coffee_mud.core.B64Encoder;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.Resources;
import com.planet_ink.coffee_mud.core.exceptions.HTTPServerException;
import com.planet_ink.coffee_web.http.MIMEType;
import com.planet_ink.coffee_web.interfaces.HTTPRequest;
import com.planet_ink.coffee_web.interfaces.HTTPResponse;

import java.util.List;


public class PlayerPortrait extends StdWebMacro {
    @Override
    public String name() {
        return "PlayerPortrait";
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
        final String foundFilename = httpReq.getUrlParameter("FILENAME");
        if ((foundFilename != null) && (foundFilename.length() > 0))
            return foundFilename;
        return filename;
    }

    @Override
    public byte[] runBinaryMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) throws HTTPServerException {
        final MIMEType mimeType = MIMEType.All.getMIMEType(getFilename(httpReq, ""));
        if (mimeType != null)
            httpResp.setHeader("Content-Type", mimeType.getType());
        final String last = httpReq.getUrlParameter("PLAYER");
        if (last == null) return null; // for binary macros, null is BREAK
        byte[] img = null;
        if (last.length() > 0) {
            img = (byte[]) Resources.getResource("CMPORTRAIT-" + last);
            if (img == null) {
                final List<PlayerData> data = CMLib.database().DBReadPlayerData(last, "CMPORTRAIT");
                if ((data != null) && (data.size() > 0)) {
                    final String encoded = data.get(0).xml();
                    img = B64Encoder.B64decode(encoded);
                    if (img != null)
                        Resources.submitResource("CMPORTRAIT-" + last, img);
                }
            }
        }
        return img;
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) throws HTTPServerException {
        return "[Unimplemented string method!]";
    }
}
