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
package com.syncleus.aethermud.game.Items.Basic;

import com.syncleus.aethermud.game.core.interfaces.SpaceObject;

import java.util.Random;


public class Asteroid extends GenSpaceBody {
    public Asteroid() {
        super();
        setName("an asteroid");
        setDisplayText("an asteroid is here");
        setDescription("it`s a big rock");
        coordinates = new long[]{Math.round(Long.MAX_VALUE * Math.random()), Math.round(Long.MAX_VALUE * Math.random()), Math.round(Long.MAX_VALUE * Math.random())};
        Random random = new Random(System.currentTimeMillis());
        radius = (5 * SpaceObject.Distance.Kilometer.dm) + (random.nextLong() % (4 * SpaceObject.Distance.Kilometer.dm));
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "Asteroid";
    }
}
