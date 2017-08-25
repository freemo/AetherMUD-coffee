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

import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;


public class Parchment extends GenReadable {
    public Parchment() {
        super();
        setName("a piece of parchment");
        setDisplayText("a piece of parchment here.");
        setDescription("looks kinda like a piece of paper");
        basePhyStats().setWeight(1);
        recoverPhyStats();
        setMaterial(RawMaterial.RESOURCE_PAPER);
    }

    @Override
    public String ID() {
        return "Parchment";
    }

}
