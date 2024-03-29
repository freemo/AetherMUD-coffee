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
package com.syncleus.aethermud.game.Abilities.Archon;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.Log;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Archon_Freeze extends ArchonSkill {
    private final static String localizedName = CMLib.lang().L("Freeze");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Freezed)");
    private static final String[] triggerStrings = I(new String[]{"FREEZE"});

    @Override
    public String ID() {
        return "Archon_Freeze";
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
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_ARCHON;
    }

    @Override
    public int maxRange() {
        return adjustedMaxInvokerRange(1);
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;

        // when this spell is on a MOBs Affected list,
        // it should consistantly prevent the mob
        // from trying to do ANYTHING except sleep
        if (msg.amISource(mob)) {
            switch (msg.sourceMinor()) {
                case CMMsg.TYP_ENTER:
                case CMMsg.TYP_ADVANCE:
                case CMMsg.TYP_LEAVE:
                case CMMsg.TYP_RECALL:
                case CMMsg.TYP_FLEE:
                    mob.tell(L("You are frozen, and cant go anywhere."));
                    return false;
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public void unInvoke() {
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;

        super.unInvoke();

        if (canBeUninvoked())
            mob.tell(L("You are no longer freezed!"));
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = getTargetAnywhere(mob, commands, givenTarget, false, true, false);
        if (target == null)
            return false;

        final Ability A = target.fetchEffect(ID());
        if (A != null) {
            A.unInvoke();
            mob.tell(L("@x1 is released from his freezedness.", target.Name()));
            return true;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MASK_MOVE | CMMsg.TYP_JUSTICE | (auto ? CMMsg.MASK_ALWAYS : 0), auto ? L("A frozen chill falls upon <T-NAME>!") : L("^F<S-NAME> freeze(s) <T-NAMESELF>.^?"));
            CMLib.color().fixSourceFightColor(msg);
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> <S-IS-ARE> frozen!"));
                beneficialAffect(mob, target, asLevel, Ability.TICKS_ALMOST_FOREVER);
                Log.sysOut("Freeze", mob.Name() + " freezed " + target.name() + ".");
            }
        } else
            return beneficialVisualFizzle(mob, target, L("<S-NAME> attempt(s) to freeze <T-NAMESELF>, but fail(s)."));
        return success;
    }
}
