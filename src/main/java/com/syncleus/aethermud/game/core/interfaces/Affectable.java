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
package com.syncleus.aethermud.game.core.interfaces;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Behaviors.interfaces.Behavior;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;

import java.util.Enumeration;


/**
 *
 * Something that can be affected, and has environmental stats that
 * can be affected as well.
 * @author Bo Zimmerman
 *
 */
public interface Affectable {
    /**
     * Object containing a set of base, unmodified, mostly numeric fields.  The values on the fields
     * in this object will be as they were set by the builder. This object is used as a basis for
     * the recoverPhyStats() method.  See the PhyStats interface for information on the fields herein.
     * @see com.syncleus.aethermud.game.core.interfaces.Affectable#phyStats()
     * @see com.syncleus.aethermud.game.core.interfaces.Affectable#recoverPhyStats()
     * @see com.syncleus.aethermud.game.Common.interfaces.PhyStats
     * @return a set of state fields
     */
    public PhyStats basePhyStats();

    /**
     * Re-sets the object containing a set of base, unmodified, mostly numeric fields.  The values on the fields
     * in this object will be as they were set by the builder. This object is used as a basis for
     * the recoverPhyStats() method.  See the PhyStats interface for information on the fields herein. This
     * method is rarely called -- the fields therein are usually set using setter methods from the PhyStats
     * interface on the object itself.
     * @see com.syncleus.aethermud.game.core.interfaces.Affectable#phyStats()
     * @see com.syncleus.aethermud.game.core.interfaces.Affectable#recoverPhyStats()
     * @see com.syncleus.aethermud.game.Common.interfaces.PhyStats
     * @param newStats a set of state fields
     */
    public void setBasePhyStats(PhyStats newStats);

    /**
     * Object containing a set of current, modified, usable, mostly numeric fields.  This object is based on
     * the object from basePhyStats() and then updated and modified by the recoverPhyStats() method.
     * See the PhyStats interface for information on the fields herein.
     * @see com.syncleus.aethermud.game.core.interfaces.Affectable#basePhyStats()
     * @see com.syncleus.aethermud.game.core.interfaces.Affectable#recoverPhyStats()
     * @see com.syncleus.aethermud.game.Common.interfaces.PhyStats
     * @return the current set of state fields
     */
    public PhyStats phyStats();

    /**
     * This method copies the basePhyStats() object into the phyStats() object, then makes repeated calls to
     * all surrounding objects  with affectPhyStats(Environmental,PhyStats) method.   Surrounding  objects
     * include the room where the object is located, the Ability objects in the Effects list, the Behaviors
     * in the behaviors list, and race/charclass/area if applicable.  Those methods will then make all necessary
     * adjustments to the values in the new phyStats() object.  When it returns, phyStats() will have a totally
     * updated object.  This method must be called in code whenever the object is placed on the map, or when
     * anything changes in its environment, such as location, effects, or other states.
     * @see com.syncleus.aethermud.game.core.interfaces.Affectable#basePhyStats()
     * @see com.syncleus.aethermud.game.core.interfaces.Affectable#phyStats()
     * @see com.syncleus.aethermud.game.core.interfaces.Affectable#addEffect(Ability)
     * @see com.syncleus.aethermud.game.core.interfaces.PhysicalAgent#addBehavior(Behavior)
     * @see com.syncleus.aethermud.game.Common.interfaces.PhyStats
     */
    public void recoverPhyStats();

    /**
     * Add a new effect to this object, whether permanent or temporary.  After calling this method,
     * recoverPhyStats() should be called next in case this ability object modifies the stats.
     * An Ability with a given ID() can only be added once per object.
     * @see com.syncleus.aethermud.game.Abilities.interfaces.Ability
     * @see com.syncleus.aethermud.game.core.interfaces.Affectable#recoverPhyStats()
     * @param to The ability object to add as an effect.
     */
    public void addEffect(Ability to);

    /**
     * Same as addEffect(Ability), but will set the Ability object as never being able to be uninvoked.
     * recoverPhyStats() method  should be called next.
     * An Ability with a given ID() can only be added once per object.
     * @see com.syncleus.aethermud.game.Abilities.interfaces.Ability
     * @see com.syncleus.aethermud.game.core.interfaces.Affectable#recoverPhyStats()
     * @param to The ability object to add as an effect.
     */
    public void addNonUninvokableEffect(Ability to);

    /**
     * Delete an effect from this object, whether permanent or temporary.  After calling this method,
     * recoverPhyStats() should be called next in case this ability object modified the stats.
     * @see com.syncleus.aethermud.game.Abilities.interfaces.Ability
     * @see com.syncleus.aethermud.game.core.interfaces.Affectable#recoverPhyStats()
     * @param to The ability object to remove as an effect on this object
     */
    public void delEffect(Ability to);

    /**
     * Returns the number of ability objects listed as effects on this object.
     * @see com.syncleus.aethermud.game.Abilities.interfaces.Ability
     * @return the number of effects this object has
     */
    public int numEffects();

    /**
     * Returns an ability object listed as an effect on this object. May return null even if the index
     * is correct to mark a race condition.
     * @see com.syncleus.aethermud.game.Abilities.interfaces.Ability
     * @see com.syncleus.aethermud.game.core.interfaces.Affectable#numEffects()
     * @param index which object to return
     * @return the ability object effecting this object
     */
    public Ability fetchEffect(int index);

    /**
     * Returns an ability object listed as an effect on this object. The object will
     * be the one with the same ID() string as passed in.
     * @see com.syncleus.aethermud.game.Abilities.interfaces.Ability
     * @see CMObject#ID()
     * @param ID the Ability ID to look for in effects
     * @return the ability object effecting this object
     */
    public Ability fetchEffect(String ID);

    /**
     * Returns an enumerator of abilities listed as effects on this object.
     * @see com.syncleus.aethermud.game.Abilities.interfaces.Ability
     * @return the enumerator of ability objects effecting this object
     */
    public Enumeration<Ability> effects();

    /**
     * Optionally uninvokes and then certainly removes all effects
     * from this object.
     * @param unInvoke send true to uninvoke before deleting
     */
    public void delAllEffects(boolean unInvoke);

    /**
     * Applies the given code to each effect on this object
     * @param applier code to execute against each object
     */
    public void eachEffect(final EachApplicable<Ability> applier);
}
