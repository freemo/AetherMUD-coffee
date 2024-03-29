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
package com.syncleus.aethermud.game.Behaviors;

import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.Enumeration;


@SuppressWarnings("rawtypes")
public class ROMPatrolman extends StdBehavior {
    int tickTock = 0;

    @Override
    public String ID() {
        return "ROMPatrolman";
    }

    @Override
    public String accountForYourself() {
        return "gang member passifying";
    }

    public void keepPeace(MOB observer) {
        if (!canFreelyBehaveNormal(observer))
            return;
        MOB victim = null;
        for (int i = 0; i < observer.location().numInhabitants(); i++) {
            final MOB inhab = observer.location().fetchInhabitant(i);
            if ((inhab != null) && (inhab.isInCombat())) {
                if (inhab.phyStats().level() > inhab.getVictim().phyStats().level())
                    victim = inhab;
                else
                    victim = inhab.getVictim();
            }
        }

        if (victim == null)
            return;
        if (BrotherHelper.isBrother(victim, observer, false))
            return;
        observer.location().show(observer, null, CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> blow(s) down hard on <S-HIS-HER> whistle. ***WHEEEEEEEEEEEET***"));
        for (final Enumeration r = observer.location().getArea().getMetroMap(); r.hasMoreElements(); ) {
            final Room R = (Room) r.nextElement();
            if ((R != observer.location()) && (R.numPCInhabitants() > 0))
                R.showHappens(CMMsg.MSG_NOISE, L("You hear a shrill whistling sound in the distance."));
        }

        Item weapon = observer.fetchWieldedItem();
        if (weapon == null)
            weapon = observer.getNaturalWeapon();
        boolean makePeace = false;
        boolean fight = false;
        switch (CMLib.dice().roll(1, 7, -1)) {
            case 0:
                observer.location().show(observer, null, CMMsg.MSG_SPEAK, L("^T<S-NAME> yell(s) 'All roit! All roit! break it up!'^?"));
                makePeace = true;
                break;
            case 1:
                observer.location().show(observer, null, CMMsg.MSG_SPEAK, L("^T<S-NAME> sigh(s) 'Society's to blame, but what's a bloke to do?'^?"));
                fight = true;
                break;
            case 2:
                observer.location().show(observer, null, CMMsg.MSG_SPEAK, L("^T<S-NAME> mumble(s) 'bloody kids will be the death of us all.'^?"));
                break;
            case 3:
                observer.location().show(observer, null, CMMsg.MSG_SPEAK, L("^T<S-NAME> yell(s) 'Stop that! Stop that!' and attack(s).^?"));
                fight = true;
                break;
            case 4:
                observer.location().show(observer, null, CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> pull(s) out his billy and go(es) to work."));
                fight = true;
                break;
            case 5:
                observer.location().show(observer, null, CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> sigh(s) in resignation and proceed(s) to break up the fight."));
                makePeace = true;
                break;
            case 6:
                observer.location().show(observer, null, CMMsg.MSG_SPEAK, L("^T<S-NAME> say(s) 'Settle down, you hooligans!'^?"));
                break;
        }

        if (makePeace) {
            final Room room = observer.location();
            for (int i = 0; i < room.numInhabitants(); i++) {
                final MOB inhab = room.fetchInhabitant(i);
                if ((inhab != null)
                    && (inhab.isInCombat())
                    && (inhab.getVictim().isInCombat())
                    && ((observer.phyStats().level() > (inhab.phyStats().level() + 5))
                    && (!CMLib.flags().isEvil(observer)))) {
                    final String msg = "<S-NAME> stop(s) <T-NAME> from fighting with " + inhab.getVictim().name();
                    final CMMsg msgs = CMClass.getMsg(observer, inhab, CMMsg.MSG_NOISYMOVEMENT, msg);
                    if (observer.location().okMessage(observer, msgs)) {
                        final MOB ivictim = inhab.getVictim();
                        if (ivictim != null)
                            ivictim.makePeace(true);
                    }
                }
            }
        } else if (fight)
            CMLib.combat().postAttack(observer, victim, weapon);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        super.tick(ticking, tickID);

        if (tickID != Tickable.TICKID_MOB)
            return true;
        final MOB mob = (MOB) ticking;
        tickTock--;
        if (tickTock <= 0) {
            tickTock = CMLib.dice().roll(1, 3, 0);
            keepPeace(mob);
        }
        return true;
    }
}
