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
package com.planet_ink.game.Abilities.Common;

import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;


public class Irrigation extends BuildingSkill {
    private final static String localizedName = CMLib.lang().L("Irrigation");
    private static final String[] triggerStrings = I(new String[]{"IRRIGATE", "IRRIGATION"});

    public Irrigation() {
        super();
    }

    @Override
    public String ID() {
        return "Irrigation";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public String supportedResourceString() {
        return "ROCK|STONE";
    }

    @Override
    public String parametersFile() {
        return "irrigation.txt";
    }

    @Override
    protected String getMainResourceName() {
        return "Material";
    }

    @Override
    protected String getSoundName() {
        return "stone.wav";
    }

    @Override
    protected boolean canDescTitleHere(final Room R) {
        if (R != null) {
            switch (R.domainType()) {
                case Room.DOMAIN_INDOORS_UNDERWATER:
                case Room.DOMAIN_OUTDOORS_UNDERWATER:
                case Room.DOMAIN_INDOORS_WATERSURFACE:
                case Room.DOMAIN_OUTDOORS_WATERSURFACE:
                    return true;
            }
        }
        return false;
    }

    @Override
    protected int[][] getBasicMaterials(final MOB mob, int woodRequired, String miscType) {
        if (miscType.length() == 0)
            miscType = "stone";
        final int[][] idata = fetchFoundResourceData(mob,
            woodRequired, miscType, null,
            0, null, null,
            false,
            0, null);
        return idata;
    }
}
