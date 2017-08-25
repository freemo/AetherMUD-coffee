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
import com.planet_ink.game.core.CMFile;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.Resources;

import java.util.List;


public class Credits extends StdCommand {
    private final String[] access = I(new String[]{"CREDITS"});

    public Credits() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        StringBuffer credits = new CMFile(Resources.buildResourcePath("text") + "credits.txt", null, CMFile.FLAG_LOGERRORS).text();
        try {
            credits = CMLib.webMacroFilter().virtualPageFilter(credits);
        } catch (final Exception ex) {
        }
        if ((credits != null) && (mob.session() != null) && (credits.length() > 0))
            mob.session().colorOnlyPrintln(credits.toString());
        else
            mob.tell(L("CoffeeMud is (C)2000-2017 by Bo Zimmerman"));
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
