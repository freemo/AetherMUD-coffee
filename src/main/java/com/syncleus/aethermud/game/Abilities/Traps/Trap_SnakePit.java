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
import com.planet_ink.game.Common.interfaces.PhyStats;
import com.planet_ink.game.Items.interfaces.CagedAnimal;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.interfaces.Physical;
import com.planet_ink.game.core.interfaces.Tickable;

import java.util.List;
import java.util.Vector;


public class Trap_SnakePit extends Trap_RoomPit {
    private final static String localizedName = CMLib.lang().L("snake pit");
    protected List<MOB> monsters = null;

    @Override
    public String ID() {
        return "Trap_SnakePit";
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
        return "some caged snakes";
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
                if ((M != null) && (M.baseCharStats().getMyRace().racialCategory().equalsIgnoreCase("Ophidian")))
                    return I;
            }
        }
        return null;
    }

    @Override
    public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean perm) {
        if (P == null)
            return null;
        Item I = getCagedAnimal(mob);
        final StringBuffer buf = new StringBuffer("<SNAKES>");
        int num = 0;
        while ((I != null) && ((++num) < 6)) {
            buf.append(((CagedAnimal) I).cageText());
            I.destroy();
            I = getCagedAnimal(mob);
        }
        buf.append("</SNAKES>");
        setMiscText(buf.toString());
        return super.setTrap(mob, P, trapBonus, qualifyingClassLevel, perm);
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
                mob.tell(L("You'll need to set down some caged snakes first."));
            return false;
        }
        return true;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((tickID == Tickable.TICKID_TRAP_RESET) && (getReset() > 0)) {
            // recage the motherfather
            if ((tickDown <= 1) && (monsters != null)) {
                for (int i = 0; i < monsters.size(); i++) {
                    final MOB M = monsters.get(i);
                    if (M.amDead() || (!M.isInCombat()))
                        M.destroy();
                }
                monsters = null;
            }
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void finishSpringing(MOB target) {
        if ((!invoker().mayIFight(target)) || (target.phyStats().weight() < 5))
            target.location().show(target, null, CMMsg.MSG_OK_ACTION, L("<S-NAME> float(s) gently into the pit!"));
        else {
            target.location().show(target, null, CMMsg.MSG_OK_ACTION, L("<S-NAME> hit(s) the pit floor with a THUMP!"));
            final int damage = CMLib.dice().roll(trapLevel() + abilityCode(), 6, 1);
            CMLib.combat().postDamage(invoker(), target, this, damage, CMMsg.MASK_MALICIOUS | CMMsg.MASK_ALWAYS | CMMsg.TYP_JUSTICE, -1, null);
        }
        final List<String> snakes = new Vector<String>();
        String t = text();
        int x = t.indexOf("</MOBITEM><MOBITEM>");
        while (x >= 0) {
            snakes.add(t.substring(0, x + 10));
            t = t.substring(x + 10);
            x = t.indexOf("</MOBITEM><MOBITEM>");
        }
        if (t.length() > 0)
            snakes.add(t);
        if (snakes.size() > 0)
            monsters = new Vector<MOB>();
        for (int i = 0; i < snakes.size(); i++) {
            t = snakes.get(i);
            final Item I = CMClass.getItem("GenCaged");
            ((CagedAnimal) I).setCageText(t);
            final MOB monster = ((CagedAnimal) I).unCageMe();
            if (monster != null) {
                monsters.add(monster);
                monster.basePhyStats().setRejuv(PhyStats.NO_REJUV);
                monster.bringToLife(target.location(), true);
                monster.setVictim(target);
                if (target.getVictim() == null)
                    target.setVictim(monster);
            }
        }
        CMLib.commands().postLook(target, true);
    }
}
