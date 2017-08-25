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
package com.syncleus.aethermud.game.Abilities.Misc;

import com.syncleus.aethermud.game.Abilities.StdAbility;
import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Abilities.interfaces.HealthCondition;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class WingFlying extends StdAbility implements HealthCondition {
    private final static String localizedName = CMLib.lang().L("Winged Flight");
    private static final String[] triggerStrings = I(new String[]{"FLAP"});
    private volatile boolean isFlying = true;

    @Override
    public String ID() {
        return "WingFlying";
    }

    @Override
    public String name() {
        return localizedName;
    }

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
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public boolean putInCommandlist() {
        return false;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_RACIALABILITY;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT;
    }

    @Override
    public String getHealthConditionDesc() {
        return "Weak Paralysis";
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (affected == null)
            return;
        if (!(affected instanceof MOB))
            return;

        if ((!CMLib.flags().isSleeping(affected))
            && (!CMLib.flags().isSitting(affected))
            && (isFlying))
            affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_FLYING);
        else
            affectableStats.setDisposition(CMath.unsetb(affectableStats.disposition(), PhyStats.IS_FLYING));
    }

    @Override
    public void setMiscText(String newText) {
        super.setMiscText(newText);
        if (newText.length() == 0)
            isFlying = true;
        else
            isFlying = CMParms.getParmBool(newText, "FLYING", true);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((tickID == Tickable.TICKID_MOB) && (ticking instanceof MOB) && (((MOB) ticking).charStats().getBodyPart(Race.BODY_WING) <= 0))
            unInvoke();
        return super.tick(ticking, tickID);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = mob;
        if (target == null)
            return false;
        if (target.charStats().getBodyPart(Race.BODY_WING) <= 0) {
            mob.tell(L("You can't flap without wings."));
            return false;
        }

        final boolean wasFlying = CMLib.flags().isFlying(target);
        Ability A = target.fetchEffect(ID());
        if (A != null)
            A.unInvoke();
        target.recoverPhyStats();
        String str = "";
        if (wasFlying)
            str = L("<S-NAME> stop(s) flapping <S-HIS-HER> wings.");
        else
            str = L("<S-NAME> start(s) flapping <S-HIS-HER> wings.");

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_NOISYMOVEMENT, str);
            if (target.location().okMessage(target, msg)) {
                target.location().send(target, msg);
                beneficialAffect(mob, target, asLevel, 9999);
                A = target.fetchEffect(ID());
                if (A != null) {
                    A.makeLongLasting();
                    A.setMiscText("FLYING=" + (!wasFlying));
                }
                target.location().recoverRoomStats();
            }
        } else
            return beneficialVisualFizzle(mob, target, L("<T-NAME> fumble(s) trying to use <T-HIS-HER> wings."));

        // return whether it worked
        return success;
    }
}
