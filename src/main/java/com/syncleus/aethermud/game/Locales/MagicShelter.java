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
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Places;

public class MagicShelter extends StdRoom {
    public MagicShelter() {
        super();
        name = "the shelter";
        displayText = L("Magic Shelter");
        setDescription("You are in a domain of complete void and peace.");
        basePhyStats.setWeight(0);
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
        return "MagicShelter";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_INDOORS_MAGIC;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;
        if (CMLib.flags().isSleeping(this))
            return true;
        if ((msg.sourceMinor() == CMMsg.TYP_RECALL)
            || (msg.sourceMinor() == CMMsg.TYP_LEAVE)) {
            msg.source().tell(L("You can't leave the shelter that way.  You'll have to revoke it."));
            return false;
        }
        return true;
    }
}
