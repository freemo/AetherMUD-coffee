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
package com.planet_ink.game.Libraries.interfaces;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Items.interfaces.Armor;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.Weapon;

import java.util.Map;

public interface ItemBalanceLibrary extends CMLibrary {
    public int timsLevelCalculator(Item I);

    public int timsLevelCalculator(Item I, Ability ADJ, Ability RES, Ability CAST, int castMul);

    public boolean fixRejuvItem(Item I);

    public void toneDownWeapon(Weapon W, Ability ADJ);

    public void toneDownArmor(Armor A, Ability ADJ);

    public boolean toneDownValue(Item I);

    public int timsBaseLevel(Item I);

    public void balanceItemByLevel(Item I);

    public int levelsFromCaster(Item savedI, Ability CAST);

    public int levelsFromAdjuster(Item savedI, Ability ADJ);

    public boolean itemFix(Item I, int lvlOr0, StringBuffer changes);

    public Ability[] getTimsAdjResCast(Item I, int[] castMul);

    public Item enchant(Item I, int pct);

    public int levelsFromAbility(Item savedI);

    public Map<String, String> timsItemAdjustments(Item I,
                                                   int level,
                                                   int material,
                                                   int hands,
                                                   int wclass,
                                                   int reach,
                                                   long worndata);
}
