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
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.HashSet;
import java.util.List;


public class Prayer_Designation extends Prayer {
    private final static String localizedName = CMLib.lang().L("Designation");

    @Override
    public String ID() {
        return "Prayer_Designation";
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
        return Ability.QUALITY_OK_OTHERS;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_UNHOLY;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectedStats) {
        super.affectPhyStats(affected, affectedStats);
        if ((affected instanceof MOB)
            && (((MOB) affected).amFollowing() == null)
            && (CMLib.flags().isInTheGame((MOB) affected, true))) {
            affected.delEffect(affected.fetchEffect(ID()));
            affectedStats.setName(null);
        } else if ((text().length() > 0))
            affectedStats.setName(text());
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (commands.size() < 2) {
            mob.tell(L("You must specify the undead, and a designation to give him."));
            return false;
        }
        String myName = (commands.get(commands.size() - 1)).trim();
        commands.remove(commands.size() - 1);
        if (myName.length() == 0) {
            mob.tell(L("You must specify a name."));
            return false;
        }
        if (myName.indexOf(' ') >= 0) {
            mob.tell(L("Your name may not contain a space."));
            return false;
        }

        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;
        if ((!CMLib.flags().isUndead(target))
            || (!target.isMonster())
            || (!mob.getGroupMembers(new HashSet<MOB>()).contains(target))) {
            mob.tell(L("This prayer only works on non-player undead in your group."));
            return false;
        }

        String baseName = myName;
        if ((target.name().toUpperCase().startsWith("A "))
            || (target.name().toUpperCase().startsWith("AN "))
            || (target.name().toUpperCase().startsWith("SOME ")))
            myName = target.name() + " named " + myName;
        else if (target.name().indexOf(' ') >= 0)
            myName = myName + ", " + target.name();

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> pray(s) at <T-NAMESELF>, granting the designation '@x2'.^?", super.prayWord(mob), baseName));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final Prayer_Designation A = (Prayer_Designation) copyOf();
                A.setMiscText(myName);
                target.addNonUninvokableEffect(A);
                mob.location().recoverRoomStats();
            }
        } else
            return beneficialWordsFizzle(mob, target, L("<S-NAME> pray(s) at <T-NAMESELF>, but nothing happens."));

        // return whether it worked
        return success;
    }
}