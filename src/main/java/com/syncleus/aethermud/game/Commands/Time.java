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
package com.syncleus.aethermud.game.Commands;

import com.syncleus.aethermud.game.Common.interfaces.PlayerStats;
import com.syncleus.aethermud.game.Common.interfaces.TimeClock;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMStrings;
import com.syncleus.aethermud.game.core.CMath;

import java.util.List;


public class Time extends StdCommand {
    private final String[] access = I(new String[]{"TIME", "DATE"});

    public Time() {
    }

    @Override
    public String[] getAccessWords() {
        return access;
    }

    @Override
    public boolean execute(MOB mob, List<String> commands, int metaFlags)
        throws java.io.IOException {
        final Room room = mob.location();
        if (room == null)
            return false;
        mob.tell(room.getArea().getTimeObj().timeDescription(mob, room));
        if ((mob.playerStats() != null) && (mob.playerStats().getBirthday() != null)) {
            final TimeClock C = CMLib.time().localClock(mob.getStartRoom());
            final int day = C.getDayOfMonth();
            final int month = C.getMonth();
            int year = C.getYear();
            final int bday = mob.playerStats().getBirthday()[PlayerStats.BIRTHDEX_DAY];
            final int bmonth = mob.playerStats().getBirthday()[PlayerStats.BIRTHDEX_MONTH];
            if ((month > bmonth) || ((month == bmonth) && (day > bday)))
                year++;

            final StringBuffer timeDesc = new StringBuffer("");
            if (C.getDaysInWeek() > 0) {
                long x = ((long) year) * ((long) C.getMonthsInYear()) * C.getDaysInMonth();
                x = x + ((long) (bmonth - 1)) * ((long) C.getDaysInMonth());
                x = x + bmonth;
                timeDesc.append(C.getWeekNames()[(int) (x % C.getDaysInWeek())] + ", ");
            }
            timeDesc.append("the " + bday + CMath.numAppendage(bday));
            timeDesc.append(" day of " + C.getMonthNames()[bmonth - 1]);
            if (C.getYearNames().length > 0)
                timeDesc.append(", " + CMStrings.replaceAll(C.getYearNames()[year % C.getYearNames().length], "#", "" + year));
            mob.tell(L("Your next birthday is @x1.", timeDesc.toString()));
        }
        return false;
    }

    @Override
    public boolean canBeOrdered() {
        return true;
    }

}
