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

import com.syncleus.aethermud.game.Behaviors.interfaces.Behavior;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class AntiVagrant extends ActiveTicker {
    protected int speakDown = 3;
    protected MOB target = null;
    protected boolean kickout = false;
    protected boolean anywhere = false;
    public AntiVagrant() {
        super();
        minTicks = 2;
        maxTicks = 3;
        chance = 99;
        tickReset();
    }

    @Override
    public String ID() {
        return "AntiVagrant";
    }

    @Override
    protected int canImproveCode() {
        return Behavior.CAN_MOBS;
    }

    @Override
    public String accountForYourself() {
        return "vagrant disliking";
    }

    @Override
    public void setParms(String parms) {
        kickout = parms.toUpperCase().indexOf("KICK") >= 0;
        anywhere = parms.toUpperCase().indexOf("ANYWHERE") >= 0;
        super.setParms(parms);
    }

    public void wakeVagrants(MOB observer) {
        if (!canFreelyBehaveNormal(observer))
            return;
        if (anywhere || (observer.location().domainType() == Room.DOMAIN_OUTDOORS_CITY)) {
            if (target != null)
                if (CMLib.flags().isSleeping(target) && (target != observer) && (CMLib.flags().canBeSeenBy(target, observer))) {
                    CMLib.commands().postSay(observer, target, L("Damn lazy good for nothing!"), false, false);
                    final CMMsg msg = CMClass.getMsg(observer, target, CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> shake(s) <T-NAME> awake."));
                    if (observer.location().okMessage(observer, msg)) {
                        observer.location().send(observer, msg);
                        target.tell(L("@x1 shakes you awake.", observer.name()));
                        CMLib.commands().postStand(target, true);
                        if ((kickout) && (CMLib.flags().isStanding(target))) {
                            CMLib.commands().postSay(observer, target, L("Go home @x1!", target.name(observer)), false, false);
                            CMLib.tracking().beMobile(target, true, false, false, false, null, null);
                        }
                    }
                } else if ((CMLib.flags().isSitting(target) && (target != observer)) && (CMLib.flags().canBeSeenBy(target, observer))) {
                    CMLib.commands().postSay(observer, target, L("Get up and move along!"), false, false);
                    final CMMsg msg = CMClass.getMsg(observer, target, CMMsg.MSG_NOISYMOVEMENT, L("<S-NAME> stand(s) <T-NAME> up."));
                    if (observer.location().okMessage(observer, msg)) {
                        observer.location().send(observer, msg);
                        CMLib.commands().postStand(target, true);
                        if ((kickout) && (CMLib.flags().isStanding(target)))
                            CMLib.tracking().beMobile(target, true, false, false, false, null, null);
                    }
                }
            target = null;
            for (int i = 0; i < observer.location().numInhabitants(); i++) {
                final MOB mob = observer.location().fetchInhabitant(i);
                if ((mob != null)
                    && (mob != observer)
                    && ((CMLib.flags().isSitting(mob)) || (CMLib.flags().isSleeping(mob)))
                    && (CMLib.flags().canBeSeenBy(mob, observer))) {
                    target = mob;
                    break;
                }
            }
        }
    }

    @Override
    public void executeMsg(Environmental affecting, CMMsg msg) {
        // believe it or not, this is for arrest behavior.
        super.executeMsg(affecting, msg);
        if ((msg.sourceMinor() == CMMsg.TYP_SPEAK)
            && (msg.sourceMessage() != null)
            && (msg.sourceMessage().toUpperCase().indexOf("SIT") >= 0))
            speakDown = 3;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        super.tick(ticking, tickID);

        if (tickID != Tickable.TICKID_MOB)
            return true;

        // believe it or not, this is for arrest behavior.
        if (speakDown > 0) {
            speakDown--;
            return true;
        }

        if ((canFreelyBehaveNormal(ticking)) && (canAct(ticking, tickID))) {
            final MOB mob = (MOB) ticking;
            wakeVagrants(mob);
            return true;
        }
        return true;
    }
}
