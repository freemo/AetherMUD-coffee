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

import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;


public class Bomb_AcidBurst extends StdBomb {
    private final static String localizedName = CMLib.lang().L("acid burst bomb");

    @Override
    public String ID() {
        return "Bomb_AcidBurst";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int trapLevel() {
        return 20;
    }

    @Override
    public String requiresToSet() {
        return "some lemons";
    }

    @Override
    public List<Item> getTrapComponents() {
        final List<Item> V = new Vector<Item>();
        V.add(CMLib.materials().makeItemResource(RawMaterial.RESOURCE_LEMONS));
        return V;
    }

    @Override
    public boolean canSetTrapOn(MOB mob, Physical P) {
        if (!super.canSetTrapOn(mob, P))
            return false;
        if ((!(P instanceof Item))
            || (((Item) P).material() != RawMaterial.RESOURCE_LEMONS)) {
            if (mob != null)
                mob.tell(L("You need some lemons to make this out of."));
            return false;
        }
        return true;
    }

    @Override
    public void spring(MOB target) {
        if (target.location() != null) {
            if ((!invoker().mayIFight(target))
                || (isLocalExempt(target))
                || (invoker().getGroupMembers(new HashSet<MOB>()).contains(target))
                || (target == invoker())
                || (doesSaveVsTraps(target)))
                target.location().show(target, null, null, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> avoid(s) the acid burst!"));
            else if (target.location().show(invoker(), target, this, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("@x1 sprays acid all over <T-NAME>!", affected.name()))) {
                super.spring(target);
                CMLib.combat().postDamage(invoker(), target, null, CMLib.dice().roll(trapLevel() + abilityCode(), 24, 1), CMMsg.MASK_ALWAYS | CMMsg.TYP_ACID, Weapon.TYPE_MELTING, L("The acid <DAMAGE> <T-NAME>!"));
            }
        }
    }

}
