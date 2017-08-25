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
package com.syncleus.aethermud.game.Exits.interfaces;

/**
 * An interface for an exit that modifies the enter and leave messages.
 * @see com.syncleus.aethermud.game.Locales.interfaces.Room
 */
public interface PrepositionExit extends Exit {
    /**
     * Returns the preposition phrase seen by entering this exit.
     * @see PrepositionExit#setEntryPreposition(String)
     * @return the preposition phrase seen by entering this exit.
     */
    public String getEntryPreposition();

    /**
     * Sets the preposition phrase seen by entering this exit.
     * @see PrepositionExit#getEntryPreposition()
     * @param phrase the preposition phrase seen by entering this exit.
     */
    public void setEntryPreposition(String phrase);

    /**
     * Returns the preposition phrase seen by leaving this exit.
     * @see PrepositionExit#setExitPreposition(String)
     * @return the preposition phrase seen by leaving this exit.
     */
    public String getExitPreposition();

    /**
     * Sets the preposition phrase seen by leaving this exit.
     * @see PrepositionExit#getExitPreposition()
     * @param phrase the preposition phrase seen by leaving this exit.
     */
    public void setExitPreposition(String phrase);
}
