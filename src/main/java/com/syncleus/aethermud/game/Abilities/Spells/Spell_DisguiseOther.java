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
package com.planet_ink.game.Abilities.Spells;

import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.collections.XVector;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Spell_DisguiseOther extends Spell_DisguiseSelf {

    private final static String localizedName = CMLib.lang().L("Disguise Other");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Disguise Other)");

    @Override
    public String ID() {
        return "Spell_DisguiseOther";
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
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (commands.size() < 2) {
            mob.tell(L("Disguise whom as whom?"));
            return false;
        }
        String whomName = commands.get(0);
        MOB target = super.getTarget(mob, new XVector<String>(whomName), givenTarget);
        if (target == null)
            return false;
        if (target == mob) {
            mob.tell(L("You can't disguise yourself with this magic."));
            return false;
        }
        commands.remove(0);
        return super.invoke(mob, commands, target, auto, asLevel);
    }
}
