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
package com.planet_ink.game.Abilities.Prayers;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Abilities.interfaces.MendingSkill;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Prayer_RestoreVoice extends Prayer implements MendingSkill {
    private final static String localizedName = CMLib.lang().L("Restore Voice");

    @Override
    public String ID() {
        return "Prayer_RestoreVoice";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_RESTORATION;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_OTHERS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_HOLY;
    }

    @Override
    public boolean supportsMending(Physical item) {
        if (!(item instanceof MOB))
            return false;
        final MOB caster = CMClass.getFactoryMOB();
        caster.basePhyStats().setLevel(CMProps.getIntVar(CMProps.Int.LASTPLAYERLEVEL));
        caster.phyStats().setLevel(CMProps.getIntVar(CMProps.Int.LASTPLAYERLEVEL));
        final boolean canMend = returnOffensiveAffects(caster, item).size() > 0;
        caster.destroy();
        return canMend;
    }

    public List<Ability> returnOffensiveAffects(MOB caster, Physical fromMe) {
        final MOB newMOB = CMClass.getFactoryMOB();
        final Vector<Ability> offenders = new Vector<Ability>(1);

        for (int a = 0; a < fromMe.numEffects(); a++) // personal
        {
            final Ability A = fromMe.fetchEffect(a);
            if (A != null) {
                newMOB.recoverPhyStats();
                A.affectPhyStats(newMOB, newMOB.phyStats());
                if ((!CMLib.flags().canSpeak(newMOB))
                    && ((A.invoker() == null)
                    || ((A.invoker() != null)
                    && (A.invoker().phyStats().level() <= (caster.phyStats().level() + 10 + (2 * getXLEVELLevel(caster)))))))
                    offenders.addElement(A);
            }
        }
        newMOB.destroy();
        return offenders;
    }

    @Override
    public int castingQuality(MOB mob, Physical target) {
        if (mob != null) {
            if (target instanceof MOB) {
                if (supportsMending(target))
                    return super.castingQuality(mob, target, Ability.QUALITY_BENEFICIAL_OTHERS);
            }
        }
        return super.castingQuality(mob, target);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        final List<Ability> offensiveAffects = returnOffensiveAffects(mob, target);

        if ((success) && (offensiveAffects.size() > 0)) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("A visible glow surrounds <T-NAME>.") : L("^S<S-NAME> @x1 for <T-NAMESELF> to speak.^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                for (int a = offensiveAffects.size() - 1; a >= 0; a--)
                    offensiveAffects.get(a).unInvoke();
            }
        } else
            beneficialWordsFizzle(mob, target, auto ? "" : L("<S-NAME> @x1 for <T-NAMESELF>, but nothing happens.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
