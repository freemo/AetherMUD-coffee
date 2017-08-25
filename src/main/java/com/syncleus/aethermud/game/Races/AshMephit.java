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


public class AshMephit extends Mephit {
    private final static String localizedStaticName = CMLib.lang().L("Ash Mephit");
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();
    private final String[] racialAbilityNames = {"Dragonbreath"};
    private final int[] racialAbilityLevels = {1,};
    private final int[] racialAbilityProficiencies = {100};
    private final boolean[] racialAbilityQuals = {false};
    private final String[] racialAbilityParms = {"dust lesser"};

    public AshMephit() {
        super();
    }

    @Override
    public String ID() {
        return "AshMephit";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setStat(CharStats.STAT_SAVE_PIERCE, affectableStats.getStat(CharStats.STAT_SAVE_PIERCE) + 50);
        affectableStats.setStat(CharStats.STAT_SAVE_FIRE, affectableStats.getStat(CharStats.STAT_SAVE_FIRE) + 50);

        affectableStats.setStat(CharStats.STAT_SAVE_BLUNT, affectableStats.getStat(CharStats.STAT_SAVE_BLUNT) - 100);
    }

    @Override
    public int lightestWeight() {
        return 10;
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
            return L("^r@x1^r is unstable and almost disintegrated!^N", mob.name(viewer));
        else if (pct < .20)
            return L("^r@x1^r is nearing disintegration.^N", mob.name(viewer));
        else if (pct < .30)
            return L("^r@x1^r is noticeably disintegrating.^N", mob.name(viewer));
        else if (pct < .40)
            return L("^y@x1^y is very damaged and slightly disintegrated.^N", mob.name(viewer));
        else if (pct < .50)
            return L("^y@x1^y is very damaged.^N", mob.name(viewer));
        else if (pct < .60)
            return L("^p@x1^p is starting to show major damage.^N", mob.name(viewer));
        else if (pct < .70)
            return L("^p@x1^p is definitely damaged.^N", mob.name(viewer));
        else if (pct < .80)
            return L("^g@x1^g is disheveled and mildly damaged.^N", mob.name(viewer));
        else if (pct < .90)
            return L("^g@x1^g is noticeably disheveled.^N", mob.name(viewer));
        else if (pct < .99)
            return L("^g@x1^g is slightly disheveled.^N", mob.name(viewer));
        else
            return L("^c@x1^c is in perfect condition.^N", mob.name(viewer));
    }

    @Override
    public DeadBody getCorpseContainer(MOB mob, Room room) {
        final DeadBody body = super.getCorpseContainer(mob, room);
        if (body != null) {
            body.setMaterial(RawMaterial.RESOURCE_ASH);
        }
        return body;
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                resources.addElement(makeResource
                    (L("a pound of ash"), RawMaterial.RESOURCE_ASH));
            }
        }
        return resources;
    }
}
