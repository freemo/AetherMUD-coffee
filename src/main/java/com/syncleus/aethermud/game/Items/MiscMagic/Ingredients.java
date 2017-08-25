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
package com.planet_ink.game.Items.MiscMagic;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Decayable;
import com.planet_ink.game.core.interfaces.Environmental;


public class Ingredients extends BagOfEndlessness {
    boolean alreadyFilled = false;

    public Ingredients() {
        super();
        setName("an ingredients bag");
        secretIdentity = "The Archon's Secret Ingredient Bag";
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "Ingredients";
    }

    protected Item makeResource(String name, int type) {
        Item I = null;
        if (((type & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_FLESH)
            || ((type & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_VEGETATION))
            I = CMClass.getItem("GenFoodResource");
        else if ((type & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_LIQUID)
            I = CMClass.getItem("GenLiquidResource");
        else
            I = CMClass.getItem("GenResource");
        I.setName(name);
        I.setDisplayText(L("@x1 has been left here.", name));
        I.setDescription(L("It looks like @x1", name));
        I.setMaterial(type);
        I.setBaseValue(RawMaterial.CODES.VALUE(type));
        I.basePhyStats().setWeight(1);
        CMLib.materials().addEffectsToResource(I);
        I.recoverPhyStats();
        I.setContainer(this);
        if (I instanceof Decayable) {
            ((Decayable) I).setDecayTime(0);
            final Ability A = I.fetchEffect("Poison_Rotten");
            if (A != null)
                I.delEffect(A);
        }
        if (owner() instanceof Room)
            ((Room) owner()).addItem(I);
        else if (owner() instanceof MOB)
            ((MOB) owner()).addItem(I);
        return I;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if ((!alreadyFilled) && (owner() != null)) {
            alreadyFilled = true;
            if (!hasContent()) {
                for (final int rsc : RawMaterial.CODES.ALL())
                    makeResource(RawMaterial.CODES.NAME(rsc).toLowerCase(), rsc);
            }
        } else if (msg.amITarget(this)
            && (msg.tool() instanceof Decayable)
            && (msg.tool() instanceof Item)
            && (((Item) msg.tool()).container() == this)
            && (((Item) msg.tool()).owner() != null)) {
            ((Decayable) msg.tool()).setDecayTime(0);
            final Ability A = ((Item) msg.tool()).fetchEffect("Poison_Rotten");
            if (A != null)
                ((Item) msg.tool()).delEffect(A);
        }
        super.executeMsg(myHost, msg);
    }
}
