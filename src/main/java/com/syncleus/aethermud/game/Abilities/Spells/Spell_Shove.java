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
package com.syncleus.aethermud.game.Abilities.Spells;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.Directions;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Spell_Shove extends Spell {

    private final static String localizedName = CMLib.lang().L("Shove");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Shoved Down)");
    public boolean doneTicking = false;

    @Override
    public String ID() {
        return "Spell_Shove";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    public int maxRange() {
        return adjustedMaxInvokerRange(4);
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return Tickable.TICKID_MOB;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_EVOCATION;
    }

    @Override
    public long flags() {
        return Ability.FLAG_MOVING;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        int dir = -1;
        if (commands.size() > 0) {
            dir = CMLib.directions().getGoodDirectionCode(commands.get(commands.size() - 1));
            commands.remove(commands.size() - 1);
        }
        if (dir < 0) {
            if (mob.isMonster()) {
                for (int d = Directions.NUM_DIRECTIONS() - 1; d >= 0; d--)
                    if ((mob.location().getRoomInDir(d) != null)
                        && (mob.location().getExitInDir(d) != null)
                        && (mob.location().getExitInDir(d).isOpen()))
                        dir = d;
            }
            if (dir < 0) {
                mob.tell(L("Shove whom which direction?  Try north, south, east, or west..."));
                return false;
            }
        }
        if ((mob.location().getRoomInDir(dir) == null)
            || (mob.location().getExitInDir(dir) == null)
            || (!mob.location().getExitInDir(dir).isOpen())) {
            mob.tell(L("You can't shove anyone that way!"));
            return false;
        }

        final MOB target = getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> get(s) shoved back!") : L("<S-NAME> incant(s) and shove(s) at <T-NAMESELF>."));
            if ((mob.location().okMessage(mob, msg)) && (target.fetchEffect(this.ID()) == null)) {
                if ((msg.value() <= 0) && (target.location() == mob.location())) {
                    mob.location().send(mob, msg);
                    target.makePeace(true);
                    final Room newRoom = mob.location().getRoomInDir(dir);
                    final Room thisRoom = mob.location();
                    final CMMsg enterMsg = CMClass.getMsg(target, newRoom, this, CMMsg.MSG_ENTER, null, CMMsg.MSG_ENTER, null, CMMsg.MSG_ENTER, L("<S-NAME> fly(s) in from @x1.", CMLib.directions().getFromCompassDirectionName(Directions.getOpDirectionCode(dir))));
                    final CMMsg leaveMsg = CMClass.getMsg(target, thisRoom, this, CMMsg.MSG_LEAVE | CMMsg.MASK_MAGIC, L("<S-NAME> <S-IS-ARE> shoved forcefully into the air and out @x1.", CMLib.directions().getInDirectionName(dir)));
                    if (thisRoom.okMessage(target, leaveMsg) && newRoom.okMessage(target, enterMsg)) {
                        thisRoom.send(target, leaveMsg);
                        newRoom.bringMobHere(target, false);
                        newRoom.send(target, enterMsg);
                        target.tell(L("\n\r\n\r"));
                        CMLib.commands().postLook(target, true);
                    }
                }
            }
        } else
            return maliciousFizzle(mob, null, L("<S-NAME> incant(s), but nothing seems to happen."));

        // return whether it worked
        return success;
    }
}
