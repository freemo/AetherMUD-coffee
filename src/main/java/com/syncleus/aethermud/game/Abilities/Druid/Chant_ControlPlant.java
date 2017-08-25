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
package com.planet_ink.game.Abilities.Druid;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.Enumeration;
import java.util.List;


public class Chant_ControlPlant extends Chant {
    private final static String localizedName = CMLib.lang().L("Control Plant");

    public static Ability isPlant(Item I) {
        if ((I != null) && (I.rawSecretIdentity().length() > 0)) {
            for (final Enumeration<Ability> a = I.effects(); a.hasMoreElements(); ) {
                final Ability A = a.nextElement();
                if ((A != null)
                    && (A.invoker() != null)
                    && (A instanceof Chant_SummonPlants))
                    return A;
            }
        }
        return null;
    }

    @Override
    public String ID() {
        return "Chant_ControlPlant";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTCONTROL;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Item myPlant = getTarget(mob, mob.location(), givenTarget, commands, Wearable.FILTER_ANY);
        if (myPlant == null)
            return false;

        if (isPlant(myPlant) == null) {
            mob.tell(L("You can't control @x1.", myPlant.name()));
            return false;
        }

        String oldController = myPlant.rawSecretIdentity();
        final Ability A = isPlant(myPlant);
        if ((oldController.length() == 0) || (!CMLib.players().playerExists(oldController))) {
            if ((A.text().length() > 0) && (CMLib.players().playerExists(A.text())))
                oldController = A.text();
        }

        final MOB oldM = (oldController.length() > 0) ? CMLib.players().getLoadPlayer(oldController) : null;

        if (oldController.equals(mob.Name())) {
            mob.tell(L("You already control @x1.", myPlant.name()));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, myPlant, this, verbalCastCode(mob, myPlant, auto), auto ? "" : L("^S<S-NAME> chant(s) to <T-NAMESELF>!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (A != null) {
                    A.setInvoker(mob);
                    A.setMiscText(mob.Name());
                }
                mob.tell(L("You wrest control of @x1 from @x2.", myPlant.name(), myPlant.secretIdentity()));
                myPlant.setSecretIdentity(mob.Name());
                Druid_MyPlants.addNewPlant(mob, myPlant);
                if (oldM != null)
                    Druid_MyPlants.removeLostPlant(oldM, myPlant);
            }

        } else
            beneficialVisualFizzle(mob, myPlant, L("<S-NAME> chant(s) to <T-NAMESELF>, but nothing happens."));

        // return whether it worked
        return success;
    }
}
