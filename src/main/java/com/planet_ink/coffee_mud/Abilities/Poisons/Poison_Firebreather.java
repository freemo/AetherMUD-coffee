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
package com.planet_ink.coffee_mud.Abilities.Poisons;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Weapon;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;


public class Poison_Firebreather extends Poison_Liquor {
    private final static String localizedName = CMLib.lang().L("Firebreather");
    private static final String[] triggerStrings = I(new String[]{"LIQUORFIRE"});

    @Override
    public String ID() {
        return "Poison_Firebreather";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    protected int POISON_TICKS() {
        return 35;
    }

    @Override
    protected int alchoholContribution() {
        return 3;
    }

    @Override
    protected int level() {
        return 3;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if ((affected == null) || (invoker == null))
            return false;
        if (!(affected instanceof MOB))
            return super.tick(ticking, tickID);

        final MOB mob = (MOB) affected;
        final Room room = mob.location();
        if ((CMLib.dice().rollPercentage() < drunkness) && (CMLib.flags().isAliveAwakeMobile(mob, true)) && (room != null)) {
            if (CMLib.dice().rollPercentage() < 40) {
                room.show(mob, null, this, CMMsg.MSG_QUIETMOVEMENT, L("<S-NAME> belch(es) fire!@x1", CMLib.protocol().msp("fireball.wav", 20)));
                for (int i = 0; i < room.numInhabitants(); i++) {
                    final MOB target = room.fetchInhabitant(i);

                    final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSK_MALICIOUS_MOVE | CMMsg.TYP_FIRE, null);
                    if ((mob != target) && (mob.mayPhysicallyAttack(target)) && (room.okMessage(mob, msg))) {
                        room.send(mob, msg);
                        invoker = mob;

                        int damage = 0;
                        int maxDie = mob.phyStats().level();
                        if (maxDie > 10)
                            maxDie = 10;
                        damage += CMLib.dice().roll(maxDie, 6, 1);
                        if (msg.value() > 0)
                            damage = (int) Math.round(CMath.div(damage, 2.0));
                        CMLib.combat().postDamage(mob, target, this, damage, CMMsg.MASK_ALWAYS | CMMsg.MASK_SOUND | CMMsg.TYP_FIRE, Weapon.TYPE_BURNING, L("^F^<FIGHT^>The fire <DAMAGE> <T-NAME>!^</FIGHT^>^?"));
                    }
                }
            } else
                room.show(mob, null, this, CMMsg.MSG_QUIETMOVEMENT, L("<S-NAME> belch(es) smoke!"));
            disableHappiness = true;
        }
        return super.tick(ticking, tickID);
    }
}
