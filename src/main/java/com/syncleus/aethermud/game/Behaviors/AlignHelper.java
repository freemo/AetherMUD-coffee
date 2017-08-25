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
package com.planet_ink.game.Behaviors;

import com.planet_ink.game.Abilities.interfaces.DiseaseAffect;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.Environmental;


public class AlignHelper extends StdBehavior {
    @Override
    public String ID() {
        return "AlignHelper";
    }

    @Override
    public String accountForYourself() {
        return "same-aligned protecting";
    }

    @Override
    public void executeMsg(Environmental affecting, CMMsg msg) {
        super.executeMsg(affecting, msg);
        if ((msg.target() == null) || (!(msg.target() instanceof MOB)))
            return;
        final MOB source = msg.source();
        final MOB observer = (MOB) affecting;
        final MOB target = (MOB) msg.target();

        if ((source != observer)
            && (target != observer)
            && (source != target)
            && (CMath.bset(msg.targetMajor(), CMMsg.MASK_MALICIOUS))
            && (!observer.isInCombat())
            && (CMLib.flags().canBeSeenBy(source, observer))
            && (CMLib.flags().canBeSeenBy(target, observer))
            && (!BrotherHelper.isBrother(source, observer, false))
            && ((!(msg.tool() instanceof DiseaseAffect)) || (((DiseaseAffect) msg.tool()).isMalicious()))
            && ((CMLib.flags().isEvil(target) && CMLib.flags().isEvil(observer))
            || (CMLib.flags().isNeutral(target) && CMLib.flags().isNeutral(observer))
            || (CMLib.flags().isGood(target) && CMLib.flags().isGood(observer)))) {
            Aggressive.startFight(observer, source, true, false, CMLib.flags().getAlignmentName(observer) + " PEOPLE UNITE! CHARGE!");
        }
    }
}
