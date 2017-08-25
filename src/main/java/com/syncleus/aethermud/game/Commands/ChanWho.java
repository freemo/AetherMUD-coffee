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
package com.planet_ink.game.Commands;

import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Common.interfaces.Session;
import com.planet_ink.game.Libraries.interfaces.ChannelsLibrary;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.CMSecurity;
import com.planet_ink.game.core.CMStrings;

import java.util.List;


public class ChanWho extends StdCommand {
    private final String[] access = I(new String[]{"CHANWHO"});

    public ChanWho() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        String channel = CMParms.combine(commands, 1);
        if ((channel == null) || (channel.length() == 0)) {
            mob.tell(L("You must specify a channel name. Try CHANNELS for a list."));
            return false;
        }
        final int x = channel.indexOf('@');
        String mud = null;
        if (x > 0) {
            mud = channel.substring(x + 1);
            final int channelInt = CMLib.channels().getChannelIndex(channel.substring(0, x).toUpperCase());
            channel = CMLib.channels().getChannel(channelInt).name().toUpperCase();
            if ((channel.length() == 0) || (channelInt < 0)) {
                mob.tell(L("You must specify a valid channel name. Try CHANNELS for a list."));
                return false;
            }
            CMLib.intermud().i3chanwho(mob, channel, mud);
            return false;
        }
        final int channelInt = CMLib.channels().getChannelIndex(channel.toUpperCase());
        channel = CMLib.channels().getChannel(channelInt).name();
        if (channelInt < 0) {
            mob.tell(L("You must specify a valid channel name. Try CHANNELS for a list."));
            return false;
        }
        final String head = "^x\n\rListening on " + channel + ":^?^.^N\n\r";
        final StringBuffer buf = new StringBuffer("");
        final boolean areareq = CMLib.channels().getChannel(channelInt).flags().contains(ChannelsLibrary.ChannelFlag.SAMEAREA);
        for (final Session S : CMLib.sessions().localOnlineIterable()) {
            MOB mob2 = S.mob();
            if ((mob2 != null) && (mob2.soulMate() != null))
                mob2 = mob2.soulMate();
            if ((CMLib.channels().mayReadThisChannel(mob, areareq, S, channelInt))
                && (mob2 != null)
                && (CMLib.flags().isInTheGame(mob2, true))
                && ((((mob2.phyStats().disposition() & PhyStats.IS_CLOAKED) == 0)
                || ((CMSecurity.isAllowedAnywhere(mob, CMSecurity.SecFlag.CLOAK) || CMSecurity.isAllowedAnywhere(mob, CMSecurity.SecFlag.WIZINV)) && (mob.phyStats().level() >= mob2.phyStats().level())))))
                buf.append("^x[^?^.^N" + CMStrings.padRight(mob2.name(), 20) + "^x]^?^.^N\n\r");
        }
        if (buf.length() == 0)
            mob.tell(L("@x1Nobody!", head));
        else
            mob.tell(head + buf.toString());
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
