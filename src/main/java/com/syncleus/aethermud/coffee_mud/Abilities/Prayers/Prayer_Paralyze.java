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
package com.planet_ink.coffee_mud.Abilities.Prayers;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;


public class Prayer_Paralyze extends Prayer {
    private final static String localizedName = CMLib.lang().L("Paralyze");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Paralyzed)");

    @Override
    public String ID() {
        return "Prayer_Paralyze";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_CORRUPTION;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_UNHOLY | Ability.FLAG_PARALYZING;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (affected == null)
            return;
        if (!(affected instanceof MOB))
            return;
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_MOVE);
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked())
            mob.tell(L("The paralysis eases out of your muscles."));
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (target instanceof MOB) {
                if (!CMLib.flags().canMove(((MOB) target)))
                    return Ability.QUALITY_INDIFFERENT;
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        int levelDiff = target.phyStats().level() - (mob.phyStats().level() + (2 * getXLEVELLevel(mob)));
        if (levelDiff < 0)
            levelDiff = 0;
        if (levelDiff > 6)
            levelDiff = 6;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        boolean success = proficiencyCheck(mob, (super.getXLEVELLevel(mob)) - ((target.charStats().getStat(CharStats.STAT_WISDOM) * 2) + (levelDiff * 5)), auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> invoke(s) an unholy paralysis upon <T-NAMESELF>.^?"));
            final CMMsg msg2 = CMClass.getMsg(mob, target, this, CMMsg.MASK_MALICIOUS | CMMsg.TYP_PARALYZE | (auto ? CMMsg.MASK_ALWAYS : 0), null);
            if ((mob.location().okMessage(mob, msg)) && (mob.location().okMessage(mob, msg2))) {
                mob.location().send(mob, msg);
                mob.location().send(mob, msg2);
                if ((msg.value() <= 0) && (msg2.value() <= 0)) {
                    // intentionally lasts longer than hold or mass paralyze, becasue its harder to succeed
                    int duration = 10 - levelDiff;
                    if (duration < 4)
                        duration = 4;
                    success = maliciousAffect(mob, target, asLevel, duration, -1) != null;
                    mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> can't move!"));
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> attempt(s) to paralyze <T-NAMESELF>, but flub(s) it."));

        // return whether it worked
        return success;
    }
}
