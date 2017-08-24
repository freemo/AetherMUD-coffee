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
package com.planet_ink.coffee_mud.Abilities.Common;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

import java.util.List;


public class Searching extends CommonSkill {
    private final static String localizedName = CMLib.lang().L("Searching");
    private static final String[] triggerStrings = I(new String[]{"SEARCH", "SEARCHING"});
    protected Room searchRoom = null;
    protected boolean success = false;
    private int bonusThisRoom = 0;

    public Searching() {
        super();
        displayText = L("You are searching...");
        verb = L("searching");
    }

    @Override
    public String ID() {
        return "Searching";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_COMMON_SKILL | Ability.DOMAIN_ALERT;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        affectableStats.setStat(CharStats.STAT_SAVE_OVERLOOKING, bonusThisRoom + proficiency() + affectableStats.getStat(CharStats.STAT_SAVE_OVERLOOKING));
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((affected != null) && (affected instanceof MOB) && (tickID == Tickable.TICKID_MOB)) {
            final MOB mob = (MOB) affected;
            if (tickUp == 1) {
                if (success == false) {
                    final StringBuffer str = new StringBuffer(L("You get distracted from your search.\n\r"));
                    commonTell(mob, str.toString());
                    unInvoke();
                    return super.tick(ticking, tickID);
                }

            }
            if (((MOB) affected).location() != searchRoom) {
                searchRoom = ((MOB) affected).location();
                bonusThisRoom = 0;
                ((MOB) affected).recoverCharStats();
            } else if (bonusThisRoom < affected.phyStats().level()) {
                bonusThisRoom += 5;
                ((MOB) affected).recoverCharStats();
            }
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void affectPhyStats(Physical affectedEnv, PhyStats affectableStats) {
        super.affectPhyStats(affectedEnv, affectableStats);
        if ((success) && (affectedEnv instanceof MOB) && (((MOB) affectedEnv).location() == searchRoom))
            affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_HIDDEN);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (super.checkStop(mob, commands))
            return true;
        verb = L("searching");
        success = false;
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        if (proficiencyCheck(mob, 0, auto))
            success = true;
        final int duration = 3 + getXLEVELLevel(mob);
        final CMMsg msg = CMClass.getMsg(mob, null, this, getActivityMessageType(), L(auto ? "" : "<S-NAME> start(s) searching."));
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            searchRoom = mob.location();
            beneficialAffect(mob, mob, asLevel, duration);
            mob.tell(L(" "));
            CMLib.commands().postLook(mob, true);
        }
        return true;
    }
}
