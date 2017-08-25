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
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Skill_Disarm extends StdSkill {
    private final static String localizedName = CMLib.lang().L("Disarm");
    private static final String[] triggerStrings = I(new String[]{"DISARM"});

    @Override
    public String ID() {
        return "Skill_Disarm";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_MARTIALLORE;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if ((mob != null) && (target != null)) {
            final MOB victim = mob.getVictim();
            if (victim == null)
                return Ability.QUALITY_INDIFFERENT;
            if (mob.isInCombat() && (mob.rangeToTarget() > 0))
                return Ability.QUALITY_INDIFFERENT;
            if (mob.fetchWieldedItem() == null)
                return Ability.QUALITY_INDIFFERENT;
            Item hisWeapon = victim.fetchWieldedItem();
            if (hisWeapon == null)
                hisWeapon = victim.fetchHeldItem();
            if ((hisWeapon == null)
                || (!(hisWeapon instanceof Weapon))
                || ((((Weapon) hisWeapon).weaponClassification() == Weapon.CLASS_NATURAL)))
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!mob.isInCombat()) {
            mob.tell(L("You must be in combat to do this!"));
            return false;
        }
        final MOB victim = super.getTarget(mob, commands, givenTarget);
        if (victim == null)
            return false;
        if (((victim == mob.getVictim()) && (mob.rangeToTarget() > 0))
            || ((victim.getVictim() == mob) && (victim.rangeToTarget() > 0))) {
            mob.tell(L("You are too far away to disarm!"));
            return false;
        }
        if (mob.fetchWieldedItem() == null) {
            mob.tell(L("You need a weapon to disarm someone!"));
            return false;
        }
        Item hisWeapon = victim.fetchWieldedItem();
        if (hisWeapon == null)
            hisWeapon = victim.fetchHeldItem();
        if ((hisWeapon == null)
            || (!(hisWeapon instanceof Weapon))
            || ((((Weapon) hisWeapon).weaponClassification() == Weapon.CLASS_NATURAL))) {
            mob.tell(L("@x1 is not wielding a weapon!", victim.charStats().HeShe()));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        int levelDiff = victim.phyStats().level() - (mob.phyStats().level() + (2 * getXLEVELLevel(mob)));
        if (levelDiff > 0)
            levelDiff = levelDiff * 5;
        else
            levelDiff = 0;
        final boolean hit = (auto) || CMLib.combat().rollToHit(mob, victim);
        final boolean success = proficiencyCheck(mob, -levelDiff, auto) && (hit);
        if ((success)
            && ((hisWeapon.fitsOn(Wearable.WORN_WIELD))
            || hisWeapon.fitsOn(Wearable.WORN_WIELD | Wearable.WORN_HELD))) {
            if (mob.location().show(mob, victim, this, CMMsg.MSG_NOISYMOVEMENT, null)) {
                final CMMsg msg = CMClass.getMsg(victim, hisWeapon, null, CMMsg.MSG_DROP, null);
                if (mob.location().okMessage(mob, msg)) {
                    mob.location().send(victim, msg);
                    mob.location().show(mob, victim, CMMsg.MSG_NOISYMOVEMENT, auto ? L("<T-NAME> is disarmed!") : L("<S-NAME> disarm(s) <T-NAMESELF>!"));
                }
            }
        } else
            maliciousFizzle(mob, victim, L("<S-NAME> attempt(s) to disarm <T-NAMESELF> and fail(s)!"));
        return success;
    }

}
