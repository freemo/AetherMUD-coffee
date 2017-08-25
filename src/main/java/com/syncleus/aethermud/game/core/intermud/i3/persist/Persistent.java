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
package com.planet_ink.game.core.intermud.i3.persist;

/**
 * com.planet_ink.game.core.intermud.i3.persist.Persistent
 * Copyright (c) 1996 George Reese
 * This source code may not be modified, copied,
 * redistributed, or used in any fashion without the
 * express written consent of George Reese.
 *
 * A Persistent object is any object that can be saved
 * to some data store.
 */

/**
 * Specified methods which must be implemented by
 * objects wishing to be saved.
 * @author George Reese (borg@imaginary.com)
 * @version 1.0
 */
public interface Persistent {
    /**
     * The Persistent has not yet been modified since last
     * save or restore.
     */
    static public final int UNMODIFIED = 0;
    /**
     * The Peersistence has been modified since last save
     * or restore.
     */
    static public final int MODIFIED = 1;
    /**
     * The Persistent is a brand new object.
     */
    static public final int NEW = 2;
    /**
     * The Persistent needs to be deleted from the data store.
     */
    static public final int DELETED = 3;

    /**
     * Prescribes a method for restoration from a data
     * store.  An implementation will usually call the
     * object's PersistentPeer method to perform the
     * actual save.
     * @throws PersistenceException thrown when an error occurs in restoring
     * @see com.planet_ink.game.core.intermud.i3.persist.PersistentPeer
     */
    public abstract void restore() throws PersistenceException;

    /**
     * Prescribes a method for saving this object's data
     * to a data store.  An implementation will usually
     * check to see if the object has been modified and
     * trigger the actual saving mechanism in its
     * PersistentPeer implementation.
     * @throws PersistenceException thrown when an error occurs in saving
     * @see com.planet_ink.game.core.intermud.i3.persist.PersistentPeer
     */
    public abstract void save() throws PersistenceException;
}
