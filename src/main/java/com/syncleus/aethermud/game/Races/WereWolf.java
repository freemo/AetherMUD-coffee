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
package com.planet_ink.game.Races;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.CharStats;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Items.interfaces.Weapon;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMSecurity;
import com.planet_ink.game.core.interfaces.Environmental;

import java.util.List;
import java.util.Vector;


public class WereWolf extends GiantWolf {
    private final static String localizedStaticName = CMLib.lang().L("WereWolf");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Canine");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 2, 2, 1, 1, 2, 2, 1, 2, 2, 1, 0, 1, 1, 0, 0};
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();
    private final int[] agingChart = {0, 4, 8, 12, 16, 20, 24, 28, 32};

    @Override
    public String ID() {
        return "WereWolf";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int shortestMale() {
        return 59;
    }

    @Override
    public int shortestFemale() {
        return 59;
    }

    @Override
    public int heightVariance() {
        return 12;
    }

    @Override
    public int lightestWeight() {
        return 80;
    }

    @Override
    public int weightVariance() {
        return 80;
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
    public int[] bodyMask() {
        return parts;
    }

    @Override
    public int[] getAgingChart() {
        return agingChart;
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, affectableStats.getStat(CharStats.STAT_DEXTERITY) + 3);
        affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, affectableStats.getStat(CharStats.STAT_INTELLIGENCE) + 3);
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        final MOB mob = (MOB) myHost;
        if (msg.amISource(mob)
            && (!msg.amITarget(mob))
            && (msg.targetMinor() == CMMsg.TYP_DAMAGE)
            && (msg.target() instanceof MOB)
            && (mob.fetchWieldedItem() == null)
            && (msg.tool() instanceof Weapon)
            && (CMLib.dice().rollPercentage() < 50)
            && (((Weapon) msg.tool()).weaponClassification() == Weapon.CLASS_NATURAL)
            && (!((MOB) msg.target()).isMonster())
            && (((msg.value()) > (((MOB) msg.target()).maxState().getHitPoints() / 5)))
            && (!CMSecurity.isDisabled(CMSecurity.DisFlag.AUTODISEASE))) {
            final Ability A = CMClass.getAbility("Disease_Lycanthropy");
            if ((A != null) && (((MOB) msg.target()).fetchEffect(A.ID()) == null) && (!CMSecurity.isAbilityDisabled(A.ID())))
                A.invoke(mob, (MOB) msg.target(), true, 0);
        }
        super.executeMsg(myHost, msg);
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                resources.addElement(makeResource
                    (L("some @x1 claws", name().toLowerCase()), RawMaterial.RESOURCE_BONE));
                for (int i = 0; i < 4; i++) {
                    resources.addElement(makeResource
                        (L("a strip of @x1 hide", name().toLowerCase()), RawMaterial.RESOURCE_FUR));
                }
                for (int i = 0; i < 2; i++) {
                    final RawMaterial meat = makeResource
                        (L("some @x1 meat", name().toLowerCase()), RawMaterial.RESOURCE_MEAT);
                    if (!CMSecurity.isDisabled(CMSecurity.DisFlag.AUTODISEASE)) {
                        final Ability A = CMClass.getAbility("Disease_Lycanthropy");
                        if ((A != null) && (!CMSecurity.isAbilityDisabled(A.ID())))
                            meat.addNonUninvokableEffect(A);
                    }
                    resources.addElement(meat);
                    resources.addElement(makeResource
                        (L("a pound of @x1 meat", name().toLowerCase()), RawMaterial.RESOURCE_MEAT));
                }
                resources.addElement(makeResource
                    (L("some @x1 blood", name().toLowerCase()), RawMaterial.RESOURCE_BLOOD));
                resources.addElement(makeResource
                    (L("a pile of @x1 bones", name().toLowerCase()), RawMaterial.RESOURCE_BONE));
            }
        }
        return resources;
    }
}
