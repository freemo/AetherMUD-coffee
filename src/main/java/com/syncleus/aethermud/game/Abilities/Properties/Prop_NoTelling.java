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
package com.planet_ink.game.Abilities.Properties;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Areas.interfaces.Area;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMSecurity;
import com.planet_ink.game.core.interfaces.Environmental;


public class Prop_NoTelling extends Property {
    @Override
    public String ID() {
        return "Prop_NoTelling";
    }

    @Override
    public String name() {
        return "Tel Neutralizing";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ROOMS | Ability.CAN_AREAS | Ability.CAN_MOBS;
    }

    @Override
    public String accountForYourself() {
        return "No Telling Field";
    }

    //protected boolean quiet=false;

    @Override
    public void setMiscText(String newMiscText) {
        super.setMiscText(newMiscText);
        //final List<String> parms=CMParms.parse(newMiscText.toUpperCase().trim());
        //quiet = parms.contains("QUIET");
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if ((msg.sourceMinor() == CMMsg.TYP_TELL)
            && (msg.target() instanceof MOB)
            && ((!(affected instanceof MOB)) || (msg.source() == affected) || (msg.target() == affected))) {
            if (affected instanceof MOB)
                msg.source().tell(L("Your message drifts into oblivion."));
            else if (affected instanceof Room) {
                if ((CMSecurity.isAllowed(msg.source(), (Room) affected, CMSecurity.SecFlag.CMDROOMS))
                    || (CMSecurity.isAllowed((MOB) msg.target(), (Room) affected, CMSecurity.SecFlag.CMDROOMS)))
                    return true;
                else if (((Room) affected).isInhabitant(msg.source()))
                    msg.source().tell(L("This is a no-tell zone."));
                else
                    msg.source().tell(L("@x1 is in a no-tell zone.", msg.target().Name()));
            } else if (affected instanceof Area) {
                if (((Area) affected).amISubOp(msg.source().Name())
                    || (((Area) affected).amISubOp(msg.target().Name()))
                    || (CMSecurity.isAllowedAnywhere(msg.source(), CMSecurity.SecFlag.CMDROOMS)))
                    return true;
                else if (CMLib.map().areaLocation(msg.source()) == affected)
                    msg.source().tell(L("This is a no-tell area."));
                else
                    msg.source().tell(L("@x1 is in a no-tell area.", msg.target().Name()));
            } else
                msg.source().tell(L("No telling."));
            return false;
        }
        return true;
    }
}
