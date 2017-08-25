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
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMSecurity;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Places;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;


public class Jungle extends StdRoom {
    public static final Integer[] resourceList = {
        Integer.valueOf(RawMaterial.RESOURCE_PLUMS),
        Integer.valueOf(RawMaterial.RESOURCE_PINEAPPLES),
        Integer.valueOf(RawMaterial.RESOURCE_COCONUTS),
        Integer.valueOf(RawMaterial.RESOURCE_BANANAS),
        Integer.valueOf(RawMaterial.RESOURCE_LIMES),
        Integer.valueOf(RawMaterial.RESOURCE_JADE),
        Integer.valueOf(RawMaterial.RESOURCE_SCALES),
        Integer.valueOf(RawMaterial.RESOURCE_HEMP),
        Integer.valueOf(RawMaterial.RESOURCE_SILK),
        Integer.valueOf(RawMaterial.RESOURCE_FRUIT),
        Integer.valueOf(RawMaterial.RESOURCE_APPLES),
        Integer.valueOf(RawMaterial.RESOURCE_BERRIES),
        Integer.valueOf(RawMaterial.RESOURCE_ORANGES),
        Integer.valueOf(RawMaterial.RESOURCE_COFFEEBEANS),
        Integer.valueOf(RawMaterial.RESOURCE_HERBS),
        Integer.valueOf(RawMaterial.RESOURCE_VINE),
        Integer.valueOf(RawMaterial.RESOURCE_LEMONS),
        Integer.valueOf(RawMaterial.RESOURCE_FUR),
        Integer.valueOf(RawMaterial.RESOURCE_FEATHERS)
    };
    public static final List<Integer> roomResources = new Vector<Integer>(Arrays.asList(resourceList));

    public Jungle() {
        super();
        name = "the jungle";
        basePhyStats.setWeight(3);
        recoverPhyStats();
        climask = Places.CLIMASK_WET | CLIMASK_HOT;
    }

    @Override
    public String ID() {
        return "Jungle";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_OUTDOORS_JUNGLE;
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
                A = CMClass.getAbility("Disease_Gonorrhea");
            else
                A = CMClass.getAbility("Disease_Malaria");
            if ((A != null) && (msg.source().fetchEffect(A.ID()) == null) && (!CMSecurity.isAbilityDisabled(A.ID())))
                A.invoke(msg.source(), msg.source(), true, 0);
        }
        super.executeMsg(myHost, msg);
    }

    @Override
    public List<Integer> resourceChoices() {
        return Jungle.roomResources;
    }
}
