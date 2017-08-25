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
package com.planet_ink.game.Abilities.Traps;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Areas.interfaces.Area;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;

import java.util.Enumeration;
import java.util.HashSet;


public class Trap_Popper extends StdTrap {
    private final static String localizedName = CMLib.lang().L("popping noise");

    @Override
    public String ID() {
        return "Trap_Popper";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ITEMS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    protected int trapLevel() {
        return 1;
    }

    @Override
    public String requiresToSet() {
        return "";
    }

    @Override
    public void spring(MOB target) {
        if ((target != invoker()) && (target.location() != null)) {
            if ((doesSaveVsTraps(target))
                || (invoker().getGroupMembers(new HashSet<MOB>()).contains(target)))
                target.location().show(target, null, null, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> avoid(s) setting off a noise trap!"));
            else if (target.location().show(target, target, this, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> set(s) off a **POP** trap!"))) {
                super.spring(target);
                final Area A = target.location().getArea();
                for (final Enumeration<Room> e = A.getMetroMap(); e.hasMoreElements(); ) {
                    final Room R = e.nextElement();
                    if (R != target.location())
                        R.showHappens(CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("You hear a loud **POP** coming from somewhere."));
                }
                if ((canBeUninvoked()) && (affected instanceof Item))
                    disable();
            }
        }
    }
}
