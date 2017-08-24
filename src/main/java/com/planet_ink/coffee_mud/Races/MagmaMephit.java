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
package com.planet_ink.coffee_mud.Races;

import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.DeadBody;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class MagmaMephit extends Mephit {
    private final static String localizedStaticName = CMLib.lang().L("Magma Mephit");
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();
    private final String[] racialAbilityNames = {"Dragonbreath"};
    private final int[] racialAbilityLevels = {1,};
    private final int[] racialAbilityProficiencies = {100};
    private final boolean[] racialAbilityQuals = {false};
    private final String[] racialAbilityParms = {"fire lesser"};
    public MagmaMephit() {
        super();
    }

    @Override
    public String ID() {
        return "MagmaMephit";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setStat(CharStats.STAT_SAVE_ACID, affectableStats.getStat(CharStats.STAT_SAVE_ACID) + 50);
        affectableStats.setStat(CharStats.STAT_SAVE_COLD, affectableStats.getStat(CharStats.STAT_SAVE_COLD) - 50);
        affectableStats.setStat(CharStats.STAT_SAVE_ELECTRIC, affectableStats.getStat(CharStats.STAT_SAVE_ELECTRIC) - 50);
        affectableStats.setStat(CharStats.STAT_SAVE_BLUNT, affectableStats.getStat(CharStats.STAT_SAVE_BLUNT) + 95);
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
    }

    @Override
    public String[] racialAbilityNames() {
        return CMParms.combine(super.racialAbilityNames(), racialAbilityNames);
    }

    @Override
    public int[] racialAbilityLevels() {
        return CMParms.combine(super.racialAbilityLevels(), racialAbilityLevels);
    }

    @Override
    public int[] racialAbilityProficiencies() {
        return CMParms.combine(super.racialAbilityProficiencies(), racialAbilityProficiencies);
    }

    @Override
    public boolean[] racialAbilityQuals() {
        return CMParms.combine(super.racialAbilityQuals(), racialAbilityQuals);
    }

    @Override
    public String[] racialAbilityParms() {
        return CMParms.combine(super.racialAbilityParms(), racialAbilityParms);
    }

    @Override
    public String healthText(MOB viewer, MOB mob) {
        final double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState().getHitPoints()));

        if (pct < .10)
            return L("^r@x1^r is almost put out!^N", mob.name(viewer));
        else if (pct < .20)
            return L("^r@x1^r is flickering alot and is almost smoked out.^N", mob.name(viewer));
        else if (pct < .30)
            return L("^r@x1^r is flickering alot and smoking massively.^N", mob.name(viewer));
        else if (pct < .40)
            return L("^y@x1^y is flickering alot and smoking a lot.^N", mob.name(viewer));
        else if (pct < .50)
            return L("^y@x1^y is flickering and smoking.^N", mob.name(viewer));
        else if (pct < .60)
            return L("^p@x1^p is flickering and smoking somewhat.^N", mob.name(viewer));
        else if (pct < .70)
            return L("^p@x1^p is showing large flickers.^N", mob.name(viewer));
        else if (pct < .80)
            return L("^g@x1^g is showing some flickers.^N", mob.name(viewer));
        else if (pct < .90)
            return L("^g@x1^g is showing small flickers.^N", mob.name(viewer));
        else if (pct < .99)
            return L("^g@x1^g is no longer in perfect condition.^N", mob.name(viewer));
        else
            return L("^c@x1^c is in perfect condition.^N", mob.name(viewer));
    }

    @Override
    public DeadBody getCorpseContainer(MOB mob, Room room) {
        final DeadBody body = super.getCorpseContainer(mob, room);
        if (body != null) {
            body.setMaterial(RawMaterial.RESOURCE_STONE);
        }
        return body;
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                resources.addElement(makeResource
                    (L("a pound of stone"), RawMaterial.RESOURCE_STONE));
            }
        }
        return resources;
    }
}
