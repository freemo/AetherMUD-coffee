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
package com.planet_ink.game.Abilities.Spells;

import com.planet_ink.game.Abilities.StdAbility;
import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.CharClasses.interfaces.CharClass;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMSecurity;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.Enumeration;
import java.util.List;


public class Spell extends StdAbility {

    protected static final int CHAIN_LENGTH = 4;
    private final static String localizedName = CMLib.lang().L("a Spell");
    private static final String[] triggerStrings = I(new String[]{"CAST", "CA", "C"});

    protected static boolean spellArmorCheck(StdAbility A, MOB mob, boolean auto) {
        if ((!auto) && (mob.isMine(A)) && (mob.location() != null)) {
            if ((!mob.isMonster())
                && (!A.disregardsArmorCheck(mob))
                && (!CMLib.utensils().armorCheck(mob, CharClass.ARMOR_CLOTH))
                && (CMLib.dice().rollPercentage() < 50)) {
                mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, A.L("<S-NAME> watch(es) <S-HIS-HER> armor absorb <S-HIS-HER> magical energy!"));
                return false;
            }
            if (!CMLib.flags().canConcentrate(mob)) {
                mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, A.L("<S-NAME> can't seem to concentrate."));
                return false;
            }
        }
        return true;
    }

    @Override
    public String ID() {
        return "Spell";
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
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL;
    }

    @Override
    public Ability maliciousAffect(MOB mob,
                                   Physical target,
                                   int asLevel,
                                   int tickAdjustmentFromStandard,
                                   int additionAffectCheckCode) {
        final Ability doneA = super.maliciousAffect(mob, target, asLevel, tickAdjustmentFromStandard, additionAffectCheckCode);
        if ((doneA != null)
            && (target != null)
            && (target instanceof MOB)
            && (mob != target)
            && (!((MOB) target).isMonster())
            && (CMLib.dice().rollPercentage() == 1)
            && (((MOB) target).charStats().getCurrentClass().baseClass().equals("Mage"))) {
            final MOB tmob = (MOB) target;
            int num = 0;
            for (final Enumeration<Ability> a = tmob.effects(); a.hasMoreElements(); ) {
                final Ability A = a.nextElement();
                if ((A != null)
                    && (A instanceof Spell)
                    && (A.abstractQuality() == Ability.QUALITY_MALICIOUS)) {
                    num++;
                    if ((num > 5) && (!CMSecurity.isDisabled(CMSecurity.DisFlag.AUTODISEASE))) {
                        final Ability A2 = CMClass.getAbility("Disease_Magepox");
                        if ((A2 != null) && (target.fetchEffect(A2.ID()) == null) && (!CMSecurity.isAbilityDisabled(A.ID())))
                            A2.invoke(mob, target, true, asLevel);
                        break;
                    }
                }
            }
        }
        return doneA;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        if (!spellArmorCheck(this, mob, auto))
            return false;
        return true;
    }
}
