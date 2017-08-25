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
import com.planet_ink.game.Areas.interfaces.Area;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Exits.interfaces.Exit;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.Vector;


public class Prop_ClosedSeason extends Property {
    protected Vector<String> closedV = null;
    boolean doneToday = false;
    private Area exitArea = null;

    @Override
    public String ID() {
        return "Prop_ClosedSeason";
    }

    @Override
    public String name() {
        return "Contingent Visibility";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ITEMS | Ability.CAN_MOBS | Ability.CAN_EXITS | Ability.CAN_ROOMS;
    }

    @Override
    public String accountForYourself() {
        return "";
    }

    @Override
    public long flags() {
        return Ability.FLAG_ADJUSTER;
    }

    @Override
    public void setMiscText(String text) {
        super.setMiscText(text);
        closedV = CMParms.parse(text.toUpperCase());
    }

    @Override
    public void executeMsg(Environmental E, CMMsg msg) {
        super.executeMsg(E, msg);
        if (exitArea != null)
            return;
        if (!(affected instanceof Exit))
            return;
        if (msg.source().location() != null)
            exitArea = msg.source().location().getArea();
    }

    protected boolean closed(Area A) {
        if (A == null)
            return false;

        for (final Room.VariationCode code : Room.VariationCode.values()) {
            if (closedV.contains(code.toString()))
                switch (code.c) {
                    case 'W':
                        if (A.getClimateObj().weatherType(null) == code.num)
                            return true;
                        break;
                    case 'C':
                        if (A.getTimeObj().getTODCode().ordinal() == code.num)
                            return true;
                        break;
                    case 'S':
                        if (A.getTimeObj().getSeasonCode().ordinal() == code.num)
                            return true;
                        break;
                }
        }
        return false;
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats affectableStats) {
        if (affected == null)
            return;
        if ((affected instanceof MOB) || (affected instanceof Item)) {
            final Room R = CMLib.map().roomLocation(affected);
            if ((R != null)
                && (closed(R.getArea()))
                && ((!(affected instanceof MOB)) || (!((MOB) affected).isInCombat()))) {
                affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_NOT_SEEN);
                affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_SEE);
                affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_MOVE);
                affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_SPEAK);
                affectableStats.setSensesMask(affectableStats.sensesMask() | PhyStats.CAN_NOT_HEAR);
            }
        } else if ((affected instanceof Room) && (closed(((Room) affected).getArea())))
            affectableStats.setDisposition(affectableStats.disposition() | PhyStats.IS_DARK);
        else if (affected instanceof Exit) {
            if (closed(exitArea == null ? CMLib.map().getFirstArea() : exitArea)) {
                if (!doneToday) {
                    doneToday = true;
                    final Exit e = ((Exit) affected);
                    e.setDoorsNLocks(e.hasADoor(), false, e.defaultsClosed(), e.hasALock(), e.hasALock(), e.defaultsLocked());
                }
            } else {
                if (doneToday) {
                    doneToday = false;
                    final Exit e = ((Exit) affected);
                    e.setDoorsNLocks(e.hasADoor(), !e.defaultsClosed(), e.defaultsClosed(), e.hasALock(), e.defaultsLocked(), e.defaultsLocked());
                }
            }
        }

    }
}
