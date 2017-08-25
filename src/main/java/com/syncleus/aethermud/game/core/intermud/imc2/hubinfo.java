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
package com.planet_ink.game.core.intermud.imc2;

import java.util.Date;

/**
 * IMC2 version 0.10 - an inter-mud communications protocol
 * Copyright (C) 1996 - 1997 Oliver Jowett: oliver@randomly.org
 *
 * IMC2 Gold versions 1.00 though 2.00 are developed by MudWorld.
 * Copyright (C) 1999 - 2002 Haslage Net Electronics (Anthony R. Haslage)
 *
 * IMC2 MUD-Net version 3.10 is developed by Alsherok and Crimson Oracles
 * Copyright (C) 2002 Roger Libiez ( Samson )
 * Additional code Copyright (C) 2002 Orion Elder
 * Registered with the United States Copyright Office
 * TX 5-555-584
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program (see the file COPYING); if not, write to the
 * Free Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 * Ported to Java by Istvan David (u_davis@users.sourceforge.net)
 *
 */
public class hubinfo {

    /* The mud's connection data for the hub */
    public String hubname = ""; /* name of hub */
    public String host = ""; /* hostname of hub */
    public int port; /* remote port of hub */
    public String serverpw = ""; /* server password */
    public String clientpw = ""; /* client password */
    public String network = "";  /* intermud network name */
    public boolean autoconnect; /* Do we autoconnect on bootup or not? - Samson */
    Date timer_duration; /* delay after next reconnect failure */
    Date last_connected; /* last connected when? */
    int connect_attempts; /* try for 3 times - shogar */
    /* Conection parameters - These don't save in the config file */
    //int desc; /* descriptor */
    int state; /* IMC_xxxx state */
    int version; /* version of remote site */
    /* try to write at end of cycle regardless of fd_set state? */
    String inbuf = ""; /* input buffer */
    int insize;
    String outbuf = ""; /* output buffer */
    int outsize;
}
