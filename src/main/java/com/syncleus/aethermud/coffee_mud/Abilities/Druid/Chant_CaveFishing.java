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
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Drink;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;


public class Chant_CaveFishing extends Chant {
    private final static String localizedName = CMLib.lang().L("Cave Fishing");
    protected int previousResource = -1;

    @Override
    public String ID() {
        return "Chant_CaveFishing";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_ANIMALAFFINITY;
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
        return CAN_ROOMS;
    }

    @Override
    public void unInvoke() {
        if ((affected instanceof Room)
            && (this.canBeUninvoked())) {
            ((Room) affected).showHappens(CMMsg.MSG_OK_VISUAL, L("The fish start to disappear!"));
            ((Room) affected).setResource(previousResource);
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Room target = mob.location();
        if (target == null)
            return false;

        Environmental waterSrc = null;
        if ((target.domainType() == Room.DOMAIN_INDOORS_WATERSURFACE)
            || (target.domainType() == Room.DOMAIN_INDOORS_UNDERWATER))
            waterSrc = target;
        else if (target.domainType() == Room.DOMAIN_INDOORS_CAVE) {
            for (int i = 0; i < target.numItems(); i++) {
                final Item I = target.getItem(i);
                if ((I instanceof Drink)
                    && (I.container() == null)
                    && (((Drink) I).liquidType() == RawMaterial.RESOURCE_FRESHWATER)
                    && (!CMLib.flags().isGettable(I)))
                    waterSrc = I;
            }
            if (waterSrc == null) {
                mob.tell(L("There is no water source here to fish in."));
                return false;
            }
        } else {
            mob.tell(L("This chant cannot be used outdoors."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            invoker = mob;
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> chant(s) to <T-NAME>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    mob.location().showHappens(CMMsg.MSG_OK_VISUAL, L("Fish start swimming around in @x1!", target.name()));
                    beneficialAffect(mob, target, asLevel, 0);
                    final Chant_CaveFishing A = (Chant_CaveFishing) target.fetchEffect(ID());
                    if (A != null) {
                        mob.location().showHappens(CMMsg.MSG_OK_VISUAL, L("Fish start swimming around in @x1!", target.name()));
                        A.previousResource = target.myResource();
                        target.setResource(RawMaterial.CODES.FISHES()[CMLib.dice().roll(1, RawMaterial.CODES.FISHES().length, -1)]);
                    }
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> chant(s) to <T-NAME>, but the magic fades."));
        // return whether it worked
        return success;
    }
}
