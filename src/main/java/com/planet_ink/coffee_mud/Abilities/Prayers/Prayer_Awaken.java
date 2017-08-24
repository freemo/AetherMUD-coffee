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
package com.planet_ink.coffee_mud.Abilities.Prayers;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.MendingSkill;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMProps;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Prayer_Awaken extends Prayer implements MendingSkill {
    private final static String localizedName = CMLib.lang().L("Awaken");

    @Override
    public String ID() {
        return "Prayer_Awaken";
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
        return Ability.FLAG_NEUTRAL;
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
        final List<Ability> offenders = new Vector<Ability>(1);

        for (int a = 0; a < fromMe.numEffects(); a++) // personal
        {
            final Ability A = fromMe.fetchEffect(a);
            if ((A != null)
                && (A.canBeUninvoked())
                && ((A.invoker() == null)
                || (A.invoker().phyStats().level() <= (caster.phyStats().level() + 1 + (2 * getXLEVELLevel(caster)))))) {
                try {
                    newMOB.recoverPhyStats();
                    A.affectPhyStats(newMOB, newMOB.phyStats());
                    if (CMLib.flags().isSleeping(newMOB))
                        offenders.add(A);
                } catch (final Exception e) {
                }
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
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? L("A feeling of wakefulness flows through the air") : L("^S<S-NAME> @x1 for wakefulness, and the area begins to fill with divine glory.^?", prayWord(mob)));
            final Room room = mob.location();
            if ((room != null) && (room.okMessage(mob, msg))) {
                room.send(mob, msg);
                for (int i = 0; i < room.numInhabitants(); i++) {
                    final MOB target = room.fetchInhabitant(i);
                    if ((target == null) || (!CMLib.flags().isSleeping(target)))
                        continue;

                    final List<Ability> offensiveAffects = returnOffensiveAffects(mob, target);

                    if (offensiveAffects.size() > 0) {
                        for (int a = offensiveAffects.size() - 1; a >= 0; a--)
                            offensiveAffects.get(a).unInvoke();
                        if ((!CMLib.flags().isStillAffectedBy(target, offensiveAffects, false)) && (target.location() != null))
                            target.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> seem(s) more awake."));
                    }
                    CMLib.commands().postStand(target, true);
                }
            }
        } else
            this.beneficialWordsFizzle(mob, null, L("<S-NAME> @x1 for wakefulness, but nothing happens.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
