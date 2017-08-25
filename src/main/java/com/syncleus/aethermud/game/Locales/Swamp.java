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
package com.planet_ink.game.Locales;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMSecurity;
import com.planet_ink.game.core.interfaces.Drink;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Places;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;


public class Swamp extends StdRoom implements Drink {
    public static final Integer[] resourceList =
        {
            Integer.valueOf(RawMaterial.RESOURCE_JADE),
            Integer.valueOf(RawMaterial.RESOURCE_SCALES),
            Integer.valueOf(RawMaterial.RESOURCE_COCOA),
            Integer.valueOf(RawMaterial.RESOURCE_COAL),
            Integer.valueOf(RawMaterial.RESOURCE_PIPEWEED),
            Integer.valueOf(RawMaterial.RESOURCE_BAMBOO),
            Integer.valueOf(RawMaterial.RESOURCE_REED),
            Integer.valueOf(RawMaterial.RESOURCE_SUGAR),
            Integer.valueOf(RawMaterial.RESOURCE_CLAY),
        };
    public static final List<Integer> roomResources = new Vector<Integer>(Arrays.asList(resourceList));
    protected int liquidType = RawMaterial.RESOURCE_FRESHWATER;

    public Swamp() {
        super();
        name = "the swamp";
        basePhyStats.setWeight(3);
        recoverPhyStats();
        climask = Places.CLIMASK_WET;
    }

    @Override
    public String ID() {
        return "Swamp";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_OUTDOORS_SWAMP;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if ((msg.amITarget(this) || (msg.targetMinor() == CMMsg.TYP_ADVANCE) || (msg.targetMinor() == CMMsg.TYP_RETREAT))
            && (!msg.source().isMonster())
            && (msg.source().curState().getHitPoints() < msg.source().maxState().getHitPoints())
            && (CMLib.dice().rollPercentage() == 1)
            && (CMLib.dice().rollPercentage() == 1)
            && (!CMSecurity.isDisabled(CMSecurity.DisFlag.AUTODISEASE))) {
            Ability A = null;
            if (CMLib.dice().rollPercentage() > 50)
                A = CMClass.getAbility("Disease_Chlamydia");
            else
                A = CMClass.getAbility("Disease_Malaria");
            if ((A != null) && (msg.source().fetchEffect(A.ID()) == null) && (!CMSecurity.isAbilityDisabled(A.ID())))
                A.invoke(msg.source(), msg.source(), true, 0);
        }
        if (msg.amITarget(this) && (msg.targetMinor() == CMMsg.TYP_DRINK)) {
            final MOB mob = msg.source();
            final boolean thirsty = mob.curState().getThirst() <= 0;
            final boolean full = !mob.curState().adjThirst(thirstQuenched(), mob.maxState().maxThirst(mob.baseWeight()));
            if (thirsty)
                mob.tell(L("You are no longer thirsty."));
            else if (full)
                mob.tell(L("You have drunk all you can."));
            if (CMLib.dice().rollPercentage() < 10) {
                Ability A = CMClass.getAbility("Disease_Malaria");
                if ((A != null) && (msg.source().fetchEffect(A.ID()) == null))
                    A.invoke(msg.source(), msg.source(), true, 0);
            }
        }
        super.executeMsg(myHost, msg);
    }

    @Override
    protected int baseThirst() {
        return 0;
    }

    @Override
    public long decayTime() {
        return 0;
    }

    @Override
    public void setDecayTime(long time) {
    }

    @Override
    public int thirstQuenched() {
        return 500;
    }

    @Override
    public int liquidHeld() {
        return Integer.MAX_VALUE - 1000;
    }

    @Override
    public int liquidRemaining() {
        return Integer.MAX_VALUE - 1000;
    }

    @Override
    public int liquidType() {
        return liquidType;
    }

    @Override
    public void setLiquidType(int newLiquidType) {
        liquidType = newLiquidType;
    }

    @Override
    public void setThirstQuenched(int amount) {
    }

    @Override
    public void setLiquidHeld(int amount) {
    }

    @Override
    public void setLiquidRemaining(int amount) {
    }

    @Override
    public boolean disappearsAfterDrinking() {
        return false;
    }

    @Override
    public boolean containsDrink() {
        return true;
    }

    @Override
    public int amountTakenToFillMe(Drink theSource) {
        return 0;
    }

    @Override
    public List<Integer> resourceChoices() {
        return Swamp.roomResources;
    }
}
