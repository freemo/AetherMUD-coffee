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
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharState;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;


public class Prop_PracticeDummy extends Property {
    protected boolean unkillable = true;
    boolean disabled = false;

    @Override
    public String ID() {
        return "Prop_PracticeDummy";
    }

    @Override
    public String name() {
        return "Practice Dummy";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    public String accountForYourself() {
        return "Undefeatable";
    }

    @Override
    public void setMiscText(String newMiscText) {
        super.setMiscText(newMiscText);
        unkillable = newMiscText.toUpperCase().indexOf("KILL") < 0;
    }

    @Override
    public void affectCharState(MOB mob, CharState affectableMaxState) {
        super.affectCharState(mob, affectableMaxState);
        if (unkillable)
            affectableMaxState.setHitPoints(99999);
    }

    @Override
    public void affectPhyStats(Physical E, PhyStats affectableStats) {
        super.affectPhyStats(E, affectableStats);
        if (unkillable)
            affectableStats.setArmor(100);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;
        if ((affected instanceof MOB)
            && (msg.amISource((MOB) affected))) {
            if ((msg.sourceMinor() == CMMsg.TYP_DEATH) && (unkillable)) {
                msg.source().tell(L("You are not allowed to die."));
                return false;
            } else if (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS)) {
                if (unkillable)
                    msg.source().curState().setHitPoints(99999);
                ((MOB) affected).makePeace(true);
                final Room room = ((MOB) affected).location();
                if (room != null)
                    for (int i = 0; i < room.numInhabitants(); i++) {
                        final MOB mob = room.fetchInhabitant(i);
                        if ((mob.getVictim() != null) && (mob.getVictim() == affected))
                            mob.makePeace(true);
                    }
                return false;
            } else if (((msg.targetMinor() == CMMsg.TYP_GET) || (msg.targetMinor() == CMMsg.TYP_PUSH) || (msg.targetMinor() == CMMsg.TYP_PULL))
                && (msg.target() instanceof Item)) {
                msg.source().tell(L("Dummys cant get anything."));
                return false;
            }
        }
        return true;
    }
}
