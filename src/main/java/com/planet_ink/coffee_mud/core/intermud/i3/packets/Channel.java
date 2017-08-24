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
package com.planet_ink.coffee_mud.core.intermud.i3.packets;

import com.planet_ink.coffee_mud.core.intermud.i3.persist.Persistent;

import java.io.Serializable;

/**
 * com.planet_ink.coffee_mud.core.intermud.i3.packets.Channel
 * An I3 channel.
 */

/**
 * This class represents an I3 channel.  The ChannelList
 * class keeps a list of this channels for tracking.
 * @author George Reese (borg@imaginary.com)
 * @version 1.0
 */

/**
 * Copyright (c) 1996 George Reese
 * This source code may not be modified, copied,
 * redistributed, or used in any fashion without the
 * express written consent of George Reese.
 */
public class Channel implements Serializable {
    public static final long serialVersionUID = 0;
    /**
     * The name of the channel
     */
    public String channel;
    /**
     * The modification status of this channel
     * @see com.planet_ink.coffee_mud.core.intermud.i3.persist.Persistent
     */
    public int modified;
    /**
     * The mud which controls the channel
     */
    public String owner;
    /**
     * The type of the mud channel
     */
    public int type;

    /**
     * Constructs a new mud channel object
     */
    public Channel() {
        super();
        modified = Persistent.UNMODIFIED;
    }

    /**
     * Constructs a copy of an existing channel
     * @param other the other channel
     */
    public Channel(Channel other) {
        super();
        channel = other.channel;
        modified = other.modified;
        owner = other.owner;
        type = other.type;
    }
}
