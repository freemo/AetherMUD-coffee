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
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Thief_Evesdrop extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Evesdrop");
    private static final String[] triggerStrings = I(new String[]{"EVESDROP"});
    public int code = 0;

    @Override
    public String ID() {
        return "Thief_Evesdrop";
    }

    @Override
    public String name() {
        return localizedName;
    }

    // can NOT have a display text since the ability instance
    // is shared between the invoker and the target
    @Override
    public String displayText() {
        return "";
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
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_ALERT;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_OTHERS;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT | USAGE_MANA;
    }

    @Override
    public int abilityCode() {
        return code;
    }

    @Override
    public void setAbilityCode(int newCode) {
        code = newCode;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        if ((msg.targetMinor() == CMMsg.TYP_TELL)
            && (msg.target() == affected)
            && (affected instanceof MOB)
            && (msg.source() != invoker())
            && (invoker() != null)
            && (invoker().location() == ((MOB) msg.target()).location())
            && (!CMLib.flags().canBeSeenBy(invoker(), (MOB) affected))
            && (CMLib.flags().isInTheGame(invoker(), true))
            && (CMLib.flags().canBeHeardSpeakingBy(affected, invoker()))) {
            final CMMsg msg2 = (CMMsg) msg.copyOf();
            String targMsg = msg.targetMessage();
            if (targMsg != null) {
                final int x = targMsg.toUpperCase().indexOf("TELL(S) YOU");
                if (x >= 0)
                    targMsg = targMsg.substring(0, x + 8) + affected.Name() + targMsg.substring(x + 11);
            }
            msg2.modify(msg.source(), invoker(), msg.tool(), msg.sourceCode(), msg.sourceMessage(), msg.targetCode(), targMsg, CMMsg.NO_EFFECT, null);
            invoker().executeMsg(invoker(), msg2);
        }
    }

    @Override
    public void unInvoke() {
        if (canBeUninvoked()) {
            if ((invoker != null) && (affected != null))
                invoker.tell(L("You are no longer evesdropping on @x1.", affected.name()));
        }
        super.unInvoke();
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (commands.size() < 1) {
            mob.tell(L("Evesdrop on whom?"));
            return false;
        }
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;
        if (target == mob) {
            mob.tell(L("You cannot evesdrop on yourself?!"));
            return false;
        }
        final Ability A = target.fetchEffect(ID());
        if (A != null) {
            if (A.invoker() == mob)
                A.unInvoke();
            else {
                mob.tell(mob, target, null, L("It is too crowded to evesdrop on <T-NAME>."));
                return false;
            }
        }
        if (mob.isInCombat()) {
            mob.tell(L("Not while you are fighting!"));
            return false;
        }
        if (CMLib.flags().canBeSeenBy(mob, target)) {
            mob.tell(L("@x1 is watching you too closely.", target.name(mob)));
            return false;
        }
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final int levelDiff = target.phyStats().level() - (mob.phyStats().level() + abilityCode() + (getXLEVELLevel(mob) * 2));

        final boolean success = proficiencyCheck(mob, -(levelDiff * 10), auto);

        if (!success) {
            final CMMsg msg = CMClass.getMsg(mob, target, null, CMMsg.MSG_OK_VISUAL, auto ? "" : L("Your attempt to evesdrop on <T-NAMESELF> fails; <T-NAME> spots you!"), CMMsg.MSG_OK_VISUAL, auto ? "" : L("You spot <S-NAME> trying to evesdrop on you."), CMMsg.NO_EFFECT, null);
            if (mob.location().okMessage(mob, msg))
                mob.location().send(mob, msg);
        } else {
            final CMMsg msg = CMClass.getMsg(mob, target, this, auto ? CMMsg.MSG_OK_VISUAL : CMMsg.MSG_THIEF_ACT, L("You are now evesdroping on <T-NAME>.  Enter 'evesdrop <targetname>' again to disengage."), CMMsg.NO_EFFECT, null, CMMsg.NO_EFFECT, null);
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, adjustedLevel(mob, 0));
            }
        }
        return success;
    }
}
