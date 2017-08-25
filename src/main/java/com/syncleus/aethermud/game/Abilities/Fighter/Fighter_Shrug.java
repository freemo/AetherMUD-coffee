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
package com.syncleus.aethermud.game.Abilities.Fighter;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Fighter_Shrug extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Shrug Off");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Braced for a hit)");
    private static final String[] triggerStrings = I(new String[]{"BRACE"});

    @Override
    public String ID() {
        return "Fighter_Shrug";
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
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_FITNESS;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((affected != null)
            && (affected instanceof MOB)
            && (msg.amITarget(affected))
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && (!msg.amISource((MOB) affected))
            && (CMLib.flags().isAliveAwakeMobile((MOB) affected, true))
            && (msg.tool() instanceof Weapon)) {
            final MOB mob = (MOB) affected;
            if (mob.location().show(mob, msg.source(), this, CMMsg.MSG_OK_ACTION, L("<S-NAME> shrug(s) off the attack from <T-NAME>."))) {
                unInvoke();
                return false;
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (!mob.isInCombat())
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if ((!auto) && (!mob.isInCombat())) {
            mob.tell(L("You must be in combat first!"));
            return false;
        }

        MOB target = mob;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        // now see if it worked
        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_QUIETMOVEMENT, auto ? L("<T-NAME> is braced for an attack!") : L("<S-NAME> brace(s) for an attack!"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            return beneficialVisualFizzle(mob, null, L("<S-NAME> attempt(s) to brace <S-HIM-HERSELF>, but get(s) distracted."));

        // return whether it worked
        return success;
    }
}
