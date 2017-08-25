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
package com.syncleus.aethermud.game.Abilities.Druid;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.Vector;


public class Chant_GrowForest extends Chant {
    private final static String localizedName = CMLib.lang().L("Grow Forest");

    @Override
    public String ID() {
        return "Chant_GrowForest";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_PLANTGROWTH;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final int type = mob.location().domainType();
        if (((type & Room.INDOORS) > 0)
            || (type == Room.DOMAIN_OUTDOORS_AIR)
            || (type == Room.DOMAIN_OUTDOORS_CITY)
            || (type == Room.DOMAIN_OUTDOORS_SPACEPORT)
            || (type == Room.DOMAIN_OUTDOORS_UNDERWATER)
            || (type == Room.DOMAIN_OUTDOORS_WATERSURFACE)) {
            mob.tell(L("This magic won't work here."));
            return false;
        }

        int material = -1;
        final Vector<Integer> choices = new Vector<Integer>();
        final String s = CMParms.combine(commands, 0);

        final List<Integer> codes = RawMaterial.CODES.COMPOSE_RESOURCES(RawMaterial.MATERIAL_WOODEN);
        for (final Integer code : codes) {
            if (code.intValue() != RawMaterial.RESOURCE_WOOD) {
                choices.addElement(code);
                final String desc = RawMaterial.CODES.NAME(code.intValue());
                if ((s.length() > 0) && (CMLib.english().containsString(desc, s)))
                    material = code.intValue();
            }
        }
        if ((material < 0) && (s.length() > 0)) {
            mob.tell(L("'@x1' is not a recognized form of tree!", s));
            return false;
        }

        if ((material < 0) && (choices.size() > 0))
            material = choices.elementAt(CMLib.dice().roll(1, choices.size(), -1)).intValue();

        if (material < 0)
            return false;

        final String shortName = RawMaterial.CODES.NAME(material);

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        // now see if it worked
        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> chant(s) to the ground.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                mob.location().showHappens(CMMsg.MSG_OK_VISUAL, L("A grove of @x1 trees sprout up.", shortName.toLowerCase()));
                mob.location().setResource(material);
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> chant(s) to the ground, but nothing happens."));

        // return whether it worked
        return success;
    }
}
