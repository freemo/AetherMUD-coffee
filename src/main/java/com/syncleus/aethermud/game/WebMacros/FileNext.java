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
import com.planet_ink.game.core.CMFile;
import com.planet_ink.game.core.collections.XVector;
import com.planet_ink.coffee_web.interfaces.HTTPRequest;
import com.planet_ink.coffee_web.interfaces.HTTPResponse;


@SuppressWarnings({"unchecked", "rawtypes"})
public class FileNext extends StdWebMacro {
    @Override
    public String name() {
        return "FileNext";
    }

    @Override
    public boolean isAdminMacro() {
        return true;
    }

    public String trimSlash(String path) {
        path = path.trim();
        while (path.startsWith("/"))
            path = path.substring(1);
        while (path.endsWith("/"))
            path = path.substring(0, path.length() - 1);
        return path;
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final java.util.Map<String, String> parms = parseParms(parm);
        String path = httpReq.getUrlParameter("PATH");
        if (path == null)
            path = "";
        final String last = httpReq.getUrlParameter("FILE");
        final MOB M = Authenticate.getAuthenticatedMob(httpReq);
        if (M == null)
            return "[authentication error]";
        if (parms.containsKey("RESET")) {
            if (last != null)
                httpReq.removeUrlParameter("FILE");
            return "";
        }
        final String fileKey = "CMFSFILE_" + trimSlash(path);
        final String pathKey = "DIRECTORYFILES_" + trimSlash(path);
        CMFile directory = (CMFile) httpReq.getRequestObjects().get(fileKey);
        if (directory == null) {
            directory = new CMFile(path, M);
            httpReq.getRequestObjects().put(fileKey, directory);
        }
        final XVector fileList = new XVector();
        if ((directory.canRead()) && (directory.isDirectory())) {
            httpReq.addFakeUrlParameter("PATH", directory.getVFSPathAndName());
            CMFile[] dirs = (CMFile[]) httpReq.getRequestObjects().get(pathKey);
            if (dirs == null) {
                dirs = CMFile.getFileList(path, M, false, true, null);
                httpReq.getRequestObjects().put(pathKey, dirs);
                for (final CMFile file : dirs) {
                    final String filepath = path.endsWith("/") ? path + file.getName() : path + "/" + file.getName();
                    httpReq.getRequestObjects().put("CMFSFILE_" + trimSlash(filepath), file);
                }
            }
            for (final CMFile dir : dirs)
                fileList.addElement(dir.getName());
        }
        fileList.sort();
        String lastID = "";
        for (int q = 0; q < fileList.size(); q++) {
            final String name = (String) fileList.elementAt(q);
            if ((last == null) || ((last.length() > 0) && (last.equals(lastID)) && (!name.equals(lastID)))) {
                httpReq.addFakeUrlParameter("FILE", name);
                return "";
            }
            lastID = name;
        }
        httpReq.addFakeUrlParameter("FILE", "");
        if (parms.containsKey("EMPTYOK"))
            return "<!--EMPTY-->";
        return " @break@";
    }

}
