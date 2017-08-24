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
import com.planet_ink.coffee_mud.Abilities.interfaces.DiseaseAffect;
import com.planet_ink.coffee_mud.Abilities.interfaces.MendingSkill;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Libraries.interfaces.TrackingLibrary;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Prayer_MassCureDisease extends Prayer implements MendingSkill {
    private final static String localizedName = CMLib.lang().L("Mass Cure Disease");
    protected int abilityCode = 0;

    @Override
    public String ID() {
        return "Prayer_MassCureDisease";
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
    public void setAbilityCode(int newCode) {
        super.setAbilityCode(newCode);
        this.abilityCode = newCode;
    }

    @Override
    public boolean supportsMending(Physical item) {
        if (!(item instanceof MOB))
            return false;
        final boolean canMend = returnOffensiveAffects(item).size() > 0;
        return canMend;
    }

    public List<Ability> returnOffensiveAffects(Physical fromMe) {
        final Vector<Ability> offenders = new Vector<Ability>();

        for (int a = 0; a < fromMe.numEffects(); a++) // personal
        {
            final Ability A = fromMe.fetchEffect(a);
            if ((A != null) && (A instanceof DiseaseAffect))
                offenders.addElement(A);
        }
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
            final CMMsg msg = CMClass.getMsg(mob, mob.location(), this, verbalCastCode(mob, mob.location(), auto), auto ? L("A healing glow surrounds this place.") : L("^S<S-NAME> @x1 to cure disease here.^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                boolean worked = false;
                final TrackingLibrary.TrackingFlags flags = CMLib.tracking().newFlags();
                int range = 100 + super.getXLEVELLevel(mob) + (2 * super.getXMAXRANGELevel(mob));
                final List<Room> checkSet = CMLib.tracking().getRadiantRooms(mob.location(), flags, range);
                for (final Room room : checkSet) {
                    final Room R = CMLib.map().getRoom(room);
                    for (int m = 0; m < R.numInhabitants(); m++) {
                        final MOB target = R.fetchInhabitant(m);
                        if (target != null) {
                            final List<Ability> offensiveAffects = returnOffensiveAffects(target);
                            if (offensiveAffects.size() > 0) {
                                boolean badOnes = false;
                                for (int a = offensiveAffects.size() - 1; a >= 0; a--) {
                                    final Ability A = (offensiveAffects.get(a));
                                    if (A instanceof DiseaseAffect) {
                                        if ((A.invoker() != mob)
                                            && ((((DiseaseAffect) A).difficultyLevel() * 10) > adjustedLevel(mob, asLevel) + abilityCode))
                                            badOnes = true;
                                        else
                                            A.unInvoke();
                                    } else
                                        A.unInvoke();
                                    if (target.fetchEffect(A.ID()) == null)
                                        worked = true;
                                }
                                if (badOnes)
                                    mob.tell(mob, target, null, L("<T-NAME> had diseases too powerful for this magic."));
                                if (!CMLib.flags().isStillAffectedBy(target, offensiveAffects, false))
                                    target.tell(L("You feel much better!"));
                            }
                        }
                    }
                }
                if ((worked) && (!auto))
                    mob.tell(L("Your healing prayer has cured the sick."));
            }
        } else
            beneficialWordsFizzle(mob, mob.location(), auto ? "" : L("<S-NAME> @x1, but nothing happens.", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
