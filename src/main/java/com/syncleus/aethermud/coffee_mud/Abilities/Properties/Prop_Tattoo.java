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
package com.planet_ink.coffee_mud.Abilities.Properties;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Tattooable;

import java.util.List;
import java.util.Vector;


public class Prop_Tattoo extends Property {
    public static List<String> getTattoos(MOB mob) {
        List<String> tattos = new Vector<String>();
        Ability A = mob.fetchAbility("Prop_Tattoo");
        if (A != null)
            tattos = CMParms.parseSemicolons(A.text().toUpperCase(), true);
        else {
            A = mob.fetchEffect("Prop_Tattoo");
            if (A != null)
                tattos = CMParms.parseSemicolons(A.text().toUpperCase(), true);
        }
        return tattos;
    }

    @Override
    public String ID() {
        return "Prop_Tattoo";
    }

    @Override
    public String name() {
        return "A Tattoo";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    public void setMiscText(String text) {
        if (affected instanceof MOB) {
            final MOB M = (MOB) affected;
            final List<String> V = CMParms.parseSemicolons(text, true);
            for (int v = 0; v < V.size(); v++) {
                String s = V.get(v);
                Tattooable T = M;
                if (s.toLowerCase().startsWith("account ")
                    && (M.playerStats() != null)
                    && (M.playerStats().getAccount() != null)) {
                    T = M.playerStats().getAccount();
                    s = s.substring(8).trim();
                }
                final int x = s.indexOf(' ');
                if ((x > 0) && (CMath.isNumber(s.substring(0, x))))
                    T.addTattoo(s.substring(x + 1).trim(), CMath.s_int(s.substring(0, x)));
                else
                    T.addTattoo(s);
            }
        }
        savable = false;
    }
}
