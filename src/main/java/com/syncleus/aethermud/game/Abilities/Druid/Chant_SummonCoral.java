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
import com.syncleus.aethermud.game.Libraries.interfaces.WorldMap;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Drink;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Chant_SummonCoral extends Chant_SummonPlants {
    private final static String localizedName = CMLib.lang().L("Summon Coral");
    private final String[] coralTypes = new String[]
        {
            "staghorn coral",
            "coral",
            "pillar coral",
            "elkhorn coral",
            "sea fan",
            "antipathes",
            "leptopsammia",
            "brain coral",
            "cup coral",
            "soft brush coral",
            "wire coral",
            "bottle brush coral",
            "lace coral",
            "blade coral",
            "box fire coral",
            "fleshy coral",
            "plate coral",
            "sheet coral"
        };

    @Override
    public String ID() {
        return "Chant_SummonCoral";
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

    public Item buildCoral(MOB mob, Room room) {

        final Item newItem = CMClass.getItem("GenItem");
        newItem.setMaterial(RawMaterial.RESOURCE_SEAWEED);
        String name = coralTypes[CMLib.dice().roll(1, coralTypes.length, -1)];
        newItem.setName(L(name));
        newItem.setDisplayText(L(CMLib.english().startWithAorAn(name) + " @x1 is here.", name));
        newItem.setDescription("");

        final Chant_SummonCoral newChant = new Chant_SummonCoral();
        newItem.basePhyStats().setLevel(10 + newChant.getX1Level(mob));
        newItem.basePhyStats().setWeight(1);
        newItem.setSecretIdentity(mob.Name());
        newItem.setMiscText(newItem.text());
        room.addItem(newItem);
        Druid_MyPlants.addNewPlant(mob, newItem);
        newItem.setExpirationDate(0);
        room.showHappens(CMMsg.MSG_OK_ACTION, CMLib.lang().L("Suddenly, @x1 appear(s) here.", newItem.name()));
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
        return buildCoral(mob, room);
    }

    public boolean isSaltWaterRoom(Room R) {
        if ((R == null)
            || (!CMLib.flags().isUnderWateryRoom(R))
            || (!(R instanceof Drink))
            || (((Drink) R).liquidType() != RawMaterial.RESOURCE_SALTWATER))
            return false;
        return true;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        Room R = CMLib.map().roomLocation(affected);
        if (isSaltWaterRoom(R) && (R.expirationDate() != 0)) {
            R.setExpirationDate(System.currentTimeMillis() + WorldMap.ROOM_EXPIRATION_MILLIS);
        }
        return true;
    }

    @Override
    public boolean rightPlace(MOB mob, boolean auto) {
        final Room R = mob.location();
        if (!isSaltWaterRoom(R)) {
            mob.tell(L("This magic will not work here."));
            return false;
        }
        return true;
    }

}
