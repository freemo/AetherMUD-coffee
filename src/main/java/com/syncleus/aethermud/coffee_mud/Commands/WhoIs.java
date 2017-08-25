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
package com.planet_ink.coffee_mud.Commands;

import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Common.interfaces.Session;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.intermud.i3.packets.Intermud;

import java.util.List;


public class WhoIs extends Who {
    private final String[] access = I(new String[]{"WHOIS"});

    public WhoIs() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        String mobName = CMParms.combine(commands, 1);
        if ((mobName == null) || (mobName.length() == 0)) {
            mob.tell(L("whois whom?"));
            return false;
        }

        final int x = mobName.indexOf("@");
        if (x >= 0) {
            if ((!(CMLib.intermud().i3online()))
                && (!CMLib.intermud().imc2online()))
                mob.tell(L("Intermud is unavailable."));
            else if (x == 0) {
                String mudName = mobName.substring(1);
                if ((mudName.toLowerCase().equals("coffeemuds") || mudName.toLowerCase().equals("all"))
                    && (CMSecurity.isAllowedAnywhere(mob, CMSecurity.SecFlag.I3))) {
                    List<String> muds = CMLib.intermud().getI3MudList(mudName.toLowerCase().equals("coffeemuds"));
                    long time = 0;
                    for (String mud : muds) {
                        CMLib.s_sleep(time + 1000);
                        long lastTime = System.currentTimeMillis();
                        CMLib.intermud().i3who(mob, mud);
                        lastTime = System.currentTimeMillis() - lastTime;
                        if (lastTime > time)
                            time = lastTime;
                    }
                } else
                    CMLib.intermud().i3who(mob, mudName);
            } else {
                String mudName = mobName.substring(x + 1);
                mobName = mobName.substring(0, x);
                if (Intermud.isAPossibleMUDName(mudName)) {
                    mudName = Intermud.translateName(mudName);
                    if (!Intermud.isUp(mudName)) {
                        mob.tell(L("@x1 is not available.", mudName));
                        return false;
                    }
                }
                CMLib.intermud().i3finger(mob, mobName, mudName);
            }
            return false;
        }

        final int[] colWidths = getShortColWidths(mob);
        final StringBuffer msg = new StringBuffer("");
        for (final Session S : CMLib.sessions().localOnlineIterable()) {
            final MOB mob2 = S.mob();
            if ((mob2 != null)
                && (((mob2.phyStats().disposition() & PhyStats.IS_CLOAKED) == 0)
                || ((CMSecurity.isAllowedAnywhere(mob, CMSecurity.SecFlag.CLOAK) || CMSecurity.isAllowedAnywhere(mob, CMSecurity.SecFlag.WIZINV))
                && (mob.phyStats().level() >= mob2.phyStats().level())))
                && (mob2.phyStats().level() > 0)
                && (mob2.name().toUpperCase().startsWith(mobName.toUpperCase())))
                msg.append(showWhoShort(mob2, colWidths));
        }
        if (msg.length() == 0)
            mob.tell(L("That person doesn't appear to be online.\n\r"));
        else {
            mob.tell(getHead(colWidths) + msg.toString());
        }
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
