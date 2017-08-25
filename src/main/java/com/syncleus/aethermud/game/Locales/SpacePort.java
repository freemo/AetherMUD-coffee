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
package com.syncleus.aethermud.game.Locales;

import com.syncleus.aethermud.game.Locales.interfaces.LocationRoom;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.interfaces.SpaceObject;


public class SpacePort extends StdRoom implements LocationRoom {
    private final static String[] MYCODES = {"COREDIR"};
    private static String[] codes = null;
    protected double[] dirFromCore = new double[2];

    public SpacePort() {
        super();
        name = "the space port";
        basePhyStats.setWeight(1);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "SpacePort";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_OUTDOORS_SPACEPORT;
    }

    @Override
    public long[] coordinates() {
        final SpaceObject planet = CMLib.map().getSpaceObject(this, true);
        if (planet != null)
            return CMLib.map().getLocation(planet.coordinates(), dirFromCore, planet.radius());
        return new long[]{0, 0, 0};
    }

    @Override
    public double[] getDirectionFromCore() {
        return dirFromCore;
    }

    @Override
    public void setDirectionFromCore(double[] dir) {
        if ((dir != null) && (dir.length == 2))
            dirFromCore = dir;
    }

    @Override
    public String getStat(String code) {
        switch (getLocCodeNum(code)) {
            case 0:
                return CMParms.toListString(this.getDirectionFromCore());
            default:
                return super.getStat(code);
        }
    }

    @Override
    public void setStat(String code, String val) {
        switch (getLocCodeNum(code)) {
            case 0:
                this.setDirectionFromCore(CMParms.toDoubleArray(CMParms.parseCommas(val, true)));
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
