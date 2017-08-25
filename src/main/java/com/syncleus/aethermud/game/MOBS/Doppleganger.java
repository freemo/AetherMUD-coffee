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
package com.syncleus.aethermud.game.MOBS;

import com.syncleus.aethermud.game.Common.interfaces.*;
import com.syncleus.aethermud.game.Items.interfaces.DeadBody;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.Random;


public class Doppleganger extends StdMOB {
    protected MOB mimicing = null;
    protected long ticksSinceMimicing = 0;
    public Doppleganger() {
        super();
        revert();
    }

    @Override
    public String ID() {
        return "Doppleganger";
    }

    protected void revert() {
        final Random randomizer = new Random(System.currentTimeMillis());
        username = "a doppleganger";
        setDescription("A formless biped creature, with wicked black eyes.");
        setDisplayText("A formless biped stands here.");
        setBasePhyStats((PhyStats) CMClass.getCommon("DefaultPhyStats"));
        setBaseCharStats((CharStats) CMClass.getCommon("DefaultCharStats"));
        setBaseState((CharState) CMClass.getCommon("DefaultCharState"));
        CMLib.factions().setAlignment(this, Faction.Align.EVIL);
        setMoney(250);
        basePhyStats.setWeight(100 + Math.abs(randomizer.nextInt() % 101));

        baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 10 + Math.abs(randomizer.nextInt() % 6));
        baseCharStats().setStat(CharStats.STAT_STRENGTH, 12 + Math.abs(randomizer.nextInt() % 6));
        baseCharStats().setStat(CharStats.STAT_DEXTERITY, 9 + Math.abs(randomizer.nextInt() % 6));

        basePhyStats().setDamage(7);
        basePhyStats().setSpeed(2.0);
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(6);
        basePhyStats().setArmor(70);

        baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20, basePhyStats().level()));

        addBehavior(CMClass.getBehavior("Mobile"));
        addBehavior(CMClass.getBehavior("MudChat"));

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((!amDead()) && (tickID == Tickable.TICKID_MOB)) {
            if (mimicing != null) {
                ticksSinceMimicing++;
                if (ticksSinceMimicing > 500) {
                    revert();
                }
            }
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public DeadBody killMeDead(boolean createBody) {
        revert();
        return super.killMeDead(createBody);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;
        if ((msg.amITarget(this)) && (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))) {
            if (mimicing != null) {
                if ((mimicing.getVictim() != null) && (mimicing.getVictim() != this))
                    mimicing = null;
                else if ((mimicing.location() != null) && (mimicing.location() != location()))
                    mimicing = null;
            }
            if ((mimicing == null) && (location() != null)) {
                location().show(this, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> take(s) on a new form!"));
                mimicing = msg.source();
                username = mimicing.Name();
                setDisplayText(mimicing.displayText());
                setDescription(mimicing.description());
                setBasePhyStats((PhyStats) mimicing.basePhyStats().copyOf());
                setBaseCharStats((CharStats) mimicing.baseCharStats().copyOf());
                setBaseState((CharState) mimicing.baseState().copyOf());
                recoverPhyStats();
                recoverCharStats();
                recoverMaxState();
                resetToMaxState();
                ticksSinceMimicing = 0;
            }
        }
        return true;
    }
}
