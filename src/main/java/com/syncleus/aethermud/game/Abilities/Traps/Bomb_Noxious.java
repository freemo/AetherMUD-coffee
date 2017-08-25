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

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;


public class Bomb_Noxious extends StdBomb {
    private final static String localizedName = CMLib.lang().L("stink bomb");

    @Override
    public String ID() {
        return "Bomb_Noxious";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int trapLevel() {
        return 12;
    }

    @Override
    public String requiresToSet() {
        return "an egg";
    }

    @Override
    public List<Item> getTrapComponents() {
        final List<Item> V = new Vector<Item>();
        V.add(CMLib.materials().makeItemResource(RawMaterial.RESOURCE_EGGS));
        return V;
    }

    @Override
    public boolean canSetTrapOn(MOB mob, Physical P) {
        if (!super.canSetTrapOn(mob, P))
            return false;
        if ((!(P instanceof Item))
            || (((Item) P).material() != RawMaterial.RESOURCE_EGGS)) {
            if (mob != null)
                mob.tell(L("You an egg to make this out of."));
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
                target.location().show(target, null, null, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> avoid(s) the stink bomb!"));
            else if (target.location().show(invoker(), target, this, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("@x1 explodes stink into <T-YOUPOSS> eyes!", affected.name()))) {
                super.spring(target);
                Ability A = CMClass.getAbility("Spell_StinkingCloud");
                if (A != null) {
                    A.invoke(target, target, true, invoker().phyStats().level() + abilityCode());
                    A = target.fetchEffect(A.ID());
                    if (A != null)
                        A.setInvoker(invoker());
                }
            }
        }
    }

}
