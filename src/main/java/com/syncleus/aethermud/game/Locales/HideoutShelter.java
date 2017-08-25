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
package com.syncleus.aethermud.game.Locales;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.interfaces.Places;

public class HideoutShelter extends MagicShelter {
    public HideoutShelter() {
        super();
        name = "the hideout";
        displayText = L("Secret Hideout");
        setDescription("You are in a small dark room.");
        basePhyStats.setWeight(0);
        basePhyStats.setDisposition(PhyStats.IS_DARK);
        recoverPhyStats();
        Ability A = CMClass.getAbility("Prop_PeaceMaker");
        if (A != null) {
            A.setSavable(false);
            addEffect(A);
        }
        A = CMClass.getAbility("Prop_NoRecall");
        if (A != null) {
            A.setSavable(false);
            addEffect(A);
        }
        A = CMClass.getAbility("Prop_NoSummon");
        if (A != null) {
            A.setSavable(false);
            addEffect(A);
        }
        A = CMClass.getAbility("Prop_NoTeleport");
        if (A != null) {
            A.setSavable(false);
            addEffect(A);
        }
        A = CMClass.getAbility("Prop_NoTeleportOut");
        if (A != null) {
            A.setSavable(false);
            addEffect(A);
        }
        climask = Places.CLIMASK_NORMAL;
    }

    @Override
    public String ID() {
        return "HideoutShelter";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_INDOORS_WOOD;
    }
}
