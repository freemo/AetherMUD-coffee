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
package com.syncleus.aethermud.game.Items.Basic;

import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.core.interfaces.Environmental;


public class GenFatWallpaper extends GenWallpaper {
    private static final String[] CODES = {"DISPLAY"};
    protected String displayText = "";
    protected long expirationDate = 0;

    @Override
    public String ID() {
        return "GenFatWallpaper";
    }

    @Override
    public String displayText() {
        return displayText;
    }

    @Override
    public void setDisplayText(String newText) {
        displayText = newText;
    }

    @Override
    public long expirationDate() {
        return expirationDate;
    }

    @Override
    public void setExpirationDate(long time) {
        expirationDate = time;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (msg.amITarget(this)
            && ((msg.targetMinor() == CMMsg.TYP_EXPIRE) || (msg.targetMinor() == CMMsg.TYP_DEATH))) {
            return true;
        }
        if (!super.okMessage(myHost, msg))
            return false;
        return true;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (msg.amITarget(this)
            && ((msg.targetMinor() == CMMsg.TYP_EXPIRE) || (msg.targetMinor() == CMMsg.TYP_DEATH)))
            destroy();
        super.executeMsg(myHost, msg);
    }

    @Override
    public String[] getStatCodes() {
        final String[] THINCODES = super.getStatCodes();
        final String[] codes = new String[THINCODES.length + 1];
        for (int c = 0; c < THINCODES.length; c++)
            codes[c] = THINCODES[c];
        codes[THINCODES.length] = "DISPLAY";
        return codes;
    }

    protected int getMyCodeNum(String code) {
        for (int i = 0; i < CODES.length; i++) {
            if (code.equalsIgnoreCase(CODES[i]))
                return i;
        }
        return -1;
    }

    @Override
    public String getStat(String code) {
        if (getMyCodeNum(code) < 0)
            return super.getStat(code);
        switch (getMyCodeNum(code)) {
            case 0:
                return displayText();
        }
        return "";
    }

    @Override
    public void setStat(String code, String val) {
        if (getMyCodeNum(code) < 0)
            super.setStat(code, val);
        else
            switch (getMyCodeNum(code)) {
                case 0:
                    setDisplayText(val);
                    break;
            }
    }

    @Override
    public boolean sameAs(Environmental E) {
        if (!(E instanceof GenFatWallpaper))
            return false;
        if (!super.sameAs(E))
            return false;
        for (int i = 0; i < CODES.length; i++) {
            if (!E.getStat(CODES[i]).equals(getStat(CODES[i]))) {
                return false;
            }
        }
        return true;
    }
}
