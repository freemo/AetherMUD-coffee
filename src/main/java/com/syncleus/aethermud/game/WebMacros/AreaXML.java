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

import com.planet_ink.game.Areas.interfaces.Area;
import com.planet_ink.game.Commands.interfaces.Command;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMSecurity;
import com.planet_ink.game.core.exceptions.HTTPServerException;
import com.planet_ink.coffee_web.interfaces.HTTPRequest;
import com.planet_ink.coffee_web.interfaces.HTTPResponse;


public class AreaXML extends StdWebMacro {
    @Override
    public String name() {
        return "AreaXML";
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
        final MOB mob = Authenticate.getAuthenticatedMob(httpReq);
        if (mob == null)
            return "area.xml";
        final Area pickedA = getLoggedArea(httpReq, mob);
        if (pickedA == null)
            return "area.xml";
        String fileName = "";
        if (pickedA.getArchivePath().length() > 0)
            fileName = pickedA.getArchivePath();
        else
            fileName = pickedA.Name();
        if (fileName.indexOf('.') < 0)
            fileName = fileName + ".cmare";
        return fileName;
    }

    protected Area getLoggedArea(HTTPRequest httpReq, MOB mob) {
        final String AREA = httpReq.getUrlParameter("AREA");
        final Area A = MUDGrinder.getAreaObject(AREA);
        if (A == null)
            return null;
        if (CMSecurity.isASysOp(mob) || A.amISubOp(mob.Name()))
            return A;
        return null;
    }

    @Override
    public byte[] runBinaryMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) throws HTTPServerException {
        httpResp.setHeader("Content-Disposition", "attachment; filename=" + getFilename(httpReq, ""));
        httpResp.setHeader("Content-Type", "application/cmare");

        final MOB mob = Authenticate.getAuthenticatedMob(httpReq);
        if (mob == null)
            return null;
        final Area pickedA = getLoggedArea(httpReq, mob);
        if (pickedA == null)
            return null;
        final Command C = CMClass.getCommand("Export");
        if (C == null)
            return null;
        Object resultO = null;
        try {
            resultO = C.executeInternal(mob, 0, "AREA", "DATA", "MEMORY", Integer.valueOf(4), null, pickedA, mob.location());
        } catch (final Exception e) {
            return null;
        }
        if (!(resultO instanceof String))
            return null;
        return ((String) resultO).getBytes();
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) throws HTTPServerException {
        return "[Unimplemented string method!]";
    }
}
