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
package com.planet_ink.game.Behaviors;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Behaviors.interfaces.Behavior;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.List;
import java.util.Vector;


public class Healer extends ActiveTicker {
    protected static final List<Ability> healingVector = new Vector<Ability>();

    public Healer() {
        super();
        minTicks = 10;
        maxTicks = 20;
        chance = 100;
        tickReset();
        if (healingVector.size() == 0) {
            healingVector.add(CMClass.getAbility("Prayer_CureBlindness"));
            healingVector.add(CMClass.getAbility("Prayer_CureDisease"));
            healingVector.add(CMClass.getAbility("Prayer_CureLight"));
            healingVector.add(CMClass.getAbility("Prayer_RemoveCurse"));
            healingVector.add(CMClass.getAbility("Prayer_Bless"));
            healingVector.add(CMClass.getAbility("Prayer_Sanctuary"));
        }
    }

    @Override
    public String ID() {
        return "Healer";
    }

    @Override
    protected int canImproveCode() {
        return Behavior.CAN_MOBS;
    }

    @Override
    public String accountForYourself() {
        return "benevolent healing";
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        super.tick(ticking, tickID);
        if ((canAct(ticking, tickID)) && (ticking instanceof MOB)) {
            final MOB mob = (MOB) ticking;
            final Room thisRoom = mob.location();
            if (thisRoom == null)
                return true;

            final double aChance = CMath.div(mob.curState().getMana(), mob.maxState().getMana());
            if ((Math.random() > aChance) || (mob.curState().getMana() < 50))
                return true;

            if (thisRoom.numPCInhabitants() > 0) {
                final MOB target = thisRoom.fetchRandomInhabitant();
                MOB followMOB = target;
                if ((target != null) && (target.amFollowing() != null))
                    followMOB = target.amUltimatelyFollowing();
                if ((target != null)
                    && (target != mob)
                    && (followMOB.getVictim() != mob)
                    && (!followMOB.isMonster())) {
                    final Ability tryThisOne = healingVector.get(CMLib.dice().roll(1, healingVector.size(), -1));
                    Ability thisOne = mob.fetchAbility(tryThisOne.ID());
                    if (thisOne == null) {
                        thisOne = (Ability) tryThisOne.copyOf();
                        thisOne.setSavable(false);
                        mob.addAbility(thisOne);
                    }
                    thisOne.setProficiency(100);
                    final Vector<String> V = new Vector<String>();
                    if (!target.isMonster())
                        V.addElement(target.name());
                    thisOne.invoke(mob, V, target, false, 0);
                }
            }
        }
        return true;
    }
}
