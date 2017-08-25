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
package com.planet_ink.game.Abilities.Fighter;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CharStats;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;


public class Fighter_ViciousBlow extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Vicious Blow");

    @Override
    public String ID() {
        return "Fighter_ViciousBlow";
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
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
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
        return Ability.ACODE_SKILL | Ability.DOMAIN_ANATOMY;
    }

    @Override
    public void affectCharStats(MOB affectedMob, CharStats affectableStats) {
        super.affectCharStats(affectedMob, affectableStats);
        affectableStats.setStat(CharStats.STAT_CRIT_DAMAGE_PCT_WEAPON, affectableStats.getStat(CharStats.STAT_CRIT_DAMAGE_PCT_WEAPON) + 50 + (5 * super.getXLEVELLevel(affectedMob)));
    }

}
