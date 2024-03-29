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
package com.syncleus.aethermud.game.Abilities.Thief;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Libraries.interfaces.XMLLibrary;
import com.syncleus.aethermud.game.Libraries.interfaces.XMLLibrary.XMLTag;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMStrings;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;


public class Thief_TrophyCount extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Trophy Count");
    private static final String[] triggerStrings = I(new String[]{"TROPHYCOUNT"});
    Hashtable<String, String[]> theList = new Hashtable<String, String[]>();

    @Override
    public String ID() {
        return "Thief_TrophyCount";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    public boolean isAutoInvoked() {
        return true;
    }

    @Override
    public boolean canBeUninvoked() {
        return false;
    }

    @Override
    public boolean disregardsArmorCheck(MOB mob) {
        return true;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_COMBATLORE;
    }

    @Override
    public String text() {
        final StringBuffer str = new StringBuffer("<MOBS>");
        for (final Enumeration<String[]> e = theList.elements(); e.hasMoreElements(); ) {
            final String[] one = e.nextElement();
            str.append("<MOB>");
            str.append(CMLib.xml().convertXMLtoTag("RACE", one[0]));
            str.append(CMLib.xml().convertXMLtoTag("KILLS", one[1]));
            str.append("</MOB>");
        }
        str.append("</MOBS>");
        return str.toString();
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if ((msg.sourceMinor() == CMMsg.TYP_DEATH)
            && (msg.tool() != null)
            && (msg.tool() == affected)) {
            final Race R = msg.source().charStats().getMyRace();
            if (!R.ID().equalsIgnoreCase("StdRace")) {
                String[] set = theList.get(R.name());
                if (set == null) {
                    set = new String[4];
                    set[0] = R.name();
                    set[1] = "0";
                    theList.put(R.name(), set);
                }
                set[1] = Integer.toString(CMath.s_int(set[1]) + 1);
                if (affected instanceof MOB) {
                    final Ability A = ((MOB) affected).fetchAbility(ID());
                    if (A != null)
                        A.setMiscText(text());
                }
            }
        }
        super.executeMsg(myHost, msg);
    }

    @Override
    public void setMiscText(String str) {
        theList.clear();
        if ((str.trim().length() > 0) && (str.trim().startsWith("<MOBS>"))) {
            final List<XMLLibrary.XMLTag> buf = CMLib.xml().parseAllXML(str);
            final List<XMLLibrary.XMLTag> V = CMLib.xml().getContentsFromPieces(buf, "MOBS");
            if (V != null)
                for (int i = 0; i < V.size(); i++) {
                    final XMLTag ablk = V.get(i);
                    if (ablk.tag().equalsIgnoreCase("MOB")) {
                        final String[] one = new String[4];
                        one[0] = ablk.getValFromPieces("RACE");
                        one[1] = ablk.getValFromPieces("KILLS");
                        theList.put(one[0], one);
                    }
                }
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        if (proficiencyCheck(mob, 0, auto)) {
            final StringBuffer str = new StringBuffer("");
            str.append(L("@x1Kills\n\r", CMStrings.padRight(L("Name"), 20)));
            for (final Enumeration<String[]> e = theList.elements(); e.hasMoreElements(); ) {
                final String[] one = e.nextElement();
                final int kills = CMath.s_int(one[1]);
                str.append(CMStrings.padRight(one[0], 20) + kills + "\n\r");
            }
            if (mob.session() != null)
                mob.session().safeRawPrintln(str.toString());
            return true;
        }
        mob.tell(L("You failed to recall your count."));
        return false;
    }
}
