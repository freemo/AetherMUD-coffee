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
package com.planet_ink.coffee_mud.Abilities.Fighter;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.AmmunitionWeapon;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;


public class Fighter_RapidShot extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Rapid Shot");

    @Override
    public String ID() {
        return "Fighter_RapidShot";
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
        return Ability.ACODE_SKILL | Ability.DOMAIN_MARTIALLORE;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if (mob.isInCombat()) {
            final Item w = mob.fetchWieldedItem();
            if ((w instanceof AmmunitionWeapon)
                && (((Weapon) w).weaponClassification() == Weapon.CLASS_RANGED)
                && (((AmmunitionWeapon) w).ammunitionType().length() > 0)
                && ((mob.rangeToTarget() >= w.minRange()) || ((w.phyStats().sensesMask() & PhyStats.SENSE_ITEMNOMINRANGE) == PhyStats.SENSE_ITEMNOMINRANGE))
                && ((mob.fetchAbility(ID()) == null) || proficiencyCheck(null, 0, false))) {
                helpProficiency(mob, 0);
                final int extraAttacks = 1 + (int) Math.round(Math.floor(CMath.div(adjustedLevel(mob, 0), 16.0)));
                for (int i = 0; (i < extraAttacks) && (w.usesRemaining() > 0); i++)
                    CMLib.combat().postAttack(mob, mob.getVictim(), w);
            }
        }
        return true;
    }
}
