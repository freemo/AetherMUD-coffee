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

import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.DeadBody;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class StoneGolem extends StdRace {
    private final static String localizedStaticName = CMLib.lang().L("Stone Golem");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Stone Golem");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 2, 2, 1, 1, 2, 2, 1, 2, 2, 1, 0, 1, 1, 0, 0};
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();
    private final int[] agingChart = {0, 0, 0, 0, 0, YEARS_AGE_LIVES_FOREVER, YEARS_AGE_LIVES_FOREVER, YEARS_AGE_LIVES_FOREVER, YEARS_AGE_LIVES_FOREVER};

    @Override
    public String ID() {
        return "StoneGolem";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int shortestMale() {
        return 64;
    }

    @Override
    public int shortestFemale() {
        return 60;
    }

    @Override
    public int heightVariance() {
        return 12;
    }

    @Override
    public int lightestWeight() {
        return 400;
    }

    @Override
    public int weightVariance() {
        return 100;
    }

    @Override
    public long forbiddenWornBits() {
        return 0;
    }

    @Override
    public String racialCategory() {
        return localizedStaticRacialCat;
    }

    @Override
    public boolean fertile() {
        return false;
    }

    @Override
    public boolean uncharmable() {
        return true;
    }

    @Override
    public int[] getBreathables() {
        return breatheAnythingArray;
    }

    @Override
    public int[] bodyMask() {
        return parts;
    }

    @Override
    public int[] getAgingChart() {
        return agingChart;
    }

    @Override
    public int availabilityCode() {
        return Area.THEME_FANTASY | Area.THEME_SKILLONLYMASK;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_GOLEM);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_DARK);
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setStat(CharStats.STAT_SAVE_POISON, affectableStats.getStat(CharStats.STAT_SAVE_POISON) + 100);
        affectableStats.setStat(CharStats.STAT_SAVE_MIND, affectableStats.getStat(CharStats.STAT_SAVE_MIND) + 100);
        affectableStats.setStat(CharStats.STAT_SAVE_GAS, affectableStats.getStat(CharStats.STAT_SAVE_GAS) + 100);
        affectableStats.setStat(CharStats.STAT_SAVE_PARALYSIS, affectableStats.getStat(CharStats.STAT_SAVE_PARALYSIS) + 100);
        affectableStats.setStat(CharStats.STAT_SAVE_UNDEAD, affectableStats.getStat(CharStats.STAT_SAVE_UNDEAD) + 100);
        affectableStats.setStat(CharStats.STAT_SAVE_DISEASE, affectableStats.getStat(CharStats.STAT_SAVE_DISEASE) + 100);
        affectableStats.setStat(CharStats.STAT_SAVE_FIRE, affectableStats.getStat(CharStats.STAT_SAVE_FIRE) + 100);
    }

    @Override
    public String makeMobName(char gender, int age) {
        return makeMobName('N', Race.AGE_MATURE);
    }

    @Override
    public String healthText(MOB viewer, MOB mob) {
        final double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState().getHitPoints()));

        if (pct < .10)
            return L("^r@x1^r is near destruction!^N", mob.name(viewer));
        else if (pct < .20)
            return L("^r@x1^r is massively cracked and damaged.^N", mob.name(viewer));
        else if (pct < .30)
            return L("^r@x1^r is extremely cracked and damaged.^N", mob.name(viewer));
        else if (pct < .40)
            return L("^y@x1^y is very cracked and damaged.^N", mob.name(viewer));
        else if (pct < .50)
            return L("^y@x1^y is cracked and damaged.^N", mob.name(viewer));
        else if (pct < .60)
            return L("^p@x1^p is cracked and slightly damaged.^N", mob.name(viewer));
        else if (pct < .70)
            return L("^p@x1^p is showing large cracks.^N", mob.name(viewer));
        else if (pct < .80)
            return L("^g@x1^g is showing some cracks.^N", mob.name(viewer));
        else if (pct < .90)
            return L("^g@x1^g is showing small cracks.^N", mob.name(viewer));
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
                resources.addElement(makeResource
                    (L("essence of golem"), RawMaterial.RESOURCE_BLOOD));
            }
        }
        return resources;
    }
}
