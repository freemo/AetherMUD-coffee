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
package com.syncleus.aethermud.game.Libraries.interfaces;

import com.syncleus.aethermud.game.Common.interfaces.Poll;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;

import java.util.Iterator;
import java.util.List;

public interface PollManager extends CMLibrary {
    public void addPoll(Poll P);

    public void removePoll(Poll P);

    public Poll getPoll(String named);

    public Poll getPoll(int x);

    public List<Poll>[] getMyPollTypes(MOB mob, boolean login);

    public Iterator<Poll> getPollList();

    public void processVote(Poll P, MOB mob);

    public void modifyVote(Poll P, MOB mob) throws java.io.IOException;

    public void processResults(Poll P, MOB mob);

    public void createPoll(Poll P);

    public void updatePollResults(Poll P);

    public void updatePoll(String oldName, Poll P);

    public void deletePoll(Poll P);

    public Poll loadPollByName(String name);

    public boolean loadPollIfNecessary(Poll P);
}
