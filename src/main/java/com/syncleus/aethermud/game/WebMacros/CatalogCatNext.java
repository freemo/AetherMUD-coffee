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
import com.syncleus.aethermud.web.interfaces.HTTPRequest;
import com.syncleus.aethermud.web.interfaces.HTTPResponse;


public class CatalogCatNext extends StdWebMacro {
    @Override
    public String name() {
        return "CatalogCatNext";
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final java.util.Map<String, String> parms = parseParms(parm);
        String last = httpReq.getUrlParameter("CATACAT");
        if (parms.containsKey("RESET")) {
            if (last != null)
                httpReq.removeUrlParameter("CATACAT");
            return "";
        }
        final boolean mobs = parms.containsKey("MOBS") || parms.containsKey("MOB");
        if (!httpReq.getRequestObjects().containsKey("CATACATS" + (mobs ? "M" : "I")))
            httpReq.getRequestObjects().put("CATACATS" + (mobs ? "M" : "I"), mobs ? CMLib.catalog().getMobCatalogCatagories() : CMLib.catalog().getItemCatalogCatagories());
        final String[] cats = (String[]) httpReq.getRequestObjects().get("CATACATS" + (mobs ? "M" : "I"));
        if (parms.containsKey("WIDTH"))
            return "" + 100 / (cats.length + 1);
        String lastID = null;
        if ((last != null) && (last.equalsIgnoreCase("")))
            last = "UNCATEGORIZED";
        for (String cat : cats) {
            if (cat.length() == 0)
                cat = "UNCATEGORIZED";
            if ((last == null) || ((lastID != null) && (last.equals(lastID)) && (!cat.equals(lastID)))) {
                httpReq.addFakeUrlParameter("CATACAT", cat);
                return "";
            }
            lastID = cat;
        }
        httpReq.addFakeUrlParameter("CATACAT", "");
        if (parms.containsKey("EMPTYOK"))
            return "<!--EMPTY-->";
        return " @break@";
    }

}
