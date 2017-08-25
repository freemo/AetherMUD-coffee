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
package com.planet_ink.game.Items.BasicTech;

public class GenPhaser2 extends GenElecWeapon {
    public GenPhaser2() {
        super();
        setName("a type-II phaser");
        setDisplayText("a type-II phaser");
        super.mode = ModeType.KILL;
        super.modeTypes = new ModeType[]{ModeType.STUN, ModeType.KILL, ModeType.DISINTEGRATE};
        setDescription("There are three activation settings: stun, kill, and disintegrate.");
    }

    @Override
    public String ID() {
        return "GenPhaser2";
    }
}
