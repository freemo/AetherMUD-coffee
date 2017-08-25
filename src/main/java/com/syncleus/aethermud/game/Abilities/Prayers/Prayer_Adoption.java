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
import com.syncleus.aethermud.game.Common.interfaces.Tattoo;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.io.IOException;
import java.util.List;


public class Prayer_Adoption extends Prayer {
    private final static String localizedName = CMLib.lang().L("Adoption");

    @Override
    public String ID() {
        return "Prayer_Adoption";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public long flags() {
        return Ability.FLAG_HOLY | Ability.FLAG_UNHOLY;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_OTHERS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_BLESSING;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Room R = mob.location();
        if (R == null)
            return false;
        if (commands.size() < 2) {
            mob.tell(L("Who is adopting whom?"));
            return false;
        }
        final String name2 = commands.get(commands.size() - 1);
        final String name1 = CMParms.combine(commands, 0, commands.size() - 1);
        MOB parent = R.fetchInhabitant(name1);
        if ((parent == null) || (!CMLib.flags().canBeSeenBy(mob, parent))) {
            mob.tell(L("You don't see @x1 here!", name1));
            return false;
        }
        final MOB child = R.fetchInhabitant(name2);

        if ((child == null) || (!CMLib.flags().canBeSeenBy(mob, child))) {
            mob.tell(L("You don't see @x1 here!", name2));
            return false;
        }
        if (child == parent) {
            mob.tell(L("@x1 cannot be adopted by @x2!", child.Name(), parent.Name()));
        }
        if ((child.isMonster()) || (child.playerStats() == null)) {
            mob.tell(L("@x1 must be a player to be adopted.", child.name()));
            return false;
        }
        if ((parent.isMonster()) || (parent.playerStats() == null)) {
            mob.tell(L("@x1 must be a player to adopt someone.", parent.name()));
            return false;
        }

        Tattoo tattChk = child.findTattoo("PARENT:");
        if (tattChk != null) {
            mob.tell(L("@x1 already has parents.", child.name()));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            try {
                if (!child.session().confirm(L("@x1 wants to adopt you.  Is this OK (y/N)?", parent.name()), "N", 5000))
                    success = false;
                if (!parent.session().confirm(L("@x1 wants you to adopt @x2.  Is this OK (y/N)?", child.name(), child.charStats().himher()), "N", 5000))
                    success = false;
            } catch (IOException e) {
                success = false;
            }
        }
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> @x1 to bless the adoption of @x3 by @x2.^?", prayForWord(mob), parent.name(), child.name()));
            if (R.okMessage(mob, msg)) {
                R.send(mob, msg);
                child.addTattoo("PARENT:" + parent.Name());
            }
        } else
            beneficialWordsFizzle(mob, null, L("<S-NAME> can not bless this adoption."));

        return success;
    }
}
