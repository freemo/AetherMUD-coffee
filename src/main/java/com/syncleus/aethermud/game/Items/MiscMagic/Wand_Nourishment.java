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
package com.planet_ink.game.Items.MiscMagic;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.interfaces.Environmental;


public class Wand_Nourishment extends StdWand {
    public Wand_Nourishment() {
        super();

        setName("a wooden wand");
        setDisplayText("a small wooden wand is here.");
        setDescription("A wand made out of wood");
        secretIdentity = "The wand of nourishment.  Hold the wand say \\`shazam\\` to it.";
        baseGoldValue = 200;
        material = RawMaterial.RESOURCE_OAK;
        recoverPhyStats();
        secretWord = "SHAZAM";
    }

    @Override
    public String ID() {
        return "Wand_Nourishment";
    }

    @Override
    public void setSpell(Ability theSpell) {
        super.setSpell(theSpell);
        secretWord = "SHAZAM";
    }

    @Override
    public void setMiscText(String newText) {
        super.setMiscText(newText);
        secretWord = "SHAZAM";
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (msg.amITarget(this)) {
            final MOB mob = msg.source();
            switch (msg.targetMinor()) {
                case CMMsg.TYP_WAND_USE:
                    if ((mob.isMine(this))
                        && (!amWearingAt(Wearable.IN_INVENTORY))
                        && (msg.targetMessage() != null)) {
                        if (msg.targetMessage().toUpperCase().indexOf("SHAZAM") >= 0) {
                            if (mob.curState().adjHunger(50, mob.maxState().maxHunger(mob.baseWeight())))
                                mob.tell(L("You are full."));
                            else
                                mob.tell(L("You feel nourished."));
                        }
                    }
                    return;
                default:
                    break;
            }
        }
        super.executeMsg(myHost, msg);
    }
}
