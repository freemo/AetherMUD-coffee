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
package com.syncleus.aethermud.game.MOBS;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Common.interfaces.Faction;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;


public class FrostGiant extends StdMOB {
    public FrostGiant() {
        super();
        username = "a frost giant";
        setDescription("A tall blueish humanoid standing about 16 feet tall and very smelly.");
        setDisplayText("A frost giant looks down at you.");
        CMLib.factions().setAlignment(this, Faction.Align.EVIL);
        setMoney(0);
        basePhyStats.setWeight(3500 + CMLib.dice().roll(1, 1000, 0));

        baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, 10 + CMLib.dice().roll(1, 6, 0));
        baseCharStats().setStat(CharStats.STAT_STRENGTH, 29);
        baseCharStats().setStat(CharStats.STAT_DEXTERITY, 9);
        baseCharStats().setMyRace(CMClass.getRace("Giant"));
        baseCharStats().getMyRace().startRacing(this, false);

        basePhyStats().setDamage(19);
        basePhyStats().setSpeed(1.0);
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(12);
        basePhyStats().setArmor(0);

        baseState.setHitPoints(CMLib.dice().roll(basePhyStats().level(), 20, basePhyStats().level()));

        Ability A = CMClass.getAbility("Immunities");
        if (A != null) {
            A.setMiscText("COLD");
            addNonUninvokableEffect(A);
        }
        A = CMClass.getAbility("Chant_FeelHeat");
        if (A != null)
            addNonUninvokableEffect(A);

        addBehavior(CMClass.getBehavior("Aggressive"));

        Weapon w = CMClass.getWeapon("BattleAxe");
        if (w != null) {
            w.wearAt(Wearable.WORN_WIELD);
            this.addItem(w);
        }

        recoverMaxState();
        resetToMaxState();
        recoverPhyStats();
        recoverCharStats();
    }

    @Override
    public String ID() {
        return "FrostGiant";
    }

}
