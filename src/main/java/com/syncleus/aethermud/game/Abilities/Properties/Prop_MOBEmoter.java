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

import com.syncleus.aethermud.game.Behaviors.interfaces.Behavior;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Prop_MOBEmoter extends Property {
    Behavior emoter = null;

    @Override
    public String ID() {
        return "Prop_MOBEmoter";
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        if (emoter == null) {
            emoter = CMClass.getBehavior("Emoter");
            emoter.setParms(text());
        }
        emoter.executeMsg(myHost, msg);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;
        if (emoter == null) {
            emoter = CMClass.getBehavior("Emoter");
            emoter.setParms(text());
        }
        return emoter.okMessage(myHost, msg);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if ((ticking instanceof MOB) && (tickID == Tickable.TICKID_MOB)) {
            if (emoter == null) {
                emoter = CMClass.getBehavior("Emoter");
                emoter.setParms(text());
            }
            if (!emoter.tick(ticking, tickID)) {
                if (CMParms.getParmInt(emoter.getParms(), "expires", 0) > 0)
                    ((MOB) ticking).delEffect(this);
            }
        }
        return true;
    }
}
