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
package com.syncleus.aethermud.game.Locales;

import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.interfaces.Drink;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;

public class IndoorUnderWater extends StdRoom implements Drink {
    protected int liquidType = RawMaterial.RESOURCE_FRESHWATER;

    public IndoorUnderWater() {
        super();
        basePhyStats.setWeight(3);
        name = "the water";
        basePhyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_SWIMMING);
        recoverPhyStats();
        atmosphere = RawMaterial.RESOURCE_FRESHWATER;
    }

    @Override
    public String ID() {
        return "IndoorUnderWater";
    }

    @Override
    public int maxRange() {
        return 5;
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_INDOORS_UNDERWATER;
    }

    @Override
    protected int baseThirst() {
        return 0;
    }

    @Override
    public long decayTime() {
        return 0;
    }

    @Override
    public void setDecayTime(long time) {
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (affected instanceof MOB)
            affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_SWIMMING);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        switch (UnderWater.isOkUnderWaterAffect(this, msg)) {
            case -1:
                return false;
            case 1:
                return true;
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        UnderWater.sinkAffects(this, msg);
    }

    @Override
    public boolean disappearsAfterDrinking() {
        return false;
    }

    @Override
    public int thirstQuenched() {
        return 500;
    }

    @Override
    public int liquidHeld() {
        return Integer.MAX_VALUE - 1000;
    }

    @Override
    public int liquidRemaining() {
        return Integer.MAX_VALUE - 1000;
    }

    @Override
    public int liquidType() {
        return liquidType;
    }

    @Override
    public void setLiquidType(int newLiquidType) {
        liquidType = newLiquidType;
    }

    @Override
    public void setThirstQuenched(int amount) {
    }

    @Override
    public void setLiquidHeld(int amount) {
    }

    @Override
    public void setLiquidRemaining(int amount) {
    }

    @Override
    public int amountTakenToFillMe(Drink theSource) {
        return 0;
    }

    @Override
    public boolean containsDrink() {
        return true;
    }

    @Override
    public List<Integer> resourceChoices() {
        return UnderWater.roomResources;
    }
}
