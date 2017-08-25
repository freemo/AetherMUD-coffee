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
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.CagedAnimal;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;


public class Trap_MonsterCage extends StdTrap {
    private final static String localizedName = CMLib.lang().L("monster cage");
    protected MOB monster = null;

    @Override
    public String ID() {
        return "Trap_MonsterCage";
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
        return 10;
    }

    @Override
    public String requiresToSet() {
        return "a caged monster";
    }

    protected Item getCagedAnimal(MOB mob) {
        if (mob == null)
            return null;
        if (mob.location() == null)
            return null;
        for (int i = 0; i < mob.location().numItems(); i++) {
            final Item I = mob.location().getItem(i);
            if (I instanceof CagedAnimal) {
                final MOB M = ((CagedAnimal) I).unCageMe();
                if (M != null)
                    return I;
            }
        }
        return null;
    }

    @Override
    public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean perm) {
        if (P == null)
            return null;
        final Item I = getCagedAnimal(mob);
        if (I != null) {
            setMiscText(((CagedAnimal) I).cageText());
            I.destroy();
        }
        return super.setTrap(mob, P, trapBonus, qualifyingClassLevel, perm);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((tickID == Tickable.TICKID_TRAP_RESET) && (getReset() > 0)) {
            // recage the motherfather
            if ((tickDown <= 1)
                && (monster != null)
                && (monster.amDead() || (!monster.isInCombat())))
                monster.destroy();
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void unInvoke() {
        if ((monster != null) && (canBeUninvoked()))
            monster.destroy();
        super.unInvoke();
    }

    @Override
    public List<Item> getTrapComponents() {
        final List<Item> V = new Vector<Item>();
        final Item I = CMClass.getItem("GenCaged");
        ((CagedAnimal) I).setCageText(text());
        I.recoverPhyStats();
        I.text();
        V.add(I);
        return V;
    }

    @Override
    public boolean canSetTrapOn(MOB mob, Physical P) {
        if (!super.canSetTrapOn(mob, P))
            return false;
        if (getCagedAnimal(mob) == null) {
            if (mob != null)
                mob.tell(L("You'll need to set down a caged animal of some sort first."));
            return false;
        }
        return true;
    }

    @Override
    public void spring(MOB target) {
        if ((target != invoker()) && (target.location() != null) && (text().length() > 0)) {
            if ((doesSaveVsTraps(target))
                || (invoker().getGroupMembers(new HashSet<MOB>()).contains(target)))
                target.location().show(target, null, null, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> avoid(s) opening a monster cage!"));
            else if (target.location().show(target, target, this, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> trip(s) open a caged monster!"))) {
                super.spring(target);
                final Item I = CMClass.getItem("GenCaged");
                ((CagedAnimal) I).setCageText(text());
                monster = ((CagedAnimal) I).unCageMe();
                if (monster != null) {
                    monster.basePhyStats().setRejuv(PhyStats.NO_REJUV);
                    monster.bringToLife(target.location(), true);
                    monster.setVictim(target);
                    if (target.getVictim() == null)
                        target.setVictim(monster);
                }
            }
        }
    }
}
