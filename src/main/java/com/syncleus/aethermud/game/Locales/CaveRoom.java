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
package com.syncleus.aethermud.game.Locales;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMSecurity;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Places;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;


public class CaveRoom extends StdRoom {
    public static final Integer[] resourceList = {
        Integer.valueOf(RawMaterial.RESOURCE_GRANITE),
        Integer.valueOf(RawMaterial.RESOURCE_OBSIDIAN),
        Integer.valueOf(RawMaterial.RESOURCE_MARBLE),
        Integer.valueOf(RawMaterial.RESOURCE_STONE),
        Integer.valueOf(RawMaterial.RESOURCE_ALABASTER),
        Integer.valueOf(RawMaterial.RESOURCE_IRON),
        Integer.valueOf(RawMaterial.RESOURCE_LEAD),
        Integer.valueOf(RawMaterial.RESOURCE_GOLD),
        Integer.valueOf(RawMaterial.RESOURCE_WHITE_GOLD),
        Integer.valueOf(RawMaterial.RESOURCE_CHROMIUM),
        Integer.valueOf(RawMaterial.RESOURCE_SILVER),
        Integer.valueOf(RawMaterial.RESOURCE_ZINC),
        Integer.valueOf(RawMaterial.RESOURCE_COPPER),
        Integer.valueOf(RawMaterial.RESOURCE_TIN),
        Integer.valueOf(RawMaterial.RESOURCE_MITHRIL),
        Integer.valueOf(RawMaterial.RESOURCE_MUSHROOMS),
        Integer.valueOf(RawMaterial.RESOURCE_FUNGUS),
        Integer.valueOf(RawMaterial.RESOURCE_GEM),
        Integer.valueOf(RawMaterial.RESOURCE_PERIDOT),
        Integer.valueOf(RawMaterial.RESOURCE_DIAMOND),
        Integer.valueOf(RawMaterial.RESOURCE_LAPIS),
        Integer.valueOf(RawMaterial.RESOURCE_BLOODSTONE),
        Integer.valueOf(RawMaterial.RESOURCE_MOONSTONE),
        Integer.valueOf(RawMaterial.RESOURCE_ALEXANDRITE),
        Integer.valueOf(RawMaterial.RESOURCE_GEM),
        Integer.valueOf(RawMaterial.RESOURCE_SCALES),
        Integer.valueOf(RawMaterial.RESOURCE_CRYSTAL),
        Integer.valueOf(RawMaterial.RESOURCE_RUBY),
        Integer.valueOf(RawMaterial.RESOURCE_EMERALD),
        Integer.valueOf(RawMaterial.RESOURCE_SAPPHIRE),
        Integer.valueOf(RawMaterial.RESOURCE_AGATE),
        Integer.valueOf(RawMaterial.RESOURCE_CITRINE),
        Integer.valueOf(RawMaterial.RESOURCE_PLATINUM)};
    public static final List<Integer> roomResources = new Vector<Integer>(Arrays.asList(resourceList));

    public CaveRoom() {
        super();
        name = "the cave";
        basePhyStats().setDisposition(basePhyStats().disposition() | PhyStats.IS_DARK);
        basePhyStats.setWeight(2);
        recoverPhyStats();
        climask = Places.CLIMASK_NORMAL;
    }

    @Override
    public String ID() {
        return "CaveRoom";
    }

    @Override
    public int domainType() {
        return Room.DOMAIN_INDOORS_CAVE;
    }

    @Override
    public int maxRange() {
        return 5;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if ((msg.amITarget(this) || (msg.targetMinor() == CMMsg.TYP_ADVANCE) || (msg.targetMinor() == CMMsg.TYP_RETREAT))
            && (!msg.source().isMonster())
            && (msg.source().curState().getHitPoints() < msg.source().maxState().getHitPoints())
            && (CMLib.dice().rollPercentage() == 1)
            && (CMLib.dice().rollPercentage() == 1)
            && (!CMSecurity.isDisabled(CMSecurity.DisFlag.AUTODISEASE))) {
            final Ability A = CMClass.getAbility("Disease_Syphilis");
            if ((A != null) && (msg.source().fetchEffect(A.ID()) == null) && (!CMSecurity.isAbilityDisabled(A.ID())))
                A.invoke(msg.source(), msg.source(), true, 0);
        }
        super.executeMsg(myHost, msg);
    }

    @Override
    public List<Integer> resourceChoices() {
        return CaveRoom.roomResources;
    }
}
