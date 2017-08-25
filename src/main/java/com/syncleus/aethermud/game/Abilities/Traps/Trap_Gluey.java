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
package com.syncleus.aethermud.game.Abilities.Traps;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;

import java.util.HashSet;


public class Trap_Gluey extends StdTrap {
    private final static String localizedName = CMLib.lang().L("gluey");

    @Override
    public String ID() {
        return "Trap_Gluey";
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
        return 11;
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
                target.location().show(target, null, null, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> clean(s) off @x1!", affected.name()));
            else if (target.location().show(target, target, this, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> notice(s) something about @x1 .. it's kinda sticky.", affected.name()))) {
                super.spring(target);
                if (affected instanceof Item) {
                    CMLib.flags().setRemovable(((Item) affected), false);
                    CMLib.flags().setDroppable(((Item) affected), false);
                }
                if ((canBeUninvoked()) && (affected instanceof Item))
                    disable();
            }
        }
    }
}
