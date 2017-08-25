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
package com.syncleus.aethermud.game.Races;

import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.DeadBody;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class SteamMephit extends Mephit {
    private final static String localizedStaticName = CMLib.lang().L("Steam Mephit");
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();
    private final String[] racialAbilityNames = {"Dragonbreath"};
    private final int[] racialAbilityLevels = {1,};
    private final int[] racialAbilityProficiencies = {100};
    private final boolean[] racialAbilityQuals = {false};
    private final String[] racialAbilityParms = {"fire lesser"};

    public SteamMephit() {
        super();
    }

    @Override
    public String ID() {
        return "SteamMephit";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setStat(CharStats.STAT_SAVE_SLASH, affectableStats.getStat(CharStats.STAT_SAVE_SLASH) + 50);
        affectableStats.setStat(CharStats.STAT_SAVE_WATER, affectableStats.getStat(CharStats.STAT_SAVE_WATER) + 50);

        affectableStats.setStat(CharStats.STAT_SAVE_PIERCE, affectableStats.getStat(CharStats.STAT_SAVE_PIERCE) + 100);

        affectableStats.setStat(CharStats.STAT_SAVE_GAS, affectableStats.getStat(CharStats.STAT_SAVE_GAS) - 100);
    }

    @Override
    public int lightestWeight() {
        return 2;
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
            return L("^r@x1^r is almost completely dissipated!^N", mob.name(viewer));
        else if (pct < .20)
            return L("^r@x1^r is very flawed and is nearing complete dissipation.^N", mob.name(viewer));
        else if (pct < .30)
            return L("^r@x1^r is very flawed and dissipating massively.^N", mob.name(viewer));
        else if (pct < .40)
            return L("^y@x1^y is very flawed and dissipating a lot.^N", mob.name(viewer));
        else if (pct < .50)
            return L("^y@x1^y is flawed and dissipating.^N", mob.name(viewer));
        else if (pct < .60)
            return L("^p@x1^p is flawed and starting to dissipate.^N", mob.name(viewer));
        else if (pct < .70)
            return L("^p@x1^p is more flawed.^N", mob.name(viewer));
        else if (pct < .80)
            return L("^g@x1^g is showing some flaws.^N", mob.name(viewer));
        else if (pct < .90)
            return L("^g@x1^g is showing small flaws.^N", mob.name(viewer));
        else if (pct < .99)
            return L("^g@x1^g is no longer in perfect condition.^N", mob.name(viewer));
        else
            return L("^c@x1^c is in perfect condition.^N", mob.name(viewer));
    }

    @Override
    public DeadBody getCorpseContainer(MOB mob, Room room) {
        final DeadBody body = super.getCorpseContainer(mob, room);
        if (body != null) {
            body.setMaterial(RawMaterial.RESOURCE_FRESHWATER);
        }
        return body;
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                resources.addElement(makeResource
                    (L("a pound of water"), RawMaterial.RESOURCE_FRESHWATER));
            }
        }
        return resources;
    }
}
