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
package com.syncleus.aethermud.game.Abilities.Prayers;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_Stasis extends Prayer {
    private final static String localizedName = CMLib.lang().L("Stasis");
    private final static String localizedStaticDisplay = CMLib.lang().L("(In stasis)");

    @Override
    public String ID() {
        return "Prayer_Stasis";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_OTHERS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_NEUTRALIZATION;
    }

    @Override
    public long flags() {
        return Ability.FLAG_NEUTRAL;
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
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        for (final int i : CharStats.CODES.SAVING_THROWS())
            affectableStats.setStat(i, affectableStats.getStat(i) + 100);
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
            mob.tell(L("The holy stasis has been lifted."));
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if ((msg.amITarget(mob)) && (msg.targetMinor() == CMMsg.TYP_DAMAGE))
            msg.setValue(0);
        else if ((msg.amITarget(mob)) && (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))) {
            msg.source().tell(msg.source(), mob, null, L("The statis field around <T-NAME> protect(s) <T-HIM-HER>."));
            return false;
        }

        return true;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> @x1 to place a stasis upon <T-NAMESELF>.^?", prayForWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    success = beneficialAffect(mob, target, asLevel, 10) != null;
                    mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> <S-IS-ARE> surrounded by a stasis field!"));
                }
            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> @x1 to place <T-NAMESELF> into stasis, but flub(s) it.", prayForWord(mob)));

        // return whether it worked
        return success;
    }
}
