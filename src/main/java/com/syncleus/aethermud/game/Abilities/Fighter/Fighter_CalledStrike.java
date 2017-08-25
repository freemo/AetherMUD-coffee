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
import com.syncleus.aethermud.game.Abilities.interfaces.LimbDamage;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Fighter_CalledStrike extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Called Strike");
    private static final String[] triggerStrings = I(new String[]{"CALLEDSTRIKE"});
    protected String gone = "";
    protected MOB target = null;
    protected int hpReq = 9;

    @Override
    public String ID() {
        return "Fighter_CalledStrike";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_MOBS;
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
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() - 100);
    }

    protected boolean amputate() {
        final MOB mob = target;
        if (mob == null)
            return false;
        LimbDamage A = (LimbDamage) mob.fetchEffect("Amputation");
        if (A == null) {
            A = (LimbDamage) CMClass.getAbility("Amputation");
            A.setAffectedOne(mob);
        }
        if (A.damageLimb(gone) != null) {
            if (mob.fetchEffect(A.ID()) == null)
                mob.addNonUninvokableEffect(A);
            return true;
        }
        return false;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((affected == null) || (!(affected instanceof MOB)) || (target == null))
            return super.okMessage(myHost, msg);
        final MOB mob = (MOB) affected;
        if (msg.amISource(mob)
            && (msg.amITarget(target))
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)) {
            int hurtAmount = msg.value();
            final int reqDivisor = hpReq + getXLEVELLevel(invoker());
            if (hurtAmount >= (target.baseState().getHitPoints() / reqDivisor)) {
                hurtAmount = (target.baseState().getHitPoints() / reqDivisor);
                msg.setValue(msg.value() + hurtAmount);
                amputate();
            } else
                mob.tell(mob, target, null, L("You failed to cut off <T-YOUPOSS> '@x1'.", gone));
            unInvoke();
        }
        return super.okMessage(myHost, msg);
    }

    protected boolean prereqs(MOB mob, boolean quiet) {
        if (mob.isInCombat() && (mob.rangeToTarget() > 0)) {
            if (!quiet)
                mob.tell(L("You are too far away to perform a called strike!"));
            return false;
        }

        final Item w = mob.fetchWieldedItem();
        if ((w == null) || (!(w instanceof Weapon))) {
            if (!quiet)
                mob.tell(L("You need a weapon to perform a called strike!"));
            return false;
        }
        final Weapon wp = (Weapon) w;
        if (wp.weaponDamageType() != Weapon.TYPE_SLASHING) {
            if (!quiet)
                mob.tell(L("You cannot amputate with @x1!", wp.name()));
            return false;
        }
        return true;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (!prereqs(mob, true))
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!prereqs(mob, false))
            return false;

        gone = "";
        hpReq = 9;
        target = null;

        if (commands.size() > 0) {
            final String s = commands.get(0);
            if (mob.location().fetchInhabitant(s) != null)
                target = mob.location().fetchInhabitant(s);
            if ((target != null) && (!CMLib.flags().canBeSeenBy(target, mob))) {
                mob.tell(L("You can't see '@x1' here.", s));
                return false;
            }
            if (target != null)
                commands.remove(0);
        }
        if (target == null)
            target = mob.getVictim();
        if ((target == null) || (target == mob)) {
            mob.tell(L("Do this to whom?"));
            return false;
        }
        if (target.fetchEffect(ID()) != null) {
            mob.tell(L("@x1 already has a call against one of @x2 limbs.", target.name(mob), target.charStats().hisher()));
            return false;
        }

        LimbDamage ampuA = (LimbDamage) target.fetchEffect("Amputation");
        if (ampuA == null) {
            ampuA = (LimbDamage) CMClass.getAbility("Amputation");
            ampuA.setAffectedOne(target);
        }

        final List<String> remainingLimbList = ampuA.unaffectedLimbSet();
        if (remainingLimbList.size() == 0) {
            if (!auto)
                mob.tell(L("There is nothing left on @x1 to cut off!", target.name(mob)));
            return false;
        }
        if (mob.isMonster())
            gone = remainingLimbList.get(CMLib.dice().roll(1, remainingLimbList.size(), -1));
        else if (commands.size() <= 0) {
            mob.tell(L("You must specify a body part to cut off."));
            final StringBuffer str = new StringBuffer(L("Parts include: "));
            for (int i = 0; i < remainingLimbList.size(); i++)
                str.append((remainingLimbList.get(i)) + ", ");
            mob.tell(str.toString().substring(0, str.length() - 2) + ".");
            return false;
        } else {
            final String off = CMParms.combine(commands, 0);
            if ((off.equalsIgnoreCase("head"))
                && (target.charStats().getBodyPart(Race.BODY_HEAD) >= 0)) {
                gone = Race.BODYPARTSTR[Race.BODY_HEAD].toLowerCase();
                hpReq = 3;
            } else
                for (int i = 0; i < remainingLimbList.size(); i++) {
                    if (remainingLimbList.get(i).toUpperCase().startsWith(off.toUpperCase())) {
                        gone = remainingLimbList.get(i);
                        break;
                    }
                }
            if (gone.length() == 0) {
                mob.tell(L("'@x1' is not a valid body part.", off));
                final StringBuffer str = new StringBuffer(L("Parts include: "));
                for (int i = 0; i < remainingLimbList.size(); i++)
                    str.append((remainingLimbList.get(i)) + ", ");
                mob.tell(str.toString().substring(0, str.length() - 2) + ".");
                return false;
            }
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        // now see if it worked
        final boolean success = proficiencyCheck(mob, 0, auto);
        if ((success) && (gone.length() > 0)) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, (auto ? CMMsg.MASK_ALWAYS : 0) | CMMsg.MASK_MALICIOUS | CMMsg.MSG_NOISYMOVEMENT, L("^F^<FIGHT^><S-NAME> call(s) '@x1'!^</FIGHT^>^?", gone));
            CMLib.color().fixSourceFightColor(msg);
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                invoker = mob;
                beneficialAffect(mob, mob, asLevel, 2);
                final Ability injuryA = target.fetchEffect("Injury");
                if (injuryA != null)
                    injuryA.setMiscText(mob.Name() + "/" + gone);
                mob.recoverPhyStats();
            }
        } else
            return maliciousFizzle(mob, null, L("<S-NAME> call(s) '@x1', but fail(s) <S-HIS-HER> attack.", gone));

        // return whether it worked
        return success;
    }
}
