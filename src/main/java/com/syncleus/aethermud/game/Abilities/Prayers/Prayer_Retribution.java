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
package com.planet_ink.game.Abilities.Prayers;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.Weapon;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;


public class Prayer_Retribution extends Prayer_BladeBarrier {
    private final static String localizedName = CMLib.lang().L("Retribution");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Retribution)");

    @Override
    public String ID() {
        return "Prayer_Retribution";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_HOLYPROTECTION;
    }

    @Override
    public long flags() {
        return Ability.FLAG_NEUTRAL;
    }

    @Override
    protected String startStr() {
        return "The power of retribution fills <T-NAME>!^?";
    }

    @Override
    protected void doDamage(MOB srcM, MOB targetM, int damage) {
        CMLib.combat().postDamage(srcM, targetM, this, damage, CMMsg.TYP_ELECTRIC | CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS, Weapon.TYPE_STRIKING, L("A bolt of retribution from <S-NAME> <DAMAGE> <T-NAME>."));
    }
}
