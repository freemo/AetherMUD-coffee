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
package com.syncleus.aethermud.game.Races;

import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.Vector;


public class Shadow extends Spirit {
    private final static String localizedStaticName = CMLib.lang().L("Shadow");
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

    @Override
    public String ID() {
        return "Shadow";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public long forbiddenWornBits() {
        return 0;
    }

    @Override
    protected boolean destroyBodyAfterUse() {
        return true;
    }

    @Override
    public int availabilityCode() {
        return Area.THEME_FANTASY | Area.THEME_SKILLONLYMASK;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        if ((CMLib.flags().isInDark(affected))
            || ((affected instanceof MOB) && (((MOB) affected).location() != null) && (CMLib.flags().isInDark((((MOB) affected).location())))))
            affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_INVISIBLE);
        affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_GOLEM);
    }
}

