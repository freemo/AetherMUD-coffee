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
package com.planet_ink.coffee_mud.Items.BasicTech;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMStrings;


public class GenDeflectionShield extends GenPersonalShield {
    public GenDeflectionShield() {
        super();
        setName("a deflection shield generator");
        setDisplayText("a deflection shield generator sits here.");
        setDescription("The deflection shield generator is worn about the body and activated to use. It deflects all manner of weapon types. ");
    }

    @Override
    public String ID() {
        return "GenDeflectionShield";
    }

    @Override
    protected String fieldOnStr(MOB viewerM) {
        return L((owner() instanceof MOB) ?
            "A deflectant field of energy surrounds <O-NAME>." :
            "A deflectant field of energy surrounds <T-NAME>.");
    }

    @Override
    protected String fieldDeadStr(MOB viewerM) {
        return L((owner() instanceof MOB) ?
            "The deflection field around <O-NAME> flickers and dies out." :
            "The deflection field around <T-NAME> flickers and dies out.");
    }

    @Override
    protected boolean doShield(MOB mob, CMMsg msg, double successFactor) {
        if (mob.location() != null) {
            if (msg.tool() instanceof Weapon) {
                final String s = "^F" + ((Weapon) msg.tool()).hitString(0) + "^N";
                if (s.indexOf("<DAMAGE> <T-HIM-HER>") > 0)
                    mob.location().show(msg.source(), msg.target(), msg.tool(), CMMsg.MSG_OK_VISUAL, CMStrings.replaceAll(s, "<DAMAGE>", L("it deflects off the shield around")));
                else if (s.indexOf("<DAMAGES> <T-HIM-HER>") > 0)
                    mob.location().show(msg.source(), msg.target(), msg.tool(), CMMsg.MSG_OK_VISUAL, CMStrings.replaceAll(s, "<DAMAGES>", L("deflects off the shield around")));
                else
                    mob.location().show(mob, msg.source(), msg.tool(), CMMsg.MSG_OK_VISUAL, L("The field around <S-NAME> deflects the <O-NAMENOART> damage."));
            } else
                mob.location().show(mob, msg.source(), msg.tool(), CMMsg.MSG_OK_VISUAL, L("The field around <S-NAME> deflects the <O-NAMENOART> damage."));
        }
        return false;
    }

    @Override
    protected boolean doesShield(MOB mob, CMMsg msg, double successFactor) {
        return activated() ? ((Math.random() >= successFactor)) : false;
    }
}
