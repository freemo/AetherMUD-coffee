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
package com.syncleus.aethermud.game.Abilities.Skills;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;


public class Skill_Resistance extends StdSkill {
    private final static String localizedName = CMLib.lang().L("Resistance");
    public int resistanceCode = 0;
    protected String displayText = "";

    @Override
    public String ID() {
        return "Skill_Resistance";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return displayText;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL;
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
    public void setMiscText(String newText) {
        super.setMiscText(newText);
        resistanceCode = 0;
        for (final int i : CharStats.CODES.SAVING_THROWS()) {
            if (newText.equalsIgnoreCase(CharStats.CODES.NAME(i)) || newText.equalsIgnoreCase(CharStats.CODES.DESC(i)))
                resistanceCode = i;
        }
        if (resistanceCode > 0)
            displayText = L("(Resistance to @x1)", newText.trim().toLowerCase());
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        if (invoker == null)
            return;
        final int amount = (int) Math.round(CMath.mul(CMath.div(proficiency(), 100.0), affected.phyStats().level()));
        if (resistanceCode > 0)
            affectableStats.setStat(resistanceCode, affectableStats.getStat(resistanceCode) + amount);
        else
            for (final int i : CharStats.CODES.SAVING_THROWS())
                affectableStats.setStat(i, affectableStats.getStat(i) + amount);
    }
}
