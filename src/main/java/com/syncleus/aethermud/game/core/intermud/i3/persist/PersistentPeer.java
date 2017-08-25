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
package com.syncleus.aethermud.game.core.intermud.i3.persist;

/**
 * com.syncleus.aethermud.game.core.intermud.i3.persist.PersistentPeer
 * Copyright (c) 1996 George Reese
 * This source code may not be modified, copied,
 * redistributed, or used in any fashion without the
 * express written consent of George Reese.
 *
 * The PersistentPeer is an interface for defining
 * a particular kind of persistence for a specific
 * object.
 */

/**
 * Any object which should persist over time should
 * have a PersistentPeer which handles saving it.
 * This allows a separate object to worry about
 * persistence issues, specifically whether the object
 * should save to a flatfile or database, and how that
 * saving occurs.
 * @author George Reese (borg@imaginary.com)
 * @version 1.0
 */
public interface PersistentPeer {
    /**
     * Gets data about this peer from storage and gives it
     * back to the object for which this peer exists.
     * @exception com.syncleus.aethermud.game.core.intermud.i3.persist.PersistenceException if an error occurs during restore
     */
    public abstract void restore() throws PersistenceException;

    /**
     * Triggers a save of its peer.  Implementing classes
     * should do whatever it takes to save the object in
     * this method.
     * @exception com.syncleus.aethermud.game.core.intermud.i3.persist.PersistenceException if a problem occurs in saving
     */
    public abstract void save() throws PersistenceException;

    /**
     * Assigns a persistent object to this peer for
     * persistence operations.
     * @param ob the implementation of com.syncleus.aethermud.game.core.intermud.i3.persist.Persistent that this is a peer for
     * @see com.syncleus.aethermud.game.core.intermud.i3.persist.Persistent
     */
    public abstract void setPersistent(Persistent ob);

    /**
     * An implementation uses this to tell its Persistent
     * that it is in the middle of restoring.
     * @return true if a restore operation is in progress
     */
    public abstract boolean isRestoring();
}
