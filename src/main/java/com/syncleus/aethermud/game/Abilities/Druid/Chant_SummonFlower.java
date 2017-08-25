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
import com.planet_ink.game.Behaviors.interfaces.Behavior;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.interfaces.Environmental;


public class Chant_SummonFlower extends Chant_SummonPlants {
    private final static String localizedName = CMLib.lang().L("Summon Flower");
    protected boolean processing = false;

    @Override
    public String ID() {
        return "Chant_SummonFlower";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTGROWTH;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    protected int canAffectCode() {
        return CAN_ITEMS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if ((msg.amITarget(littlePlantsI))
            && (!processing)
            && ((msg.targetMinor() == CMMsg.TYP_GET) || (msg.targetMinor() == CMMsg.TYP_PUSH) || (msg.targetMinor() == CMMsg.TYP_PULL))) {
            processing = true;
            final Ability A = littlePlantsI.fetchEffect(ID());
            if (A != null) {
                CMLib.threads().deleteTick(A, -1);
                littlePlantsI.delEffect(A);
                littlePlantsI.setSecretIdentity("");
            }
            if (littlePlantsI.fetchBehavior("Decay") == null) {
                final Behavior B = CMClass.getBehavior("Decay");
                B.setParms("min=" + CMProps.getIntVar(CMProps.Int.TICKSPERMUDMONTH) + " max=" + CMProps.getIntVar(CMProps.Int.TICKSPERMUDMONTH) + " chance=100");
                littlePlantsI.addBehavior(B);
                B.executeMsg(myHost, msg);
            }
            processing = false;
        }
    }

    public Item buildFlower(MOB mob, Room room) {
        final Item newItem = CMClass.getItem("GenItem");
        newItem.setMaterial(RawMaterial.RESOURCE_GREENS);
        switch (CMLib.dice().roll(1, 5, 0)) {
            case 1:
                newItem.setName(L("a red rose"));
                newItem.setDisplayText(L("a red rose is growing here."));
                newItem.setDescription("");
                break;
            case 2:
                newItem.setName(L("a nice daisy"));
                newItem.setDisplayText(L("a nice daisy is growing here."));
                newItem.setDescription("");
                break;
            case 3:
                newItem.setName(L("a white carnation"));
                newItem.setDisplayText(L("a beautiful white carnation is growing here"));
                newItem.setDescription("");
                break;
            case 4:
                newItem.setName(L("a happy sunflower"));
                newItem.setDisplayText(L("a happy sunflower is growing here."));
                newItem.setDescription(L("Happy flowers have little yellow blooms."));
                break;
            case 5:
                newItem.setName(L("a lovely gladiola"));
                newItem.setDisplayText(L("a lovely gladiola is growing here."));
                newItem.setDescription("");
                break;
        }
        newItem.setSecretIdentity(mob.Name());
        newItem.setMiscText(newItem.text());
        Druid_MyPlants.addNewPlant(mob, newItem);
        room.addItem(newItem);
        final Chant_SummonFlower newChant = new Chant_SummonFlower();
        newItem.basePhyStats().setLevel(10 + newChant.getX1Level(mob));
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
        return buildFlower(mob, room);
    }
}
