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
package com.syncleus.aethermud.game.Abilities.Prayers;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Libraries.interfaces.TrackingLibrary;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_Omnipresence extends Prayer {
    private final static String localizedName = CMLib.lang().L("Omnipresence");

    @Override
    public String ID() {
        return "Prayer_Omnipresence";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_COMMUNING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public long flags() {
        return Ability.FLAG_HOLY;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        final boolean success = proficiencyCheck(mob, 0, auto);
        if (!success)
            this.beneficialVisualFizzle(mob, null, L("<S-NAME> @x1, but <S-YOU-ARE> unanswered.", prayWord(mob)));
        else {
            final CMMsg msg = CMClass.getMsg(mob, null, null, verbalCastCode(mob, null, auto), L("^S<S-NAME> @x1 for the power of omnipresence.^?", prayWord(mob)));
            int numLayers = super.getXLEVELLevel(mob) + 1 + (2 * super.getXMAXRANGELevel(mob));
            if (CMLib.ableMapper().qualifyingLevel(mob, this) > 1)
                numLayers += ((super.adjustedLevel(mob, 0) / CMLib.ableMapper().qualifyingLevel(mob, this)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final Room thatRoom = mob.location();
                final TrackingLibrary.TrackingFlags flags = CMLib.tracking().newFlags()
                    .plus(TrackingLibrary.TrackingFlag.NOAIR)
                    .plus(TrackingLibrary.TrackingFlag.NOEMPTYGRIDS)
                    .plus(TrackingLibrary.TrackingFlag.NOWATER);
                mob.tell(L("Your mind is filled with visions as your presence expands...."));
                final List<Room> list = CMLib.tracking().getRadiantRooms(thatRoom, flags, numLayers);
                for (final Room R : list) {
                    if (CMLib.flags().canAccess(mob, R)) {
                        final CMMsg msg2 = CMClass.getMsg(mob, R, CMMsg.MSG_LOOK, null);
                        R.executeMsg(mob, msg2);
                    }
                }
            }
        }

        return success;
    }
}
