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
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharState;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.DeadBody;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Mold extends StdRace {
    private final static String localizedStaticName = CMLib.lang().L("Mold");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Vegetation");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0};
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();
    private final int[] agingChart = {0, 0, 0, 0, 0, YEARS_AGE_LIVES_FOREVER, YEARS_AGE_LIVES_FOREVER, YEARS_AGE_LIVES_FOREVER, YEARS_AGE_LIVES_FOREVER};

    @Override
    public String ID() {
        return "Mold";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int shortestMale() {
        return 1;
    }

    @Override
    public int shortestFemale() {
        return 1;
    }

    @Override
    public int heightVariance() {
        return 1;
    }

    @Override
    public int lightestWeight() {
        return 5;
    }

    @Override
    public int weightVariance() {
        return 1;
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
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_SPEAK | PhyStats.CAN_NOT_TASTE);
        affectableStats.setAttackAdjustment(affectableStats.attackAdjustment() + (2 * affected.phyStats().level()));
        affectableStats.setDamage(affectableStats.damage() + (affected.phyStats().level() / 2));
    }

    @Override
    public void affectCharState(MOB affectedMOB, CharState affectableState) {
        affectableState.setHitPoints(affectableState.getHitPoints() * 4);
        affectableState.setHunger((Integer.MAX_VALUE / 2) + 10);
        affectedMOB.curState().setHunger(affectableState.getHunger());
        affectedMOB.curState().setMana(0);
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        affectableStats.setStat(CharStats.STAT_GENDER, 'N');
        affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 1);
        affectableStats.setRacialStat(CharStats.STAT_WISDOM, 1);
        affectableStats.setRacialStat(CharStats.STAT_CHARISMA, 1);
        affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 1);
        affectableStats.setStat(CharStats.STAT_SAVE_POISON, affectableStats.getStat(CharStats.STAT_SAVE_POISON) + 100);
        affectableStats.setStat(CharStats.STAT_SAVE_COLD, affectableStats.getStat(CharStats.STAT_SAVE_COLD) - 100);
        affectableStats.setStat(CharStats.STAT_SAVE_MIND, affectableStats.getStat(CharStats.STAT_SAVE_MIND) + 100);
        affectableStats.setStat(CharStats.STAT_SAVE_GAS, affectableStats.getStat(CharStats.STAT_SAVE_GAS) + 100);
        affectableStats.setStat(CharStats.STAT_SAVE_PARALYSIS, affectableStats.getStat(CharStats.STAT_SAVE_PARALYSIS) + 100);
        affectableStats.setStat(CharStats.STAT_SAVE_UNDEAD, affectableStats.getStat(CharStats.STAT_SAVE_UNDEAD) + 100);
        affectableStats.setStat(CharStats.STAT_SAVE_DISEASE, affectableStats.getStat(CharStats.STAT_SAVE_DISEASE) + 100);

        affectableStats.setStat(CharStats.STAT_SAVE_PIERCE, affectableStats.getStat(CharStats.STAT_SAVE_PIERCE) + 95);
        affectableStats.setStat(CharStats.STAT_SAVE_SLASH, affectableStats.getStat(CharStats.STAT_SAVE_SLASH) + 95);
        affectableStats.setStat(CharStats.STAT_SAVE_BLUNT, affectableStats.getStat(CharStats.STAT_SAVE_BLUNT) + 95);
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
            naturalWeapon.setName(L("a moldy surface"));
            naturalWeapon.setRanges(0, 5);
            naturalWeapon.setUsesRemaining(1000);
            naturalWeapon.setMaterial(RawMaterial.RESOURCE_BARLEY);
            naturalWeapon.setWeaponDamageType(Weapon.TYPE_MELTING);
        }
        return naturalWeapon;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((myHost != null)
            && (myHost instanceof MOB)) {
            if (msg.amISource((MOB) myHost)) {
                if (((msg.targetMinor() == CMMsg.TYP_LEAVE)
                    || (msg.sourceMinor() == CMMsg.TYP_ADVANCE)
                    || (msg.sourceMinor() == CMMsg.TYP_RETREAT))) {
                    msg.source().tell(L("You can't really go anywhere -- you are a mold!"));
                    return false;
                }
            } else if (msg.amITarget(myHost) && (msg.targetMinor() == CMMsg.TYP_DAMAGE)) {
                switch (msg.sourceMinor()) {
                    case CMMsg.TYP_FIRE: {
                        ((MOB) myHost).curState().setHitPoints(((MOB) myHost).curState().getHitPoints() + msg.value());
                        msg.setValue(1);
                    }
                    break;
                    case CMMsg.TYP_COLD:
                        msg.setValue(msg.value() * 2);
                        break;
                }
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public String makeMobName(char gender, int age) {
        return makeMobName('N', Race.AGE_MATURE);
    }

    @Override
    public DeadBody getCorpseContainer(MOB mob, Room room) {
        final DeadBody body = super.getCorpseContainer(mob, room);
        if (body != null) {
            body.setMaterial(RawMaterial.RESOURCE_HERBS);
        }
        return body;
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                resources.addElement(makeResource
                    (L("a palm-full of @x1", name().toLowerCase()), RawMaterial.RESOURCE_HERBS));
            }
        }
        return resources;
    }

    @Override
    public String healthText(MOB viewer, MOB mob) {
        final double pct = (CMath.div(mob.curState().getHitPoints(), mob.maxState().getHitPoints()));

        if (pct < .10)
            return L("^r@x1^r is near destruction!^N", mob.name(viewer));
        else if (pct < .20)
            return L("^r@x1^r is massively scrapped and damaged.^N", mob.name(viewer));
        else if (pct < .30)
            return L("^r@x1^r is extremely scrapped and damaged.^N", mob.name(viewer));
        else if (pct < .40)
            return L("^y@x1^y is very scrapped and damaged.^N", mob.name(viewer));
        else if (pct < .50)
            return L("^y@x1^y is scrapped and damaged.^N", mob.name(viewer));
        else if (pct < .60)
            return L("^p@x1^p is scrapped and slightly damaged.^N", mob.name(viewer));
        else if (pct < .70)
            return L("^p@x1^p is showing numerous scrapes.^N", mob.name(viewer));
        else if (pct < .80)
            return L("^g@x1^g is showing some scrapes.^N", mob.name(viewer));
        else if (pct < .90)
            return L("^g@x1^g is showing small scrapes.^N", mob.name(viewer));
        else if (pct < .99)
            return L("^g@x1^g is no longer in perfect condition.^N", mob.name(viewer));
        else
            return L("^c@x1^c is in perfect condition.^N", mob.name(viewer));
    }
}

