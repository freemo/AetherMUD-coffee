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
package com.planet_ink.game.Abilities.Druid;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Libraries.interfaces.TrackingLibrary;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Chant_MassFungalGrowth extends Chant_SummonFungus {
    private final static String localizedName = CMLib.lang().L("Mass Fungal Growth");

    @Override
    public String ID() {
        return "Chant_MassFungalGrowth";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTGROWTH;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final Vector<Room> V = new Vector<Room>();
        TrackingLibrary.TrackingFlags flags;
        flags = CMLib.tracking().newFlags()
            .plus(TrackingLibrary.TrackingFlag.OPENONLY)
            .plus(TrackingLibrary.TrackingFlag.AREAONLY)
            .plus(TrackingLibrary.TrackingFlag.NOEMPTYGRIDS)
            .plus(TrackingLibrary.TrackingFlag.NOAIR)
            .plus(TrackingLibrary.TrackingFlag.NOWATER);
        CMLib.tracking().getRadiantRooms(mob.location(), V, flags, null, adjustedLevel(mob, asLevel) + (2 * super.getXMAXRANGELevel(mob)), null);
        for (int v = V.size() - 1; v >= 0; v--) {
            final Room R = V.elementAt(v);
            if (((R.domainType() != Room.DOMAIN_INDOORS_CAVE) && ((R.getAtmosphere() & RawMaterial.MATERIAL_ROCK) == 0))
                || (R == mob.location()))
                V.removeElementAt(v);
        }
        if (V.size() > 0) {
            mob.location().show(mob, null, CMMsg.MASK_ALWAYS | CMMsg.TYP_NOISE, L("The faint sound of fungus popping into existence can be heard."));
            int done = 0;
            for (int v = 0; v < V.size(); v++) {
                final Room R = V.elementAt(v);
                if (R == mob.location())
                    continue;
                buildMyThing(mob, R);
                if ((done++) == adjustedLevel(mob, asLevel))
                    break;
            }
        }

        return true;
    }
}
