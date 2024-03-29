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
package com.syncleus.aethermud.game.Items.CompTech;

import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.ShipEngine;
import com.syncleus.aethermud.game.Items.interfaces.TechComponent;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.SpaceObject;


public class StdShipFuellessThruster extends StdElecCompItem implements ShipEngine {
    protected int maxThrust = 8900000;
    protected int minThrust = 0;
    protected double thrust = 0;
    protected long specificImpulse = SpaceObject.VELOCITY_SUBLIGHT;
    protected double fuelEfficiency = 0.33;
    protected boolean constantThrust = true;
    protected TechComponent.ShipDir[] ports = TechComponent.ShipDir.values();

    public StdShipFuellessThruster() {
        super();
        setName("a space drive");
        basePhyStats.setWeight(5000);
        setDisplayText("a space drive sits here.");
        setDescription("");
        baseGoldValue = 500000;
        basePhyStats().setLevel(1);
        recoverPhyStats();
        setMaterial(RawMaterial.RESOURCE_STEEL);
    }

    @Override
    public String ID() {
        return "StdShipFuellessThruster";
    }

    @Override
    public boolean sameAs(Environmental E) {
        if (!(E instanceof StdShipFuellessThruster))
            return false;
        return super.sameAs(E);
    }

    @Override
    public double getFuelEfficiency() {
        return fuelEfficiency;
    }

    @Override
    public void setFuelEfficiency(double amt) {
        fuelEfficiency = amt;
    }

    @Override
    public int getMaxThrust() {
        return maxThrust;
    }

    @Override
    public void setMaxThrust(int max) {
        maxThrust = max;
    }

    @Override
    public double getThrust() {
        return thrust;
    }

    @Override
    public void setThrust(double current) {
        thrust = current;
    }

    @Override
    public long getSpecificImpulse() {
        return specificImpulse;
    }

    @Override
    public void setSpecificImpulse(long amt) {
        specificImpulse = amt;
    }

    @Override
    public TechType getTechType() {
        return TechType.SHIP_ENGINE;
    }

    @Override
    protected double getComputedEfficiency() {
        return super.getComputedEfficiency() * this.getInstalledFactor();
    }

    @Override
    public int getMinThrust() {
        return minThrust;
    }

    @Override
    public void setMinThrust(int min) {
        this.minThrust = min;
    }

    @Override
    public boolean isConstantThruster() {
        return constantThrust;
    }

    @Override
    public void setConstantThruster(boolean isConstant) {
        constantThrust = isConstant;
    }

    /**
     * Gets set of available thrust ports on this engine.
     * @see ShipEngine#setAvailPorts(TechComponent.ShipDir[])
     * @return the set of available thrust ports.
     */
    @Override
    public TechComponent.ShipDir[] getAvailPorts() {
        return ports;
    }

    /**
     * Sets set of available thrust ports on this engine.
     * @see ShipEngine#getAvailPorts()
     * @param ports the set of available thrust ports.
     */
    @Override
    public void setAvailPorts(TechComponent.ShipDir[] ports) {
        this.ports = ports;
    }

    @Override
    public boolean consumeFuel(int amount) {
        if (this.powerRemaining() > amount) {
            this.setPowerRemaining(powerRemaining() - amount);
            return true;
        }
        return false;
    }

    @Override
    public void executeMsg(Environmental myHost, CMMsg msg) {
        super.executeMsg(myHost, msg);
        StdShipThruster.executeThrusterMsg(this, myHost, circuitKey, msg);
    }
}
