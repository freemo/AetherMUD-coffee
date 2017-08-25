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
package com.syncleus.aethermud.game.Items.Armor;

import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class GlowingMageArmor extends StdArmor {
    public GlowingMageArmor() {
        super();

        setName("a mystical glowing breast plate");
        setDisplayText("If this is sitting around somewhere, something is wrong!");
        setDescription("This suit of armor is made from magical energy, but looks sturdy and protective.");
        properWornBitmap = Wearable.WORN_TORSO;
        wornLogicalAnd = false;
        basePhyStats().setArmor(45);
        basePhyStats().setWeight(0);
        basePhyStats().setAbility(0);
        baseGoldValue = 40000;
        material = RawMaterial.RESOURCE_NOTHING;
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "GlowingMageArmor";
    }

    @Override
    public boolean isSavable() {
        return false;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if ((amWearingAt(Wearable.IN_INVENTORY) || (owner() == null) || (owner() instanceof Room))
            && (!amDestroyed()))
            destroy();

        final MOB mob = msg.source();
        if (!msg.amITarget(this))
            return true;
        else if ((msg.targetMinor() == CMMsg.TYP_GET)
            || (msg.targetMinor() == CMMsg.TYP_PUSH)
            || (msg.targetMinor() == CMMsg.TYP_PULL)
            || (msg.targetMinor() == CMMsg.TYP_REMOVE)) {
            mob.tell(L("The mage armor cannot be removed from where it is."));
            return false;
        }
        return true;
    }
}
