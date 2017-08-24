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
package com.planet_ink.coffee_mud.Items.BasicTech;

import com.planet_ink.coffee_mud.Common.interfaces.Manufacturer;
import com.planet_ink.coffee_mud.Items.Basic.StdContainer;
import com.planet_ink.coffee_mud.Items.interfaces.Electronics;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;


public class StdElecContainer extends StdContainer implements Electronics {
    protected long powerCapacity = 100;
    protected long power = 100;
    protected boolean activated = false;
    protected String manufacturer = "RANDOM";
    protected Manufacturer cachedManufact = null;
    public StdElecContainer() {
        super();
        setName("an electronic container");
        setDisplayText("an electronic container sits here.");
        setDescription("You can't tell what it is by looking at it.");

        material = RawMaterial.RESOURCE_STEEL;
        baseGoldValue = 0;
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "StdElecContainer";
    }

    @Override
    public long powerCapacity() {
        return powerCapacity;
    }

    @Override
    public void setPowerCapacity(long capacity) {
        powerCapacity = capacity;
    }

    @Override
    public long powerRemaining() {
        return power;
    }

    @Override
    public void setPowerRemaining(long remaining) {
        power = remaining;
    }

    @Override
    public boolean activated() {
        return activated;
    }

    @Override
    public void activate(boolean truefalse) {
        activated = truefalse;
    }

    @Override
    public int powerNeeds() {
        return 0;
    }

    @Override
    public int techLevel() {
        return phyStats().ability();
    }

    @Override
    public void setTechLevel(int lvl) {
        basePhyStats.setAbility(lvl);
        recoverPhyStats();
    }

    @Override
    public String getManufacturerName() {
        return manufacturer;
    }

    @Override
    public void setManufacturerName(String name) {
        cachedManufact = null;
        if (name != null)
            manufacturer = name;
    }

    @Override
    public TechType getTechType() {
        return TechType.GIZMO;
    }

    protected double getComputedEfficiency() {
        double generatedAmount = getFinalManufacturer().getEfficiencyPct();
        if (subjectToWearAndTear() && (usesRemaining() <= 200))
            generatedAmount *= CMath.div(usesRemaining(), 100.0);
        return generatedAmount;
    }

    @Override
    public Manufacturer getFinalManufacturer() {
        if (cachedManufact == null) {
            cachedManufact = CMLib.tech().getManufacturerOf(this, manufacturer.toUpperCase().trim());
            if (cachedManufact == null)
                cachedManufact = CMLib.tech().getDefaultManufacturer();
        }
        return cachedManufact;
    }
}
