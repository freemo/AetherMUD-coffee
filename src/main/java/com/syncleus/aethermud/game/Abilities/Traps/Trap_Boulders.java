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

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Abilities.interfaces.Trap;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;


public class Trap_Boulders extends StdTrap {
    private final static String localizedName = CMLib.lang().L("boulders");

    @Override
    public String ID() {
        return "Trap_Boulders";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ROOMS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    protected int trapLevel() {
        return 20;
    }

    @Override
    public String requiresToSet() {
        return "50 pounds of boulders";
    }

    @Override
    public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean perm) {
        if (P == null)
            return null;
        if (mob != null) {
            final Item I = findMostOfMaterial(mob.location(), RawMaterial.MATERIAL_ROCK);
            if (I != null)
                super.destroyResources(mob.location(), I.material(), 50);
        }
        return super.setTrap(mob, P, trapBonus, qualifyingClassLevel, perm);
    }

    @Override
    public List<Item> getTrapComponents() {
        final List<Item> V = new Vector<Item>();
        for (int i = 0; i < 50; i++)
            V.add(CMLib.materials().makeItemResource(RawMaterial.RESOURCE_STONE));
        return V;
    }

    @Override
    public boolean canSetTrapOn(MOB mob, Physical P) {
        if (!super.canSetTrapOn(mob, P))
            return false;
        if (mob != null) {
            final Item I = findMostOfMaterial(mob.location(), RawMaterial.MATERIAL_ROCK);
            if ((I == null)
                || (super.findNumberOfResource(mob.location(), I.material()) < 50)) {
                mob.tell(L("You'll need to set down at least 50 pounds of rock first."));
                return false;
            }
            if (P instanceof Room) {
                final Room R = (Room) P;
                if ((R.domainType() != Room.DOMAIN_INDOORS_CAVE)
                    && (R.domainType() != Room.DOMAIN_OUTDOORS_MOUNTAINS)
                    && (R.domainType() != Room.DOMAIN_OUTDOORS_ROCKS)
                    && (R.domainType() != Room.DOMAIN_OUTDOORS_HILLS)
                    && ((R.getAtmosphere() & RawMaterial.MATERIAL_ROCK) == 0)) {
                    mob.tell(L("You can only set this trap in caves, or by mountains or hills."));
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void spring(MOB target) {
        if ((target != invoker()) && (target.location() != null)) {
            if ((!invoker().mayIFight(target))
                || (isLocalExempt(target))
                || (invoker().getGroupMembers(new HashSet<MOB>()).contains(target))
                || (target == invoker())
                || (doesSaveVsTraps(target)))
                target.location().show(target, null, null, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> avoid(s) setting off a boulder trap!"));
            else if (target.location().show(target, target, this, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> trigger(s) a trap!"))) {
                super.spring(target);
                final int damage = CMLib.dice().roll(trapLevel() + abilityCode(), 20, 1);
                CMLib.combat().postDamage(invoker(), target, this, damage, CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS | CMMsg.TYP_JUSTICE, Weapon.TYPE_BASHING, L("Dozens of boulders <DAMAGE> <T-NAME>!"));
            }
        }
    }
}
