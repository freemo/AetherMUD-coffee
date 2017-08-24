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

import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_web.interfaces.HTTPRequest;
import com.planet_ink.coffee_web.interfaces.HTTPResponse;


public class AddRequestParameter extends StdWebMacro {
    @Override
    public String name() {
        return "AddRequestParameter";
    }

    @Override
    public String runMacro(HTTPRequest httpReq, String parm, HTTPResponse httpResp) {
        final String str = "";
        final java.util.Map<String, String> parms = parseParms(parm);

        for (final String key : parms.keySet()) {
            if (key != null) {
                String val = parms.get(key);
                if (val == null)
                    val = "";
                if ((val.equals("++") && (httpReq.isUrlParameter(key))))
                    val = "" + (CMath.s_int(httpReq.getUrlParameter(key)) + 1);
                else if ((val.equals("--") && (httpReq.isUrlParameter(key))))
                    val = "" + (CMath.s_int(httpReq.getUrlParameter(key)) - 1);

                httpReq.addFakeUrlParameter(key, val);
            }
        }
        return clearWebMacros(str);
    }
}
