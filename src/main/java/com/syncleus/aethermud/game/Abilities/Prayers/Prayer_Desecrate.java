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
package com.planet_ink.game.Abilities.Prayers;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.DeadBody;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_Desecrate extends Prayer {
    private final static String localizedName = CMLib.lang().L("Desecrate");

    @Override
    public String ID() {
        return "Prayer_Desecrate";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_DEATHLORE;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public long flags() {
        return Ability.FLAG_UNHOLY;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_ITEMS;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        Item target = null;
        if ((commands.size() == 0) && (!auto) && (givenTarget == null))
            target = Prayer_Sacrifice.getBody(mob.location());
        if (target == null)
            target = getTarget(mob, mob.location(), givenTarget, commands, Wearable.FILTER_UNWORNONLY);
        if (target == null)
            return false;

        if ((!(target instanceof DeadBody))
            || (target.rawSecretIdentity().toUpperCase().indexOf("FAKE") >= 0)) {
            mob.tell(L("You may only desecrate the dead."));
            return false;
        }
        if ((((DeadBody) target).isPlayerCorpse())
            && (!((DeadBody) target).getMobName().equals(mob.Name()))
            && (((DeadBody) target).hasContent())) {
            mob.tell(L("You are not allowed to desecrate a players corpse."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> feel(s) desecrated!") : L("^S<S-NAME> desecrate(s) <T-NAMESELF> before @x1.^?", hisHerDiety(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (CMLib.flags().isEvil(mob)) {
                    double exp = 5.0;
                    final int levelLimit = CMProps.getIntVar(CMProps.Int.EXPRATE);
                    final int levelDiff = (mob.phyStats().level()) - target.phyStats().level();
                    if (levelDiff > levelLimit)
                        exp = 0.0;
                    if (exp > 0.0)
                        CMLib.leveler().postExperience(mob, null, null, (int) Math.round(exp) + super.getXPCOSTLevel(mob), false);
                }
                target.destroy();
                mob.location().recoverRoomStats();
            }
        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> attempt(s) to desecrate <T-NAMESELF>, but fail(s)."));

        // return whether it worked
        return success;
    }
}
