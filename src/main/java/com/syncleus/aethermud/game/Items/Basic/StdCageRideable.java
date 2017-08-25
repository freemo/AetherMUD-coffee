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
package com.planet_ink.game.Items.Basic;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Behaviors.interfaces.Behavior;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.Container;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Rideable;

import java.util.Enumeration;


public class StdCageRideable extends StdRideable {
    public StdCageRideable() {
        super();
        setName("a cage wagon");
        setDisplayText("a cage wagon sits here.");
        setDescription("It\\`s of solid wood construction with metal bracings.  The door has a key hole.");
        capacity = 5000;
        setContainTypes(Container.CONTAIN_BODIES | Container.CONTAIN_CAGED);
        material = RawMaterial.RESOURCE_OAK;
        baseGoldValue = 15;
        basePhyStats().setWeight(1000);
        rideBasis = Rideable.RIDEABLE_WAGON;
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "StdCageRideable";
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if ((msg.amITarget(this))
            && ((msg.targetMinor() == CMMsg.TYP_LOOK) || (msg.targetMinor() == CMMsg.TYP_EXAMINE))) {
            synchronized (this) {
                final boolean wasOpen = isOpen;
                isOpen = true;
                CMLib.commands().handleBeingLookedAt(msg);
                isOpen = wasOpen;
            }
            if (behaviors != null) {
                for (final Behavior B : behaviors) {
                    if (B != null)
                        B.executeMsg(this, msg);
                }

            }
            for (final Enumeration<Ability> a = effects(); a.hasMoreElements(); ) {
                final Ability A = a.nextElement();
                if (A != null)
                    A.executeMsg(this, msg);
            }
            return;
        }
        super.executeMsg(myHost, msg);
    }
}
