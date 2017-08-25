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
package com.syncleus.aethermud.game.Items.Weapons;

import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.core.CMLib;


public class DrowQuarterstaff extends Mace {
    public DrowQuarterstaff() {
        super();

        setName("a quarterstaff");
        setDisplayText("a quarterstaff is on the ground.");
        setDescription("A quarterstaff made out of a very dark material metal.");
        secretIdentity = "A Drow quarterstaff";
        basePhyStats().setAbility(CMLib.dice().roll(1, 6, 0));
        basePhyStats().setLevel(1);
        basePhyStats().setWeight(4);
        basePhyStats().setAttackAdjustment(0);
        basePhyStats().setDamage(6);
        basePhyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_BONUS);
        baseGoldValue = 2500;
        recoverPhyStats();
        material = RawMaterial.RESOURCE_STEEL;
        weaponDamageType = TYPE_BASHING;
    }

    @Override
    public String ID() {
        return "DrowQuarterstaff";
    }

}
