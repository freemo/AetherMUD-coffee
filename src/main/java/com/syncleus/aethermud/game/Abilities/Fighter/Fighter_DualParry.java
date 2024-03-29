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
package com.syncleus.aethermud.game.Abilities.Fighter;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class Fighter_DualParry extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Dual Parry");
    boolean lastTime = false;

    @Override
    public String ID() {
        return "Fighter_DualParry";
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
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;

        if (msg.amITarget(mob)
            && (CMLib.flags().isAliveAwakeMobileUnbound(mob, true))
            && (msg.targetMinor() == CMMsg.TYP_WEAPONATTACK)
            && (mob.rangeToTarget() == 0)) {
            if ((msg.tool() instanceof Item)) {
                final Item attackerWeapon = (Item) msg.tool();
                final Item myOtherWeapon = mob.fetchHeldItem();
                if ((myOtherWeapon != null)
                    && (attackerWeapon != null)
                    && (myOtherWeapon instanceof Weapon)
                    && (attackerWeapon instanceof Weapon)
                    && (!myOtherWeapon.rawLogicalAnd())
                    && (CMLib.flags().canBeSeenBy(msg.source(), mob))
                    && (((Weapon) myOtherWeapon).weaponClassification() != Weapon.CLASS_FLAILED)
                    && (((Weapon) myOtherWeapon).weaponClassification() != Weapon.CLASS_RANGED)
                    && (((Weapon) myOtherWeapon).weaponClassification() != Weapon.CLASS_THROWN)
                    && (((Weapon) myOtherWeapon).weaponClassification() != Weapon.CLASS_NATURAL)
                    && (((Weapon) attackerWeapon).weaponClassification() != Weapon.CLASS_FLAILED)
                    && (((Weapon) attackerWeapon).weaponClassification() != Weapon.CLASS_NATURAL)
                    && (((Weapon) attackerWeapon).weaponClassification() != Weapon.CLASS_RANGED)
                    && (((Weapon) attackerWeapon).weaponClassification() != Weapon.CLASS_THROWN)) {
                    final CMMsg msg2 = CMClass.getMsg(mob, msg.source(), this, CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> parr(ys) @x1 attack with @x2!", attackerWeapon.name(), myOtherWeapon.name()));
                    if ((proficiencyCheck(null, mob.charStats().getStat(CharStats.STAT_DEXTERITY) - 90 + (2 * getXLEVELLevel(mob)), false))
                        && (!lastTime)
                        && (mob.location().okMessage(mob, msg2))) {
                        lastTime = true;
                        mob.location().send(mob, msg2);
                        helpProficiency(mob, 0);
                        return false;
                    }
                    lastTime = false;
                }
            }
        }
        return true;
    }
}
