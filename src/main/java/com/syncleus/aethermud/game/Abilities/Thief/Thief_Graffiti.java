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
package com.syncleus.aethermud.game.Abilities.Thief;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Thief_Graffiti extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Graffiti");
    private static final String[] triggerStrings = I(new String[]{"GRAFFITI"});

    @Override
    public String ID() {
        return "Thief_Graffiti";
    }

    @Override
    public String name() {
        return localizedName;
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
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_STREETSMARTS;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final String str = CMParms.combine(commands, 0);
        if (str.length() == 0) {
            mob.tell(L("What would you like to write here?"));
            return false;
        }
        Room target = mob.location();
        if ((auto) && (givenTarget != null) && (givenTarget instanceof Room))
            target = (Room) givenTarget;

        if ((mob.location().domainType() != Room.DOMAIN_OUTDOORS_CITY)
            && (mob.location().domainType() != Room.DOMAIN_INDOORS_WOOD)
            && (mob.location().domainType() != Room.DOMAIN_INDOORS_STONE)) {
            mob.tell(L("You can't put graffiti here."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        int levelDiff = target.phyStats().level() - (mob.phyStats().level() + abilityCode() + (2 * getXLEVELLevel(mob)));
        if (levelDiff < 0)
            levelDiff = 0;
        levelDiff *= 5;
        final boolean success = proficiencyCheck(mob, -levelDiff, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_DELICATE_SMALL_HANDS_ACT, L("<S-NAME> write(s) graffiti here."));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final Item I = CMClass.getItem("GenWallpaper");
                I.setName(L("Graffiti"));
                CMLib.flags().setReadable(I, true);
                I.recoverPhyStats();
                I.setReadableText(str);
                switch (CMLib.dice().roll(1, 6, 0)) {
                    case 1:
                        I.setDescription(L("Someone has scribbed some graffiti here.  Try reading it."));
                        break;
                    case 2:
                        I.setDescription(L("A cryptic message has been written on the walls.  Try reading it."));
                        break;
                    case 3:
                        I.setDescription(L("Someone wrote a message here to read."));
                        break;
                    case 4:
                        I.setDescription(L("A strange message is written here.  Read it."));
                        break;
                    case 5:
                        I.setDescription(L("This graffiti looks like it is in @x1 handwriting.  Read it!", mob.name()));
                        break;
                    case 6:
                        I.setDescription(L("The wall is covered in graffiti.  You might want to read it."));
                        break;
                }
                mob.location().addItem(I);
                I.recoverPhyStats();
                mob.location().recoverRoomStats();
            }
        } else
            beneficialVisualFizzle(mob, target, L("<S-NAME> attempt(s) to write graffiti here, but fails."));
        return success;
    }
}
