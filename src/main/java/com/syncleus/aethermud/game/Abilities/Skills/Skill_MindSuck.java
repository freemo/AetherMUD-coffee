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
package com.planet_ink.game.Abilities.Skills;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.List;


public class Skill_MindSuck extends StdSkill {
    private final static String localizedName = CMLib.lang().L("Mind Suck");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Mind Sucking)");
    private static final String[] triggerStrings = I(new String[]{"MINDSUCK"});

    @Override
    public String ID() {
        return "Skill_MindSuck";
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
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_GRAPPLING;
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
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        /*
		if((affected!=null)
		&&((msg.source()==affected)||(msg.source()==invoker))
		&&((msg.targetMinor()==CMMsg.TYP_LEAVE)||(msg.targetMinor()==CMMsg.TYP_RECALL)))
		{
			unInvoke();
		}
		*/
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        return super.okMessage(myHost, msg);
    }

    public boolean suckableBrain(final MOB invoker, final MOB mob) {
        if ((invoker.isInCombat())
            || (mob.isInCombat())
            || ((CMLib.flags().isAliveAwakeMobileUnbound(mob, false))
            && (mob.amFollowing() != invoker))
            || (mob.location() != CMLib.map().roomLocation(invoker))
            || (mob.amDead())
            || (mob == invoker))
            return false;
        return true;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((affecting() == null) || (!(affecting() instanceof MOB)))
            return false;
        final MOB mob = (MOB) affecting();
        if (!suckableBrain(invoker, mob))
            unInvoke();
        else if (!mob.location().show(invoker, mob, CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> continue(s) sucking out <T-YOUPOSS> brain.")))
            return false;
        return super.tick(ticking, tickID);
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        final MOB invoker = this.invoker;
        boolean suckableBrain = suckableBrain(invoker, mob);

        super.unInvoke();

        if (canBeUninvoked() && (invoker != null)) {
            if (suckableBrain) {
                mob.location().show(invoker, mob, CMMsg.MSG_OK_VISUAL, L("<S-NAME> finish consuming the brain of <T-NAME>."));
                final boolean hungry = invoker.curState().getHunger() <= 0;
                CMLib.combat().postDeath(invoker, mob, null);
                final boolean full = !invoker.curState().adjHunger(CMProps.getIntVar(CMProps.Int.HUNGER_FULL), invoker.maxState().maxHunger(invoker.baseWeight()));
                if ((hungry) && (invoker.curState().getHunger() > 0))
                    invoker.tell(L("You are no longer hungry."));
                else if (full)
                    invoker.tell(L("You are full."));
            } else if ((!mob.amDead())
                && (mob.location() == invoker.location())
                && (CMLib.flags().canBeSeenBy(invoker, mob))) {
                if (invoker.location() != null)
                    invoker.location().show(invoker, mob, CMMsg.MSG_OK_VISUAL, L("<S-NAME> lose(s) <S-HIS-HER> grip on <T-NAME>!"));
                CMLib.combat().postAttack(mob, invoker, mob.fetchWieldedItem());
            } else if ((!mob.amDead()) && (invoker.location() != null))
                invoker.location().show(invoker, mob, CMMsg.MSG_OK_VISUAL, L("<S-NAME> lose(s) <S-HIS-HER> grip on <T-NAME>, who staggers away."));
        }
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_MOVE);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if ((mob != null) && (target != null)) {
            if (!(target instanceof MOB))
                return Ability.QUALITY_INDIFFERENT;
            if (!suckableBrain(mob, (MOB) target))
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (mob.isInCombat()) {
            mob.tell(L("Not while you are fighting!"));
            return false;
        } else if (!CMLib.flags().isAliveAwakeMobileUnbound(target, false)) {
            // OK!
        } else if (target.amFollowing() == mob) {
            // OK!
        } else {
            mob.tell(L("@x1 does not appear willing to have @x2 brain sucked.", target.name(mob), target.charStats().hisher()));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        int levelDiff = (mob.phyStats().level() - target.phyStats().level()) * 10;
        if (levelDiff > 0)
            levelDiff = 0;

        boolean success = proficiencyCheck(mob, levelDiff, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_NOISYMOVEMENT | CMMsg.MASK_MALICIOUS | (auto ? CMMsg.MASK_ALWAYS : 0),
                auto ? L("<T-YOUPOSS> brain start(s) draining away.") :
                    L("^S<S-NAME> attempt(s) to suck the brain out of <T-NAMESELF> for nourishment.^?"));
            if (mob.location().okMessage(mob, msg)) {
                msg.setSourceCode(msg.sourceCode() & (~CMMsg.MASK_MALICIOUS));
                msg.setTargetCode(msg.targetCode() & (~CMMsg.MASK_MALICIOUS));
                msg.setOthersCode(msg.othersCode() & (~CMMsg.MASK_MALICIOUS));
                mob.location().send(mob, msg);
                mob.makePeace(true);
                target.makePeace(true);
                if (msg.value() <= 0) {
                    beneficialAffect(mob, target, asLevel, 4);
                    mob.location().recoverPhyStats();
                }
                mob.makePeace(true);
                target.makePeace(true);
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> attempt(s) to suck <T-YOUPOSS> mind, but fail(s)."));

        // return whether it worked
        return success;
    }
}
