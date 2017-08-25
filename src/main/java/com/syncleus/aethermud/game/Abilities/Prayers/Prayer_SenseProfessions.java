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
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;


public class Prayer_SenseProfessions extends Prayer {
    private final static String localizedName = CMLib.lang().L("Sense Professions");

    @Override
    public String ID() {
        return "Prayer_SenseProfessions";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_COMMUNING;
    }

    @Override
    public int enchantQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public long flags() {
        return Ability.FLAG_HOLY;
    }

    protected int senseWhat() {
        return ACODE_COMMON_SKILL;
    }

    protected String senseWhatStr() {
        return "professions";
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;
        if (target == mob) {
            mob.tell(L("You already know your own @x1!.", senseWhatStr()));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> @x1 and peer(s) at <T-NAMESELF>.^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final Vector<String> professionsV = new Vector<String>();
                for (final Enumeration<Ability> a = target.allAbilities(); a.hasMoreElements(); ) {
                    final Ability A = a.nextElement();
                    if ((A != null)
                        && ((A.classificationCode() & Ability.ALL_ACODES) == senseWhat()))
                        professionsV.addElement(A.name() + " (" + A.proficiency() + "%)");
                }
                if (professionsV.size() == 0)
                    mob.tell(mob, target, null, L("<T-NAME> seem(s) like <T-HE-SHE> has no @x1.", senseWhatStr()));
                else
                    mob.tell(mob, target, null, L("<T-NAME> seem(s) like <T-HE-SHE> understands the following @x1: @x2", senseWhatStr(), CMParms.toListString(professionsV)));
            }
        } else
            beneficialWordsFizzle(mob, target, auto ? "" : L("<S-NAME> @x1 and peer(s) at <T-NAMESELF>, but then blink(s).", prayWord(mob)));

        // return whether it worked
        return success;
    }
}
