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
import com.syncleus.aethermud.game.Items.interfaces.Armor;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.PhysicalAgent;


public class Prop_WearSpellCast extends Prop_HaveSpellCast {
    public boolean checked = false;
    public boolean disabled = false;
    public boolean layered = false;

    @Override
    public String ID() {
        return "Prop_WearSpellCast";
    }

    @Override
    public String name() {
        return "Casting spells when worn";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ITEMS;
    }

    @Override
    public String accountForYourself() {
        return spellAccountingsWithMask("Casts ", " on the wearer.");
    }

    @Override
    public int triggerMask() {
        return TriggeredAffect.TRIGGER_WEAR_WIELD;
    }

    public void check(MOB mob, Armor A) {
        if (!layered) {
            checked = true;
            disabled = false;
        }
        final boolean oldDisabled = disabled;
        if (A.amWearingAt(Wearable.IN_INVENTORY)) {
            checked = false;
            return;
        }
        if (checked)
            return;
        Item I = null;
        disabled = false;
        for (int i = 0; i < mob.numItems(); i++) {
            I = mob.getItem(i);
            if ((I instanceof Armor)
                && (!I.amWearingAt(Wearable.IN_INVENTORY))
                && ((I.rawWornCode() & A.rawWornCode()) > 0)
                && (I != A)) {
                disabled = A.getClothingLayer() <= ((Armor) I).getClothingLayer();
                if (disabled) {
                    break;
                }
            }
        }
        if ((!oldDisabled) && (disabled))
            this.removeMyAffectsFromLastMOB();
        checked = true;
    }

    @Override
    public void setMiscText(String newText) {
        super.setMiscText(newText);
        layered = CMParms.parseSemicolons(newText.toUpperCase(), true).indexOf("LAYERED") >= 0;
    }

    @Override
    public void executeMsg(Environmental host, CMMsg msg) {
        if ((affected instanceof Armor) && (msg.source() == ((Armor) affected).owner())) {
            if ((msg.targetMinor() == CMMsg.TYP_REMOVE)
                || (msg.sourceMinor() == CMMsg.TYP_WEAR)
                || (msg.sourceMinor() == CMMsg.TYP_WIELD)
                || (msg.sourceMinor() == CMMsg.TYP_HOLD)
                || (msg.sourceMinor() == CMMsg.TYP_DROP))
                checked = false;
            else {
                check(msg.source(), (Armor) affected);
                super.executeMsg(host, msg);
            }
        } else
            super.executeMsg(host, msg);
    }

    @Override
    public boolean addMeIfNeccessary(PhysicalAgent source, Physical target, boolean makeLongLasting, int asLevel, short maxTicks) {
        if (disabled && checked)
            return false;
        return super.addMeIfNeccessary(source, target, makeLongLasting, asLevel, maxTicks);
    }

    @Override
    public void affectPhyStats(Physical host, PhyStats affectableStats) {
        if (processing)
            return;
        processing = true;
        if ((host != null) && (host instanceof Item)) {
            myItem = (Item) host;

            final boolean worn = (!myItem.amWearingAt(Wearable.IN_INVENTORY))
                && ((!myItem.amWearingAt(Wearable.WORN_FLOATING_NEARBY)) || (myItem.fitsOn(Wearable.WORN_FLOATING_NEARBY)));

            if ((lastMOB instanceof MOB)
                && (((MOB) lastMOB).location() != null)
                && ((myItem.owner() != lastMOB) || (!worn)))
                removeMyAffectsFromLastMOB();

            if ((lastMOB == null)
                && (worn)
                && (myItem.owner() != null)
                && (myItem.owner() instanceof MOB)
                && (((MOB) myItem.owner()).location() != null)) {
                if (myItem instanceof Armor)
                    check((MOB) myItem.owner(), ((Armor) myItem));
                addMeIfNeccessary(myItem.owner(), myItem.owner(), true, 0, maxTicks);
            }
        }
        processing = false;
    }
}
