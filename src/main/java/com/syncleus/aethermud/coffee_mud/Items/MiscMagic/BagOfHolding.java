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
package com.planet_ink.coffee_mud.Items.MiscMagic;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.Basic.SmallSack;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.MiscMagic;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;


public class BagOfHolding extends SmallSack implements MiscMagic {
    public BagOfHolding() {
        super();

        setName("a small sack");
        setDisplayText("a small black sack is crumpled up here.");
        setDescription("A nice silk sack to put your things in.");
        secretIdentity = "A Bag of Holding";
        basePhyStats().setLevel(1);
        capacity = 1000;
        baseGoldValue = 25000;
        basePhyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_BONUS);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "BagOfHolding";
    }

    @Override
    public void executeMsg(Environmental host, CMMsg msg) {
        super.executeMsg(host, msg);
        if ((msg.targetMinor() == CMMsg.TYP_PUT)
            && (msg.target() instanceof BagOfHolding)
            && (msg.tool() instanceof BagOfHolding)) {
            ((Item) msg.target()).destroy();
            ((Item) msg.tool()).destroy();
            msg.source().tell(L("The bag implodes in your hands!"));
        }
    }

    @Override
    public void recoverPhyStats() {
        basePhyStats().setWeight(0);
        super.recoverPhyStats();
        basePhyStats().setWeight(-recursiveWeight());
        super.recoverPhyStats();
    }
}
