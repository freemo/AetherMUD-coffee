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
package com.syncleus.aethermud.game.Libraries.interfaces;

import com.syncleus.aethermud.game.MOBS.interfaces.MOB;

import java.util.List;


public interface I3Interface extends CMLibrary {
    public void i3who(MOB mob, String mudName);

    public void i3tell(MOB mob, String tellName, String mudName, String message);

    public void i3channel(MOB mob, String channelName, String message);

    public void i3locate(MOB mob, String mobName);

    public void i3finger(MOB mob, String mobName, String mudName);

    public void i3pingRouter(MOB mob);

    public void giveI3MudList(MOB mob);

    public List<String> getI3MudList(boolean aethermudOnly);

    public void giveIMC2MudList(MOB mob);

    public void registerIMC2(Object O);

    public boolean isI3channel(String channelName);

    public boolean isIMC2channel(String channelName);

    public boolean i3online();

    public boolean imc2online();

    public void i3chanwho(MOB mob, String channel, String mudName);

    public void giveI3ChannelsList(MOB mob);

    public void giveIMC2ChannelsList(MOB mob);

    public void i3channelAdd(MOB mob, String channel);

    public void i3channelListen(MOB mob, String channel);

    public void i3channelSilence(MOB mob, String channel);

    public void i3channelRemove(MOB mob, String channel);

    public void i3mudInfo(MOB mob, String parms);

    public void imc2mudInfo(MOB mob, String parms);
}
