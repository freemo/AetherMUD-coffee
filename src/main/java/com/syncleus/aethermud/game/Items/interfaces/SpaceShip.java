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
package com.syncleus.aethermud.game.Items.interfaces;

import com.syncleus.aethermud.game.core.interfaces.SpaceObject;


/**
 * A Space Ship, which is a space object that's dockable and can change direction.
 * @author Bo Zimmerman
 *
 */
public interface SpaceShip extends SpaceObject, BoardableShip {
    /**
     * Sets one of the ship flags
     * @see SpaceShip.ShipFlag
     * @see SpaceShip#getShipFlag(ShipFlag)
     *
     * @param flag the flag to set or clear
     * @param flagValue the flag value
     */
    public void setShipFlag(final ShipFlag flag, final boolean flagValue);

    /**
     * Gets one of the ship flags
     * @see SpaceShip.ShipFlag
     * @see SpaceShip#setShipFlag(ShipFlag, boolean)
     *
     * @param flag the flag to set or clear
     * @return flagValue the flag value
     */
    public boolean getShipFlag(final ShipFlag flag);

    /**
     * Space ships are unique in having an Item stand-in for planet-side access,
     * as well as an Area object.  This method returns the object that resides in
     * the official space grid.
     * @return the official space version of this ship
     */
    public SpaceObject getShipSpaceObject();

    /**
     * The Outer Mold Line coefficient -- how streamlined are you?
     * @return the coefficient, from 0.05-0.3
     */
    public double getOMLCoeff();

    /**
     * Set the Outer Mold Line coefficient -- how streamlined are you?
     * @param coeff the Outer Mold Line coefficient
     */
    public void setOMLCoeff(double coeff);

    /**
     * The direction of facing of this object in radians.
     * @return 2 dimensional array for the direction of facing
     */
    public double[] facing();

    /**
     * Sets the direction of facing of this object in radians.
     * @param dir 2 dimensional array for the direction of facing
     */
    public void setFacing(double[] dir);

    /**
     * The full 360 orientation of the top of the object in radians.
     * @return radian for the direction of orientation
     */
    public double roll();

    /**
     * Sets the full 360 orientation of the top of the object in radians.
     * @param dir radian for the direction of orientation
     */
    public void setRoll(double dir);

    /**
     *
     * @author Bo Zimmerman
     *
     */
    public enum ShipFlag {
        IN_THE_AIR,
        NO_GRAVITY
    }
}
