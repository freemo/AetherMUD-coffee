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
package com.syncleus.aethermud.game.Abilities.Druid;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Libraries.interfaces.TrackingLibrary;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMStrings;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Chant_SenseSentience extends Chant {
    private final static String localizedName = CMLib.lang().L("Sense Sentience");

    @Override
    public String ID() {
        return "Chant_SenseSentience";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_BREEDING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> chant(s) softly to <S-HIM-HERSELF>!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final StringBuffer lines = new StringBuffer("^x");
                lines.append(CMStrings.padRight(L("Name"), 25) + "| ");
                lines.append(CMStrings.padRight(L("Location"), 17) + "^.^N\n\r");
                TrackingLibrary.TrackingFlags flags;
                flags = CMLib.tracking().newFlags()
                    .plus(TrackingLibrary.TrackingFlag.AREAONLY);
                int range = 50 + super.getXLEVELLevel(mob) + (2 * super.getXMAXRANGELevel(mob));
                final List<Room> checkSet = CMLib.tracking().getRadiantRooms(mob.location(), flags, range);
                if (!checkSet.contains(mob.location()))
                    checkSet.add(mob.location());
                for (final Room room : checkSet) {
                    final Room R = CMLib.map().getRoom(room);
                    if ((((R.domainType() & Room.INDOORS) == 0)
                        && (R.domainType() != Room.DOMAIN_OUTDOORS_CITY)
                        && (R.domainType() != Room.DOMAIN_OUTDOORS_SPACEPORT))
                        || (R == mob.location()))
                        for (int m = 0; m < R.numInhabitants(); m++) {
                            final MOB M = R.fetchInhabitant(m);
                            if ((M != null) && (M.charStats().getStat(CharStats.STAT_INTELLIGENCE) >= 2)) {
                                lines.append("^!" + CMStrings.padRight(M.name(mob), 25) + "^?| ");
                                lines.append(R.displayText(mob));
                                lines.append("\n\r");
                            }
                        }
                }
                mob.tell(lines.toString() + "^.");
            }
        } else
            beneficialVisualFizzle(mob, null, L("<S-NAME> chant(s) softly to <S-HIM-HERSELF>, but the magic fades."));

        return success;
    }
}
