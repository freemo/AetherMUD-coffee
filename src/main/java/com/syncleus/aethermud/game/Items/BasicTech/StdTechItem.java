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

import com.planet_ink.game.Items.Basic.StdItem;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Items.interfaces.Technical;

/*
   Copyright 2016-2017 Bo Zimmerman

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

	   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
public class StdTechItem extends StdItem implements Technical {
    public StdTechItem() {
        super();
        setName("a piece of technology");
        setDisplayText("a small piece of technology sits here.");
        setDescription("You can't tell what it is by looking at it.");

        material = RawMaterial.RESOURCE_STEEL;
        baseGoldValue = 0;
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "StdTechItem";
    }

    @Override
    public int techLevel() {
        return phyStats().ability();
    }

    @Override
    public void setTechLevel(int lvl) {
        basePhyStats.setAbility(lvl);
        recoverPhyStats();
    }

    @Override
    public TechType getTechType() {
        return TechType.GIZMO;
    }

}
