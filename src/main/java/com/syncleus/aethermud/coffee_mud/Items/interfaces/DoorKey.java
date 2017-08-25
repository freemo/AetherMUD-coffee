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
package com.planet_ink.coffee_mud.Items.interfaces;

/**
 * Represents a key-like thing that opens a door, or a chest,
 * or anything else locked.
 * @author Bo Zimmerman
 */
public interface DoorKey extends Item {
    /**
     * Gets the "key name", which is a unique string that specifies
     * what this key opens.  The lock must have a matching key
     * @see com.planet_ink.coffee_mud.Exits.interfaces.Exit#keyName()
     * @see DoorKey#setKey(String)
     * @return the unique key string that identifies the lock
     */
    public String getKey();

    /**
     * Sets the "key name", which is a unique string that specifies
     * what this key opens.  The lock must have a matching key
     * @see com.planet_ink.coffee_mud.Exits.interfaces.Exit#keyName()
     * @see DoorKey#getKey()
     * @param keyName the unique key string that identifies the lock
     */
    public void setKey(String keyName);
}
