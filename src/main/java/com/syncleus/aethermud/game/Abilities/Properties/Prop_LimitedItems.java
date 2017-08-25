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
package com.planet_ink.game.Abilities.Properties;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.Log;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class Prop_LimitedItems extends Property {
    public static Map<String, List<Item>> instances = new Hashtable<String, List<Item>>();
    public static boolean[] playersLoaded = new boolean[1];
    protected boolean norecurse = false;
    protected boolean destroy = false;

    @Override
    public String ID() {
        return "Prop_LimitedItems";
    }

    @Override
    public String name() {
        return "Limited Item";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ITEMS;
    }

    @Override
    public String accountForYourself() {
        if (CMath.s_int(text()) <= 0)
            return "Only 1 may exist";
        return "Only " + CMath.s_int(text()) + " may exist.";
    }

    protected void countIfNecessary(Item I) {
        if (CMLib.flags().isInTheGame(I, false)) {
            int max = CMath.s_int(text());
            List<Item> myInstances = null;
            synchronized (instances) {
                if (!instances.containsKey(I.Name())) {
                    myInstances = new Vector<Item>();
                    instances.put(I.Name(), myInstances);
                    myInstances.add(I);
                }
            }
            myInstances = instances.get(I.Name());
            if (myInstances != null) {
                synchronized (myInstances) {
                    if (!myInstances.contains(I)) {
                        if (max == 0)
                            max++;
                        int num = 0;
                        for (int i = myInstances.size() - 1; i >= 0; i--)
                            if (!myInstances.get(i).amDestroyed())
                                num++;
                            else
                                myInstances.remove(i);
                        if (num >= max) {
                            I.destroy();
                            destroy = true;
                        } else
                            myInstances.add(I);
                    }
                }
            }
        }
    }

    @Override
    public void executeMsg(Environmental host, CMMsg msg) {
        super.executeMsg(host, msg);
        if ((msg.targetMinor() == CMMsg.TYP_ENTER)
            && (CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
            && (affected instanceof Item)
            && ((!(((Item) affected).owner() instanceof MOB))
            || ((MOB) ((Item) affected).owner()).playerStats() == null))
            countIfNecessary((Item) affected);
    }

    @Override
    public void affectPhyStats(Physical E, PhyStats affectableStats) {
        super.affectPhyStats(E, affectableStats);

        if ((!(E instanceof Item)) || (((Item) E).owner() == null))
            return;

        if (!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
            return;

        if (norecurse)
            return;
        norecurse = true;

        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.SENSE_UNLOCATABLE);

        synchronized (playersLoaded) {
            if (!playersLoaded[0]) {
                playersLoaded[0] = true;
                Log.sysOut("Prop_LimitedItems", "Checking player inventories");
                final List<String> V = CMLib.database().getUserList();
                for (final String name : V) {
                    final MOB M = CMLib.players().getLoadPlayer(name);
                    if ((M != null) && (M.location() != null) && (M.location().isInhabitant(M)))
                        Log.sysOut("Prop_LimitedItems", M.name() + " is in the Game!!!");
                }
                Log.sysOut("Prop_LimitedItems", "Done checking player inventories");
            }
        }
        if ((((Item) affected).owner() instanceof MOB)
            && (((MOB) ((Item) affected).owner()).playerStats() != null))
            countIfNecessary((Item) affected);
        if (destroy)
            ((Item) affected).destroy();
        norecurse = false;
    }
}
