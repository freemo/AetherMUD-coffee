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
package com.syncleus.aethermud.game.Commands;

import com.syncleus.aethermud.game.Common.interfaces.Clan;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Common.interfaces.Session;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMSecurity;
import com.syncleus.aethermud.game.core.collections.Pair;
import com.syncleus.aethermud.game.core.collections.XVector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ClanWho extends Who {
    private final String[] access = I(new String[]{"CLANWHO", "CLWH"});

    public ClanWho() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        final String clanName = CMParms.combine(commands, 1).toUpperCase();
        final StringBuffer msg = new StringBuffer("");
        final List<String> clanList = new XVector<String>();
        if (clanName.trim().length() > 0) {
            final Clan C = CMLib.clans().findClan(clanName);
            if (C == null)
                mob.tell(L("There's no such clan as '@x1'.", clanName));
            else
                clanList.add(C.clanID());
        } else {
            for (final Pair<Clan, Integer> c : mob.clans())
                clanList.add(c.first.clanID());
            if (clanList.size() == 0) {
                mob.tell(L("You need to specify a clan."));
                return false;
            }
        }
        final Set<MOB> alreadyDone = new HashSet<MOB>();
        final int[] colWidths = getShortColWidths(mob);
        for (final String clanID : clanList) {
            final Clan C = CMLib.clans().getClan(clanID);
            if (C != null) {
                msg.append("\n\r^x").append(C.getGovernmentName()).append(" ").append(C.getName()).append("\n\r");
                msg.append(getHead(colWidths));
                for (final Session S : CMLib.sessions().localOnlineIterable()) {
                    MOB mob2 = S.mob();
                    if ((mob2 != null) && (mob2.soulMate() != null))
                        mob2 = mob2.soulMate();

                    if ((mob2 != null)
                        && (!S.isStopped())
                        && ((((mob2.phyStats().disposition() & PhyStats.IS_CLOAKED) == 0)
                        || ((CMSecurity.isAllowedAnywhere(mob, CMSecurity.SecFlag.CLOAK) || CMSecurity.isAllowedAnywhere(mob, CMSecurity.SecFlag.WIZINV)) && (mob.phyStats().level() >= mob2.phyStats().level()))))
                        && (mob2.getClanRole(C.clanID()) != null)
                        && (!alreadyDone.contains(mob2))
                        && (CMLib.flags().isInTheGame(mob2, true))
                        && (mob2.phyStats().level() > 0)) {
                        msg.append(showWhoShort(mob2, colWidths));
                        alreadyDone.add(mob2);
                    }
                }
            }
        }
        mob.tell(msg.toString());
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
