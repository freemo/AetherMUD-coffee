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
package com.planet_ink.game.Items.Weapons;

import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Items.interfaces.Weapon;
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Environmental;


public class EternityQuarterstaff extends Quarterstaff {
    public EternityQuarterstaff() {
        super();

        setName("a quarterstaff");
        setDisplayText("a wooden quarterstaff lies on the ground.");
        setDescription("It\\`s long and wooden, just like a quarterstaff ought to be.");
        secretIdentity = "A quarterstaff fashioned from a branch of one of the Fox god's Eternity Trees.  A truly wondrous gift.";
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(20);
        basePhyStats.setWeight(4);
        this.setUsesRemaining(50);
        basePhyStats().setAttackAdjustment(0);
        basePhyStats().setDamage(12);
        baseGoldValue += 5000;
        basePhyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_BONUS);
        wornLogicalAnd = true;
        properWornBitmap = Wearable.WORN_HELD | Wearable.WORN_WIELD;
        material = RawMaterial.RESOURCE_OAK;
        weaponDamageType = TYPE_BASHING;
        weaponClassification = Weapon.CLASS_STAFF;
        recoverPhyStats();

    }

    @Override
    public String ID() {
        return "EternityQuarterstaff";
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        final MOB mob = msg.source();
        switch (msg.sourceMinor()) {
            case CMMsg.TYP_SPEAK:
                if ((mob.isMine(this))
                    && (!amWearingAt(Wearable.IN_INVENTORY))
                    && (msg.target() instanceof MOB)
                    && (mob.location().isInhabitant((MOB) msg.target()))) {
                    final MOB target = (MOB) msg.target();
                    final int x = msg.targetMessage().toUpperCase().indexOf("HEAL");
                    if (x >= 0) {
                        if (usesRemaining() > 0) {
                            this.setUsesRemaining(this.usesRemaining() - 5);
                            final CMMsg msg2 = CMClass.getMsg(mob, target, this, CMMsg.MSG_CAST_VERBAL_SPELL, L("<S-NAME> point(s) <S-HIS-HER> quarterstaff at <T-NAMESELF>, and delivers a healing beam of light."));
                            if (mob.location().okMessage(mob, msg2)) {
                                final int healing = 1 + (int) Math.round(CMath.div(phyStats().level(), 10.0));
                                target.curState().adjHitPoints(healing, target.maxState());
                                target.tell(L("You feel a little better!"));
                                return;
                            }

                        }
                    }
                }
                break;
            default:
                break;
        }
        super.executeMsg(myHost, msg);
    }
}
