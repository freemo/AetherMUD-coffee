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
import com.planet_ink.game.Items.interfaces.Food;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.ItemPossessor;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Chant_SummonHerb extends Chant {
    private final static String localizedName = CMLib.lang().L("Summon Herbs");

    @Override
    public String ID() {
        return "Chant_SummonHerb";
    }

    @Override
    public String name() {
        return localizedName;
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
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTGROWTH;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {

        if (((mob.location().domainType() & Room.INDOORS) > 0) && (!auto)) {
            mob.tell(L("You must be outdoors to try this."));
            return false;
        }
        if ((mob.location().domainType() == Room.DOMAIN_OUTDOORS_CITY)
            || (mob.location().domainType() == Room.DOMAIN_OUTDOORS_SPACEPORT)
            || (mob.location().domainType() == Room.DOMAIN_OUTDOORS_AIR)
            || (CMLib.flags().isWateryRoom(mob.location()))) {
            mob.tell(L("This magic will not work here."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        // now see if it worked
        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> chant(s) to the ground.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                for (int i = 0; i < ((adjustedLevel(mob, asLevel) / 4) + 1); i++) {
                    final Food newItem = (Food) CMClass.getBasicItem("GenFoodResource");
                    newItem.setName(L("some herbs"));
                    newItem.setDisplayText(L("Some herbs are growing here."));
                    newItem.setDescription("");
                    newItem.setMaterial(RawMaterial.RESOURCE_HERBS);
                    newItem.setNourishment(1);
                    CMLib.materials().addEffectsToResource(newItem);
                    newItem.setMiscText(newItem.text());
                    mob.location().addItem(newItem, ItemPossessor.Expire.Resource);
                }
                mob.location().showHappens(CMMsg.MSG_OK_ACTION, L("Some herbs quickly begin to grow here."));
                mob.location().recoverPhyStats();
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> chant(s) to the ground, but nothing happens."));

        // return whether it worked
        return success;
    }
}
