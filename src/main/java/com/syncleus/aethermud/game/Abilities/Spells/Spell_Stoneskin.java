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
package com.planet_ink.game.Abilities.Spells;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Items.interfaces.Weapon;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Spell_Stoneskin extends Spell {

    private final static String localizedName = CMLib.lang().L("Stoneskin");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Stoneskin)");
    int HitsRemaining = 0;
    int oldHP = -1;

    @Override
    public String ID() {
        return "Spell_Stoneskin";
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
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_TRANSMUTATION;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setArmor(affectableStats.armor() - 1 - getXLEVELLevel(invoker()));
    }

    @Override
    public void unInvoke() {
        // undo the affects of this spell
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        super.unInvoke();

        if (canBeUninvoked())
            if ((mob.location() != null) && (!mob.amDead()))
                mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("<S-YOUPOSS> skin softens."));
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if (msg.amITarget(mob)
            && (msg.tool() != null)
            && (!mob.amDead())
            && (CMLib.dice().rollPercentage() > 75)
            && ((msg.targetMinor() == CMMsg.TYP_DAMAGE) && ((msg.value()) > 0) && (msg.tool() instanceof Weapon))) {
            msg.modify(msg.source(), msg.target(), msg.tool(), CMMsg.NO_EFFECT, null, CMMsg.NO_EFFECT, null, CMMsg.NO_EFFECT, null);
            msg.addTrailerMsg(CMClass.getMsg((MOB) msg.target(), msg.source(), CMMsg.MSG_OK_VISUAL, L("The stone skin around <S-NAME> absorbs the attack from <T-NAME>.")));
            if ((--HitsRemaining) <= 0)
                unInvoke();
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

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            invoker = mob;

            final CMMsg msg = CMClass.getMsg(mob, target, this, somanticCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> wave(s) <S-HIS-HER> hands around <T-NAMESELF>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                mob.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> watch(es) <S-HIS-HER> skin turn hard as stone!"));
                HitsRemaining = 3 + (adjustedLevel(mob, asLevel) / 5);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            return beneficialVisualFizzle(mob, target, L("<S-NAME> attempt(s) to invoke a spell, but fail(s) miserably."));

        // return whether it worked
        return success;
    }
}
