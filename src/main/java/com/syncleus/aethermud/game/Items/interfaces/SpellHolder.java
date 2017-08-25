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
package com.syncleus.aethermud.game.Items.interfaces;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;

import java.util.List;


/**
 * A spell holder is an item that can contain spells for use in some
 * way, such as a scroll, staff, wand, pills, potions, etc.
 * @author Bo Zimmerman
 */
public interface SpellHolder extends Item {
    /**
     * Returns the list of ability objects that this item contains.
     * @see SpellHolder#getSpellList()
     * @see SpellHolder#setSpellList(String)
     * @return the list of ability objects that this item contains
     */
    public List<Ability> getSpells();

    /**
     * Gets the list of abilities that this item contains as
     * a semicolon-delimited list, with any spell arguments
     * in parenthesis after the spell ID.
     * @see SpellHolder#setSpellList(String)
     * @return the list of ability names semicolon delimited
     */
    public String getSpellList();

    /**
     * Sets the list of abilities that this item contains as
     * a semicolon-delimited list, with any spell arguments
     * in parenthesis after the spell ID.
     * @see SpellHolder#getSpellList()
     * @param list the list of ability names semicolon delimited
     */
    public void setSpellList(String list);
}
