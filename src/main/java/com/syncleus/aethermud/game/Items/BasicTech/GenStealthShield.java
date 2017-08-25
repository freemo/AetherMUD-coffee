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
package com.planet_ink.game.Items.BasicTech;

import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Items.interfaces.Technical;
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;


public class GenStealthShield extends GenTickerShield {

    public GenStealthShield() {
        super();
        setName("a personal stealth generator");
        setDisplayText("a personal stealth generator sits here.");
        setDescription("");
    }

    @Override
    public String ID() {
        return "GenStealthShield";
    }

    @Override
    protected String fieldOnStr(MOB viewerM) {
        return L((owner() instanceof MOB) ?
            "A stealth field surrounds <O-NAME>." :
            "A stealth field surrounds <T-NAME>.");
    }

    @Override
    protected String fieldDeadStr(MOB viewerM) {
        return L((owner() instanceof MOB) ?
            "The stealth field around <O-NAME> flickers and dies out as <O-HE-SHE> fade(s) back into view." :
            "The stealth field around <T-NAME> flickers and dies out as <T-HE-SHE> fade(s) back into view.");
    }

    @Override
    public void affectPhyStats(final Physical affected, final PhyStats affectableStats) {
        if (activated() && (affected == owner()) && (owner() instanceof MOB) && (!amWearingAt(Wearable.IN_INVENTORY)) && (powerRemaining() > 0))
            affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_INVISIBLE);
        super.affectPhyStats(affected, affectableStats);
    }

    @Override
    public boolean okMessage(Environmental myHost, CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;
        if (msg.amITarget(owner()) && (owner() instanceof MOB) && (!amWearingAt(Wearable.IN_INVENTORY))) {
            if ((msg.targetMinor() == CMMsg.TYP_LOOK) && (msg.source() != owner())) {
                if ((msg.tool() instanceof Technical) && (CMath.bset(msg.targetMajor(), CMMsg.MASK_CNTRLMSG))) {
                    if (((Technical) msg.tool()).techLevel() > techLevel())
                        return true;
                    return false;
                }
            }
        }
        return true;
    }

}
