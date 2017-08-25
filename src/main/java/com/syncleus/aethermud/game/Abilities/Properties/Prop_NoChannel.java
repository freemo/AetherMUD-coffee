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
package com.syncleus.aethermud.game.Abilities.Properties;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Environmental;

import java.util.List;


public class Prop_NoChannel extends Property {
    protected List<String> channels = null;
    protected boolean receive = true;
    protected boolean sendOK = false;

    @Override
    public String ID() {
        return "Prop_NoChannel";
    }

    @Override
    public String name() {
        return "Channel Neutralizing";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ROOMS | Ability.CAN_AREAS;
    }

    @Override
    public String accountForYourself() {
        return "No Channeling Field";
    }

    @Override
    public void setMiscText(String newText) {
        super.setMiscText(newText);
        channels = CMParms.parseSemicolons(newText.toUpperCase(), true);
        int x = channels.indexOf("SENDOK");
        sendOK = (x >= 0);
        if (sendOK)
            channels.remove(x);
        x = channels.indexOf("QUIET");
        receive = (x < 0);
        if (!receive)
            channels.remove(x);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if (msg.othersMajor(CMMsg.MASK_CHANNEL)) {
            final int channelInt = msg.othersMinor() - CMMsg.TYP_CHANNEL;
            if ((msg.source() == affected) || (!(affected instanceof MOB))
                && ((channels == null) || (channels.size() == 0) || (channels.contains(CMLib.channels().getChannel(channelInt).name())))) {
                if (!sendOK) {
                    if (msg.source() == affected)
                        msg.source().tell(L("Your message drifts into oblivion."));
                    else if ((!(affected instanceof MOB))
                        && (CMLib.map().roomLocation(affected) == msg.source().location()))
                        msg.source().tell(L("This is a no-channel area."));
                    return false;
                }
                if (!receive) {
                    if ((msg.source() != affected)
                        || ((!(affected instanceof MOB)) && (CMLib.map().roomLocation(affected) != msg.source().location())))
                        return false;
                }
            }
        }
        return true;
    }
}
