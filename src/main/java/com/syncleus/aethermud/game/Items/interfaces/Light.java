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

/**
 * A Light is an item that can emit light for some period of time.
 * Lights can be put out, but when they go out of their own accord
 * they can be destroyed automatically, or not.
 * @author Bo Zimmerman
 */
public interface Light extends Item {
    /**
     * Gets the duration, in ticks, that this light object will emit
     * light before stopping for whatever reason.
     * @see Light#setDuration(int)
     * @return duration the duration of the light source, in ticks.
     */
    public int getDuration();

    /**
     * Sets the duration, in ticks, that this light object will emit
     * light before stopping for whatever reason.
     * @see Light#getDuration()
     * @param duration the duration of the light source, in ticks.
     */
    public void setDuration(int duration);

    /**
     * Gets whether this Light item automatically destroys itself after
     * it is lit, and then its duration runs out.
     * @see Light#setDestroyedWhenBurntOut(boolean)
     * @return true if it is destroyed after use, false otherwise
     */
    public boolean destroyedWhenBurnedOut();

    /**
     * Sets whether this Light item automatically destroys itself after
     * it is lit, and then its duration runs out.
     * @see Light#destroyedWhenBurnedOut()
     * @param truefalse true if it is destroyed after use, false otherwise
     */
    public void setDestroyedWhenBurntOut(boolean truefalse);

    /**
     * Returns true if this particular light item is sensitive to rain
     * and nasty weather such that rain or water will cause it to stop
     * being lit.
     * @return true if it goes out in the rain
     */
    public boolean goesOutInTheRain();

    /**
     * Gets whether this light item is presently emitting any light.
     * @see Light#light(boolean)
     * @return true if it is lit and emitting light
     */
    public boolean isLit();

    /**
     * Sets whether this light item is presently emitting any light.
     * @see Light#isLit()
     * @param isLit true if it is lit and emitting light
     */
    public void light(boolean isLit);

}
