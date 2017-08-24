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
package com.planet_ink.coffee_mud.Abilities.Songs;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;


public class Song_SingleMindedness extends Song {
    private final static String localizedName = CMLib.lang().L("Single Mindedness");
    protected CMMsg themsg = null;

    @Override
    public String ID() {
        return "Song_SingleMindedness";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    protected boolean HAS_QUANTITATIVE_ASPECT() {
        return false;
    }

    @Override
    public void executeMsg(Environmental ticking, CMMsg msg) {
        super.executeMsg(ticking, msg);
        if ((themsg == null)
            && (msg.source() != invoker())
            && (msg.source() == affected)
            && (msg.sourceMessage() != null)
            && (msg.sourceMessage().length() > 0)
            && (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS)))
            themsg = msg;
    }

    @Override
    public boolean okMessage(Environmental ticking, CMMsg msg) {
        if ((themsg != null)
            && (msg.source() != invoker())
            && (msg.source() == affected)
            && (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS))
            && (themsg.sourceMinor() != msg.sourceMinor())) {
            msg.source().tell(msg.source(), null, null, L("The only thing you have a mind to do is '@x1'.", themsg.sourceMessage()));
            return false;
        }
        return super.okMessage(ticking, msg);
    }
}
