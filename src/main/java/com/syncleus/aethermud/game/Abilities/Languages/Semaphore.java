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
package com.syncleus.aethermud.game.Abilities.Languages;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Abilities.interfaces.Language;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.BoardableShip;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMStrings;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Rideable;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;


public class Semaphore extends StdLanguage {
    private final static String localizedName = CMLib.lang().L("Semaphore");
    public static List<String[]> wordLists = null;

    @Override
    public String ID() {
        return "Semaphore";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String translate(String language, String word) {
        return fixCase(word, "flag");
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((affected instanceof MOB)
            && (beingSpoken(ID()))
            && (msg.source() == affected)
            && (msg.sourceMessage() != null)
            && (msg.tool() == null)
            && (msg.sourceMinor() == CMMsg.TYP_SPEAK)) {
            final MOB mob = (MOB) affected;
            Room R = CMLib.map().roomLocation(mob);
            if (!super.okMessage(myHost, msg))
                return false;
            if ((msg.tool() == this) && (R != null)) {
                if ((R.getArea() instanceof BoardableShip)
                    && ((R.domainType() & Room.INDOORS) == 0)) {
                    final Room room = CMLib.map().roomLocation(((BoardableShip) R.getArea()).getShipItem());
                    if (room != null) {
                        final CMMsg outerMsg = (CMMsg) msg.copyOf();
                        msg.modify(msg.source(), null, null, CMMsg.NO_EFFECT, null);
                        msg.addTrailerRunnable(new Runnable() {
                            @Override
                            public void run() {
                                room.send(msg.source(), outerMsg);
                            }
                        });
                    }
                }
            }
            return true;
        } else
            return super.okMessage(myHost, msg);
    }

    @Override
    public List<String[]> translationVector(String language) {
        return wordLists;
    }

    @Override
    protected boolean processSourceMessage(CMMsg msg, String str, int numToMess) {
        if (msg.sourceMessage() == null)
            return true;
        int wordStart = msg.sourceMessage().indexOf('\'');
        if (wordStart < 0)
            return true;
        String wordsSaid = CMStrings.getSayFromMessage(msg.sourceMessage());
        if (wordsSaid == null)
            return true;
        if (numToMess > 0)
            wordsSaid = messChars(ID(), wordsSaid, numToMess);
        final String fullMsgStr = CMStrings.substituteSayInMessage(msg.sourceMessage(), wordsSaid);
        wordStart = fullMsgStr.indexOf('\'');
        String startFullMsg = fullMsgStr.substring(0, wordStart);
        if (startFullMsg.indexOf("YELL(S)") > 0) {
            msg.source().tell(L("You can't yell in semaphore."));
            return false;
        }
        final String oldStartFullMsg = startFullMsg;
        startFullMsg = CMStrings.replaceFirstWord(startFullMsg, "say(s)", "flag(s)");
        startFullMsg = CMStrings.replaceFirstWord(startFullMsg, "ask(s)", "flag(s) askingly");
        startFullMsg = CMStrings.replaceFirstWord(startFullMsg, "exclaim(s)", "flag(s) excitedly");
        if (oldStartFullMsg.equals(startFullMsg)) {
            int x = startFullMsg.toLowerCase().lastIndexOf("(s)");
            if (x < 0)
                x = startFullMsg.trim().length();
            else
                x += 3;
            startFullMsg = startFullMsg.substring(0, x) + " in semaphore flags" + startFullMsg.substring(x);
        }

        msg.modify(msg.source(),
            msg.target(),
            this,
            CMath.unsetb(msg.sourceCode(), CMMsg.MASK_SOUND | CMMsg.MASK_MOUTH) | CMMsg.MASK_MOVE,
            startFullMsg + fullMsgStr.substring(wordStart),
            msg.targetCode(),
            msg.targetMessage(),
            msg.othersCode(),
            msg.othersMessage());
        return true;
    }

    @Override
    protected boolean processNonSourceMessages(CMMsg msg, String str, int numToMess) {
        final String fullOtherMsgStr = (msg.othersMessage() == null) ? msg.targetMessage() : msg.othersMessage();
        if (fullOtherMsgStr == null)
            return true;
        final int wordStart = fullOtherMsgStr.indexOf('\'');
        if (wordStart < 0)
            return true;
        String wordsSaid = CMStrings.getSayFromMessage(fullOtherMsgStr);
        if (wordsSaid == null)
            return true;
        int numWords = 1;
        for (int i = 0; i < wordsSaid.length(); i++) {
            if ((wordsSaid.charAt(i) == ' ')
                && ((i == 0) || (wordsSaid.charAt(i - 1) != ' ')))
                numWords++;
        }
        String startFullMsg = fullOtherMsgStr.substring(0, wordStart);
        StringBuilder verbs = new StringBuilder("flag(s) ");
        for (int i = 0; i < numWords; i++) {
            String verb;
            switch (CMLib.dice().roll(1, 8, 0)) {
                case 1:
                    verb = L("up");
                    break;
                case 2:
                    verb = L("up-right");
                    break;
                case 3:
                    verb = L("sideways right");
                    break;
                case 4:
                    verb = L("down-right");
                    break;
                case 5:
                    verb = L("up-left");
                    break;
                case 6:
                    verb = L("sideways left");
                    break;
                case 7:
                    verb = L("down-left");
                    break;
                case 8:
                    verb = L("down");
                    break;
                default:
                    verb = L("more");
                    break;
            }
            verbs.append(verb).append(" ");
        }
        final String oldStartFullMsg = startFullMsg;
        startFullMsg = CMStrings.replaceFirstWord(startFullMsg, "tell(s)", verbs.toString());
        startFullMsg = CMStrings.replaceFirstWord(startFullMsg, "say(s)", verbs.toString());
        startFullMsg = CMStrings.replaceFirstWord(startFullMsg, "ask(s)", verbs.toString() + " askingly");
        startFullMsg = CMStrings.replaceFirstWord(startFullMsg, "exclaim(s)", verbs.toString() + " excitedly");
        if (oldStartFullMsg.equals(startFullMsg)) {
            int x = startFullMsg.toLowerCase().lastIndexOf("(s)");
            if (x < 0)
                x = startFullMsg.trim().length();
            else
                x += 3;
            startFullMsg = startFullMsg.substring(0, x) + " in semaphore" + startFullMsg.substring(x);
        }
        msg.modify(msg.source(),
            msg.target(),
            this,
            msg.sourceCode(),
            msg.sourceMessage(),
            CMath.unsetb(msg.targetCode(), CMMsg.MASK_SOUND) | CMMsg.MASK_MOVE,
            startFullMsg.trim() + ".",
            CMath.unsetb(msg.othersCode(), CMMsg.MASK_SOUND) | CMMsg.MASK_MOVE,
            startFullMsg.trim() + ".");
        return true;
    }

    @Override
    protected boolean translateOthersMessage(CMMsg msg, String sourceWords) {
        if ((msg.othersMessage() != null) && (sourceWords != null)) {
            String otherMes = msg.othersMessage();
            if ((otherMes.lastIndexOf('\'') == otherMes.indexOf('\'')))
                otherMes = otherMes.replace('.', ' ') + '\'' + sourceWords + '\'';
            if (msg.target() != null)
                otherMes = CMLib.aetherFilter().fullOutFilter(null, (MOB) affected, msg.source(), msg.target(), msg.tool(), otherMes, false);
            msg.addTrailerMsg(CMClass.getMsg(msg.source(), affected, null, CMMsg.NO_EFFECT, null, msg.othersCode(), L("@x1 (translated from @x2)", CMStrings.substituteSayInMessage(otherMes, sourceWords), name()), CMMsg.NO_EFFECT, null));
            return true;
        }
        return false;
    }

    @Override
    protected boolean translateTargetMessage(CMMsg msg, String sourceWords) {
        if (msg.amITarget(affected) && (msg.targetMessage() != null)) {
            String otherMes = msg.targetMessage();
            if ((otherMes.lastIndexOf('\'') == otherMes.indexOf('\'')))
                otherMes = otherMes.replace('.', ' ') + '\'' + sourceWords + '\'';
            if (msg.target() != null)
                otherMes = CMLib.aetherFilter().fullOutFilter(null, (MOB) affected, msg.source(), msg.target(), msg.tool(), otherMes, false);
            msg.addTrailerMsg(CMClass.getMsg(msg.source(), affected, null, CMMsg.NO_EFFECT, null, msg.targetCode(), L("@x1 (translated from @x2)", CMStrings.substituteSayInMessage(otherMes, sourceWords), name()), CMMsg.NO_EFFECT, null));
            return true;
        }
        return false;
    }

    @Override
    protected boolean translateChannelMessage(CMMsg msg, String sourceWords) {
        if (CMath.bset(msg.sourceMajor(), CMMsg.MASK_CHANNEL) && (msg.othersMessage() != null)) {
            String otherMes = msg.othersMessage();
            if ((otherMes.lastIndexOf('\'') == otherMes.indexOf('\'')))
                otherMes = otherMes.replace('.', ' ') + '\'' + sourceWords + '\'';
            msg.addTrailerMsg(CMClass.getMsg(msg.source(), null, null, CMMsg.NO_EFFECT, CMMsg.NO_EFFECT, msg.othersCode(), L("@x1 (translated from @x2)", CMStrings.substituteSayInMessage(otherMes, sourceWords), name())));
            return true;
        }
        return false;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        final Physical affected = this.affected;
        if ((affected instanceof MOB) && (this.beingSpoken(ID()))) {
            final MOB mob = (MOB) affected;
            final Room R = mob.location();
            if ((R != null)
                && (R.getArea() instanceof BoardableShip)
                || ((mob.riding() != null) && (mob.riding().rideBasis() == Rideable.RIDEABLE_WATER))) {
                // fine.
            } else {
                Ability A = mob.fetchAbility(ID());
                if (A == null)
                    A = this;
                A.invoke(mob, new Vector<String>(1), null, false, 0);
            }
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!auto) {
            boolean isCurrentlySpeaking = false;
            for (final Enumeration<Ability> a = mob.effects(); a.hasMoreElements(); ) {
                final Ability A = a.nextElement();
                if ((A != null) && (A instanceof Language)) {
                    if (A.ID().equals(ID()))
                        isCurrentlySpeaking = ((Language) A).beingSpoken(ID());
                }
            }
            if (!isCurrentlySpeaking) {
                final Room R = mob.location();
                if ((R != null)
                    && (R.getArea() instanceof BoardableShip)
                    || ((mob.riding() != null) && (mob.riding().rideBasis() == Rideable.RIDEABLE_WATER))) {
                    // fine.
                } else {
                    mob.tell(L("You must be on a ship or boat to speak this."));
                    return false;
                }
            }
        }

        super.invoke(mob, commands, givenTarget, auto, asLevel);

        return true;
    }

}
