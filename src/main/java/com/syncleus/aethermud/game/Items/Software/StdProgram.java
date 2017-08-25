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
package com.syncleus.aethermud.game.Items.Software;

import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.Basic.StdItem;
import com.syncleus.aethermud.game.Items.interfaces.Computer;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Software;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class StdProgram extends StdItem implements Software {
    protected StringBuilder nextMsg = new StringBuilder("");
    protected String currentScreen = "";
    public StdProgram() {
        super();
        setName("a software disk");
        setDisplayText("a small disk sits here.");
        setDescription("It appears to be a general software program.");

        basePhyStats.setWeight(1);
        material = RawMaterial.RESOURCE_STEEL;
        baseGoldValue = 1000;
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "StdProgram";
    }

    @Override
    public void setCircuitKey(String key) {
    }

    @Override
    public int techLevel() {
        return phyStats().ability();
    }

    @Override
    public void setTechLevel(int lvl) {
        basePhyStats.setAbility(lvl);
        recoverPhyStats();
    }

    @Override
    public String getParentMenu() {
        return "";
    }

    @Override
    public String getInternalName() {
        return "";
    }

    @Override
    public boolean isActivationString(String word) {
        return false;
    }

    @Override
    public boolean isDeActivationString(String word) {
        return false;
    }

    @Override
    public boolean isCommandString(String word, boolean isActive) {
        return false;
    }

    @Override
    public TechType getTechType() {
        return TechType.PERSONAL_SOFTWARE;
    }

    @Override
    public String getActivationMenu() {
        return "";
    }

    @Override
    public String getCurrentScreenDisplay() {
        return currentScreen;
    }

    public void setCurrentScreenDisplay(String msg) {
        this.currentScreen = msg;
    }

    @Override
    public String getScreenMessage() {
        synchronized (nextMsg) {
            final String msg = nextMsg.toString();
            nextMsg.setLength(0);
            return msg;
        }
    }

    @Override
    public void addScreenMessage(String msg) {
        synchronized (nextMsg) {
            nextMsg.append(msg).append("\n\r");
        }
    }

    protected void forceUpMenu() {
        if ((container() instanceof Computer) && (((Computer) container()).getActiveMenu().equals(getInternalName())))
            ((Computer) container()).setActiveMenu(getParentMenu());
    }

    protected void forceNewMessageScan() {
        if (container() instanceof Computer)
            ((Computer) container()).forceReadersSeeNew();
    }

    protected void forceNewMenuRead() {
        if (container() instanceof Computer)
            ((Computer) container()).forceReadersMenu();
    }

    public boolean checkActivate(MOB mob, String message) {
        return true;
    }

    public boolean checkDeactivate(MOB mob, String message) {
        return true;
    }

    public boolean checkTyping(MOB mob, String message) {
        return true;
    }

    public boolean checkPowerCurrent(int value) {
        return true;
    }

    @Override
    public boolean okMessage(Environmental host, CMMsg msg) {
        if (msg.amITarget(this)) {
            switch (msg.targetMinor()) {
                case CMMsg.TYP_ACTIVATE:
                    if (!checkActivate(msg.source(), msg.targetMessage()))
                        return false;
                    break;
                case CMMsg.TYP_DEACTIVATE:
                    if (!checkDeactivate(msg.source(), msg.targetMessage()))
                        return false;
                    break;
                case CMMsg.TYP_WRITE:
                    if (!checkTyping(msg.source(), msg.targetMessage()))
                        return false;
                    break;
                case CMMsg.TYP_POWERCURRENT:
                    if (!checkPowerCurrent(msg.value()))
                        return false;
                    break;
            }
        }
        return super.okMessage(host, msg);
    }

    public void onActivate(MOB mob, String message) {

    }

    public void onDeactivate(MOB mob, String message) {

    }

    public void onTyping(MOB mob, String message) {

    }

    public void onPowerCurrent(int value) {

    }

    @Override
    public void executeMsg(Environmental host, CMMsg msg) {
        if (msg.amITarget(this)) {
            switch (msg.targetMinor()) {
                case CMMsg.TYP_ACTIVATE:
                    onActivate(msg.source(), msg.targetMessage());
                    break;
                case CMMsg.TYP_DEACTIVATE:
                    onDeactivate(msg.source(), msg.targetMessage());
                    break;
                case CMMsg.TYP_WRITE:
                    onTyping(msg.source(), msg.targetMessage());
                    break;
                case CMMsg.TYP_POWERCURRENT:
                    onPowerCurrent(msg.value());
                    break;
            }
        }
        super.executeMsg(host, msg);
    }

    public String display(long d) {
        return CMLib.english().sizeDescShort(d);
    }

    public String display(long[] coords) {
        return CMLib.english().coordDescShort(coords);
    }

    public String display(double[] dir) {
        return CMLib.english().directionDescShort(dir);
    }

    public String displayPerSec(long speed) {
        return CMLib.english().speedDescShort(speed);
    }
}
