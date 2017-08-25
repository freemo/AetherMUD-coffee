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
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMProps;


public class GenAbsorbantShield extends GenPersonalShield {
    public GenAbsorbantShield() {
        super();
        setName("an absorption shield generator");
        setDisplayText("an absorption shield generator sits here.");
        setDescription("The absorption shield generator is worn about the body and activated to use. It absorbs all manner of weapon types. ");
        setDescription("The integrity shield generator is worn about the body and activated to use. It protects against disruption and disintegration beams. ");
    }

    @Override
    public String ID() {
        return "GenAbsorbantShield";
    }

    @Override
    protected String fieldOnStr(MOB viewerM) {
        return L((owner() instanceof MOB) ?
            "A sparkling field of energy surrounds <O-NAME>." :
            "A sparkling field of energy surrounds <T-NAME>.");
    }

    @Override
    protected String fieldDeadStr(MOB viewerM) {
        return L((owner() instanceof MOB) ?
            "The sparkling field around <O-NAME> flickers and dies out." :
            "The sparkling field around <T-NAME> flickers and dies out.");
    }

    @Override
    protected boolean doShield(MOB mob, CMMsg msg, double successFactor) {
        if (msg.value() <= 0)
            return true;
        if ((successFactor >= 1.0) || ((successFactor > 0.0) && (msg.value() == 1))) {
            mob.location().show(mob, msg.source(), null, CMMsg.MSG_OK_VISUAL, L("The sparkling field around <S-NAME> completely absorbs the @x1 attack from <T-NAME>.", msg.tool().name()));
            msg.setValue(0);
        } else if (successFactor >= 0.0) {
            msg.setValue((int) Math.round(successFactor * msg.value()));
            final String showDamage = CMProps.getVar(CMProps.Str.SHOWDAMAGE).equalsIgnoreCase("YES") ? " (" + Math.round(successFactor * 100.0) + ")" : "";
            if (successFactor >= 0.75)
                msg.addTrailerMsg(CMClass.getMsg(mob, msg.source(), msg.tool(), CMMsg.MSG_OK_VISUAL, L("The sparkling field around <S-NAME> absorbs most@x1 of the <O-NAMENOART> damage.", showDamage)));
            else if (successFactor >= 0.50)
                msg.addTrailerMsg(CMClass.getMsg(mob, msg.source(), msg.tool(), CMMsg.MSG_OK_VISUAL, L("The sparkling field around <S-NAME> absorbs much@x1 of the <O-NAMENOART> damage.", showDamage)));
            else if (successFactor >= 0.25)
                msg.addTrailerMsg(CMClass.getMsg(mob, msg.source(), msg.tool(), CMMsg.MSG_OK_VISUAL, L("The sparkling field around <S-NAME> absorbs some@x1 of the <O-NAMENOART> damage.", showDamage)));
            else
                msg.addTrailerMsg(CMClass.getMsg(mob, msg.source(), msg.tool(), CMMsg.MSG_OK_VISUAL, L("The sparkling field around <S-NAME> absorbs a little@x1 of the <O-NAMENOART> damage.", showDamage)));
        }
        return true;
    }

    @Override
    protected boolean doesShield(MOB mob, CMMsg msg, double successFactor) {
        return activated();
    }
}
