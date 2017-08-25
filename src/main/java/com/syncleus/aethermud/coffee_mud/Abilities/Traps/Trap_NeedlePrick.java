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
package com.planet_ink.coffee_mud.Abilities.Traps;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.Trap;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Items.interfaces.Item;
import com.planet_ink.coffee_mud.Items.interfaces.RawMaterial;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.interfaces.Drink;
import com.planet_ink.coffee_mud.core.interfaces.Physical;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;


public class Trap_NeedlePrick extends StdTrap {
    private final static String localizedName = CMLib.lang().L("poisoned pricker");

    @Override
    public String ID() {
        return "Trap_NeedlePrick";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ITEMS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    protected int trapLevel() {
        return 4;
    }

    @Override
    public String requiresToSet() {
        return "some poison";
    }

    public List<Ability> returnOffensiveAffects(Physical fromMe) {
        final List<Ability> offenders = new Vector<Ability>();

        for (final Enumeration<Ability> a = fromMe.effects(); a.hasMoreElements(); ) {
            final Ability A = a.nextElement();
            if ((A != null) && ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_POISON))
                offenders.add(A);
        }
        return offenders;
    }

    protected Item getPoison(MOB mob) {
        if (mob == null)
            return null;
        if (mob.location() == null)
            return null;
        for (int i = 0; i < mob.location().numItems(); i++) {
            final Item I = mob.location().getItem(i);
            if ((I != null)
                && (I instanceof Drink)) {
                final List<Ability> V = returnOffensiveAffects(I);
                if (V.size() > 0)
                    return I;
            }
        }
        return null;
    }

    @Override
    public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean perm) {
        if (P == null)
            return null;
        final Item I = getPoison(mob);
        if (I != null) {
            final List<Ability> V = returnOffensiveAffects(I);
            if (V.size() > 0)
                setMiscText(V.get(0).ID());
            I.destroy();
        }
        return super.setTrap(mob, P, trapBonus, qualifyingClassLevel, perm);
    }

    @Override
    public List<Item> getTrapComponents() {
        final List<Item> V = new Vector<Item>();
        final Item I = CMLib.materials().makeItemResource(RawMaterial.RESOURCE_POISON);
        Ability A = CMClass.getAbility(text());
        if (A == null)
            A = CMClass.getAbility("Poison");
        I.addNonUninvokableEffect(A);
        V.add(I);
        return V;
    }

    @Override
    public boolean canSetTrapOn(MOB mob, Physical P) {
        if (!super.canSetTrapOn(mob, P))
            return false;
        final Item I = getPoison(mob);
        if ((I == null)
            && (mob != null)) {
            mob.tell(L("You'll need to set down some poison first."));
            return false;
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
                target.location().show(target, null, null, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> avoid(s) touching a needle!"));
            else if (target.location().show(target, target, this, CMMsg.MASK_ALWAYS | CMMsg.MSG_NOISE, L("<S-NAME> prick(s) <S-HIM-HERSELF> on needle trap!"))) {
                super.spring(target);
                Ability A = CMClass.getAbility(text());
                if (A == null)
                    A = CMClass.getAbility("Poison");
                if (A != null)
                    A.invoke(invoker(), target, true, 0);
                if ((canBeUninvoked()) && (affected instanceof Item))
                    disable();
            }
        }
    }
}
