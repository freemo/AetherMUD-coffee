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
package com.syncleus.aethermud.game.WebMacros.grinder;

import com.syncleus.aethermud.game.Common.interfaces.AbilityComponent;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.web.interfaces.HTTPRequest;

import java.util.List;
import java.util.Vector;


public class GrinderComponent {
    public String name() {
        return "GrinderComponent";
    }

    public String runMacro(HTTPRequest httpReq, String parm) {
        final String last = httpReq.getUrlParameter("COMPONENT");
        if (last == null)
            return " @break@";
        if (last.length() > 0) {
            final String fixedCompID = last.replace(' ', '_').toUpperCase();
            final List<AbilityComponent> set = new Vector<AbilityComponent>();
            int posDex = 1;
            while (httpReq.isUrlParameter(fixedCompID + "_PIECE_CONNECTOR_" + posDex) && httpReq.getUrlParameter(fixedCompID + "_PIECE_CONNECTOR_" + posDex).trim().length() > 0) {
                final String mask = httpReq.getUrlParameter(fixedCompID + "_PIECE_MASK_" + posDex);
                final String str = httpReq.getUrlParameter(fixedCompID + "_PIECE_STRING_" + posDex);
                final String amt = httpReq.getUrlParameter(fixedCompID + "_PIECE_AMOUNT_" + posDex);
                final String conn = httpReq.getUrlParameter(fixedCompID + "_PIECE_CONNECTOR_" + posDex);
                final String loc = httpReq.getUrlParameter(fixedCompID + "_PIECE_LOCATION_" + posDex);
                final String type = httpReq.getUrlParameter(fixedCompID + "_PIECE_TYPE_" + posDex);
                final String consumed = httpReq.getUrlParameter(fixedCompID + "_PIECE_CONSUMED_" + posDex);
                if (!conn.equalsIgnoreCase("DELETE")) {
                    final AbilityComponent able = (AbilityComponent) CMClass.getCommon("DefaultAbilityComponent");
                    able.setAmount(CMath.s_int(amt));
                    if (posDex == 1)
                        able.setConnector(AbilityComponent.CompConnector.AND);
                    else
                        able.setConnector(AbilityComponent.CompConnector.valueOf(conn));
                    able.setConsumed((consumed != null) && (consumed.equalsIgnoreCase("on") || consumed.equalsIgnoreCase("checked")));
                    able.setLocation(AbilityComponent.CompLocation.valueOf(loc));
                    able.setMask(mask);
                    able.setType(AbilityComponent.CompType.valueOf(type), str);
                    set.add(able);
                }
                posDex++;
            }

            if (CMLib.ableComponents().getAbilityComponentMap().containsKey(last.toUpperCase().trim())) {
                final List<AbilityComponent> xset = CMLib.ableComponents().getAbilityComponentMap().get(last.toUpperCase().trim());
                xset.clear();
                xset.addAll(set);
            } else
                CMLib.ableComponents().getAbilityComponentMap().put(last.toUpperCase().trim(), set);
            CMLib.ableComponents().alterAbilityComponentFile(last.toUpperCase().trim(), false);
        }
        return "";
    }
}
