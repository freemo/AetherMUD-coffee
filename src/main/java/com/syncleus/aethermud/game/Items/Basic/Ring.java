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

import com.planet_ink.game.Items.interfaces.Wearable;


public class Ring extends StdItem {
    public Ring() {
        super();
        setName("an ordinary ring");
        setDisplayText("a nondescript ring sits here doing nothing.");
        setDescription("It looks like a ring you wear on your fingers.");

        properWornBitmap = Wearable.WORN_LEFT_FINGER | Wearable.WORN_RIGHT_FINGER;
        wornLogicalAnd = false;
        baseGoldValue = 50;
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "Ring";
    }

}
