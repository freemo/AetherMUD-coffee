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

import com.planet_ink.coffee_mud.Common.interfaces.PlayerStats;
import com.planet_ink.coffee_mud.Libraries.interfaces.ChannelsLibrary;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;

import java.util.List;


public class NoChannel extends StdCommand {
    private final String[] access = null;

    public NoChannel() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        final PlayerStats pstats = mob.playerStats();
        if (pstats == null)
            return false;
        String channelName = commands.get(0).toUpperCase().trim().substring(2);
        commands.remove(0);
        int channelNum = -1;
        for (int c = 0; c < CMLib.channels().getNumChannels(); c++) {
            final ChannelsLibrary.CMChannel chan = CMLib.channels().getChannel(c);
            if (chan.name().equalsIgnoreCase(channelName)) {
                channelNum = c;
                channelName = chan.name();
            }
        }
        if (channelNum < 0)
            for (int c = 0; c < CMLib.channels().getNumChannels(); c++) {
                final ChannelsLibrary.CMChannel chan = CMLib.channels().getChannel(c);
                if (chan.name().toUpperCase().startsWith(channelName)) {
                    channelNum = c;
                    channelName = chan.name();
                }
            }
        if ((channelNum < 0)
            || (!CMLib.masking().maskCheck(CMLib.channels().getChannel(channelNum).mask(), mob, true))) {
            mob.tell(L("This channel is not available to you."));
            return false;
        }
        if (!CMath.isSet(pstats.getChannelMask(), channelNum)) {
            pstats.setChannelMask(pstats.getChannelMask() | (1 << channelNum));
            mob.tell(L("The @x1 channel has been turned off.  Use `@x2` to turn it back on.", channelName, channelName.toUpperCase()));
        } else
            mob.tell(L("The @x1 channel is already off.", channelName));
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
