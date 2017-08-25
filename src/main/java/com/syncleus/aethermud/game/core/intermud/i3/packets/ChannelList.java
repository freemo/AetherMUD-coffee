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
package com.planet_ink.game.core.intermud.i3.packets;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * Copyright (c) 1996 George Reese
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ChannelList implements Serializable {
    public static final long serialVersionUID = 0;
    private final Hashtable list;
    private int id;

    public ChannelList() {
        super();
        id = -1;
        list = new Hashtable(10, 5);
    }

    public ChannelList(int i) {
        this();
        id = i;
    }

    public void addChannel(Channel c) {
        if (c.channel == null) {
            return;
        }
        list.put(c.channel, c);
    }

    public Channel getChannel(String channel) {
        if (!list.containsKey(channel)) {
            return null;
        }
        return (Channel) list.get(channel);
    }

    public void removeChannel(Channel c) {
        if (c.channel == null) {
            return;
        }
        list.remove(c.channel);
    }

    public int getChannelListId() {
        return id;
    }

    public void setChannelListId(int x) {
        id = x;
    }

    public Hashtable getChannels() {
        return list;
    }
}
