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
package com.planet_ink.coffee_mud.Abilities.Druid;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Drink;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;


public class Chant_SensePoison extends Chant {
    private final static String localizedName = CMLib.lang().L("Sense Poison");

    @Override
    public String ID() {
        return "Chant_SensePoison";
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
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS | CAN_ITEMS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_PRESERVING;
    }

    public List<Ability> returnOffensiveAffects(Physical fromMe) {
        final Vector<Ability> offenders = new Vector<Ability>();

        for (final Enumeration<Ability> a = fromMe.effects(); a.hasMoreElements(); ) {
            final Ability A = a.nextElement();
            if ((A != null) && ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_POISON))
                offenders.addElement(A);
        }
        if (fromMe instanceof MOB) {
            final MOB mob = (MOB) fromMe;
            for (final Enumeration<Ability> a = mob.allAbilities(); a.hasMoreElements(); ) {
                final Ability A = a.nextElement();
                if ((A != null) && ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_POISON))
                    offenders.addElement(A);
            }
        }
        return offenders;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Physical target = getAnyTarget(mob, commands, givenTarget, Wearable.FILTER_UNWORNONLY);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        final List<Ability> offensiveAffects = returnOffensiveAffects(target);

        if ((success) && ((offensiveAffects.size() > 0)
            || ((target instanceof Drink) && (((Drink) target).liquidType() == RawMaterial.RESOURCE_POISON)))) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> chant(s) over <T-NAME>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final StringBuffer buf = new StringBuffer(L("@x1 contains: ", target.name()));
                if (offensiveAffects.size() == 0)
                    buf.append(L("weak impurities, "));
                else
                    for (int i = 0; i < offensiveAffects.size(); i++)
                        buf.append(offensiveAffects.get(i).name() + ", ");
                mob.tell(buf.toString().substring(0, buf.length() - 2));
            }
        } else
            beneficialWordsFizzle(mob, target, auto ? "" : L("<S-NAME> chant(s) over <T-NAME>, but receives no insight."));

        // return whether it worked
        return success;
    }
}
