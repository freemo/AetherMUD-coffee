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
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;

import java.util.List;
import java.util.Vector;


public class Buffalo extends Cow {
    private final static String localizedStaticName = CMLib.lang().L("Buffalo");
    private final static String localizedStaticRacialCat = CMLib.lang().L("Bovine");
    //  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
    private static final int[] parts = {0, 2, 2, 1, 1, 0, 0, 1, 4, 4, 1, 0, 1, 1, 1, 0};
    protected static Vector<RawMaterial> resources = new Vector<RawMaterial>();

    @Override
    public String ID() {
        return "Buffalo";
    }

    @Override
    public String name() {
        return localizedStaticName;
    }

    @Override
    public int shortestMale() {
        return 48;
    }

    @Override
    public int shortestFemale() {
        return 48;
    }

    @Override
    public int heightVariance() {
        return 6;
    }

    @Override
    public int lightestWeight() {
        return 350;
    }

    @Override
    public int weightVariance() {
        return 100;
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
    public Weapon myNaturalWeapon() {
        if (naturalWeapon == null) {
            naturalWeapon = CMClass.getWeapon("StdWeapon");
            naturalWeapon.setName(L("a pair of deadly hoofs"));
            naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
            naturalWeapon.setUsesRemaining(1000);
            naturalWeapon.setWeaponDamageType(Weapon.TYPE_BASHING);
        }
        return naturalWeapon;
    }

    @Override
    public void affectCharStats(MOB affectedMOB, CharStats affectableStats) {
        super.affectCharStats(affectedMOB, affectableStats);
        affectableStats.setRacialStat(CharStats.STAT_STRENGTH, 17);
        affectableStats.setRacialStat(CharStats.STAT_DEXTERITY, 5);
        affectableStats.setRacialStat(CharStats.STAT_INTELLIGENCE, 1);
    }

    @Override
    public Race mixRace(Race race, String newRaceID, String newRaceName) {
        if (ID().equalsIgnoreCase("Buffalo")) {
            if ((race != null) && (race.ID().equalsIgnoreCase("Buffalo")))
                return CMClass.getRace("Buffalo");
            if ((race != null) && (race.ID().equalsIgnoreCase("Cow"))) {
                if (ID().equals("Buffalo"))
                    return CMClass.getRace("Buffalo");
                return CMClass.getRace("Cow");
            }
        }
        return super.mixRace(race, newRaceID, newRaceName);
    }

    @Override
    public String makeMobName(char gender, int age) {
        switch (age) {
            case Race.AGE_INFANT:
            case Race.AGE_TODDLER:
            case Race.AGE_CHILD:
                return name().toLowerCase() + " calf";
            case Race.AGE_YOUNGADULT: {
                switch (gender) {
                    case 'M':
                    case 'm':
                        return "young " + name().toLowerCase() + " bull";
                    case 'F':
                    case 'f':
                        return "young " + name().toLowerCase() + " cow";
                    default:
                        return name().toLowerCase();
                }
            }
            case Race.AGE_MATURE:
            case Race.AGE_MIDDLEAGED:
            default: {
                switch (gender) {
                    case 'M':
                    case 'm':
                        return name().toLowerCase() + " bull";
                    case 'F':
                    case 'f':
                        return name().toLowerCase() + " cow";
                    default:
                        return name().toLowerCase();
                }
            }
            case Race.AGE_OLD:
            case Race.AGE_VENERABLE:
            case Race.AGE_ANCIENT: {
                switch (gender) {
                    case 'M':
                    case 'm':
                        return "old " + name().toLowerCase() + " bull";
                    case 'F':
                    case 'f':
                        return "old " + name().toLowerCase() + " cow";
                    default:
                        return "old " + name().toLowerCase();
                }
            }
        }
    }

    @Override
    public List<RawMaterial> myResources() {
        synchronized (resources) {
            if (resources.size() == 0) {
                for (int i = 0; i < 10; i++) {
                    resources.addElement(makeResource
                        (L("a strip of @x1 hide", name().toLowerCase()), RawMaterial.RESOURCE_FUR));
                }
                for (int i = 0; i < 5; i++) {
                    resources.addElement(makeResource
                        (L("some @x1 meat", name().toLowerCase()), RawMaterial.RESOURCE_BEEF));
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
