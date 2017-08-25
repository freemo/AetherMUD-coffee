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
package com.syncleus.aethermud.game.Behaviors;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Behaviors.interfaces.Behavior;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Exits.interfaces.Exit;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.*;
import com.syncleus.aethermud.game.core.interfaces.CMObject;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.PhysicalAgent;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class StdBehavior implements Behavior {
    protected static final String[] CODES = {"CLASS", "TEXT"};
    protected String parms = "";
    protected boolean isSavableBehavior = true;

    public StdBehavior() {
        super();
        //CMClass.bumpCounter(this,CMClass.CMObjectType.BEHAVIOR);//removed for perf
    }

    public static boolean canActAtAll(Tickable affecting) {
        return CMLib.flags().canActAtAll(affecting);
    }

    public static boolean canFreelyBehaveNormal(Tickable affecting) {
        return CMLib.flags().canFreelyBehaveNormal(affecting);
    }

    @Override
    public String ID() {
        return "StdBehavior";
    }

    @Override
    public String name() {
        return ID();
    }

    protected int canImproveCode() {
        return Behavior.CAN_MOBS;
    }

    @Override
    public long flags() {
        return 0;
    }

    @Override
    public boolean grantsAggressivenessTo(MOB M) {
        return false;
    }

    @Override
    public int getTickStatus() {
        return Tickable.STATUS_NOT;
    }

    @Override
    public void initializeClass() {
    }

    @Override
    public String accountForYourself() {
        return "";
    }

    /** return a new instance of the object*/
    @Override
    public CMObject newInstance() {
        try {
            return this.getClass().newInstance();
        } catch (final Exception e) {
            Log.errOut(ID(), e);
        }
        return new StdBehavior();
    }

    @Override
    public CMObject copyOf() {
        try {
            final Behavior B = (Behavior) this.clone();
            //CMClass.bumpCounter(B,CMClass.CMObjectType.BEHAVIOR);//removed for perf
            B.setParms(getParms());
            return B;
        } catch (final CloneNotSupportedException e) {
            return new StdBehavior();
        }
    }

    @Override
    public void registerDefaultQuest(String questName) {
    }

    @Override
    public void startBehavior(PhysicalAgent forMe) {
    }

    @Override
    public boolean isSavable() {
        return isSavableBehavior;
    }

    // protected void
    // finalize(){CMClass.unbumpCounter(this,CMClass.CMObjectType.BEHAVIOR);}//removed
    // for perf
    @Override
    public void setSavable(boolean truefalse) {
        isSavableBehavior = truefalse;
    }

    @Override
    public boolean amDestroyed() {
        return false;
    }

    @Override
    public void destroy() {
        parms = "";
    }

    protected MOB getBehaversMOB(Tickable ticking) {
        if (ticking == null)
            return null;

        if (ticking instanceof MOB)
            return (MOB) ticking;
        else if (ticking instanceof Item) {
            if (((Item) ticking).owner() != null) {
                if (((Item) ticking).owner() instanceof MOB)
                    return (MOB) ((Item) ticking).owner();
            }
        }

        return null;
    }

    protected Room getBehaversRoom(Tickable ticking) {
        if (ticking == null)
            return null;

        if (ticking instanceof Room)
            return (Room) ticking;

        final MOB mob = getBehaversMOB(ticking);
        if (mob != null)
            return mob.location();

        if (ticking instanceof Item) {
            if (((Item) ticking).owner() != null) {
                if (((Item) ticking).owner() instanceof Room)
                    return (Room) ((Item) ticking).owner();
            }
        }
        return null;
    }

    @Override
    public String getParms() {
        return parms;
    }

    @Override
    public void setParms(String parameters) {
        parms = parameters;
    }

    @Override
    public String parmsFormat() {
        return CMParms.FORMAT_UNDEFINED;
    }

    @Override
    public int compareTo(CMObject o) {
        return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));
    }

    @Override
    public List<String> externalFiles() {
        return null;
    }

    @Override
    public void executeMsg(Environmental affecting, CMMsg msg) {
        return;
    }

    @Override
    public boolean okMessage(Environmental oking, CMMsg msg) {
        return true;
    }

    @Override
    public boolean canImprove(int can_code) {
        return CMath.bset(canImproveCode(), can_code);
    }

    @Override
    public boolean canImprove(PhysicalAgent E) {
        if ((E == null) && (canImproveCode() == 0))
            return true;
        if (E == null)
            return false;
        if ((E instanceof MOB) && ((canImproveCode() & Ability.CAN_MOBS) > 0))
            return true;
        if ((E instanceof Item) && ((canImproveCode() & Ability.CAN_ITEMS) > 0))
            return true;
        if ((E instanceof Exit) && ((canImproveCode() & Ability.CAN_EXITS) > 0))
            return true;
        if ((E instanceof Room) && ((canImproveCode() & Ability.CAN_ROOMS) > 0))
            return true;
        if ((E instanceof Area) && ((canImproveCode() & Ability.CAN_AREAS) > 0))
            return true;
        return false;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((ticking instanceof Environmental) && (((Environmental) ticking).amDestroyed()))
            return false;
        return true;
    }

    /**
     * Localize an internal string -- shortcut. Same as calling:
     * @see com.syncleus.aethermud.game.Libraries.interfaces.LanguageLibrary#fullSessionTranslation(String, String...)
     * Call with the string to translate, which may contain variables of the form @x1, @x2, etc. The array in xs
     * is then used to replace the variables AFTER the string is translated.
     * @param str the string to translate
     * @param xs the array of variables to replace
     * @return the translated string, with all variables in place
     */
    public String L(final String str, final String... xs) {
        return CMLib.lang().fullSessionTranslation(str, xs);
    }

    @Override
    public String[] getStatCodes() {
        return CODES;
    }

    protected int getCodeNum(String code) {
        for (int i = 0; i < CODES.length; i++) {
            if (code.equalsIgnoreCase(CODES[i]))
                return i;
        }
        return -1;
    }

    @Override
    public String getStat(String code) {
        switch (getCodeNum(code)) {
            case 0:
                return ID();
            case 1:
                return getParms();
        }
        return "";
    }

    @Override
    public void setStat(String code, String val) {
        switch (getCodeNum(code)) {
            case 0:
                return;
            case 1:
                setParms(val);
                break;
        }
    }

    @Override
    public int getSaveStatIndex() {
        return getStatCodes().length;
    }

    @Override
    public boolean isStat(String code) {
        return CMParms.indexOf(getStatCodes(), code.toUpperCase().trim()) >= 0;
    }

    public boolean sameAs(Behavior E) {
        if (!(E instanceof StdBehavior))
            return false;
        for (int i = 0; i < CODES.length; i++) {
            if (!E.getStat(CODES[i]).equals(getStat(CODES[i])))
                return false;
        }
        return true;
    }
}
