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
package com.planet_ink.coffee_mud.Behaviors;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Behaviors.interfaces.Behavior;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.collections.XVector;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;


public class NiceAbilities extends ActiveTicker {
    private List<Ability> mySkills = null;
    private int numAllSkills = -1;

    public NiceAbilities() {
        super();
        minTicks = 10;
        maxTicks = 20;
        chance = 100;
        tickReset();
    }

    @Override
    public String ID() {
        return "NiceAbilities";
    }

    @Override
    protected int canImproveCode() {
        return Behavior.CAN_MOBS;
    }

    @Override
    public String accountForYourself() {
        return "random benevolent skill using";
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
                    if ((numAllSkills != mob.numAllAbilities()) || (mySkills == null)) {
                        numAllSkills = mob.numAbilities();
                        mySkills = new ArrayList<Ability>();
                        for (final Enumeration<Ability> e = mob.allAbilities(); e.hasMoreElements(); ) {
                            final Ability tryThisOne = e.nextElement();
                            if ((tryThisOne != null)
                                && (tryThisOne.abstractQuality() == Ability.QUALITY_BENEFICIAL_OTHERS)
                                && (((tryThisOne.classificationCode() & Ability.ALL_ACODES) != Ability.ACODE_PRAYER)
                                || tryThisOne.appropriateToMyFactions(mob))) {
                                mySkills.add(tryThisOne);
                            }
                        }
                    }
                    if (mySkills.size() > 0) {
                        final Ability tryThisOne = mySkills.get(CMLib.dice().roll(1, mySkills.size(), -1));
                        if ((mob.fetchEffect(tryThisOne.ID()) == null)
                            && (tryThisOne.castingQuality(mob, target) == Ability.QUALITY_BENEFICIAL_OTHERS)) {
                            tryThisOne.setProficiency(CMLib.ableMapper().getMaxProficiency(mob, true, tryThisOne.ID()));
                            final Vector<String> V = new XVector<String>("$" + target.Name() + "$");
                            V.addElement(target.name());
                            tryThisOne.invoke(mob, V, target, false, 0);
                        }
                    }
                }
            }
        }
        return true;
    }
}
