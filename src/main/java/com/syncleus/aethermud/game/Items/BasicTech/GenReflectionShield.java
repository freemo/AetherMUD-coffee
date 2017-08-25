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
package com.syncleus.aethermud.game.Items.BasicTech;

import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Electronics;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMStrings;


public class GenReflectionShield extends GenPersonalShield {
    public GenReflectionShield() {
        super();
        setName("a reflection shield generator");
        setDisplayText("a reflection shield generator sits here.");
        setDescription("The reflection shield generator is worn about the body and activated to use. It protects against laser type weapons. ");
    }

    @Override
    public String ID() {
        return "GenReflectionShield";
    }

    @Override
    protected String fieldOnStr(MOB viewerM) {
        return L((owner() instanceof MOB) ?
            "A reflecting field of energy surrounds <O-NAME>." :
            "A reflecting field of energy surrounds <T-NAME>.");
    }

    @Override
    protected String fieldDeadStr(MOB viewerM) {
        return L((owner() instanceof MOB) ?
            "The reflecting field around <O-NAME> flickers and dies out." :
            "The reflecting field around <T-NAME> flickers and dies out.");
    }

    @Override
    protected boolean doShield(MOB mob, CMMsg msg, double successFactor) {
        if (mob.location() != null) {
            if (msg.tool() instanceof Weapon) {
                final String s = "^F" + ((Weapon) msg.tool()).hitString(0) + "^N";
                if (s.indexOf("<DAMAGE>") > 0)
                    mob.location().show(msg.source(), msg.target(), msg.tool(), CMMsg.MSG_OK_VISUAL, CMStrings.replaceAll(s, "<DAMAGE>", L("it reflects off the shield around")));
                else if (s.indexOf("<DAMAGES>") > 0)
                    mob.location().show(msg.source(), msg.target(), msg.tool(), CMMsg.MSG_OK_VISUAL, CMStrings.replaceAll(s, "<DAMAGES>", L("reflects off the shield around")));
                else
                    mob.location().show(mob, msg.source(), msg.tool(), CMMsg.MSG_OK_VISUAL, L("The field around <S-NAME> reflects the <O-NAMENOART> damage."));
            } else
                mob.location().show(mob, msg.source(), msg.tool(), CMMsg.MSG_OK_VISUAL, L("The field around <S-NAME> reflects the <O-NAMENOART> damage."));
        }
        return false;
    }

    @Override
    protected boolean doesShield(MOB mob, CMMsg msg, double successFactor) {
        if (!activated())
            return false;
        if ((msg.tool() instanceof Electronics)
            && (msg.tool() instanceof Weapon)
            && (Math.random() >= successFactor)
            && (((Weapon) msg.tool()).weaponDamageType() == Weapon.TYPE_LASERING)) {
            return true;
        }
        return false;
    }
}
