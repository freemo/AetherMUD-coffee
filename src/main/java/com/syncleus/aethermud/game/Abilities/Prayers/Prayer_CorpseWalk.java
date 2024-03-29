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
import com.syncleus.aethermud.game.Items.interfaces.DeadBody;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Prayer_CorpseWalk extends Prayer {
    private final static String localizedName = CMLib.lang().L("Corpse Walk");

    @Override
    public String ID() {
        return "Prayer_CorpseWalk";
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
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public long flags() {
        return Ability.FLAG_TRANSPORTING | Ability.FLAG_UNHOLY;
    }

    @Override
    protected int overrideMana() {
        return Ability.COST_PCT + 50;
    }

    public Item findCorpseRoom(List<Item> candidates) {
        for (int m = 0; m < candidates.size(); m++) {
            final Item item = candidates.get(m);
            if ((item instanceof DeadBody) && (((DeadBody) item).isPlayerCorpse())) {
                Room newRoom = CMLib.map().roomLocation(item);
                if (newRoom != null)
                    return item;
            }
        }
        for (int m = 0; m < candidates.size(); m++) {
            final Item item = candidates.get(m);
            if (item instanceof DeadBody) {
                Room newRoom = CMLib.map().roomLocation(item);
                if (newRoom != null)
                    return item;
            }
        }
        return null;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (commands.size() < 1) {
            mob.tell(L("You must specify the name of a corpse within range of this magic."));
            return false;
        }
        final String corpseName = CMParms.combine(commands, 0).trim().toUpperCase();

        List<Item> candidates = CMLib.map().findRoomItems(mob.location().getArea().getProperMap(), mob, corpseName, false, 5);
        Item corpseItem = this.findCorpseRoom(candidates);
        Room newRoom = null;
        if (corpseItem != null)
            newRoom = CMLib.map().roomLocation(corpseItem);
        if (newRoom == null) {
            candidates = CMLib.map().findRoomItems(CMLib.map().rooms(), mob, corpseName, false, 5);
            corpseItem = this.findCorpseRoom(candidates);
            if (corpseItem != null)
                newRoom = CMLib.map().roomLocation(corpseItem);
        }
        candidates.clear();
        if (newRoom == null) {
            mob.tell(L("You can't seem to fixate on a corpse called '@x1', perhaps it has decayed?", corpseName));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, corpseItem, this, CMMsg.MASK_MOVE | verbalCastCode(mob, corpseItem, auto), auto ? "" : L("^S<S-NAME> @x1!^?", prayWord(mob)));
            if ((mob.location().okMessage(mob, msg)) && (newRoom.okMessage(mob, msg))) {
                mob.location().send(mob, msg);
                final List<MOB> h = properTargetList(mob, givenTarget, false);
                if (h == null)
                    return false;

                final Room thisRoom = mob.location();
                for (final MOB follower : h) {
                    if (corpseItem != null) {
                        final CMMsg enterMsg = CMClass.getMsg(follower, newRoom, this, CMMsg.MSG_ENTER, null, CMMsg.MSG_ENTER, null, CMMsg.MSG_ENTER, L("<S-NAME> emerge(s) from @x1.", corpseItem.name()));
                        final CMMsg leaveMsg = CMClass.getMsg(follower, thisRoom, this, CMMsg.MSG_LEAVE | CMMsg.MASK_MAGIC, L("<S-NAME> <S-IS-ARE> sucked into the ground."));
                        if (thisRoom.okMessage(follower, leaveMsg) && newRoom.okMessage(follower, enterMsg)) {
                            if (follower.isInCombat()) {
                                CMLib.commands().postFlee(follower, ("NOWHERE"));
                                follower.makePeace(false);
                            }
                            thisRoom.send(follower, leaveMsg);
                            newRoom.bringMobHere(follower, true);
                            newRoom.send(follower, enterMsg);
                            follower.tell(L("\n\r\n\r"));
                            CMLib.commands().postLook(follower, true);
                        } else if (follower == mob)
                            break;
                    }
                }
            }

        } else
            beneficialVisualFizzle(mob, corpseItem, L("<S-NAME> @x1, but nothing happens.", prayWord(mob)));
        // return whether it worked
        return success;
    }
}
