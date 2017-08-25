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
package com.syncleus.aethermud.game.Abilities.Prayers;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;


public class Prayer_DoomAura extends Prayer_BladeBarrier {
    private final static String localizedName = CMLib.lang().L("Doom Aura");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Doom Aura)");

    @Override
    public String ID() {
        return "Prayer_DoomAura";
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
        return Ability.ACODE_PRAYER | Ability.DOMAIN_CORRUPTION;
    }

    @Override
    public long flags() {
        return Ability.FLAG_UNHOLY;
    }

    @Override
    protected String startStr() {
        return "An aura of doom appears around <T-NAME>!^?";
    }

    @Override
    protected void doDamage(MOB srcM, MOB targetM, int damage) {
        CMLib.combat().postDamage(srcM, targetM, this, damage, CMMsg.TYP_UNDEAD | CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS, Weapon.TYPE_BURNING, L("The aura of doom around <S-NAME> <DAMAGE> <T-NAME>."));
    }
}
