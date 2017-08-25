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
package com.syncleus.aethermud.game.Abilities.Druid;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Chant_SummonWater extends Chant {
    private final static String localizedName = CMLib.lang().L("Summon Water");
    protected Room SpringLocation = null;
    protected Item littleSpring = null;

    @Override
    public String ID() {
        return "Chant_SummonWater";
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
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_WEATHER_MASTERY;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ITEMS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public void unInvoke() {
        if (SpringLocation == null)
            return;
        if (littleSpring == null)
            return;
        if (canBeUninvoked())
            SpringLocation.showHappens(CMMsg.MSG_OK_VISUAL, L("The little spring dries up."));
        super.unInvoke();
        if (canBeUninvoked()) {
            final Item spring = littleSpring; // protects against uninvoke loops!
            littleSpring = null;
            spring.destroy();
            SpringLocation.recoverRoomStats();
            SpringLocation = null;
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (((mob.location().domainType() & Room.INDOORS) > 0) && (!auto)) {
            mob.tell(L("You must be outdoors for this chant to work."));
            return false;
        }
        if ((mob.location().domainType() == Room.DOMAIN_OUTDOORS_CITY)
            || (mob.location().domainType() == Room.DOMAIN_OUTDOORS_SPACEPORT)
            || (CMLib.flags().isWateryRoom(mob.location()))
            || (mob.location().domainType() == Room.DOMAIN_OUTDOORS_AIR)) {
            mob.tell(L("This magic will not work here."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        // now see if it worked
        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> chant(s) for water.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final String itemID = "Spring";

                final Item newItem = CMClass.getItem(itemID);

                if (newItem == null) {
                    mob.tell(L("There's no such thing as a '@x1'.\n\r", itemID));
                    return false;
                }

                mob.location().addItem(newItem);
                mob.location().showHappens(CMMsg.MSG_OK_ACTION, L("Suddenly, @x1 starts flowing here.", newItem.name()));
                SpringLocation = mob.location();
                littleSpring = newItem;
                beneficialAffect(mob, newItem, asLevel, 0);
                mob.location().recoverPhyStats();
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> chant(s) for water, but nothing happens."));

        // return whether it worked
        return success;
    }
}
