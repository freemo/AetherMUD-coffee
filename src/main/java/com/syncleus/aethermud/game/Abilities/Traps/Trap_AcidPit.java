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
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;
import java.util.Vector;


public class Trap_AcidPit extends Trap_RoomPit {
    private final static String localizedName = CMLib.lang().L("acid pit");

    @Override
    public String ID() {
        return "Trap_AcidPit";
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
        return 18;
    }

    @Override
    public String requiresToSet() {
        return L("some limes");
    }

    @Override
    public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean perm) {
        if (P == null)
            return null;
        if (mob != null) {
            Item I = this.findFirstResource(mob.location(), RawMaterial.RESOURCE_LIMES);
            if (I != null)
                super.destroyResources(mob.location(), I.material(), 1);
        }
        return super.setTrap(mob, P, trapBonus, qualifyingClassLevel, perm);
    }

    @Override
    public List<Item> getTrapComponents() {
        final List<Item> V = new Vector<Item>();
        V.add(CMLib.materials().makeItemResource(RawMaterial.RESOURCE_LIMES));
        return V;
    }

    @Override
    public boolean canSetTrapOn(MOB mob, Physical P) {
        if (!super.canSetTrapOn(mob, P))
            return false;
        if (mob != null) {
            Item I = this.findFirstResource(mob.location(), RawMaterial.RESOURCE_LIMES);
            if ((I == null)
                || (super.findNumberOfResource(mob.location(), I.material()) < 1)) {
                mob.tell(L("You'll need to set down some limes first."));
                return false;
            }
        }
        return true;
    }

    @Override
    public int baseRejuvTime(int level) {
        int time = super.baseRejuvTime(level);
        if (time < 15)
            time = 15;
        return time;
    }

    @Override
    public void finishSpringing(MOB target) {
        if ((!invoker().mayIFight(target)) || (target.phyStats().weight() < 5))
            target.location().show(target, null, CMMsg.MSG_OK_ACTION, L("<S-NAME> float(s) gently into the pit!"));
        else {
            target.location().show(target, null, CMMsg.MSG_OK_ACTION, L("<S-NAME> hit(s) the pit floor with a THUMP!"));
            final int damage = CMLib.dice().roll(trapLevel() + abilityCode(), 6, 1);
            CMLib.combat().postDamage(invoker(), target, this, damage, CMMsg.MASK_MALICIOUS | CMMsg.TYP_ACID, -1, null);
            target.location().showHappens(CMMsg.MSG_OK_VISUAL, L("Acid starts pouring into the room!"));
        }
        CMLib.commands().postLook(target, true);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((tickID == Tickable.TICKID_TRAP_RESET) && (getReset() > 0)) {
            if ((sprung)
                && (affected instanceof Room)
                && (pit != null)
                && (pit.size() > 1)
                && (!disabled())) {
                final Room R = pit.get(0);
                for (int i = 0; i < R.numInhabitants(); i++) {
                    final MOB M = R.fetchInhabitant(i);
                    if ((M != null) && (M != invoker())) {
                        final int damage = CMLib.dice().roll(trapLevel() + abilityCode(), 6, 1);
                        CMLib.combat().postDamage(invoker(), M, this, damage, CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS | CMMsg.TYP_ACID, Weapon.TYPE_MELTING, L("The acid <DAMAGE> <T-NAME>!"));
                        CMLib.combat().postRevengeAttack(M, invoker);
                    }
                }
                return super.tick(ticking, tickID);
            }
            return false;
        }
        return super.tick(ticking, tickID);
    }

}
