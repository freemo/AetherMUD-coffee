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
import com.syncleus.aethermud.game.Exits.interfaces.Exit;
import com.syncleus.aethermud.game.Items.interfaces.Container;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.Directions;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.ShopKeeper;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.Iterator;
import java.util.List;


public class Prayer_SenseTraps extends Prayer {
    private final static String localizedName = CMLib.lang().L("Sense Traps");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Sensing Traps)");
    Room lastRoom = null;

    @Override
    public String ID() {
        return "Prayer_SenseTraps";
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
    public int classificationCode() {
        return Ability.ACODE_PRAYER | Ability.DOMAIN_COMMUNING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    public int enchantQuality() {
        return Ability.QUALITY_BENEFICIAL_SELF;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_NEUTRAL;
    }

    @Override
    public void unInvoke() {
        if (!(affected instanceof MOB))
            return;
        final MOB mob = (MOB) affected;
        if (canBeUninvoked())
            lastRoom = null;
        super.unInvoke();
        if (canBeUninvoked())
            mob.tell(L("Your senses are no longer sensitive to traps."));
    }

    public String trapCheck(Physical P) {
        if (P != null)
            if (CMLib.utensils().fetchMyTrap(P) != null)
                return L("@x1 is trapped.\n\r", P.name());
        return "";
    }

    public String trapHere(MOB mob, Physical P) {
        final StringBuffer msg = new StringBuffer("");
        if (P == null)
            return msg.toString();
        if ((P instanceof Room) && (CMLib.flags().canBeSeenBy(P, mob))) {
            msg.append(trapCheck(P));
            final Room R = (Room) P;
            for (int d = Directions.NUM_DIRECTIONS() - 1; d >= 0; d--) {
                final Exit E = R.getExitInDir(d);
                if ((E != null) && (R.getRoomInDir(d) != null)) {
                    final Exit E2 = R.getReverseExit(d);
                    msg.append(trapHere(mob, E));
                    msg.append(trapHere(mob, E2));
                    break;
                }
            }
            for (int i = 0; i < R.numItems(); i++) {
                final Item I = R.getItem(i);
                if ((I != null) && (I.container() == null))
                    msg.append(trapHere(mob, I));
            }
            for (int m = 0; m < R.numInhabitants(); m++) {
                final MOB M = R.fetchInhabitant(m);
                if ((M != null) && (M != mob))
                    msg.append(trapHere(mob, M));
            }
        } else if ((P instanceof Container) && (CMLib.flags().canBeSeenBy(P, mob))) {
            final Container C = (Container) P;
            final List<Item> V = C.getDeepContents();
            for (int v = 0; v < V.size(); v++)
                if (trapCheck(V.get(v)).length() > 0)
                    msg.append(L("@x1 contains something trapped.\n", C.name()));
        } else if ((P instanceof Item) && (CMLib.flags().canBeSeenBy(P, mob)))
            msg.append(trapCheck(P));
        else if ((P instanceof Exit) && (CMLib.flags().canBeSeenBy(P, mob)))
            msg.append(trapCheck(P));
        else if ((P instanceof MOB) && (CMLib.flags().canBeSeenBy(P, mob))) {
            for (int i = 0; i < ((MOB) P).numItems(); i++) {
                final Item I = ((MOB) P).getItem(i);
                if (trapCheck(I).length() > 0)
                    return P.name() + " is carrying something trapped.\n";
            }
            final ShopKeeper SK = CMLib.aetherShops().getShopKeeper(P);
            if (SK != null) {
                for (final Iterator<Environmental> i = SK.getShop().getStoreInventory(); i.hasNext(); ) {
                    final Environmental E2 = i.next();
                    if (E2 instanceof Item)
                        if (trapCheck((Item) E2).length() > 0)
                            return P.name() + " has something trapped in stock.\n";
                }
            }
        }
        return msg.toString();
    }

    public void messageTo(MOB mob) {
        final String here = trapHere(mob, mob.location());
        if (here.length() > 0)
            mob.tell(here);
        else {
            String last = "";
            String dirs = "";
            for (int d = Directions.NUM_DIRECTIONS() - 1; d >= 0; d--) {
                final Room R = mob.location().getRoomInDir(d);
                final Exit E = mob.location().getExitInDir(d);
                if ((R != null) && (E != null) && (trapHere(mob, R).length() > 0)) {
                    if (last.length() > 0)
                        dirs += ", " + last;
                    last = CMLib.directions().getFromCompassDirectionName(d);
                }
            }
            if ((dirs.length() == 0) && (last.length() > 0))
                mob.tell(L("You sense a trap to @x1.", last));
            else if ((dirs.length() > 2) && (last.length() > 0))
                mob.tell(L("You sense a trap to @x1, and @x2.", dirs.substring(2), last));
        }
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if ((tickID == Tickable.TICKID_MOB)
            && (affected instanceof MOB)
            && (((MOB) affected).location() != null)
            && ((lastRoom == null) || (((MOB) affected).location() != lastRoom))) {
            lastRoom = ((MOB) affected).location();
            messageTo((MOB) affected);
        }
        return true;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        MOB target = mob;
        if ((auto) && (givenTarget != null) && (givenTarget instanceof MOB))
            target = (MOB) givenTarget;
        if (target.fetchEffect(this.ID()) != null) {
            mob.tell(target, null, null, L("<S-NAME> <S-IS-ARE> already sensing traps."));
            return false;
        }
        final boolean success = proficiencyCheck(mob, 0, auto);

        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? L("<T-NAME> gain(s) trap sensitivities!") : L("^S<S-NAME> @x1, and gain(s) sensitivity to traps!^?", prayWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            beneficialVisualFizzle(mob, null, L("<S-NAME> @x1, but nothing happens.", prayWord(mob)));

        return success;
    }
}
