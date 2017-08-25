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
package com.planet_ink.coffee_mud.Abilities.Prayers;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Items.interfaces.DeadBody;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.collections.Pair;
import com.planet_ink.coffee_mud.core.collections.PairList;
import com.planet_ink.coffee_mud.core.collections.PairVector;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Rideable;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;


public class Prayer_ResurrectMount extends Prayer_Resurrect {
    private final static String localizedName = CMLib.lang().L("Resurrect Mount");
    protected volatile Rideable lastRider = null;
    protected PairList<Integer, Rideable> ridden = new PairVector<Integer, Rideable>();

    @Override
    public String ID() {
        return "Prayer_ResurrectMount";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_DEATHLORE;
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
    protected int canTargetCode() {
        return Ability.CAN_ITEMS;
    }

    @Override
    public boolean isAutoInvoked() {
        return true;
    }

    @Override
    public boolean canBeUninvoked() {
        return false;
    }

    @Override
    protected boolean canResurrectNormalMobs() {
        return true;
    }

    @Override
    public boolean supportsMending(Physical item) {

        if (item instanceof DeadBody) {
            DeadBody bodyI = (DeadBody) item;
            MOB M = bodyI.getSavedMOB();
            if (M == null) {
                int x = ridden.indexOfFirst(Integer.valueOf(bodyI.getMobHash()));
                if (x >= 0) {
                    M = (MOB) ridden.getSecond(x);
                    bodyI.setSavedMOB(M);
                    ridden.remove(x); // because it`s a copy anyway...
                }
                return (M != null);
            } else
                return ridden.containsFirst(Integer.valueOf(bodyI.getMobHash()));
        }
        return false;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (affected instanceof MOB) {
            final MOB M = (MOB) affected;
            if ((M.riding() != null)
                && (M.riding() != lastRider)
                && (M.riding() instanceof MOB)) {
                lastRider = M.riding();
                final Room R = ((MOB) lastRider).location();
                if ((R != null)
                    && (CMLib.flags().flaggedAffects(lastRider, Ability.FLAG_SUMMONING).size() == 0)) {
                    int x = ridden.indexOfFirst(Integer.valueOf(lastRider.hashCode()));
                    final Pair<Integer, Rideable> addMe;
                    if (x > 0)
                        addMe = ridden.remove(x);
                    else {
                        final Rideable copyR = (Rideable) lastRider.copyOf();
                        R.delInhabitant((MOB) copyR);
                        ((MOB) copyR).setLocation(null);
                        CMLib.threads().deleteAllTicks(copyR);
                        addMe = new Pair<Integer, Rideable>(Integer.valueOf(lastRider.hashCode()), copyR);
                    }
                    ridden.add(addMe);
                    if (ridden.size() > 5)
                        ridden.remove(0);
                }
            }
        }
        return true;
    }

}
