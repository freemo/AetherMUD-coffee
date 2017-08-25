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
package com.syncleus.aethermud.game.Items.Basic;

import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Drink;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class GenLantern extends GenLightSource {
    public static final int DURATION_TICKS = 800;

    public GenLantern() {
        super();
        setName("a hooded lantern");
        setDisplayText("a hooded lantern sits here.");
        setDescription("");

        basePhyStats().setWeight(5);
        setDuration(DURATION_TICKS);
        destroyedWhenBurnedOut = false;
        goesOutInTheRain = false;
        baseGoldValue = 60;
        setMaterial(RawMaterial.RESOURCE_STEEL);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "GenLantern";
    }

    @Override
    public boolean isGeneric() {
        return true;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (msg.amITarget(this)) {
            final MOB mob = msg.source();
            switch (msg.targetMinor()) {
                case CMMsg.TYP_FILL:
                    if ((msg.tool() != msg.target())
                        && (msg.tool() instanceof Drink)) {
                        if (((Drink) msg.tool()).liquidType() != RawMaterial.RESOURCE_LAMPOIL) {
                            mob.tell(L("You can only fill @x1 with lamp oil!", name()));
                            return false;
                        }
                        final Drink thePuddle = (Drink) msg.tool();
                        if (!thePuddle.containsDrink()) {
                            mob.tell(L("@x1 is empty.", thePuddle.name()));
                            return false;
                        }
                        if (this.getDuration() >= (int) Math.round(CMath.mul(DURATION_TICKS, .9))) {
                            mob.tell(L("@x1 is full of oil.", name()));
                            return false;
                        }
                        return true;
                    }
                    mob.tell(L("You can't fill @x1 from that.", name()));
                    return false;
                default:
                    break;
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (msg.amITarget(this)) {
            switch (msg.targetMinor()) {
                case CMMsg.TYP_FILL:
                    if ((msg.tool() instanceof Drink)) {
                        final Drink thePuddle = (Drink) msg.tool();
                        int amountToTake = 1;
                        if (!thePuddle.containsDrink())
                            amountToTake = 0;
                        thePuddle.setLiquidRemaining(thePuddle.liquidRemaining() - amountToTake);
                        setDuration(DURATION_TICKS);
                        setDescription("The lantern still looks like it has some oil in it.");
                    }
                    break;
                default:
                    break;
            }
        }
        super.executeMsg(myHost, msg);
    }
}
