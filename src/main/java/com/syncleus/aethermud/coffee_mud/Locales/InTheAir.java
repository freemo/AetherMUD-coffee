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
package com.planet_ink.coffee_mud.Locales;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.Climate;
import com.planet_ink.coffee_mud.Exits.interfaces.Exit;
import com.planet_ink.coffee_mud.Items.interfaces.DeadBody;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Libraries.interfaces.TrackingLibrary;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.Vector;


public class InTheAir extends StdRoom {
    public InTheAir() {
        super();
        basePhyStats.setWeight(1);
        name = "the sky";
        recoverPhyStats();
    }

    public static void airAffects(Room room, CMMsg msg) {
        if (CMLib.flags().isSleeping(room))
            return;
        boolean foundReversed = false;
        boolean foundNormal = false;
        final Vector<Physical> needToFall = new Vector<Physical>();
        final Vector<Physical> mightNeedAdjusting = new Vector<Physical>();
        for (int i = 0; i < room.numInhabitants(); i++) {
            final MOB mob = room.fetchInhabitant(i);
            if ((mob != null)
                && ((mob.getStartRoom() == null) || (mob.getStartRoom() != room))) {
                final Ability A = mob.fetchEffect("Falling");
                if (A != null) {
                    if (CMath.s_bool(A.getStat("REVERSED"))) {
                        foundReversed = true;
                        mightNeedAdjusting.addElement(mob);
                    }
                    foundNormal = foundNormal || (A.proficiency() <= 0);
                } else
                    needToFall.addElement(mob);
            }
        }
        for (int i = 0; i < room.numItems(); i++) {
            final Item item = room.getItem(i);
            if (item != null) {
                final Ability A = item.fetchEffect("Falling");
                if (A != null) {
                    if (CMath.s_bool(A.getStat("REVERSED"))) {
                        foundReversed = true;
                        mightNeedAdjusting.addElement(item);
                    }
                    foundNormal = foundNormal || (A.proficiency() <= 0);
                } else if (item.container() == null)
                    needToFall.addElement(item);
            }
        }
        final boolean reversed = ((foundReversed) && (!foundNormal));
        for (final Physical P : mightNeedAdjusting) {
            final Ability A = P.fetchEffect("Falling");
            if (A != null)
                A.setStat("REVERSED", reversed + "");
        }
        final TrackingLibrary tracker = CMLib.tracking();
        for (final Physical P : needToFall)
            tracker.makeFall(P, room, reversed);
    }

    public static boolean isOkAirAffect(Room room, CMMsg msg) {
        if (CMLib.flags().isSleeping(room))
            return true;
        if ((msg.sourceMinor() == CMMsg.TYP_SIT)
            && (!(msg.target() instanceof Exit))
            && (!(msg.target() instanceof DeadBody))
            && (!msg.sourceMajor(CMMsg.MASK_ALWAYS))) {
            msg.source().tell(CMLib.lang().L("You can't sit here."));
            return false;
        }
        if ((msg.sourceMinor() == CMMsg.TYP_SLEEP) && (!msg.sourceMajor(CMMsg.MASK_ALWAYS))) {
            msg.source().tell(CMLib.lang().L("You can't sleep here."));
            return false;
        }

        if ((msg.targetMinor() == CMMsg.TYP_ENTER)
            && (msg.amITarget(room))) {
            final MOB mob = msg.source();
            if ((!CMLib.flags().isInFlight(mob)) && (!CMLib.flags().isFalling(mob))) {
                mob.tell(CMLib.lang().L("You can't fly."));
                return false;
            }
            if (CMLib.dice().rollPercentage() > 50) {
                switch (room.getArea().getClimateObj().weatherType(room)) {
                    case Climate.WEATHER_BLIZZARD:
                        room.show(mob, null, CMMsg.MSG_OK_VISUAL, CMLib.lang().L("The swirling blizzard inhibits <S-YOUPOSS> progress."));
                        return false;
                    case Climate.WEATHER_HAIL:
                        room.show(mob, null, CMMsg.MSG_OK_VISUAL, CMLib.lang().L("The hail storm inhibits <S-YOUPOSS> progress."));
                        return false;
                    case Climate.WEATHER_RAIN:
                        room.show(mob, null, CMMsg.MSG_OK_VISUAL, CMLib.lang().L("The rain storm inhibits <S-YOUPOSS> progress."));
                        return false;
                    case Climate.WEATHER_SLEET:
                        room.show(mob, null, CMMsg.MSG_OK_VISUAL, CMLib.lang().L("The biting sleet inhibits <S-YOUPOSS> progress."));
                        return false;
                    case Climate.WEATHER_THUNDERSTORM:
                        room.show(mob, null, CMMsg.MSG_OK_VISUAL, CMLib.lang().L("The thunderstorm inhibits <S-YOUPOSS> progress."));
                        return false;
                    case Climate.WEATHER_WINDY:
                        room.show(mob, null, CMMsg.MSG_OK_VISUAL, CMLib.lang().L("The hard winds inhibit <S-YOUPOSS> progress."));
                        return false;
                }
            }
        }
        InTheAir.airAffects(room, msg);
        return true;
    }

    @Override
    public String ID() {
        return "InTheAir";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_OUTDOORS_AIR;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;
        return isOkAirAffect(this, msg);
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        InTheAir.airAffects(this, msg);
    }
}
