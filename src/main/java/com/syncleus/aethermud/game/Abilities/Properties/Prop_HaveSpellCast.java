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
package com.syncleus.aethermud.game.Abilities.Properties;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Abilities.interfaces.TriggeredAffect;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;


public class Prop_HaveSpellCast extends Prop_SpellAdder {
    protected Item myItem = null;

    @Override
    public String ID() {
        return "Prop_HaveSpellCast";
    }

    @Override
    public String name() {
        return "Casting spells when owned";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ITEMS;
    }

    @Override
    public int triggerMask() {
        return TriggeredAffect.TRIGGER_GET;
    }

    @Override
    public long flags() {
        return Ability.FLAG_CASTER;
    }

    @Override
    public String accountForYourself() {
        return spellAccountingsWithMask("Casts ", " on the owner.");
    }

    @Override
    public void setAffectedOne(Physical P) {
        if (P == null) {
            if ((lastMOB instanceof MOB)
                && (((MOB) lastMOB).location() != null))
                removeMyAffectsFromLastMOB();
        }
        super.setAffectedOne(P);
    }

    @Override
    public void executeMsg(Environmental host, CMMsg msg) {
    }

    @Override
    public void affectPhyStats(Physical host, PhyStats affectableStats) {
        if (processing)
            return;
        processing = true;
        if (host instanceof Item) {
            myItem = (Item) host;

            if ((lastMOB instanceof MOB)
                && ((myItem.owner() != lastMOB) || (myItem.amDestroyed()))
                && (((MOB) lastMOB).location() != null))
                removeMyAffectsFromLastMOB();

            if ((lastMOB == null)
                && (myItem.owner() instanceof MOB)
                && (((MOB) myItem.owner()).location() != null))
                addMeIfNeccessary(myItem.owner(), myItem.owner(), true, 0, maxTicks);
        }
        processing = false;
    }
}
