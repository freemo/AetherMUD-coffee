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
package com.planet_ink.coffee_mud.Abilities.Thief;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.HealthCondition;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Thief_Sap extends ThiefSkill implements HealthCondition {
    private final static String localizedName = CMLib.lang().L("Sap");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Knocked out)");
    private static final String[] triggerStrings = I(new String[]{"SAP"});

    @Override
    public String ID() {
        return "Thief_Sap";
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
        return CAN_MOBS;
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
    public String getHealthConditionDesc() {
        return "Unconscious";
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;

        // when this spell is on a MOBs Affected list,
        // it should consistantly prevent the mob
        // from trying to do ANYTHING except sleep
        if ((msg.amISource(mob)) && (!msg.sourceMajor(CMMsg.MASK_ALWAYS))) {
            if ((msg.sourceMajor(CMMsg.MASK_EYES))
                || (msg.sourceMajor(CMMsg.MASK_HANDS))
                || (msg.sourceMajor(CMMsg.MASK_MOUTH))
                || (msg.sourceMajor(CMMsg.MASK_MOVE))) {
                if (msg.sourceMessage() != null)
                    mob.tell(L("You are way too drowsy."));
                return false;
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        // when this spell is on a MOBs Affected list,
        // it should consistantly put the mob into
        // a sleeping state, so that nothing they do
        // can get them out of it.
        affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_SLEEPING);
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked()) {
            if ((mob.location() != null) && (!mob.amDead())) {
                mob.location().show(mob, null, CMMsg.MSG_OK_ACTION, L("<S-NAME> regain(s) consciousness."));
                CMLib.commands().postStand(mob, true);
            } else
                mob.tell(L("You regain consciousness."));
        }
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (mob.isInCombat())
                return Ability.QUALITY_INDIFFERENT;
            if (!(target instanceof MOB))
                return Ability.QUALITY_INDIFFERENT;
            if (CMLib.flags().canBeSeenBy(mob, (MOB) target))
                return Ability.QUALITY_INDIFFERENT;
            if (mob.baseWeight() < (((MOB) target).baseWeight() - 100))
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!auto) {
            if (mob.isInCombat()) {
                mob.tell(L("Not while you are fighting!"));
                return false;
            }

            if (CMLib.flags().canBeSeenBy(mob, target)) {
                mob.tell(L("@x1 is watching you way too closely.", target.name(mob)));
                return false;
            }

            if (mob.baseWeight() < (target.baseWeight() - 100)) {
                mob.tell(L("@x1 is too big to knock out!", target.name(mob)));
                return false;
            }
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        int levelDiff = target.phyStats().level() - (mob.phyStats().level() + (2 * getXLEVELLevel(mob)));
        if (levelDiff > 0)
            levelDiff = levelDiff * 3;
        else
            levelDiff = 0;
        // now see if it worked
        final boolean hit = (auto) || CMLib.combat().rollToHit(mob, target);
        boolean success = proficiencyCheck(mob, (-levelDiff) + (-((target.charStats().getStat(CharStats.STAT_STRENGTH) - mob.charStats().getStat(CharStats.STAT_STRENGTH)))), auto) && (hit);
        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_THIEF_ACT | CMMsg.MASK_SOUND | CMMsg.MSK_MALICIOUS_MOVE | (auto ? CMMsg.MASK_ALWAYS : 0), auto ? "" : L("^F^<FIGHT^><S-NAME> sneak(s) up behind <T-NAMESELF> and whack(s) <T-HIM-HER> on the head!^</FIGHT^>^?"));
            CMLib.color().fixSourceFightColor(msg);
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    target.location().show(target, null, CMMsg.MSG_OK_ACTION, L("<S-NAME> hit(s) the floor!"));
                    success = maliciousAffect(mob, target, asLevel, 3, -1) != null;
                    final Set<MOB> H = mob.getGroupMembers(new HashSet<MOB>());
                    MOB M = null;
                    mob.makePeace(true);
                    for (final Object element : H) {
                        M = (MOB) element;
                        if (M.getVictim() == target)
                            M.setVictim(null);
                    }
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> sneak(s) up and attempt(s) to knock <T-NAMESELF> out, but fail(s)."));

        // return whether it worked
        return success;
    }
}
