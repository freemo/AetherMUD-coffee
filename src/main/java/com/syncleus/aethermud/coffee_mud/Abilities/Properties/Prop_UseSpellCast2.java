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
package com.planet_ink.coffee_mud.Abilities.Properties;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Food;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.core.interfaces.Drink;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;


public class Prop_UseSpellCast2 extends Prop_UseSpellCast {
    @Override
    public String ID() {
        return "Prop_UseSpellCast2";
    }

    @Override
    public String name() {
        return "Casting spells when used";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ITEMS;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (processing)
            return;
        processing = true;

        if (affected == null)
            return;
        final Item myItem = (Item) affected;
        if (myItem.owner() == null)
            return;
        switch (msg.sourceMinor()) {
            case CMMsg.TYP_DRINK:
                if ((myItem instanceof Drink)
                    && (msg.amITarget(myItem)))
                    addMeIfNeccessary(msg.source(), msg.source(), 0, maxTicks);
                break;
            case CMMsg.TYP_POUR:
                if ((myItem instanceof Drink)
                    && (msg.tool() == myItem)
                    && (msg.target() instanceof Physical))
                    addMeIfNeccessary(msg.source(), (Physical) msg.target(), 0, maxTicks);
                break;
            case CMMsg.TYP_EAT:
                if ((myItem instanceof Food)
                    && (msg.amITarget(myItem)))
                    addMeIfNeccessary(msg.source(), msg.source(), 0, maxTicks);
                break;
            case CMMsg.TYP_GET:
                if ((!(myItem instanceof Drink))
                    && (!(myItem instanceof Food))
                    && (msg.amITarget(myItem)))
                    addMeIfNeccessary(msg.source(), msg.source(), 0, maxTicks);
                break;
        }
        processing = false;
    }
}
