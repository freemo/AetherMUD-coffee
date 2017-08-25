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
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;


public class Chant_SummonSeaweed extends Chant_SummonPlants {
    private final static String localizedName = CMLib.lang().L("Summon Seaweed");

    @Override
    public String ID() {
        return "Chant_SummonSeaweed";
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
        return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTGROWTH;
    }

    @Override
    protected int canAffectCode() {
        return CAN_ITEMS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    protected boolean seaOk() {
        return true;
    }

    public Item buildSeaweed(MOB mob, Room room) {
        final Item newItem = CMClass.getItem("GenItem");
        newItem.setMaterial(RawMaterial.RESOURCE_SEAWEED);
        switch (CMLib.dice().roll(1, 5, 0)) {
            case 1:
                newItem.setName(L("some algae"));
                newItem.setDisplayText(L("some algae is here."));
                newItem.setDescription("");
                break;
            case 2:
                newItem.setName(L("some seaweed"));
                newItem.setDisplayText(L("some seaweed is here."));
                newItem.setDescription("");
                break;
            case 3:
                newItem.setName(L("some kelp"));
                newItem.setDisplayText(L("some kelp is here"));
                newItem.setDescription("");
                break;
            case 4:
                newItem.setName(L("some coral"));
                newItem.setDisplayText(L("some coral is here."));
                newItem.setDescription("");
                break;
            case 5:
                newItem.setName(L("some sponge"));
                newItem.setDisplayText(L("some sponge is here."));
                newItem.setDescription("");
                break;
        }
        final Chant_SummonSeaweed newChant = new Chant_SummonSeaweed();
        newItem.basePhyStats().setLevel(10 + newChant.getX1Level(mob));
        newItem.basePhyStats().setWeight(1);
        newItem.setSecretIdentity(mob.Name());
        newItem.setMiscText(newItem.text());
        room.addItem(newItem);
        Druid_MyPlants.addNewPlant(mob, newItem);
        newItem.setExpirationDate(0);
        room.showHappens(CMMsg.MSG_OK_ACTION, CMLib.lang().L("Suddenly, @x1 sprout(s) up here.", newItem.name()));
        newChant.plantsLocationR = room;
        newChant.littlePlantsI = newItem;
        if (CMLib.law().doesOwnThisLand(mob, room)) {
            newChant.setInvoker(mob);
            newChant.setMiscText(mob.Name());
            newItem.addNonUninvokableEffect(newChant);
        } else
            newChant.beneficialAffect(mob, newItem, 0, (newChant.adjustedLevel(mob, 0) * 240) + 450);
        room.recoverPhyStats();
        return newItem;
    }

    @Override
    protected Item buildMyPlant(MOB mob, Room room) {
        return buildSeaweed(mob, room);
    }

    @Override
    public boolean rightPlace(MOB mob, boolean auto) {
        if ((!auto) && (mob.location().domainType() & Room.INDOORS) > 0) {
            mob.tell(L("You must be outdoors for this chant to work."));
            return false;
        }

        if (!CMLib.flags().isWateryRoom(mob.location())) {
            mob.tell(L("This magic will not work here."));
            return false;
        }
        return true;
    }

}
