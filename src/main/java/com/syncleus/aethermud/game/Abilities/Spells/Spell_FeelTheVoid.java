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
package com.syncleus.aethermud.game.Abilities.Spells;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Spell_FeelTheVoid extends Spell {

    protected final static int mask =
        PhyStats.CAN_NOT_TASTE
            | PhyStats.CAN_NOT_SMELL
            | PhyStats.CAN_NOT_SEE
            | PhyStats.CAN_NOT_HEAR;
    protected final static int mask2 = Integer.MAX_VALUE
        - PhyStats.CAN_SEE_BONUS
        - PhyStats.CAN_SEE_DARK
        - PhyStats.CAN_SEE_EVIL
        - PhyStats.CAN_SEE_GOOD
        - PhyStats.CAN_SEE_HIDDEN
        - PhyStats.CAN_SEE_HIDDEN_ITEMS
        - PhyStats.CAN_SEE_INFRARED
        - PhyStats.CAN_SEE_INVISIBLE
        - PhyStats.CAN_SEE_METAL
        - PhyStats.CAN_SEE_SNEAKERS
        - PhyStats.CAN_SEE_VICTIM;
    private final static String localizedName = CMLib.lang().L("Feel The Void");
    private final static String localizedStaticDisplay = CMLib.lang().L("(In a Void)");

    @Override
    public String ID() {
        return "Spell_FeelTheVoid";
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
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_ILLUSION;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setSensesMask(mask & mask2);
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        super.unInvoke();

        if (canBeUninvoked())
            mob.tell(L("You are no longer in the void."));
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
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> invoke(s) the void at <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    if (target.location() == mob.location()) {
                        target.location().show(target, null, CMMsg.MSG_OK_ACTION, L("<S-NAME> stand(s) dazed and quiet!"));
                        success = maliciousAffect(mob, target, asLevel, 0, -1) != null;
                    }
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> invoke(s) at <T-NAMESELF>, but the spell fizzles."));

        // return whether it worked
        return success;
    }
}
