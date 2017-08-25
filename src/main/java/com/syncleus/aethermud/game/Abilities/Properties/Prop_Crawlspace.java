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
package com.planet_ink.game.Abilities.Properties;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Exits.interfaces.Exit;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;


public class Prop_Crawlspace extends Property {
    @Override
    public String ID() {
        return "Prop_Crawlspace";
    }

    @Override
    public String name() {
        return "Room navigation limitation";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_EXITS | Ability.CAN_ROOMS | Ability.CAN_AREAS;
    }

    @Override
    public String accountForYourself() {
        return "Must be crawled through.";
    }

    @Override
    public long flags() {
        return Ability.FLAG_ADJUSTER;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.SENSE_ROOMCRUNCHEDIN);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((affected != null) && ((affected instanceof Room) || (affected instanceof Exit))) {
            switch (msg.targetMinor()) {
                case CMMsg.TYP_FLEE:
                case CMMsg.TYP_ENTER:
                case CMMsg.TYP_LEAVE:
                    if (((msg.amITarget(affected)) || (msg.tool() == affected))
                        && (msg.sourceMinor() != CMMsg.TYP_RECALL)
                        && (msg.source().phyStats().height() > 24)
                        && (!CMLib.flags().isSitting(msg.source()))) {
                        if (msg.targetMinor() == CMMsg.TYP_FLEE) {
                            final CMMsg sitMsg = CMClass.getMsg(msg.source(), null, null, CMMsg.MSG_SIT, null);
                            final Room R = msg.source().location();
                            if ((R != null)
                                && (!CMLib.flags().isSitting(msg.source()))
                                && (R.okMessage(msg.source(), sitMsg)))
                                R.send(msg.source(), sitMsg);
                        } else if (msg.source().phyStats().height() > 120) {
                            msg.source().tell(L("You cannot fit in there."));
                            return false;
                        }
                        msg.source().tell(L("You must crawl that way."));
                        return false;
                    }
                    break;
                case CMMsg.TYP_STAND:
                    if ((affected instanceof Room)
                        && (msg.source().phyStats().height() > 12)) {
                        if (CMLib.flags().isSleeping(msg.source())) {
                            final MOB mob = msg.source();
                            int oldDisposition = mob.basePhyStats().disposition();
                            oldDisposition = oldDisposition & (~(PhyStats.IS_SLEEPING | PhyStats.IS_SNEAKING | PhyStats.IS_SITTING | PhyStats.IS_CUSTOM));
                            mob.basePhyStats().setDisposition(oldDisposition | PhyStats.IS_SITTING);
                            mob.recoverPhyStats();
                            mob.recoverCharStats();
                            mob.recoverMaxState();
                            mob.tell(L("You wake up, but you are still crawling."));
                            return false;
                        }
                        msg.source().tell(L("You cannot stand up here, try crawling."));
                        return false;
                    }
                    break;
            }
        }
        return super.okMessage(myHost, msg);
    }
}
