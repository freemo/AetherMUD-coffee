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
package com.syncleus.aethermud.game.Abilities.Songs;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;
import java.util.Set;


public class Skill_Upstage extends BardSkill {
    private final static String localizedName = CMLib.lang().L("Upstage");
    private static final String[] triggerStrings = I(new String[]{"UPSTAGE"});
    protected int previousRange = 0;
    protected MOB previousVictim = null;

    @Override
    public String ID() {
        return "Skill_Upstage";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return L("(Upstaging " + (invoker() == null ? "an actor" : invoker().name()) + ")");
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
    public long flags() {
        return Ability.FLAG_MOVING;
    }

    @Override
    public void executeMsg(Environmental myHost, CMMsg msg) {
        super.executeMsg(myHost, msg);
        if ((invoker() != null)
            && (msg.source() == affected)
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && (msg.target() == invoker())
            && (affected instanceof MOB)) {
            unInvoke();
        }
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (affected instanceof MOB) {
            final MOB M = (MOB) affected;
            if ((invoker() == null)
                || (M.location() != invoker().location())
                || (!CMLib.flags().isInTheGame(invoker(), true))) {
                unInvoke();
                return false;
            }
        }
        return true;
    }

    public void unInvoke() {
        final Physical affected = this.affected;
        super.unInvoke();
        if (affected instanceof MOB) {
            MOB M = (MOB) affected;
            M.setVictim(previousVictim);
            M.setRangeToTarget(previousRange);
            ((MOB) affected).tell(L("You are no longer upstaged."));
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final Set<MOB> h = this.properTargets(mob, givenTarget, auto);
        if (h.size() == 1) {
            mob.tell(L("There doesn't appear to be anyone to upstage."));
            return false;
        }

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, CMMsg.MASK_MAGIC | CMMsg.MSK_MALICIOUS_MOVE | CMMsg.TYP_JUSTICE | (auto ? CMMsg.MASK_ALWAYS : 0),
                auto ? "" : L("<S-NAME> begin(s) upstaging everyone."));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                for (final MOB target : h) {
                    final CMMsg msg2 = CMClass.getMsg(mob, target, this, CMMsg.MASK_MAGIC | CMMsg.MASK_MALICIOUS | CMMsg.MSG_NOISYMOVEMENT | (auto ? CMMsg.MASK_ALWAYS : 0), null);
                    if (mob.location().okMessage(mob, msg2)) {
                        mob.location().send(mob, msg2);
                        if (msg2.value() <= 0) {
                            if (target.isInCombat()) {
                                Skill_Upstage A = (Skill_Upstage) maliciousAffect(mob, target, asLevel, 0, CMMsg.TYP_MIND);
                                if (A != null) {
                                    A.previousRange = target.rangeToTarget();
                                    A.previousVictim = target.getVictim();
                                    target.setRangeToTarget(CMLib.dice().roll(1, mob.location().maxRange(), 0) + (super.getXLEVELLevel(mob) / 3));
                                    target.setVictim(mob);
                                }
                            }
                        }
                    }
                }
            }
            setTimeOfNextCast(mob);
        } else
            return maliciousFizzle(mob, null, L("<S-NAME> attempt(s) to upstage everyone, but fail(s)."));
        return success;
    }
}