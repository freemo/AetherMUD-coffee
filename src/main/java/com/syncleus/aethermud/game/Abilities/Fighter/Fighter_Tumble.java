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
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Fighter_Tumble extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Tumble");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Tumbling)");
    private static final String[] triggerStrings = I(new String[]{"TUMBLE"});
    public int hits = 0;

    @Override
    public String ID() {
        return "Fighter_Tumble";
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
        return Ability.ACODE_SKILL | Ability.DOMAIN_ACROBATIC;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if ((invoker == null) && (affected instanceof MOB))
            invoker = (MOB) affected;
        if (invoker != null) {
            final float f = (float) CMath.mul(0.2, getXLEVELLevel(invoker));
            affectableStats.setDamage(affectableStats.damage() - (int) Math.round(CMath.div(affectableStats.damage(), 2.0 + f)));
            affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() - (int) Math.round(CMath.div(affectableStats.attackAdjustment(), 2.0 + f)));
        }
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if (!mob.isInCombat())
            this.unInvoke();
        else if ((msg.amITarget(mob))
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && ((msg.value()) > 0)) {
            if ((!mob.amDead())
                && (msg.tool() instanceof Weapon)) {
                msg.modify(msg.source(), msg.target(), msg.tool(), CMMsg.NO_EFFECT, null, CMMsg.NO_EFFECT, null, CMMsg.NO_EFFECT, null);
                if (!((MOB) msg.target()).amDead())
                    msg.addTrailerMsg(CMClass.getMsg((MOB) msg.target(), msg.source(), CMMsg.MSG_OK_VISUAL, L("<S-NAME> tumble(s) around the attack from <T-NAME>.")));
                if ((++hits) >= 2)
                    unInvoke();
            }
        }
        return true;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (mob.fetchEffect(this.ID()) != null)
                return Ability.QUALITY_INDIFFERENT;
            if (!mob.isInCombat())
                return Ability.QUALITY_INDIFFERENT;
            if (!CMLib.flags().isAliveAwakeMobile(mob, true))
                return Ability.QUALITY_INDIFFERENT;
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (mob.fetchEffect(this.ID()) != null) {
            mob.tell(L("You are already tumbling."));
            return false;
        }

        if ((!auto) && (!mob.isInCombat())) {
            mob.tell(L("You aren't in combat!"));
            return false;
        }
        if (!CMLib.flags().isAliveAwakeMobile(mob, true)) {
            mob.tell(L("You need to stand up!"));
            return false;
        }

        MOB target = mob;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_QUIETMOVEMENT, auto ? L("<T-NAME> begin(s) tumbling around!") : L("<S-NAME> tumble(s) around!"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                hits = 0;
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            beneficialVisualFizzle(mob, null, L("<S-NAME> attempt(s) to tumble, but goof(s) it up."));
        return success;
    }
}
