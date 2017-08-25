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
package com.planet_ink.coffee_mud.Abilities.Skills;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.BoardableShip;
import com.planet_ink.coffee_mud.Items.interfaces.SailingShip;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;


public class Skill_SeaManeuvers extends StdSkill {
    private final static String localizedName = CMLib.lang().L("Sea Maneuvers");

    @Override
    public String ID() {
        return "Skill_SeaManeuvers";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    public boolean isAutoInvoked() {
        return true;
    }

    @Override
    public boolean canBeUninvoked() {
        return false;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_SEATRAVEL;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        final Physical affected = this.affected;
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        final Room R = mob.location();
        if ((msg.sourceMinor() == CMMsg.TYP_ADVANCE)
            && (msg.target() instanceof Room)
            && (R != null)
            && (R.getArea() instanceof BoardableShip)
            && (R.roomID().length() > 0)
            && (msg.source().riding() == ((BoardableShip) R.getArea()).getShipItem())
            && (msg.source().Name().equals(msg.source().riding().Name()))
            && (msg.sourceMessage() != null)
            && (msg.targetMessage() != null)
            && (CMLib.directions().getStrictCompassDirectionCode(msg.sourceMessage()) >= 0)
            && (CMLib.directions().getStrictCompassDirectionCode(msg.targetMessage()) >= 0)
            && (!msg.sourceMessage().equals(msg.targetMessage()))) {
            final SailingShip ship = (SailingShip) ((BoardableShip) R.getArea()).getShipItem();
            if (ship.isInCombat()) {
                if (proficiencyCheck(mob, 0, false)) {
                    helpProficiency(mob, 0);
                    final String oldDirection = msg.sourceMessage();
                    final String newDirection = msg.targetMessage();
                    msg.setSourceMessage(newDirection);
                    if (msg.othersMessage() != null)
                        msg.setOthersMessage(CMStrings.replaceFirst(msg.othersMessage(), oldDirection, newDirection));
                }
            }
        }
        return true;
    }
}
