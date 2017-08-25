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
package com.syncleus.aethermud.game.Abilities.Druid;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Chant_Rockfeet extends Chant {
    private final static String localizedName = CMLib.lang().L("Rockfeet");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Rockfeet)");

    @Override
    public String ID() {
        return "Chant_Rockfeet";
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
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_SHAPE_SHIFTING;
    }

    @Override
    public int maxRange() {
        return adjustedMaxInvokerRange(10);
    }

    @Override
    public int minRange() {
        return 0;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    public boolean bubbleAffect() {
        return true;
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
    public void unInvoke() {
        MOB M = null;
        if (affected instanceof MOB)
            M = (MOB) affected;
        super.unInvoke();
        if ((canBeUninvoked()) && (M != null) && (!M.amDead()))
            M.tell(L("Your hands and feet don't seem so heavy any more."));
    }

    @Override
    public void executeMsg(Environmental host, CMMsg msg) {
        super.executeMsg(host, msg);
        if ((msg.source() == affected)
            && (CMath.bset(msg.sourceMajor(), CMMsg.MASK_HANDS)
            || CMath.bset(msg.sourceMajor(), CMMsg.MASK_MOVE))
            && (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))) {
            if (CMLib.dice().rollPercentage() > (msg.source().charStats().getStat(CharStats.STAT_STRENGTH) * 3)) {
                msg.source().curState().adjMovement(-1, msg.source().maxState());
                if (msg.source().maxState().getFatigue() > Long.MIN_VALUE / 2)
                    msg.source().curState().adjFatigue(CMProps.getTickMillis(), msg.source().maxState());
            }
        }
        return;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (target instanceof MOB) {
                if ((((MOB) target).getWearPositions(Wearable.WORN_HANDS) == 0)
                    && (((MOB) target).getWearPositions(Wearable.WORN_FEET) == 0))
                    return Ability.QUALITY_INDIFFERENT;
            }
            final Room R = mob.location();
            if (R != null) {
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if ((target.getWearPositions(Wearable.WORN_HANDS) == 0)
            && (target.getWearPositions(Wearable.WORN_FEET) == 0)) {
            if (!auto)
                mob.tell(L("@x1 doesn't have hands or feet to affect...", target.name(mob)));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {

            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> chant(s) at <T-NAME> heavily!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    maliciousAffect(mob, target, asLevel, 0, -1);
                    target.tell(L("Your hands and feet feel extremely heavy!"));
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> chant(s) at <T-NAME>, but the magic fizzles."));

        // return whether it worked
        return success;
    }
}
