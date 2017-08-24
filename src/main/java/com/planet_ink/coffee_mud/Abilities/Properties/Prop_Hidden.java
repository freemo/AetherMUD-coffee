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
package com.planet_ink.coffee_mud.Abilities.Properties;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.CharStats;
import com.planet_ink.coffee_mud.Common.interfaces.PhyStats;
import com.planet_ink.coffee_mud.Items.interfaces.Container;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

import java.util.Vector;


public class Prop_Hidden extends Property {
    protected int ticksSinceLoss = 100;
    protected boolean unLocatable = false;

    @Override
    public String ID() {
        return "Prop_Hidden";
    }

    @Override
    public String name() {
        return "Persistant Hiddenness";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS
            | Ability.CAN_ITEMS
            | Ability.CAN_EXITS
            | Ability.CAN_AREAS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_ADJUSTER;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (!(affected instanceof MOB))
            return;

        final MOB mob = (MOB) affected;

        if (msg.amISource(mob)) {

            if (((!msg.sourceMajor(CMMsg.MASK_SOUND)
                || (msg.sourceMinor() == CMMsg.TYP_SPEAK)
                || (msg.sourceMinor() == CMMsg.TYP_ENTER)
                || (msg.sourceMinor() == CMMsg.TYP_LEAVE)
                || (msg.sourceMinor() == CMMsg.TYP_RECALL)))
                && (!msg.sourceMajor(CMMsg.MASK_ALWAYS))
                && (msg.sourceMinor() != CMMsg.TYP_LOOK)
                && (msg.sourceMinor() != CMMsg.TYP_EXAMINE)
                && (msg.sourceMajor() > 0)) {
                ticksSinceLoss = 0;
                mob.recoverPhyStats();
            }
        }
        return;
    }

    @Override
    public void setMiscText(String text) {
        super.setMiscText(text);
        if (!(affected instanceof MOB)) {
            final Vector<String> parms = CMParms.parse(text.toUpperCase());
            unLocatable = parms.contains("UNLOCATABLE");
        }
    }

    @Override
    public void affectCharStats(MOB affected, CharStats affectableStats) {
        super.affectCharStats(affected, affectableStats);
        affectableStats.setStat(CharStats.STAT_SAVE_DETECTION, 100 + affectableStats.getStat(CharStats.STAT_SAVE_DETECTION));
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (ticksSinceLoss < 999999)
            ticksSinceLoss++;
        return super.tick(ticking, tickID);
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        super.affectPhyStats(affected, affectableStats);
        if (affected instanceof MOB) {
            affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_SEE_HIDDEN);
            if (ticksSinceLoss > 30)
                affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_HIDDEN);
        } else {
            if (unLocatable)
                affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.SENSE_UNLOCATABLE);
            if (affected instanceof Item) {
                if ((((Item) affected).owner() instanceof Room)
                    && ((!(affected instanceof Container))
                    || (!((Container) affected).hasADoor())
                    || (!((Container) affected).isOpen())))
                    affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_HIDDEN);
            } else
                affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_HIDDEN);
        }
    }
}
