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
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.core.CMLib;


public class Chant_SenseOres extends Chant_SensePlants {
    private final static String localizedName = CMLib.lang().L("Sense Ores");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Sensing Ores)");
    private final int[] myMats = {RawMaterial.MATERIAL_ROCK,
        RawMaterial.MATERIAL_METAL};

    @Override
    public String ID() {
        return "Chant_SenseOres";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_ROCKCONTROL;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    public long flags() {
        return Ability.FLAG_TRACKING;
    }

    @Override
    protected String word() {
        return "ores";
    }

    @Override
    protected int[] okMaterials() {
        return myMats;
    }

    @Override
    protected int[] okResources() {
        return null;
    }
}
