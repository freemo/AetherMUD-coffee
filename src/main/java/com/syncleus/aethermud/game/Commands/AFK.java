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

import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMParms;

import java.util.List;


public class AFK extends StdCommand {
    private final String[] access = I(new String[]{"AFK"});

    public AFK() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        if (mob.session() == null)
            return false;
        if (mob.session().isAfk())
            mob.session().setAfkFlag(false);
        else {
            mob.session().setAFKMessage(CMParms.combine(commands, 1));
            mob.session().setAfkFlag(true);
        }
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
