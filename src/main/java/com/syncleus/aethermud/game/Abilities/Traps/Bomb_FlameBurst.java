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

import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Items.interfaces.Weapon;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Drink;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;


public class Bomb_FlameBurst extends StdBomb {
    private final static String localizedName = CMLib.lang().L("flame burst bomb");

    @Override
    public String ID() {
        return "Bomb_FlameBurst";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int trapLevel() {
        return 17;
    }

    @Override
    public String requiresToSet() {
        return "some lamp oil";
    }

    @Override
    public List<Item> getTrapComponents() {
        final List<Item> V = new Vector<Item>();
        V.add(CMClass.getBasicItem("OilFlask"));
        return V;
    }

    @Override
    public boolean canSetTrapOn(MOB mob, Physical P) {
        if (!super.canSetTrapOn(mob, P))
            return false;
        if ((!(P instanceof Item))
            || (!(P instanceof Drink))
            || (!((((Drink) P).containsDrink()) || (((Drink) P).liquidType() != RawMaterial.RESOURCE_LAMPOIL)))
            && (((Item) P).material() != RawMaterial.RESOURCE_LAMPOIL)) {
            if (mob != null)
                mob.tell(L("You need some lamp oil to make this out of."));
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
                target.location().show(target, null, null, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> avoid(s) the flame burst!"));
            else if (target.location().show(invoker(), target, this, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, (affected.name() + " flames all over <T-NAME>!") + CMLib.protocol().msp("fireball.wav", 30))) {
                super.spring(target);
                CMLib.combat().postDamage(invoker(), target, null, CMLib.dice().roll(trapLevel() + abilityCode(), 12, 1), CMMsg.MASK_ALWAYS | CMMsg.TYP_FIRE, Weapon.TYPE_BURNING, L("The flames <DAMAGE> <T-NAME>!"));
            }
        }
    }

}
