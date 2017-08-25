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
package com.syncleus.aethermud.game.Items.Basic;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class GenMirror extends GenItem {
    protected boolean oncePerRound = false;

    public GenMirror() {
        super();
        setName("a generic mirror");
        basePhyStats.setWeight(2);
        setDisplayText("a generic mirror sits here.");
        setDescription("You see yourself in it!");
        baseGoldValue = 5;
        basePhyStats().setLevel(1);
        recoverPhyStats();
        setMaterial(RawMaterial.RESOURCE_GLASS);
    }

    @Override
    public String ID() {
        return "GenMirror";
    }

    @Override
    public String description() {
        return "You see yourself in it!";
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((owner == null) || (!(owner instanceof MOB)) || (amWearingAt(Wearable.IN_INVENTORY)))
            return super.okMessage(myHost, msg);

        final MOB mob = (MOB) owner;
        if ((msg.amITarget(mob))
            && (!oncePerRound)
            && (msg.tool() instanceof Ability)
            && ((msg.tool().ID().equals("Spell_FleshStone"))
            || (msg.tool().ID().equals("Prayer_FleshRock")))
            && (!mob.amDead())
            && (mob != msg.source())) {
            oncePerRound = true;
            mob.location().show(mob, null, CMMsg.MSG_OK_VISUAL, L("@x1 reflects the vicious magic!", name()));
            final Ability A = (Ability) msg.tool();
            A.invoke(mob, msg.source(), true, phyStats().level());
            return false;
        }
        oncePerRound = false;
        return super.okMessage(myHost, msg);
    }

}
