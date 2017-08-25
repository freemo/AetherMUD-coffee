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

import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.CMath;

import java.util.List;


public class PageBreak extends StdCommand {
    private final String[] access = I(new String[]{"PAGEBREAK"});

    public PageBreak() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        if ((mob == null) || (mob.playerStats() == null))
            return false;

        if (commands.size() < 2) {
            final String pageBreak = (mob.playerStats().getPageBreak() != 0) ? ("" + mob.playerStats().getPageBreak()) : "Disabled";
            mob.tell(L("Change your page break to what? Your current page break setting is: @x1. Enter a number larger than 0 or 'disable'.", pageBreak));
            return false;
        }
        final String newBreak = CMParms.combine(commands, 1);
        int newVal = mob.playerStats().getWrap();
        if ((CMath.isInteger(newBreak)) && (CMath.s_int(newBreak) > 0))
            newVal = CMath.s_int(newBreak);
        else if ("DISABLED".startsWith(newBreak.toUpperCase()))
            newVal = 0;
        else {
            mob.tell(L("'@x1' is not a valid setting. Enter a number larger than 0 or 'disable'.", newBreak));
            return false;
        }
        mob.playerStats().setPageBreak(newVal);
        final String pageBreak = (mob.playerStats().getPageBreak() != 0) ? ("" + mob.playerStats().getPageBreak()) : "Disabled";
        mob.tell(L("Your new page break setting is: @x1.", pageBreak));
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}

