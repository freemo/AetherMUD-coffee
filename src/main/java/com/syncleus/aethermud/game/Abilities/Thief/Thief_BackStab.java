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
package com.syncleus.aethermud.game.Abilities.Thief;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Thief_BackStab extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Back Stab");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Backstabbing)");
    private static final String[] triggerStrings = I(new String[]{"BACKSTAB", "BS"});
    protected String lastMOB = "";
    protected int controlCode = 0;

    @Override
    public String ID() {
        return "Thief_BackStab";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
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
    public int usageType() {
        return USAGE_MOVEMENT;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_DIRTYFIGHTING;
    }

    @Override
    public int abilityCode() {
        return controlCode;
    }

    @Override
    public void setAbilityCode(int newCode) {
        super.setAbilityCode(newCode);
        controlCode = newCode;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        final int factor = (int) Math.round(CMath.div(adjustedLevel((MOB) affected, 0), 6.0)) + 2 + abilityCode();
        affectableStats.setDamage(affectableStats.damage() * factor);
        affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() + 100 + (10 * getXLEVELLevel(invoker())));
    }

    @Override
    public void executeMsg(Environmental myHost, CMMsg msg) {
        super.executeMsg(myHost, msg);
        if ((msg.source() == invoker())
            && (msg.target() instanceof MOB)
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && (msg.tool() == msg.source().fetchWieldedItem())
            && (!("" + msg.target()).equals(lastMOB))) {
            if ((msg.trailerMsgs() == null) || (msg.trailerMsgs().size() == 0))
                msg.addTrailerMsg(CMClass.getMsg(msg.source(), msg.target(), msg.tool(), CMMsg.MSG_OK_ACTION, L("<S-NAME> stab(s) <T-NAME> in the back with <O-NAME>!")));
            lastMOB = "" + msg.target();
        }
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if ((mob != null) && (target != null)) {
            if (!(target instanceof MOB))
                return Ability.QUALITY_INDIFFERENT;
            if (mob.isInCombat())
                return Ability.QUALITY_INDIFFERENT;
            if (CMLib.flags().canBeSeenBy(mob, (MOB) target))
                return Ability.QUALITY_INDIFFERENT;
            if (lastMOB.equals(target + ""))
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if ((commands.size() < 1) && (givenTarget == null)) {
            mob.tell(L("Backstab whom?"));
            return false;
        }
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (CMLib.flags().canBeSeenBy(mob, target)) {
            mob.tell(L("@x1 is watching you too closely to do that.", target.name(mob)));
            return false;
        }
        if (lastMOB.equals(target + "")) {
            mob.tell(target, null, null, L("@x1 is watching <S-HIS-HER> back too closely to do that again.", target.name(mob)));
            return false;
        }
        if (mob.isInCombat()) {
            mob.tell(L("You are too busy to focus on backstabbing right now."));
            return false;
        }

        CMLib.commands().postDraw(mob, false, true);

        final Item I = mob.fetchWieldedItem();
        Weapon weapon = null;
        if ((I != null) && (I instanceof Weapon))
            weapon = (Weapon) I;
        if (weapon == null) {
            mob.tell(mob, target, null, L("Backstab <T-HIM-HER> with what? You need to wield a weapon!"));
            return false;
        }
        if ((weapon.weaponClassification() == Weapon.CLASS_BLUNT)
            || (weapon.weaponClassification() == Weapon.CLASS_HAMMER)
            || (weapon.weaponClassification() == Weapon.CLASS_FLAILED)
            || (weapon.weaponClassification() == Weapon.CLASS_RANGED)
            || (weapon.weaponClassification() == Weapon.CLASS_THROWN)
            || (weapon.weaponClassification() == Weapon.CLASS_STAFF)) {
            mob.tell(mob, target, weapon, L("You cannot stab anyone with <O-NAME>."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        boolean success = proficiencyCheck(mob, 0, auto);

        final CMMsg msg = CMClass.getMsg(mob, target, this, (auto ? CMMsg.MSG_OK_ACTION : CMMsg.MSG_THIEF_ACT), auto ? "" : L("<S-NAME> attempt(s) to stab <T-NAMESELF> in the back!"));
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            if (((!success) || (CMLib.flags().canBeSeenBy(mob, target)) || (msg.value() > 0)) && (!CMLib.flags().isSleeping(target)))
                mob.location().show(target, mob, CMMsg.MSG_OK_VISUAL, auto ? "" : L("<S-NAME> spot(s) <T-NAME>!"));
            else {
                mob.addEffect(this);
                mob.recoverPhyStats();
            }
            try {
                CMLib.combat().postAttack(mob, target, weapon);
            } finally {
                mob.delEffect(this);
                mob.recoverPhyStats();
            }
            lastMOB = "" + target;
        } else
            success = false;
        return success;
    }

}
