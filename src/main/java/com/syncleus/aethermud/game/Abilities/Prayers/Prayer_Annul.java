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
package com.syncleus.aethermud.game.Abilities.Prayers;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.AetherTableRow;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.Libraries.interfaces.ChannelsLibrary;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_Annul extends Prayer {
    private final static String localizedName = CMLib.lang().L("Annul");

    @Override
    public String ID() {
        return "Prayer_Annul";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_NEUTRALIZATION;
    }

    @Override
    public long flags() {
        return Ability.FLAG_NEUTRAL;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_OTHERS;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;
        if (!target.isMarriedToLiege()) {
            mob.tell(L("@x1 is not married!", target.name(mob)));
            return false;
        }
        if (target.fetchItem(null, Wearable.FILTER_WORNONLY, "wedding band") != null) {
            mob.tell(L("@x1 must remove the wedding band first.", target.name(mob)));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> annul(s) the marriage between <T-NAMESELF> and @x1.^?", target.getLiegeID()));
            if (mob.location().okMessage(mob, msg)) {
                if ((!target.isMonster()) && (target.soulMate() == null))
                    CMLib.aetherTables().bump(target, AetherTableRow.STAT_DIVORCES);
                mob.location().send(mob, msg);
                final List<String> channels = CMLib.channels().getFlaggedChannelNames(ChannelsLibrary.ChannelFlag.DIVORCES);
                for (int i = 0; i < channels.size(); i++)
                    CMLib.commands().postChannel(channels.get(i), mob.clans(), L("@x1 and @x2 just had their marriage annulled.", target.name(), target.getLiegeID()), true);
                final MOB M = CMLib.players().getPlayer(target.getLiegeID());
                if (M != null)
                    M.setLiegeID("");
                target.setLiegeID("");
            }
        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> clear(s) <S-HIS-HER> throat."));

        return success;
    }
}
