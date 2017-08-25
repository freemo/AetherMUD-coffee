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
package com.planet_ink.coffee_mud.Items.Armor;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Items.interfaces.Wearable;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;


public class MichaelsMithrilChain extends StdArmor {
    public MichaelsMithrilChain() {
        super();

        setName("a chain mail vest made of mithril");
        setDisplayText("a chain mail vest made from the dwarven alloy mithril");
        setDescription("This chain mail vest is made from a dwarven alloy called mithril, making it very light.");
        properWornBitmap = Wearable.WORN_TORSO;
        secretIdentity = "Michael\\`s Mithril Chain! (Armor Value:+75, Protection from Lightning)";
        baseGoldValue += 10000;
        wornLogicalAnd = false;
        basePhyStats().setArmor(50);
        basePhyStats().setWeight(40);
        basePhyStats().setAbility(75);
        basePhyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_BONUS);
        recoverPhyStats();
        material = RawMaterial.RESOURCE_MITHRIL;
    }

    @Override
    public String ID() {
        return "MichaelsMithrilChain";
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((msg.target() == null) || (!(msg.target() instanceof MOB)))
            return true;

        final MOB mob = (MOB) msg.target();
        if ((msg.targetMinor() == CMMsg.TYP_ELECTRIC)
            && (!this.amWearingAt(Wearable.IN_INVENTORY))
            && (!this.amWearingAt(Wearable.WORN_HELD))
            && (owner() == mob)) {
            mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> appear(s) to be unaffected."));
            return false;
        }
        return true;
    }

}
