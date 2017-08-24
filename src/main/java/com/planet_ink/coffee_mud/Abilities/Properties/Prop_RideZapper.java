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
import com.planet_ink.coffee_mud.Abilities.interfaces.TriggeredAffect;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Rideable;


public class Prop_RideZapper extends Prop_HaveZapper {
    @Override
    public String ID() {
        return "Prop_RideZapper";
    }

    @Override
    public String name() {
        return "Restrictions to riding";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ITEMS | Ability.CAN_MOBS;
    }

    @Override
    protected String defaultMessage() {
        return "<O-NAME> zaps <S-NAME>, making <S-HIM-HER> jump up!";
    }

    @Override
    public String accountForYourself() {
        return "Mounting restricted as follows: " + CMLib.masking().maskDesc(miscText);
    }

    @Override
    public int triggerMask() {
        return TriggeredAffect.TRIGGER_MOUNT;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (affected == null)
            return true;
        if (!(affected instanceof Rideable))
            return true;

        final MOB mob = msg.source();
        if (mob.location() == null)
            return true;

        if (msg.amITarget(affected))
            switch (msg.targetMinor()) {
                case CMMsg.TYP_SIT:
                case CMMsg.TYP_SLEEP:
                case CMMsg.TYP_MOUNT:
                case CMMsg.TYP_ENTER:
                    if ((!CMLib.masking().maskCheck(text(), mob, actual)) && (CMLib.dice().rollPercentage() <= percent)) {
                        mob.location().show(mob, null, affected, CMMsg.MSG_OK_VISUAL, msgStr);
                        return false;
                    }
                    break;
            }
        return true;
    }
}
