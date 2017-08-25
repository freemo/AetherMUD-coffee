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
package com.planet_ink.game.Items.Basic;

import com.planet_ink.game.Items.interfaces.RawMaterial;


public class Wineskin extends StdDrink {
    public Wineskin() {
        super();

        setName("a wineskin");
        amountOfThirstQuenched = 200;
        amountOfLiquidHeld = 1000;
        amountOfLiquidRemaining = 1000;
        basePhyStats.setWeight(10);
        capacity = 5;
        setDisplayText("a tough little wineskin sits here.");
        setDescription("Looks like it could hold quite a bit of drink.");
        baseGoldValue = 10;
        material = RawMaterial.RESOURCE_LEATHER;
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "Wineskin";
    }

}
