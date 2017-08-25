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

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMSecurity;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Insect extends StdRace {
    private final static String localizedStaticName = CMLib.lang().L("Insect");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Insect");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {2, 2, 0, 1, 1, 0, 0, 1, 2, 2, 0, 0, 1, 0, 0, 0};
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();
    private final int[] agingChart = {0, 0, 0, 1, 1, 1, 1, 2, 2};

    public Insect() {
        super();
        super.naturalAbilImmunities.add("Disease_Gonorrhea");
        super.naturalAbilImmunities.add("Disease_Malaria");
    }

    @Override
    public String ID() {
        return "Insect";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int shortestMale() {
        return 2;
    }

    @Override
    public int shortestFemale() {
        return 2;
    }

    @Override
    public int heightVariance() {
        return 0;
    }

    @Override
    public int lightestWeight() {
        return 1;
    }

    @Override
    public int weightVariance() {
        return 0;
    }

    @Override
    public long forbiddenWornBits() {
        return Integer.MAX_VALUE;
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
    public int availabilityCode() {
        return Area.THEME_FANTASY | Area.THEME_SKILLONLYMASK;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_SNEAKING);
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
            && (((Weapon) msg.tool()).weaponClassification() == Weapon.CLASS_NATURAL)
            && (!((MOB) msg.target()).isMonster())
            && (((msg.value()) > (((MOB) msg.target()).maxState().getHitPoints() / 20)))
            && (!CMSecurity.isDisabled(CMSecurity.DisFlag.AUTODISEASE))) {
            final Ability A = CMClass.getAbility("Disease_Lyme");
            if ((A != null) && (((MOB) msg.target()).fetchEffect(A.ID()) == null) && (!CMSecurity.isAbilityDisabled(A.ID())))
                A.invoke(mob, (MOB) msg.target(), true, 0);
        }
        super.executeMsg(myHost, msg);
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 3);
        affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 3);
        affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 1);
        affectableStats.setStat(CharStats.STAT_SAVE_POISON, affectableStats.getStat(CharStats.STAT_SAVE_POISON) + 100);
    }

    @Override
    public String arriveStr() {
        return "creeps in";
    }

    @Override
    public String leaveStr() {
        return "creeps";
    }

    @Override
    public Weapon myNaturalWeapon() {
        if (naturalWeapon == null) {
            naturalWeapon = CMClass.getWeapon("StdWeapon");
            naturalWeapon.setName(L("a nasty maw"));
            naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
            naturalWeapon.setUsesRemaining(1000);
            naturalWeapon.setWeaponDamageType(Weapon.TYPE_NATURAL);
        }
        return naturalWeapon;
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                resources.addElement(makeResource
                    (L("some @x1 guts", name().toLowerCase()), RawMaterial.RESOURCE_MEAT));
            }
        }
        return resources;
    }

    @Override
    public String makeMobName(char gender, int age) {
        switch (age) {
            case Race.AGE_INFANT:
            case Race.AGE_TODDLER:
            case Race.AGE_CHILD:
                return "baby " + name().toLowerCase();
            default:
                return super.makeMobName('N', age);
        }
    }
}
