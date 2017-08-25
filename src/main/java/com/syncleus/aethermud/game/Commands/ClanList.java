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
import com.syncleus.aethermud.game.Common.interfaces.Clan.Trophy;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMStrings;
import com.syncleus.aethermud.game.core.CMath;

import java.util.Enumeration;
import java.util.List;


@SuppressWarnings("rawtypes")
public class ClanList extends StdCommand {
    private final String[] access = I(new String[]{"CLANLIST", "CLANS"});

    public ClanList() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        final boolean trophySystemActive = CMLib.clans().trophySystemActive();
        final StringBuffer head = new StringBuffer("");
        head.append("^x[");
        head.append(CMStrings.padRight(L("Clan Name"), 30) + "| ");
        head.append(CMStrings.padRight(L("Type"), 10) + "| ");
        head.append(CMStrings.padRight(L("Status"), 7) + "| ");
        head.append(CMStrings.padRight(L("Members"), 7));
        if (trophySystemActive)
            head.append(" | " + CMStrings.padRight(L("Trophies"), 8));
        head.append("]^.^? \n\r");
        final StringBuffer msg = new StringBuffer("");
        for (final Enumeration e = CMLib.clans().clans(); e.hasMoreElements(); ) {
            final Clan thisClan = (Clan) e.nextElement();
            if (!thisClan.isPubliclyListedFor(mob))
                continue;

            final StringBuffer trophySet = new StringBuffer("");
            if (trophySystemActive) {
                for (final Trophy t : Trophy.values()) {
                    if (CMath.bset(thisClan.getTrophies(), t.flagNum()))
                        trophySet.append(t.codeString.charAt(0));
                }
            }

            msg.append(" ");
            msg.append("^<CLAN^>" + CMStrings.padRight(CMStrings.removeColors(thisClan.clanID()), 30) + "^</CLAN^>  ");
            msg.append(CMStrings.padRight(thisClan.getGovernmentName(), 10) + "  ");
            boolean war = false;
            for (final Enumeration e2 = CMLib.clans().clans(); e2.hasMoreElements(); ) {
                final Clan C = (Clan) e2.nextElement();
                if ((C != thisClan)
                    && ((thisClan.getClanRelations(C.clanID()) == Clan.REL_WAR)
                    || (C.getClanRelations(thisClan.clanID()) == Clan.REL_WAR))) {
                    war = true;
                    break;
                }
            }
            String status = (war) ? "At War" : "Active";
            switch (thisClan.getStatus()) {
                case Clan.CLANSTATUS_FADING:
                    status = "Inactive";
                    break;
                case Clan.CLANSTATUS_PENDING:
                    status = "Pending";
                    break;
            }
            msg.append(CMStrings.padRight(status, 7) + "  ");
            msg.append(CMStrings.padRight(Integer.toString(thisClan.getSize()), 7) + "   ");
            msg.append(trophySet);
            msg.append("\n\r");
        }
        mob.tell(head.toString() + msg.toString());
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
