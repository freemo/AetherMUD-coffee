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
package com.planet_ink.game.Abilities.Songs;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Environmental;


public class Skill_Stability extends BardSkill {
    private final static String localizedName = CMLib.lang().L("Stability");

    @Override
    public String ID() {
        return "Skill_Stability";
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
        return Ability.QUALITY_INDIFFERENT;
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
        return Ability.ACODE_SKILL | Ability.DOMAIN_ACROBATIC;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if ((msg.tool() instanceof Ability)
            && (msg.amITarget(affected))
            && (msg.sourceMinor() != CMMsg.TYP_TEACH)
            && (((Ability) msg.tool()).abstractQuality() == Ability.QUALITY_MALICIOUS)
            && (CMath.bset(((Ability) msg.tool()).flags(), Ability.FLAG_MOVING))
            && ((mob.fetchAbility(ID()) == null) || proficiencyCheck(null, -40 + (2 * getXLEVELLevel(mob)), false))) {
            Room roomS = null;
            Room roomD = null;
            if (msg.target() instanceof MOB)
                roomD = ((MOB) msg.target()).location();
            if (msg.source().location() != null)
                roomS = msg.source().location();
            if (msg.target() instanceof Room)
                roomD = (Room) msg.target();

            if ((roomS != null) && (roomD != null) && (roomS == roomD))
                roomD = null;

            if (roomS != null)
                roomS.show((MOB) affected, null, msg.tool(), CMMsg.MSG_OK_VISUAL, L("<S-NAME> remain(s) stable despite the <O-NAME>."));
            if (roomD != null)
                roomD.show((MOB) affected, null, msg.tool(), CMMsg.MSG_OK_VISUAL, L("<S-NAME> remain(s) stable despite the <O-NAME>."));
            helpProficiency((MOB) affected, 0);
            return false;
        }
        return true;
    }

}
