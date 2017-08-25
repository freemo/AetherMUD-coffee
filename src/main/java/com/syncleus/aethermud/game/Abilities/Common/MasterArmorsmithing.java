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
package com.planet_ink.game.Abilities.Common;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Abilities.interfaces.ItemCraftor;
import com.planet_ink.game.Abilities.interfaces.TriggeredAffect;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class MasterArmorsmithing extends Armorsmithing implements ItemCraftor {
    private final static String localizedName = CMLib.lang().L("Master Armorsmithing");
    private static final String[] triggerStrings = I(new String[]{"MARMORSMITH", "MASTERARMORSMITHING"});

    @Override
    public String ID() {
        return "MasterArmorsmithing";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public String parametersFile() {
        return "masterarmorsmith.txt";
    }

    @Override
    protected List<List<String>> loadRecipes() {
        return super.loadRecipes(parametersFile());
    }

    @Override
    protected boolean masterCraftCheck(final Item I) {
        if (I.basePhyStats().level() < 30) {
            Ability A;
            for (int i = 0; i < I.numEffects(); i++) {
                A = I.fetchEffect(i);
                if (A instanceof TriggeredAffect) {
                    final long flags = A.flags();
                    final int triggers = ((TriggeredAffect) A).triggerMask();
                    if (CMath.bset(flags, Ability.FLAG_ADJUSTER)
                        && CMath.bset(triggers, TriggeredAffect.TRIGGER_WEAR_WIELD))
                        return false;
                }
            }
        }
        return true;
    }

    @Override
    protected boolean autoGenInvoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel, int autoGenerate, boolean forceLevels, List<Item> crafted) {
        if (super.checkStop(mob, commands))
            return true;

        if (super.checkInfo(mob, commands))
            return true;

        randomRecipeFix(mob, addRecipes(mob, loadRecipes()), commands, autoGenerate);
        if (commands.size() == 0) {
            commonTell(mob, L("Make what? Enter \"marmorsmith list\" for a list, \"marmorsmith info <item>\", \"marmorsmith scan\", "
                + "\"marmorsmith learn <item>\", \"marmorsmith mend <item>\", or \"marmorsmith stop\" to cancel."));
            return false;
        }
        return super.autoGenInvoke(mob, commands, givenTarget, auto, asLevel, autoGenerate, forceLevels, crafted);
    }
}
