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

import com.planet_ink.game.Common.interfaces.Poll;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;

import java.util.List;


public class PollCmd extends StdCommand {
    private final String[] access = I(new String[]{"POLL"});

    public PollCmd() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        if ((mob == null) || mob.isMonster())
            return false;
        final java.util.List<Poll>[] mypolls = CMLib.polls().getMyPollTypes(mob, (commands == null));

        if ((mypolls[0].size() == 0) && (mypolls[2].size() == 0)) {
            if ((commands != null) && (mypolls[1].size() == 0)) {
                mob.tell(L("No polls are available at this time."));
                return false;
            } else if (commands == null) {
                if (mypolls[1].size() > 0)
                    mob.tell(L("@x1 poll(s) <-IS-ARE> awaiting your participation.", "" + mypolls[1].size()));
                return false;
            }
        }

        for (final Poll P : mypolls[0]) {
            CMLib.polls().processVote(P, mob);
            if (P.mayISeeResults(mob)) {
                CMLib.polls().processResults(P, mob);
                mob.session().prompt(L("Press ENTER to continue:\n\r"));
            }
        }
        if (commands == null) {
            if (mypolls[1].size() == 1)
                mob.tell(L("\n\r^H@x1 other poll(s) <-IS-ARE> awaiting your participation.^N\n\r", "" + mypolls[1].size()));
            if (mypolls[2].size() > 0)
                mob.tell(L("\n\r^HResults from @x1 poll(s) <-IS-ARE> still available.^N\n\r", "" + mypolls[2].size()));
            return true;
        }
        for (final Poll P : mypolls[1]) {
            CMLib.polls().processVote(P, mob);
            if (P.mayISeeResults(mob)) {
                CMLib.polls().processResults(P, mob);
                mob.session().prompt(L("Press ENTER to continue:"));
            }
        }

        if (mypolls[2].size() > 0)
            mob.tell(L("\n\r^HPrevious polling results:^N\n\r"));
        int i = 0;
        for (final Poll P : mypolls[2]) {
            if (P.mayISeeResults(mob)) {
                CMLib.polls().processResults(P, mob);
                if (i < mypolls[2].size() - 1)
                    mob.session().prompt(L("Press ENTER to continue:\n\r"));
            }
            i++;
        }
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return false;
    }

}
