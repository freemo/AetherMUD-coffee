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
package com.planet_ink.coffee_mud.Abilities.Traps;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;


public class Trap_Boomerang extends StdTrap {
    private final static String localizedName = CMLib.lang().L("boomerang");

    @Override
    public String ID() {
        return "Trap_Boomerang";
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
        return 24;
    }

    @Override
    public String requiresToSet() {
        return "";
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        final boolean wasSprung = sprung;
        super.executeMsg(myHost, msg);
        if ((!wasSprung) && (sprung)) {
            msg.setSourceCode(CMMsg.NO_EFFECT);
            msg.setTargetCode(CMMsg.NO_EFFECT);
            msg.setOthersCode(CMMsg.NO_EFFECT);
        }
    }

    @Override
    public void spring(MOB target) {
        if ((target != invoker()) && (target.location() != null)) {
            final boolean ok = ((invoker() != null) && (invoker().location() != null));
            if ((!ok) || (doesSaveVsTraps(target)))
                target.location().show(target, null, null, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> foil(s) a trap on @x1!", affected.name()));
            else if (target.location().show(target, target, this, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> set(s) off a trap!"))) {
                if (affected instanceof Item) {
                    ((Item) affected).unWear();
                    ((Item) affected).removeFromOwnerContainer();
                    invoker().addItem((Item) affected);
                    invoker().tell(invoker(), affected, null, L("Magically, <T-NAME> appear(s) in your inventory."));
                }
                super.spring(target);
                if ((canBeUninvoked()) && (affected instanceof Item))
                    disable();
            }
        }
    }
}
