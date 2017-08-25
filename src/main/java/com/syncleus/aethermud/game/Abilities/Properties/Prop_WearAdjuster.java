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
package com.planet_ink.game.Abilities.Properties;

import com.planet_ink.game.Abilities.interfaces.TriggeredAffect;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.Armor;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.interfaces.Environmental;


public class Prop_WearAdjuster extends Prop_HaveAdjuster {
    public boolean checked = false;
    public boolean disabled = false;
    public boolean layered = false;

    @Override
    public String ID() {
        return "Prop_WearAdjuster";
    }

    @Override
    public String name() {
        return "Adjustments to stats when worn";
    }

    @Override
    public String accountForYourself() {
        return super.fixAccoutingsWithMask("Affects on the wearer: " + parameters[0], parameters[1]);
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
    public boolean canApply(MOB mob) {
        if (!super.canApply(mob))
            return false;
        if (disabled && checked)
            return false;
        if ((!((Item) affected).amWearingAt(Wearable.IN_INVENTORY))
            && ((!((Item) affected).amWearingAt(Wearable.WORN_FLOATING_NEARBY)) || (((Item) affected).fitsOn(Wearable.WORN_FLOATING_NEARBY))))
            return true;
        return false;
    }
}
