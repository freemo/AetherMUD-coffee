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

import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;

import java.util.List;

/**
 * A computer is a particular type of electronics panel that holds
 * software, and has readers who monitor the messages this panel
 * generates, as well as use the Type command to enter data into
 * this panel's software.
 * @see Software
 * @see ElecPanel
 * @author Bo Zimmerman
 *
 */
public interface Computer extends ElecPanel {
    /**
     * Returns the list of Software objects installed in this computer.
     * @see Software
     * @return the list of Software objects installed in this computer
     */
    public List<Software> getSoftware();

    /**
     * Returns the list of mobs currently monitoring the output of this
     * computers software.
     * @return the list of mobs currently monitoring the output
     */
    public List<MOB> getCurrentReaders();

    /**
     * Forces all the current readers to "read" the computer, typically
     * seeing the menu.
     * @see Computer#getCurrentReaders()
     * @see Computer#forceReadersSeeNew()
     */
    public void forceReadersMenu();

    /**
     * Forces all the current readers to see any new messages that
     * should be seen by anyone monitoring the computer.
     * @see Computer#getCurrentReaders()
     * @see Computer#forceReadersMenu()
     */
    public void forceReadersSeeNew();

    /**
     * Most software supports different levels of menu, and some software
     * is even a sub-menu unto itself.  This method returns the current
     * active menu.
     * @see Computer#setActiveMenu(String)
     * @return internalName the menu to set as current and active
     */
    public String getActiveMenu();

    /**
     * Most software supports different levels of menu, and some software
     * is even a sub-menu unto itself.  This method forces the system to
     * recognize one of those menus as current.  The software takes it
     * from there.
     * @see Computer#getActiveMenu()
     * @param internalName the menu to set as current and active
     */
    public void setActiveMenu(String internalName);
}
