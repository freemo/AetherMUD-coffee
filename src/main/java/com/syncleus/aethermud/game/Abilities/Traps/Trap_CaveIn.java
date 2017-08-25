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
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;


public class Trap_CaveIn extends StdTrap {
    private final static String localizedName = CMLib.lang().L("cave-in");

    @Override
    public String ID() {
        return "Trap_CaveIn";
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
        return 22;
    }

    @Override
    public String requiresToSet() {
        return "100 pounds of wood";
    }

    @Override
    public int baseRejuvTime(int level) {
        return 6;
    }

    @Override
    public List<Item> getTrapComponents() {
        final List<Item> V = new Vector<Item>();
        for (int i = 0; i < 100; i++)
            V.add(CMLib.materials().makeItemResource(RawMaterial.RESOURCE_WOOD));
        return V;
    }

    @Override
    public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean perm) {
        if (P == null)
            return null;
        if (mob != null) {
            final Item I = findMostOfMaterial(mob.location(), RawMaterial.MATERIAL_WOODEN);
            if (I != null)
                super.destroyResources(mob.location(), I.material(), 100);
        }
        return super.setTrap(mob, P, trapBonus, qualifyingClassLevel, perm);
    }

    @Override
    public boolean canSetTrapOn(MOB mob, Physical P) {
        if (!super.canSetTrapOn(mob, P))
            return false;
        if (mob != null) {
            final Item I = findMostOfMaterial(mob.location(), RawMaterial.MATERIAL_WOODEN);
            if ((I == null)
                || (super.findNumberOfResource(mob.location(), I.material()) < 100)) {
                mob.tell(L("You'll need to set down at least 100 pounds of wood first."));
                return false;
            }
        }
        if (P instanceof Room) {
            final Room R = (Room) P;
            if ((R.domainType() != Room.DOMAIN_INDOORS_CAVE)
                && ((R.getAtmosphere() & RawMaterial.MATERIAL_ROCK) == 0)) {
                if (mob != null)
                    mob.tell(L("You can only set this trap in caves."));
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((sprung)
            && (affected != null)
            && (!disabled())
            && (tickDown >= 0)) {
            if (((msg.targetMinor() == CMMsg.TYP_LEAVE)
                || (msg.targetMinor() == CMMsg.TYP_ENTER)
                || (msg.targetMinor() == CMMsg.TYP_FLEE))
                && (msg.amITarget(affected))) {
                msg.source().tell(L("The cave-in prevents entry or exit from here."));
                return false;
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public void spring(MOB target) {
        if ((target != invoker()) && (target.location() != null)) {
            if ((doesSaveVsTraps(target))
                || (invoker().getGroupMembers(new HashSet<MOB>()).contains(target)))
                target.location().show(target, null, null, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> avoid(s) setting off a cave-in!"));
            else {
                String triggerMsg = L("<S-NAME> trigger(s) a cave-in!");
                String damageMsg = L("The cave-in <DAMAGE> <T-NAME>!");
                if (newMessaging.size() > 0) {
                    triggerMsg = newMessaging.get(0);
                    if (newMessaging.size() > 1)
                        damageMsg = newMessaging.get(1);
                }
                if (target.location().show(target, target, this, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, triggerMsg)) {
                    super.spring(target);
                    if ((affected != null)
                        && (affected instanceof Room)) {
                        final Room R = (Room) affected;
                        for (int i = 0; i < R.numInhabitants(); i++) {
                            final MOB M = R.fetchInhabitant(i);
                            if ((M != null) && (M != invoker())) {
                                if (invoker().mayIFight(M)) {
                                    final int damage = CMLib.dice().roll(trapLevel() + abilityCode(), 20, 1);
                                    CMLib.combat().postDamage(invoker(), M, this, damage, CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS | CMMsg.TYP_JUSTICE, Weapon.TYPE_BASHING, damageMsg);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
