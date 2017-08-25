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

import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.SpaceObject;

import java.util.Random;


public class Moonlet extends GenSpaceBody {
    public Moonlet() {
        super();
        setName("a small moon");
        setDisplayText("a small moon is floating here");
        setDescription("it`s not a space station");
        coordinates = new long[]{Math.round(Long.MAX_VALUE * Math.random()), Math.round(Long.MAX_VALUE * Math.random()), Math.round(Long.MAX_VALUE * Math.random())};
        Random random = new Random(System.currentTimeMillis());
        radius = (SpaceObject.Distance.MoonRadius.dm / 10) + (random.nextLong() % Math.round(CMath.mul(SpaceObject.Distance.MoonRadius.dm, 0.099)));
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "Moonlet";
    }
}
