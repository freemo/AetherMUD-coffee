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
package com.syncleus.aethermud.game.Items.MiscMagic;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class Wand_Fire extends StdWand {
    public Wand_Fire() {
        super();

        setName("a gold wand");
        setDisplayText("a golden wand is here.");
        setDescription("A wand made out of gold, with a deep red ruby at the tip");
        secretIdentity = "The wand of fire.  Responds to 'Blaze' and 'Burn'";
        this.setUsesRemaining(50);
        baseGoldValue = 20000;
        basePhyStats().setLevel(12);
        material = RawMaterial.RESOURCE_OAK;
        recoverPhyStats();
        secretWord = "BLAZE, BURN";
    }

    @Override
    public String ID() {
        return "Wand_Fire";
    }

    @Override
    public void setSpell(Ability theSpell) {
        super.setSpell(theSpell);
        secretWord = "BLAZE, BURN";
    }

    @Override
    public void setMiscText(String newText) {
        super.setMiscText(newText);
        secretWord = "BLAZE, BURN";
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        final MOB mob = msg.source();
        switch (msg.sourceMinor()) {
            case CMMsg.TYP_WAND_USE:
                if ((mob.isMine(this))
                    && (!amWearingAt(Wearable.IN_INVENTORY))
                    && (msg.target() instanceof MOB)
                    && (mob.location().isInhabitant((MOB) msg.target()))) {
                    final MOB target = (MOB) msg.target();
                    int x = msg.targetMessage().toUpperCase().indexOf("BLAZE");
                    if (x >= 0) {
                        final Ability spell = CMClass.getAbility("Spell_BurningHands");
                        if ((usesRemaining() > 0) && (spell != null) && (useTheWand(spell, mob, 0))) {
                            this.setUsesRemaining(this.usesRemaining() - 1);
                            spell.invoke(mob, target, true, phyStats().level());
                            return;
                        }
                    }
                    x = msg.targetMessage().toUpperCase().indexOf("BURN");
                    if (x >= 0) {
                        final Ability spell = CMClass.getAbility("Spell_Fireball");
                        if ((usesRemaining() > 4) && (spell != null) && (useTheWand(spell, mob, 0))) {
                            this.setUsesRemaining(this.usesRemaining() - 5);
                            spell.invoke(mob, target, true, phyStats().level());
                            return;
                        }
                    }
                }
                return;
            default:
                break;
        }
        super.executeMsg(myHost, msg);
    }
}
