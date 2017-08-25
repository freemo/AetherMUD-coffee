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
import com.planet_ink.game.Abilities.interfaces.Trap;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Items.interfaces.Weapon;
import com.planet_ink.game.Locales.interfaces.Room;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;


public class Trap_CrushingRoom extends StdTrap {
    private final static String localizedName = CMLib.lang().L("crushing room");

    @Override
    public String ID() {
        return "Trap_CrushingRoom";
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
        return 24;
    }

    @Override
    public String requiresToSet() {
        return "100 pounds of stone";
    }

    @Override
    public int baseRejuvTime(int level) {
        return 16;
    }

    @Override
    public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean perm) {
        if (P == null)
            return null;
        if (mob != null) {
            final Item I = findMostOfMaterial(mob.location(), RawMaterial.MATERIAL_ROCK);
            if (I != null)
                super.destroyResources(mob.location(), I.material(), 100);
        }
        return super.setTrap(mob, P, trapBonus, qualifyingClassLevel, perm);
    }

    @Override
    public List<Item> getTrapComponents() {
        final List<Item> V = new Vector<Item>();
        for (int i = 0; i < 100; i++)
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
                || (super.findNumberOfResource(mob.location(), I.material()) < 100)) {
                mob.tell(L("You'll need to set down at least 100 pounds of stone first."));
                return false;
            }
        }
        if (P instanceof Room) {
            final Room R = (Room) P;
            if ((R.domainType() & Room.INDOORS) == 0) {
                if (mob != null)
                    mob.tell(L("You can only set this trap indoors."));
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
                || (msg.targetMinor() == CMMsg.TYP_FLEE))
                && (msg.amITarget(affected))) {
                msg.source().tell(L("The exits are blocked! You can't get out!"));
                return false;
            } else if ((msg.targetMinor() == CMMsg.TYP_ENTER)
                && (msg.amITarget(affected))) {
                msg.source().tell(L("The entry to that room is blocked!"));
                return false;
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((tickID == Tickable.TICKID_TRAP_RESET) && (getReset() > 0)) {
            if ((sprung)
                && (affected instanceof Room)
                && (!disabled())
                && (tickDown >= 0)) {
                final Room R = (Room) affected;
                if (tickDown > 13)
                    R.showHappens(CMMsg.MSG_OK_VISUAL, L("The walls start closing in around you!"));
                else if (tickDown > 4) {
                    for (int i = 0; i < R.numInhabitants(); i++) {
                        final MOB M = R.fetchInhabitant(i);
                        if ((M != null) && (M != invoker())) {
                            if (invoker().mayIFight(M)) {
                                final int damage = CMLib.dice().roll(trapLevel() + abilityCode(), 30, 1);
                                CMLib.combat().postDamage(invoker(), M, this, damage, CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS | CMMsg.TYP_JUSTICE, Weapon.TYPE_BASHING, L("The crushing walls <DAMAGE> <T-NAME>!"));
                            }
                        }
                    }
                } else {
                    R.showHappens(CMMsg.MSG_OK_VISUAL, L("The walls begin retracting..."));
                }
            }
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void spring(MOB target) {
        if ((target != invoker()) && (target.location() != null)) {
            if ((doesSaveVsTraps(target))
                || (invoker().getGroupMembers(new HashSet<MOB>()).contains(target)))
                target.location().show(target, null, null, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> avoid(s) setting off a trap!"));
            else if (target.location().show(target, target, this, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> trigger(s) a trap!"))) {
                super.spring(target);
                target.location().showHappens(CMMsg.MSG_OK_VISUAL, L("The exits are blocked off! The walls start closing in!"));
            }
        }
    }
}
