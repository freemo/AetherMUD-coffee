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
package com.syncleus.aethermud.game.Commands;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.collections.XVector;

import java.util.List;


@SuppressWarnings({"unchecked", "rawtypes"})
public class Spells extends Skills {
    private final String[] access = I(new String[]{"SPELLS", "SP"});

    public Spells() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        final String qual = CMParms.combine(commands, 1).toUpperCase();
        if (parsedOutIndividualSkill(mob, qual, Ability.ACODE_SPELL))
            return true;
        final int[] level = new int[1];
        final int[] domain = new int[1];
        final String[] domainName = new String[1];
        domainName[0] = "";
        level[0] = -1;
        parseDomainInfo(mob, commands, new XVector(Integer.valueOf(Ability.ACODE_SPELL)), level, domain, domainName);
        final StringBuffer msg = new StringBuffer("");
        msg.append(L("\n\r^HYour @x1spells:^? @x2", domainName[0].replace('_', ' '), getAbilities(mob, mob, Ability.ACODE_SPELL, domain[0], true, level[0]).toString()));
        if (!mob.isMonster())
            mob.session().wraplessPrintln(msg.toString() + "\n\r");
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
