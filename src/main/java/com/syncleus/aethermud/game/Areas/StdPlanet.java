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
package com.planet_ink.game.Areas;

import com.planet_ink.game.Common.interfaces.TimeClock;
import com.planet_ink.game.core.*;
import com.planet_ink.game.core.interfaces.BoundedObject;
import com.planet_ink.game.core.interfaces.SpaceObject;

import java.util.Random;


public class StdPlanet extends StdTimeZone implements SpaceObject {
    private final static String[] MYCODES = {"COORDS", "RADIUS"};
    protected static double[] emptyDirection = new double[2];
    private static String[] codes = null;
    protected long[] coordinates = new long[3];
    protected long radius;

    public StdPlanet() {
        super();

        myClock = (TimeClock) CMClass.getCommon("DefaultTimeClock");
        coordinates = new long[]{Math.round(Long.MAX_VALUE * Math.random()), Math.round(Long.MAX_VALUE * Math.random()), Math.round(Long.MAX_VALUE * Math.random())};
        Random random = new Random(System.currentTimeMillis());
        radius = SpaceObject.Distance.PlanetRadius.dm + (random.nextLong() % (SpaceObject.Distance.PlanetRadius.dm / 20));
    }

    @Override
    public String ID() {
        return "StdPlanet";
    }

    @Override
    public long[] coordinates() {
        return coordinates;
    }

    @Override
    public void setCoords(long[] coords) {
        if ((coords != null) && (coords.length == 3))
            CMLib.map().moveSpaceObject(this, coords);
    }

    @Override
    public double[] direction() {
        return emptyDirection;
    }

    @Override
    public void setDirection(double[] dir) {
    }

    @Override
    public double speed() {
        return 0;
    }

    @Override
    public void setSpeed(double v) {
    }

    @Override
    public long radius() {
        return radius;
    }

    @Override
    public void setRadius(long radius) {
        this.radius = radius;
    }

    @Override
    public void setName(String newName) {
        super.setName(newName);
        myClock.setLoadName(newName);
    }

    @Override
    public SpaceObject knownTarget() {
        return null;
    }

    @Override
    public void setKnownTarget(SpaceObject O) {
    }

    @Override
    public SpaceObject knownSource() {
        return null;
    }

    @Override
    public void setKnownSource(SpaceObject O) {
    }

    @Override
    public long getMass() {
        return radius * MULTIPLIER_PLANET_MASS;
    }

    @Override
    public BoundedCube getBounds() {
        return new BoundedObject.BoundedCube(coordinates(), radius());
    }

    @Override
    public String getStat(String code) {
        switch (getLocCodeNum(code)) {
            case 0:
                return CMParms.toListString(this.coordinates());
            case 1:
                return "" + radius();
            default:
                return super.getStat(code);
        }
    }

    @Override
    public void setStat(String code, String val) {
        switch (getLocCodeNum(code)) {
            case 0:
                setCoords(CMParms.toLongArray(CMParms.parseCommas(val, true)));
                coordinates[0] = coordinates[0] % SpaceObject.Distance.GalaxyRadius.dm;
                coordinates[1] = coordinates[1] % SpaceObject.Distance.GalaxyRadius.dm;
                coordinates[2] = coordinates[2] % SpaceObject.Distance.GalaxyRadius.dm;
                break;
            case 1:
                setRadius(CMath.s_long(val));
                break;
            default:
                super.setStat(code, val);
                break;
        }
    }

    protected int getLocCodeNum(String code) {
        for (int i = 0; i < MYCODES.length; i++) {
            if (code.equalsIgnoreCase(MYCODES[i]))
                return i;
        }
        return -1;
    }

    @Override
    public String[] getStatCodes() {
        return (codes != null) ? codes : (codes = CMProps.getStatCodesList(CMParms.appendToArray(super.getStatCodes(), MYCODES), this));
    }
}
