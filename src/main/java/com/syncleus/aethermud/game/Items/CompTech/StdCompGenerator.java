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
package com.planet_ink.game.Items.CompTech;

import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.PowerGenerator;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Environmental;


public class StdCompGenerator extends StdCompFuelConsumer implements PowerGenerator {
    protected int generatedAmtPerTick = 1;

    public StdCompGenerator() {
        super();
        setName("a generator");
        setDisplayText("a generator sits here.");
        setDescription("If you put the right fuel in it, I'll bet it makes power.");

        material = RawMaterial.RESOURCE_STEEL;
        setPowerCapacity(1000);
        setPowerRemaining(0);
        baseGoldValue = 0;
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "StdCompGenerator";
    }

    @Override
    public int getGeneratedAmountPerTick() {
        return generatedAmtPerTick;
    }

    @Override
    public void setGeneratedAmountPerTick(int amt) {
        generatedAmtPerTick = amt;
    }

    @Override
    public TechType getTechType() {
        return TechType.SHIP_GENERATOR;
    }

    @Override
    public void executeMsg(Environmental myHost, CMMsg msg) {
        super.executeMsg(myHost, msg);
        if (msg.amITarget(this)) {
            switch (msg.targetMinor()) {
                case CMMsg.TYP_GET:
                    clearFuelCache();
                    break;
                case CMMsg.TYP_INSTALL:
                    clearFuelCache();
                    break;
                case CMMsg.TYP_ACTIVATE:
                    clearFuelCache();
                    if ((msg.source().location() != null) && (!CMath.bset(msg.targetMajor(), CMMsg.MASK_CNTRLMSG)))
                        msg.source().location().show(msg.source(), this, CMMsg.MSG_OK_VISUAL, L("<S-NAME> power(s) up <T-NAME>."));
                    this.activate(true);
                    break;
                case CMMsg.TYP_DEACTIVATE:
                    clearFuelCache();
                    if ((msg.source().location() != null) && (!CMath.bset(msg.targetMajor(), CMMsg.MASK_CNTRLMSG)))
                        msg.source().location().show(msg.source(), this, CMMsg.MSG_OK_VISUAL, L("<S-NAME> shut(s) down <T-NAME>."));
                    this.activate(false);
                    break;
                case CMMsg.TYP_LOOK:
                    if (CMLib.flags().canBeSeenBy(this, msg.source()))
                        msg.source().tell(L("@x1 is currently @x2", name(), (activated() ? "delivering power.\n\r" : "deactivated/shut down.\n\r")));
                    return;
                case CMMsg.TYP_POWERCURRENT:
                    if ((msg.value() == 0) && (activated())) {
                        if (((powerCapacity() - powerRemaining()) >= getGeneratedAmountPerTick())
                            || (powerRemaining() < getGeneratedAmountPerTick())) {
                            double generatedAmount = getGeneratedAmountPerTick();
                            generatedAmount *= this.getComputedEfficiency() * this.getFinalManufacturer().getEfficiencyPct();
                            long newAmount = powerRemaining() + Math.round(generatedAmount);
                            if (newAmount > powerCapacity())
                                newAmount = powerCapacity();
                            setPowerRemaining(newAmount);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public boolean sameAs(Environmental E) {
        if (!(E instanceof StdCompGenerator))
            return false;
        return super.sameAs(E);
    }
}
