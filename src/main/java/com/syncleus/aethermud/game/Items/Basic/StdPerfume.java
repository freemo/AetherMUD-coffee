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
package com.syncleus.aethermud.game.Items.Basic;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Behaviors.interfaces.Behavior;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Perfume;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Environmental;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;


public class StdPerfume extends StdDrink implements Perfume {
    List<String> smellList = new Vector<String>();

    public StdPerfume() {
        super();
        setName("a bottle of perfume");
        setDisplayText("a bottle of perfume sits here.");

        material = RawMaterial.RESOURCE_GLASS;
        amountOfThirstQuenched = 1;
        amountOfLiquidHeld = 10;
        amountOfLiquidRemaining = 10;
        disappearsAfterDrinking = true;
        liquidType = RawMaterial.RESOURCE_PERFUME;
        capacity = 0;
        baseGoldValue = 100;
        setRawProperLocationBitmap(Wearable.WORN_WIELD | Wearable.WORN_ABOUT_BODY | Wearable.WORN_FLOATING_NEARBY | Wearable.WORN_HELD | Wearable.WORN_ARMS | Wearable.WORN_BACK | Wearable.WORN_EARS | Wearable.WORN_EYES | Wearable.WORN_FEET | Wearable.WORN_HANDS | Wearable.WORN_HEAD | Wearable.WORN_LEFT_FINGER | Wearable.WORN_RIGHT_FINGER | Wearable.WORN_LEGS | Wearable.WORN_LEFT_WRIST | Wearable.WORN_MOUTH | Wearable.WORN_NECK | Wearable.WORN_RIGHT_WRIST | Wearable.WORN_TORSO | Wearable.WORN_WAIST);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "StdPerfume";
    }

    @Override
    public List<String> getSmellEmotes() {
        return smellList;
    }

    @Override
    public String getSmellList() {
        final StringBuffer list = new StringBuffer("");
        for (int i = 0; i < smellList.size(); i++)
            list.append((smellList.get(i)) + ";");
        return list.toString();
    }

    @Override
    public void setSmellList(String list) {
        smellList = CMParms.parseSemicolons(list, true);
    }

    @Override
    public void wearIfAble(MOB mob) {
        Ability E = mob.fetchEffect("Prop_MOBEmoter");
        if (E != null)
            mob.tell(L("You can't put any perfume on right now."));
        else {
            E = CMClass.getAbility("Prop_MOBEmoter");
            String s = getSmellList();
            if (s.toUpperCase().indexOf("EXPIRES") < 0)
                s = "expires=50 " + s;
            if (s.toUpperCase().trim().startsWith("SMELL "))
                E.setMiscText(s);
            else
                E.setMiscText("SMELL " + s);
            mob.addNonUninvokableEffect(E);
            E.setSavable(false);
        }
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (msg.target() == this) {
            if (msg.targetMinor() == CMMsg.TYP_WEAR)
                return true;
            if (!super.okMessage(myHost, msg))
                return false;
            if ((msg.targetMinor() == CMMsg.TYP_DRINK)
                && (liquidType() != RawMaterial.RESOURCE_FRESHWATER)) {
                msg.source().tell(L("You don't want to be drinking that."));
                return false;
            }
            return true;
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (msg.target() == this) {
            if (msg.targetMinor() == CMMsg.TYP_WEAR) {
                // the order that these things are checked in should
                // be holy, and etched in stone.
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
                amountOfLiquidRemaining -= amountOfThirstQuenched;
                wearIfAble(msg.source());
                if (disappearsAfterDrinking)
                    destroy();
                return;
            }
        }
        super.executeMsg(myHost, msg);
    }
}
