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
package com.planet_ink.game.Commands;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.collections.XVector;

import java.util.List;


public class Prayers extends Skills {
    private final String[] access = I(new String[]{"PRAYERS"});

    public Prayers() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        final StringBuffer msg = new StringBuffer("");
        final String qual = CMParms.combine(commands, 1).toUpperCase();
        if (parsedOutIndividualSkill(mob, qual, Ability.ACODE_PRAYER))
            return true;
        final int[] level = new int[1];
        final int[] domain = new int[1];
        final String[] domainName = new String[1];
        domainName[0] = "";
        level[0] = -1;
        parseDomainInfo(mob, commands, new XVector<Integer>(Integer.valueOf(Ability.ACODE_PRAYER)), level, domain, domainName);
        msg.append(L("\n\r^HYour @x1prayers:^? @x2", domainName[0].replace('_', ' '), getAbilities(mob, mob, Ability.ACODE_PRAYER, domain[0], true, level[0]).toString()));
        if (!mob.isMonster())
            mob.session().wraplessPrintln(msg.toString());
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
