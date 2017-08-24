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
package com.planet_ink.coffee_mud.Abilities.Songs;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.LimbDamage;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.Races.interfaces.Race;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.collections.XVector;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

import java.util.List;
import java.util.Set;


public class Skill_BreakALeg extends BardSkill {
    private final static String localizedName = CMLib.lang().L("Break A Leg");
    private static final String[] triggerStrings = I(new String[]{"BREAKALEG"});

    @Override
    public String ID() {
        return "Skill_BreakALeg";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return L("(Feeling a leg pain)");
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
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_THEATRE;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT | USAGE_MANA;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (affected instanceof MOB) {
            final MOB mob = (MOB) affected;

            if (mob.charStats().getBodyPart(Race.BODY_LEG) <= 0) {
                unInvoke();
                return false;
            } else if (CMLib.dice().rollPercentage() <= (5 + this.getXLEVELLevel(invoker()))) {
                unInvoke();
                LimbDamage dA = (LimbDamage) CMClass.getAbility("BrokenLimbs");
                if (dA != null)
                    dA.invoke(invoker(), new XVector<String>("LEG"), mob, true, -1);
                return false;
            }
        }

        return true;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if ((mob != null) && (target != null)) {
            if (target.fetchEffect(ID()) != null)
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final Set<MOB> h = properTargets(mob, givenTarget, auto);
        if (h == null) {
            mob.tell(L("There doesn't appear to be anyone here worth wishing luck to."));
            return false;
        }

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, CMMsg.MASK_MAGIC | CMMsg.MSK_MALICIOUS_MOVE | CMMsg.TYP_JUSTICE | (auto ? CMMsg.MASK_ALWAYS : 0),
                auto ? "" : L("<S-NAME> wish(es) everyone luck, saying 'Break a Leg!'."));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                for (final Object element : h) {
                    final MOB target = (MOB) element;
                    if (CMLib.flags().canBeHeardSpeakingBy(mob, target) && (target.charStats().getBodyPart(Race.BODY_LEG) > 0)) {
                        final CMMsg msg2 = CMClass.getMsg(mob, target, this, CMMsg.MASK_MAGIC | CMMsg.MSK_MALICIOUS_MOVE | CMMsg.TYP_MIND | (auto ? CMMsg.MASK_ALWAYS : 0), null);
                        if (mob.location().okMessage(mob, msg2)) {
                            mob.location().send(mob, msg2);
                            if ((msg.value() <= 0) && (msg2.value() <= 0))
                                maliciousAffect(mob, target, asLevel, 20 + this.getXTIMELevel(mob), -1);
                        }
                    }
                }
            }
            setTimeOfNextCast(mob);
        } else
            return maliciousFizzle(mob, null, L("<S-NAME> attempt(s) to wish everyone luck, but fail(s)."));
        return success;
    }
}
