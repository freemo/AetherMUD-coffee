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

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;
import java.util.Vector;


public class Lich extends Skeleton {
    private final static String localizedStaticName = CMLib.lang().L("Lich");
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

    @Override
    public String ID() {
        return "Lich";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setStat(CharStats.STAT_CONSTITUTION, affectableStats.getStat(CharStats.STAT_CONSTITUTION) - 4);
        affectableStats.setStat(CharStats.STAT_MAX_CONSTITUTION_ADJ, affectableStats.getStat(CharStats.STAT_MAX_CONSTITUTION_ADJ) - 4);
        affectableStats.setStat(CharStats.STAT_CHARISMA, affectableStats.getStat(CharStats.STAT_CHARISMA) + 6);
        affectableStats.setStat(CharStats.STAT_MAX_CHARISMA_ADJ, affectableStats.getStat(CharStats.STAT_MAX_CHARISMA_ADJ) + 6);
    }

    @Override
    public List<RawMaterial> myResources() {
        return resources;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!(ticking instanceof MOB))
            return super.tick(ticking, tickID);
        final MOB myChar = (MOB) ticking;
        if ((tickID == Tickable.TICKID_MOB) && (CMLib.dice().rollPercentage() < 10)) {
            final Ability A = CMClass.getAbility("Spell_Fear");
            if (A != null) {
                A.setMiscText("WEAK");
                A.invoke(myChar, null, true, 0);
            }
        }
        return super.tick(myChar, tickID);
    }
}
