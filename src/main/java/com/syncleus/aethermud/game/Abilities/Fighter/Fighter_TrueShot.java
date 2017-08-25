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
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.Weapon;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;


public class Fighter_TrueShot extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("True Shot");
    protected boolean gettingBonus = false;

    @Override
    public String ID() {
        return "Fighter_TrueShot";
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
        return Ability.ACODE_SKILL | Ability.DOMAIN_MARTIALLORE;
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
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        if (!(affected instanceof MOB))
            return;
        final Item w = ((MOB) affected).fetchWieldedItem();
        if ((w == null) || (!(w instanceof Weapon)))
            return;
        if ((((Weapon) w).weaponClassification() == Weapon.CLASS_RANGED)
            || (((Weapon) w).weaponClassification() == Weapon.CLASS_THROWN)) {
            gettingBonus = true;
            final int bonus = (int) Math.round(CMath.mul(affectableStats.attackAdjustment(), (CMath.div(proficiency(), 200.0 - (10 * getXLEVELLevel(invoker()))))));
            affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() + bonus);
        } else
            gettingBonus = false;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);

        if (!(affected instanceof MOB))
            return;

        final MOB mob = (MOB) affected;

        if ((msg.amISource(mob))
            && (gettingBonus)
            && (msg.targetMinor() == CMMsg.TYP_WEAPONATTACK)
            && (CMLib.dice().rollPercentage() > 95)
            && (mob.isInCombat())
            && (!mob.amDead())
            && (msg.target() instanceof MOB))
            helpProficiency(mob, 0);
    }
}
