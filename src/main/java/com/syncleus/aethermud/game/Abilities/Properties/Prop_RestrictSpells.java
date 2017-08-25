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
package com.syncleus.aethermud.game.Abilities.Properties;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class Prop_RestrictSpells extends Property {
    @Override
    public String ID() {
        return "Prop_RestrictSpells";
    }

    @Override
    public String name() {
        return "Specific Spell Neutralizing";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ROOMS | Ability.CAN_AREAS | Ability.CAN_MOBS;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if ((msg.tool() instanceof Ability)
            && (text().toUpperCase().indexOf(msg.tool().ID().toUpperCase()) >= 0)) {
            Room roomS = null;
            Room roomD = null;
            if ((msg.target() instanceof MOB) && (((MOB) msg.target()).location() != null))
                roomD = ((MOB) msg.target()).location();
            else if (msg.source().location() != null)
                roomS = msg.source().location();
            else if (msg.target() instanceof Room)
                roomD = (Room) msg.target();

            if ((roomS != null) && (roomD != null) && (roomS == roomD))
                roomD = null;

            final Ability A = (Ability) msg.tool();
            if (((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_CHANT)
                || ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_SPELL)
                || ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_PRAYER)
                || ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_SONG)) {
                if (roomS != null)
                    roomS.showHappens(CMMsg.MSG_OK_VISUAL, L("Magic energy fizzles and is absorbed into the air."));
                if (roomD != null)
                    roomD.showHappens(CMMsg.MSG_OK_VISUAL, L("Magic energy fizzles and is absorbed into the air."));
                if ((msg.source().location() != null)
                    && (msg.source().location() != roomS)
                    && (msg.source().location() != roomD))
                    msg.source().location().showHappens(CMMsg.MSG_OK_VISUAL, L("Magic energy fizzles and is absorbed into the air."));
            }
            return false;
        }
        return true;
    }
}
