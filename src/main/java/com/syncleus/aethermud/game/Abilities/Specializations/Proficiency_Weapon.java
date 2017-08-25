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
package com.syncleus.aethermud.game.Abilities.Specializations;

import com.syncleus.aethermud.game.Abilities.StdAbility;
import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.Libraries.interfaces.MaskingLibrary;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.collections.DoubleFilterer;
import com.syncleus.aethermud.game.core.interfaces.Environmental;

import java.util.Arrays;


public class Proficiency_Weapon extends StdAbility {
    private final static String localizedName = CMLib.lang().L("Weapon Proficiency");
    protected int weaponClass = -1;
    protected int secondWeaponClass = -1;
    protected String weaponZappermask = null;
    protected DoubleFilterer<Item>[] lastFilter = null;
    protected DoubleFilterer<Item>[] newFilter = null;
    protected DoubleFilterer<Item> myFilter = null;

    @Override
    public String ID() {
        return "Proficiency_Weapon";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
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
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_WEAPON_USE;
    }

    protected String getSpecificWeaponType() {
        return "Weapon";
    }

    protected String getWeaponMask() {
        if (weaponZappermask == null) {
            final StringBuilder str = new StringBuilder("-CLASSTYPE +" + getSpecificWeaponType() + " ");

            if (weaponClass >= 0)
                str.append("+WEAPONCLASS -" + Weapon.CLASS_DESCS[weaponClass] + " ");
            if (secondWeaponClass >= 0)
                str.append("+WEAPONCLASS -" + Weapon.CLASS_DESCS[secondWeaponClass] + " ");
            weaponZappermask = str.toString();
        }
        return weaponZappermask;
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        if (myFilter == null) {
            final String zapMask = getWeaponMask();
            myFilter = new com.syncleus.aethermud.game.core.collections.DoubleFilterer<Item>() {
                final MaskingLibrary.CompiledZMask mask = CMLib.masking().maskCompile(zapMask);

                @Override
                public com.syncleus.aethermud.game.core.collections.DoubleFilterer.Result getFilterResult(Item obj) {
                    return CMLib.masking().maskCheck(mask, obj, true) ? DoubleFilterer.Result.ALLOWED : DoubleFilterer.Result.NOTAPPLICABLE;
                }
            };
        }
        if ((proficiency() > 95) || (proficiencyCheck(affected, 0, false))) {
            if (affectableStats.getItemProficiencies() == lastFilter) {
                affectableStats.setItemProficiencies(newFilter);
            } else {
                lastFilter = affectableStats.getItemProficiencies();
                newFilter = Arrays.copyOf(lastFilter, lastFilter.length + 1);
                newFilter[newFilter.length - 1] = myFilter;
            }
        }
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if ((myFilter != null)
            && (affected instanceof MOB)
            && (msg.amISource((MOB) affected))
            && (CMLib.dice().rollPercentage() < 5)
            && ((msg.sourceMinor() == CMMsg.TYP_THROW) || (msg.sourceMinor() == CMMsg.TYP_WEAPONATTACK))
            && (msg.tool() instanceof Weapon)
            && (msg.target() instanceof MOB)
            && (myFilter.getFilterResult((Weapon) msg.tool()) == DoubleFilterer.Result.ALLOWED)
            )
            helpProficiency((MOB) affected, 0);
        super.executeMsg(myHost, msg);
    }
}
