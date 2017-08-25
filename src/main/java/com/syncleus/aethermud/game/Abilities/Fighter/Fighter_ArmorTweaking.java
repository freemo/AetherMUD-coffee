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
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Fighter_ArmorTweaking extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Armor Tweaking");
    private static final String[] triggerStrings = I(new String[]{"ARMORTWEAK", "TWEAK"});
    private int armorBonus = 1;

    @Override
    public String ID() {
        return "Fighter_ArmorTweaking";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ITEMS;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_ITEMS;
    }

    @Override
    public int maxRange() {
        return adjustedMaxInvokerRange(0);
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_ARMORUSE;
    }

    @Override
    public int usageType() {
        return USAGE_MANA;
    }

    @Override
    public void executeMsg(Environmental host, CMMsg msg) {
        super.executeMsg(host, msg);
        if (affected instanceof Item) {
            if (((Item) affected).amWearingAt(Wearable.IN_INVENTORY)
                || ((invoker() != null) && (CMLib.flags().isInTheGame(invoker(), false) && (((Item) affected).owner() != invoker()))))
                unInvoke();
        }
    }

    @Override
    public void setMiscText(String newMiscText) {
        super.setMiscText(newMiscText);
        if (newMiscText.length() > 0)
            armorBonus = CMath.s_int(newMiscText);
    }

    @Override
    public void unInvoke() {
        if ((affected instanceof Item)
            && (!((Item) affected).amDestroyed())
            && (((Item) affected).owner() instanceof MOB)) {
            final MOB M = (MOB) ((Item) affected).owner();
            if ((!M.amDead()) && (CMLib.flags().isInTheGame(M, true)) && (!((Item) affected).amWearingAt(Wearable.IN_INVENTORY)))
                M.tell(M, affected, null, L("<T-NAME> no longer feel(s) quite as snuggly tweaked."));
        }
        super.unInvoke();
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats stats) {
        if ((affected instanceof Item) && (armorBonus > 0) && (((Item) affected).owner() instanceof MOB)) {
            stats.setArmor(stats.armor() + armorBonus);
            stats.addAmbiance("^w*^?");
        }
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if ((mob.isMonster()) || (mob.isInCombat()))
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {

        final Item armor = super.getTarget(mob, null, givenTarget, null, commands, Wearable.FILTER_WORNONLY);
        if (armor == null)
            return false;
        if ((!armor.amWearingAt(Wearable.WORN_ABOUT_BODY))
            && (!armor.amWearingAt(Wearable.WORN_ARMS))
            && (!armor.amWearingAt(Wearable.WORN_BACK))
            && (!armor.amWearingAt(Wearable.WORN_HANDS))
            && (!armor.amWearingAt(Wearable.WORN_HEAD))
            && (!armor.amWearingAt(Wearable.WORN_LEGS))
            && (!armor.amWearingAt(Wearable.WORN_NECK))
            && (!armor.amWearingAt(Wearable.WORN_TORSO))
            && (!armor.amWearingAt(Wearable.WORN_WAIST))) {
            mob.tell(L("@x1 can not be tweaked to provide any more benefit.", armor.name()));
            return false;
        }
        if ((!auto) && (mob.isInCombat())) {
            mob.tell(L("You are a bit too busy to do that right now."));
            return false;
        }
        final int bonus = (int) Math.round(CMath.mul(0.10 + (0.10 * getXLEVELLevel(mob)), armor.phyStats().armor()));
        if (bonus < 1) {
            mob.tell(L("@x1 is too weak of an armor to provide any more benefit from tweaking.", armor.name()));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final String str = auto ? L("@x1 snuggly covers <S-NAME>!", armor.name()) : L("<S-NAME> tweak(s) <T-NAMESELF> until it is as snuggly protective as possible.");
            final CMMsg msg = CMClass.getMsg(mob, armor, this, CMMsg.MSG_NOISYMOVEMENT, str);
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, armor, asLevel, 0);
                final Ability A = armor.fetchEffect(ID());
                if (A != null) {
                    A.setMiscText("" + bonus);
                    A.makeLongLasting();
                }
                armor.recoverPhyStats();
                mob.location().recoverRoomStats();
            }
        } else
            return beneficialVisualFizzle(mob, armor, L("<S-NAME> attempt(s) to tweak <T-NAME>, but just can't get it quite right."));
        return success;
    }

}
