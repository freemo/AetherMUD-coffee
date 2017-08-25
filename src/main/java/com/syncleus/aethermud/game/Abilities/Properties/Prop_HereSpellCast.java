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
import com.syncleus.aethermud.game.Abilities.interfaces.TriggeredAffect;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.Vector;


public class Prop_HereSpellCast extends Prop_HaveSpellCast {
    protected int lastNum = -1;
    private Vector<MOB> lastMOBs = new Vector<MOB>();

    @Override
    public String ID() {
        return "Prop_HereSpellCast";
    }

    @Override
    public String name() {
        return "Casting spells when here";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ROOMS;
    }

    @Override
    public boolean bubbleAffect() {
        return true;
    }

    @Override
    public String accountForYourself() {
        return spellAccountingsWithMask("Casts ", " on those here.");
    }

    @Override
    public int triggerMask() {
        return TriggeredAffect.TRIGGER_ENTER;
    }

    @Override
    public void setMiscText(String newText) {
        super.setMiscText(newText);
        lastMOBs = new Vector<MOB>();
    }

    public void process(MOB mob, Room room, int code) // code=0 add/sub, 1=addon, 2=subon
    {
        if ((code == 2) || ((code == 0) && (lastNum != room.numInhabitants()))) {
            for (int v = lastMOBs.size() - 1; v >= 0; v--) {
                final MOB lastMOB = lastMOBs.elementAt(v);
                if ((lastMOB.location() != room)
                    || ((mob == lastMOB) && (code == 2))) {
                    removeMyAffectsFrom(lastMOB);
                    lastMOBs.removeElementAt(v);
                }
            }
            lastNum = room.numInhabitants();
        }
        if ((!lastMOBs.contains(mob))
            && ((code == 1) || ((code == 0) && (room.isInhabitant(mob))))) {
            if (addMeIfNeccessary(mob, mob, true, 0, maxTicks))
                lastMOBs.addElement(mob);
        }
    }

    @Override
    public void executeMsg(Environmental host, CMMsg msg) {
        if (processing)
            return;
        if ((((msg.targetMinor() == CMMsg.TYP_ENTER) && (msg.target() == affected))
            || ((msg.targetMinor() == CMMsg.TYP_RECALL) && (msg.target() == affected)))
            && (affected instanceof Room))
            process(msg.source(), (Room) affected, 1);
        else if ((((msg.targetMinor() == CMMsg.TYP_LEAVE) && (msg.target() == affected))
            || ((msg.targetMinor() == CMMsg.TYP_RECALL) && (msg.target() != affected)))
            && (affected instanceof Room))
            process(msg.source(), (Room) affected, 2);
    }

    @Override
    public void affectPhyStats(Physical host, PhyStats affectableStats) {
        if (processing)
            return;
        try {
            processing = true;
            if ((host instanceof MOB) && (affected instanceof Room))
                process((MOB) host, (Room) affected, 0);
        } finally {
            processing = false;
        }
    }
}
