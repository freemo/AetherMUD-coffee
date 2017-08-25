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
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Chant_CheetahBurst extends Chant {
    private final static String localizedName = CMLib.lang().L("Cheetah Burst");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Cheetah Burst)");
    protected int cheetahTick = 3;

    public Chant_CheetahBurst() {
        super();
        cheetahTick = 3;
    }

    @Override
    public String ID() {
        return "Chant_CheetahBurst";
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
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (cheetahTick == 1)
            affectableStats.setSpeed(affectableStats.speed() + 3.0 + CMath.mul(0.1, getXLEVELLevel(invoker())));
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        super.unInvoke();

        if (canBeUninvoked())
            mob.tell(L("You begin to slow down to a normal speed."));
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (!(affected instanceof MOB))
            return true;
        final MOB mob = (MOB) affected;
        if ((--cheetahTick) == 0) {
            mob.recoverPhyStats();
            cheetahTick = 3;
        } else if (cheetahTick == 1)
            mob.recoverPhyStats();
        mob.curState().adjMovement(mob.charStats().getStat(CharStats.STAT_STRENGTH) / 5, mob.maxState());
        return true;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        MOB target = mob;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;
        if (target == null)
            return false;
        if (target.fetchEffect(ID()) != null) {
            mob.tell(target, null, null, L("<S-NAME> <S-IS-ARE> already at a cheetah's speed."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> chant(s) and snarl(s)!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (target.location() == mob.location()) {
                    target.location().show(target, null, CMMsg.MSG_OK_ACTION, L("<S-NAME> gain(s) cheetah-like reflexes!"));
                    beneficialAffect(mob, target, asLevel, 0);
                    final Chant_CheetahBurst A = (Chant_CheetahBurst) target.fetchEffect(ID());
                    if (A != null)
                        A.cheetahTick = 3;
                }
            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> chant(s) and snarl(s), but nothing more happens."));

        // return whether it worked
        return success;
    }
}
