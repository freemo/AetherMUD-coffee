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
package com.planet_ink.coffee_mud.Abilities.Specializations;

import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;


public class Specialization_Weapon extends StdAbility {
    private final static String localizedName = CMLib.lang().L("Weapon Specialization");
    protected boolean activated = false;
    protected int weaponClass = -1;
    protected int secondWeaponClass = -1;

    @Override
    public String ID() {
        return "Specialization_Weapon";
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
    public boolean isAutoInvoked() {
        return true;
    }

    @Override
    public boolean canBeUninvoked() {
        return false;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_WEAPON_USE;
    }

    protected int getDamageBonus(MOB mob, int dmgType) {
        switch (dmgType) {
            case Weapon.TYPE_SLASHING:
                return getX1Level(mob);
            case Weapon.TYPE_PIERCING:
                return getX2Level(mob);
            case Weapon.TYPE_BASHING:
                return getX3Level(mob);
            case Weapon.TYPE_SHOOT:
                return getX2Level(mob);
            default:
                return 0;
        }
    }

    protected boolean isWeaponMatch(Weapon W) {
        if ((W.weaponClassification() == weaponClass)
            || (weaponClass < 0)
            || (W.weaponClassification() == secondWeaponClass))
            return true;
        return false;
    }

    protected boolean canDamage(MOB mob, Weapon W) {
        return !W.amWearingAt(Wearable.IN_INVENTORY);
    }

    protected boolean isWearableItem(Item I) {
        return (I instanceof Weapon) && isWeaponMatch((Weapon) I);
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (activated && (msg.source() == affected)) {
            if ((msg.target() instanceof MOB)
                && (msg.tool() instanceof Weapon)) {
                final Weapon w = (Weapon) msg.tool();
                if (isWeaponMatch(w)) {
                    if ((msg.targetMinor() == CMMsg.TYP_WEAPONATTACK) && (CMLib.dice().rollPercentage() < 10))
                        helpProficiency((MOB) affected, 0);
                    else if ((msg.targetMinor() == CMMsg.TYP_DAMAGE)
                        && (msg.value() > 0)
                        && (canDamage(msg.source(), w)))
                        msg.setValue(msg.value() + (this.getDamageBonus(msg.source(), w.weaponDamageType())));
                }
            }
        }
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        activated = false;
        if (affected instanceof MOB) {
            final Item myWeapon = ((MOB) affected).fetchWieldedItem();
            if ((myWeapon instanceof Weapon)
                && (isWeaponMatch((Weapon) myWeapon))) {
                activated = true;
                affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()
                    + (int) Math.round((15.0 + (3.0 * getXLEVELLevel((MOB) affected))) * (CMath.div(proficiency(), 100.0))));

            }
        }
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;
        if ((msg.source() == affected)
            && (msg.target() instanceof Item)
            && ((msg.targetMinor() == CMMsg.TYP_HOLD) || (msg.targetMinor() == CMMsg.TYP_WIELD))
            && (isWearableItem((Item) msg.target()))
            && (((Item) msg.target()).phyStats().level() > msg.source().phyStats().level()))
            ((Item) msg.target()).phyStats().setLevel(((Item) msg.target()).phyStats().level() - ((1 + getX4Level(msg.source())) / 2));
        return true;
    }

}
