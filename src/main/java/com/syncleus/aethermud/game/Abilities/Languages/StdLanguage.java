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

import com.syncleus.aethermud.game.Abilities.StdAbility;
import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Abilities.interfaces.Language;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Libraries.interfaces.AbilityComponents;
import com.syncleus.aethermud.game.Libraries.interfaces.ExpertiseLibrary;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.*;
import com.syncleus.aethermud.game.core.collections.XVector;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.LandTitle;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.*;


@SuppressWarnings({"unchecked", "rawtypes"})
public class StdLanguage extends StdAbility implements Language {
    protected static final String CANCEL_WORD = "CANCEL";
    private final static String localizedName = CMLib.lang().L("Languages");
    private static final String[] triggerStrings = I(new String[]{"SPEAK"});
    private final static String consonants = "bcdfghjklmnpqrstvwxz";
    private final static String vowels = "aeiouy";
    private static Map<String, String> emptyHash = new Hashtable<String, String>();
    private static List<String[]> emptyVector = new Vector<String[]>();
    protected boolean spoken = false;
    protected boolean alwaysSpoken = false;

    @Override
    public String ID() {
        return "StdLanguage";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String writtenName() {
        return name();
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS | Ability.CAN_ITEMS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
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
    protected ExpertiseLibrary.SkillCostDefinition getRawTrainingCost() {
        return CMProps.getLangSkillGainCost(ID());
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_LANGUAGE;
    }

    @Override
    public boolean beingSpoken(String language) {
        return alwaysSpoken || spoken;
    }

    @Override
    public void setBeingSpoken(String language, boolean beingSpoken) {
        spoken = alwaysSpoken || beingSpoken;
    }

    @Override
    public Map<String, String> translationHash(String language) {
        return emptyHash;
    }

    @Override
    public List<String[]> translationVector(String language) {
        return emptyVector;
    }

    @Override
    public void setMiscText(String newMiscText) {
        if (newMiscText.length() > 0) {
            alwaysSpoken = CMParms.getParmBool(newMiscText, "ALWAYS", false);
            spoken = CMParms.getParmBool(newMiscText, "SPOKEN", spoken);
        }
        super.setMiscText(newMiscText);
    }

    @Override
    public List<String> languagesSupported() {
        return new XVector(ID());
    }

    @Override
    public boolean translatesLanguage(String language) {
        return ID().equalsIgnoreCase(language);
    }

    @Override
    public int getProficiency(String language) {
        if (ID().equalsIgnoreCase(language))
            return proficiency();
        return 0;
    }

    @Override
    public String displayText() {
        if (beingSpoken(ID()))
            return "(Speaking " + name() + ")";
        return "";
    }

    protected String fixCase(String like, String make) {
        final StringBuffer s = new StringBuffer(make);
        char lastLike = ' ';
        for (int x = 0; x < make.length(); x++) {
            if (x < like.length())
                lastLike = like.charAt(x);
            s.setCharAt(x, fixCase(lastLike, make.charAt(x)));
        }
        return s.toString();
    }

    protected char fixCase(char like, char make) {
        if (Character.isUpperCase(like))
            return Character.toUpperCase(make);
        return Character.toLowerCase(make);
    }

    @Override
    public String translate(String language, String word) {
        if (translationHash(language).containsKey(word.toUpperCase()))
            return fixCase(word, translationHash(language).get(word.toUpperCase()));
        final MOB M = CMLib.players().getPlayer(word);
        if (M != null)
            return word;
        final List<String[]> translationVector = translationVector(language);
        if (translationVector.size() > 0) {
            String[] choices = null;
            try {
                choices = translationVector.get(word.length() - 1);
            } catch (final Exception e) {
            }
            if (choices == null)
                choices = translationVector.get(translationVector.size() - 1);
            return choices[CMath.abs(word.toLowerCase().hashCode()) % choices.length];
        }
        return word;
    }

    protected int numChars(String words) {
        int num = 0;
        for (int i = 0; i < words.length(); i++) {
            if (Character.isLetter(words.charAt(i)))
                num++;
        }
        return num;
    }

    public String messChars(String language, String words, int numToMess) {
        numToMess = numToMess / 2;
        if (numToMess == 0)
            return words;
        final StringBuffer w = new StringBuffer(words);
        while (numToMess > 0) {
            final int x = CMLib.dice().roll(1, words.length(), -1);
            final char c = words.charAt(x);
            if (Character.isLetter(c)) {
                if (vowels.indexOf(c) >= 0)
                    w.setCharAt(x, fixCase(c, vowels.charAt(CMLib.dice().roll(1, vowels.length(), -1))));
                else
                    w.setCharAt(x, fixCase(c, consonants.charAt(CMLib.dice().roll(1, consonants.length(), -1))));
                numToMess--;
            }
        }
        return w.toString();
    }

    public String scrambleAll(String language, String str, int numToMess) {
        final StringBuffer newStr = new StringBuffer("");
        int start = 0;
        int end = 0;
        int state = -1;
        while (start <= str.length()) {
            char c = '\0';
            if (end >= str.length())
                c = ' ';
            else
                c = str.charAt(end);
            switch (state) {
                case -1:
                    if (Character.isLetter(c)) {
                        state = 0;
                        end++;
                    } else {
                        newStr.append(c);
                        end++;
                        start = end;
                    }
                    break;
                case 0:
                    if (Character.isLetter(c))
                        end++;
                    else if (Character.isDigit(c)) {
                        newStr.append(str.substring(start, end + 1));
                        end++;
                        start = end;
                        state = 1;
                    } else {
                        newStr.append(translate(language, str.substring(start, end)) + c);
                        end++;
                        start = end;
                        state = -1;
                    }
                    break;
                case 1:
                    if (Character.isLetterOrDigit(c)) {
                        newStr.append(c);
                        end++;
                        start = end;
                    } else {
                        newStr.append(c);
                        end++;
                        start = end;
                        state = -1;
                    }
                    break;
            }
        }
        return newStr.toString();
    }

    protected Language getMyTranslator(String id, Physical P, Language winner) {
        if (P == null)
            return winner;
        for (final Enumeration<Ability> a = P.effects(); a.hasMoreElements(); ) {
            final Ability A = a.nextElement();
            if ((A instanceof Language)
                && ((Language) A).translatesLanguage(id)
                && ((winner == null)
                || ((Language) A).getProficiency(id) > winner.getProficiency(id))) {
                winner = (Language) A;
            }
        }
        return winner;
    }

    protected Language getAnyTranslator(String id, MOB mob) {
        Language winner = null;
        winner = getMyTranslator(id, mob, winner);
        winner = getMyTranslator(id, mob.location(), winner);
        for (int i = 0; i < mob.numItems(); i++)
            winner = getMyTranslator(id, mob.getItem(i), winner);
        return winner;
    }

    protected boolean processSourceMessage(CMMsg msg, String str, int numToMess) {
        String smsg = CMStrings.getSayFromMessage(msg.sourceMessage());
        if (smsg != null) {
            if (numToMess > 0)
                smsg = messChars(ID(), smsg, numToMess);
            msg.modify(msg.source(),
                msg.target(),
                this,
                msg.sourceCode(),
                CMStrings.substituteSayInMessage(msg.sourceMessage(), smsg),
                msg.targetCode(),
                msg.targetMessage(),
                msg.othersCode(),
                msg.othersMessage());
        }
        return true;
    }

    protected boolean processNonSourceMessages(CMMsg msg, String str, int numToMess) {
        str = scrambleAll(ID(), str, numToMess);
        msg.modify(msg.source(),
            msg.target(),
            this,
            msg.sourceCode(),
            msg.sourceMessage(),
            msg.targetCode(),
            CMStrings.substituteSayInMessage(msg.targetMessage(), str),
            msg.othersCode(),
            CMStrings.substituteSayInMessage(msg.othersMessage(), str));
        return true;
    }

    protected boolean tryLinguisticWriting(CMMsg msg) {
        if (msg.target() instanceof Physical) {
            final Physical P = (Physical) msg.target();
            for (final Enumeration<Ability> a = P.effects(); a.hasMoreElements(); ) {
                final Ability A = a.nextElement();
                if ((A instanceof Language) && (!A.ID().equals(ID()))) {
                    msg.source().tell(L("@x1 is already written in @x2 and can not have @x3 writing added.", P.name(msg.source()), A.name(), writtenName()));
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((affected instanceof MOB)
            && (beingSpoken(ID()))) {
            if ((msg.source() == affected)
                && (msg.sourceMessage() != null)
                && (msg.tool() == null)
                && ((msg.sourceMinor() == CMMsg.TYP_SPEAK)
                || (msg.sourceMinor() == CMMsg.TYP_TELL)
                || (CMath.bset(msg.sourceMajor(), CMMsg.MASK_CHANNEL)))) {
                String str = CMStrings.getSayFromMessage(msg.othersMessage());
                if (str == null)
                    str = CMStrings.getSayFromMessage(msg.targetMessage());
                if (str != null) {
                    final int numToMess = (int) Math.round(CMath.mul(numChars(str), CMath.div(100 - getProficiency(ID()), 100)));
                    if (!processSourceMessage(msg, str, numToMess))
                        return false;
                    if (!processNonSourceMessages(msg, str, numToMess))
                        return false;
                    if (CMLib.flags().isAliveAwakeMobile((MOB) affected, true))
                        helpProficiency((MOB) affected, 0);
                }
            } else if ((msg.sourceMinor() == CMMsg.TYP_WRITE)
                && (msg.source() == affected)
                && (msg.target() instanceof Item)
                && (((Item) msg.target()).isReadable())
                && (msg.targetMessage() != null)
                && (msg.targetMessage().length() > 0)) {
                if (!tryLinguisticWriting(msg))
                    return false;
            } else if ((msg.target() == affected) && (msg.source() != affected)) {
                switch (msg.targetMinor()) {
                    case CMMsg.TYP_ORDER:
                    case CMMsg.TYP_BUY:
                    case CMMsg.TYP_BID:
                    case CMMsg.TYP_SELL:
                    case CMMsg.TYP_LIST:
                    case CMMsg.TYP_VIEW:
                    case CMMsg.TYP_WITHDRAW:
                    case CMMsg.TYP_DEPOSIT: {
                        // yes, this means that a mob speaking Common to a marketing player will get failed,
                        // however, remember that the LISTer language doesn't matter, only the responding (this) language.
                        // also, think about muds where there is no Common (an interesting mud!)
                        if ((!CMSecurity.isAllowed(msg.source(), msg.source().location(), CMSecurity.SecFlag.ORDER))
                            && (!CMSecurity.isAllowed(msg.source(), msg.source().location(), CMSecurity.SecFlag.CMDMOBS) || (!((MOB) msg.target()).isMonster()))
                            && (!CMSecurity.isAllowed(msg.source(), msg.source().location(), CMSecurity.SecFlag.CMDROOMS) || (!((MOB) msg.target()).isMonster()))) {
                            final Language L;
                            if ((msg.tool() instanceof Language) && (msg.tool().ID().equals(ID())))
                                L = (Language) msg.tool();
                            else
                                L = getAnyTranslator(ID(), msg.source());
                            if ((L == null)
                                || (!L.beingSpoken(ID()))
                                || ((CMLib.dice().rollPercentage() * 2) > (L.getProficiency(ID()) + getProficiency(ID())))) {
                                msg.setTargetCode(CMMsg.TYP_SPEAK);
                                msg.setSourceCode(CMMsg.TYP_SPEAK);
                                msg.setOthersCode(CMMsg.TYP_SPEAK);
                                String reply = null;
                                if ((L == null) || (!L.beingSpoken(ID())))
                                    reply = "<S-NAME> <S-IS-ARE> speaking " + name() + " and <T-NAME> would not understand <S-HIM-HER>.";
                                else
                                    reply = "<T-NAME> <T-IS-ARE> having trouble understanding <S-YOUPOSS> pronunciation.";
                                msg.addTrailerMsg(CMClass.getMsg((MOB) msg.target(), msg.source(), null, CMMsg.MSG_OK_VISUAL, reply));
                            }
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public boolean canBeLearnedBy(MOB teacher, MOB student) {
        if (!super.canBeLearnedBy(teacher, student))
            return false;
        if (student == null)
            return true;
        final AbilityComponents.AbilityLimits remainders = CMLib.ableComponents().getSpecialSkillRemainder(student, this);
        if (remainders.languageSkills() <= 0) {
            if (teacher != null)
                teacher.tell(L("@x1 can not learn any more languages.", student.name(teacher)));
            student.tell(L("You have learned the maximum @xlanguages, and may not learn any more.", "" + remainders.maxLanguageSkills()));
            return false;
        }
        return true;
    }

    @Override
    public void teach(MOB teacher, MOB student) {
        super.teach(teacher, student);
        if ((student != null) && (student.fetchAbility(ID()) != null)) {
            final AbilityComponents.AbilityLimits remainders = CMLib.ableComponents().getSpecialSkillRemainder(student, this);
            if (remainders.languageSkills() <= 0)
                student.tell(L("@x1 may not learn any more languages.", student.name()));
            else if (remainders.languageSkills() <= Integer.MAX_VALUE / 2)
                student.tell(L("@x1 may learn @x2 more languages.", student.name(), "" + remainders.languageSkills()));
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (!auto) {
            boolean alreadySpeaking = false;
            boolean found = false;
            for (final Enumeration<Ability> a = mob.effects(); a.hasMoreElements(); ) {
                final Ability A = a.nextElement();
                if ((A != null) && (A instanceof Language)) {
                    if (mob.isMonster())
                        A.setProficiency(100);
                    if (A.ID().equals(ID())) {
                        found = true;
                        alreadySpeaking = ((Language) A).beingSpoken(A.ID());
                        ((Language) A).setBeingSpoken(ID(), true);
                    } else
                        ((Language) A).setBeingSpoken(ID(), false);
                }
            }
            isAnAutoEffect = false;
            if (found) {
                if (alreadySpeaking)
                    mob.tell(L("You were already speaking @x1.", name()));
                else
                    mob.tell(L("You are now speaking @x1.", name()));
            } else
                mob.tell(L("You are now speaking Common."));
        } else
            setBeingSpoken(ID(), true);
        return true;
    }

    protected boolean translateOthersMessage(CMMsg msg, String sourceWords) {
        if ((msg.othersMessage() != null) && (msg.othersMessage().indexOf('\'') > 0)) {
            String otherMes = msg.othersMessage();
            if (msg.target() != null)
                otherMes = CMLib.aetherFilter().fullOutFilter(null, (MOB) affected, msg.source(), msg.target(), msg.tool(), otherMes, false);
            msg.addTrailerMsg(CMClass.getMsg(msg.source(), affected, null, CMMsg.NO_EFFECT, null, msg.othersCode(), L("@x1 (translated from @x2)", CMStrings.substituteSayInMessage(otherMes, sourceWords), name()), CMMsg.NO_EFFECT, null));
            return true;
        }
        return false;
    }

    protected boolean translateTargetMessage(CMMsg msg, String sourceWords) {
        if (msg.amITarget(affected) && (msg.targetMessage() != null)) {
            String otherMes = msg.targetMessage();
            if (msg.target() != null)
                otherMes = CMLib.aetherFilter().fullOutFilter(null, (MOB) affected, msg.source(), msg.target(), msg.tool(), otherMes, false);
            msg.addTrailerMsg(CMClass.getMsg(msg.source(), affected, null, CMMsg.NO_EFFECT, null, msg.targetCode(), L("@x1 (translated from @x2)", CMStrings.substituteSayInMessage(otherMes, sourceWords), name()), CMMsg.NO_EFFECT, null));
            return true;
        }
        return false;
    }

    protected boolean translateChannelMessage(CMMsg msg, String sourceWords) {
        if (CMath.bset(msg.sourceMajor(), CMMsg.MASK_CHANNEL)) {
            msg.addTrailerMsg(CMClass.getMsg(msg.source(), null, null, CMMsg.NO_EFFECT, CMMsg.NO_EFFECT, msg.othersCode(), L("@x1 (translated from @x2)", CMStrings.substituteSayInMessage(msg.othersMessage(), sourceWords), name())));
            return true;
        }
        return false;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);

        if ((affected instanceof MOB)
            && (!msg.amISource((MOB) affected))
            && ((msg.sourceMinor() == CMMsg.TYP_SPEAK)
            || (msg.sourceMinor() == CMMsg.TYP_TELL)
            || (CMath.bset(msg.sourceMajor(), CMMsg.MASK_CHANNEL)))
            && (msg.sourceMessage() != null)
            && (msg.tool() instanceof Language)
            && (msg.tool().ID().equals(ID()))) {
            String str = CMStrings.getSayFromMessage(msg.sourceMessage());
            if (str != null) {
                final int numToMess = (int) Math.round(CMath.mul(numChars(str), CMath.div(100 - getProficiency(ID()), 100)));
                if (numToMess > 0)
                    str = messChars(ID(), str, numToMess);
                if (!translateChannelMessage(msg, str)) {
                    if (!translateTargetMessage(msg, str))
                        translateOthersMessage(msg, str);
                }
            }
        } else if ((affected instanceof MOB)
            && (msg.source() == affected)
            && (beingSpoken(ID()))
            && (msg.target() instanceof Item)
            && (msg.sourceMinor() == CMMsg.TYP_WRITE)
            && (((Item) msg.target()).isReadable())
            && (msg.targetMessage() != null)
            && (msg.targetMessage().length() > 0)) {
            final Item I = (Item) msg.target();
            Ability L = null;
            for (int i = I.numEffects() - 1; i >= 0; i--) // reverse enumeration
            {
                L = I.fetchEffect(i);
                if (L instanceof Language) {
                    I.delEffect(L);
                    break;
                }
            }
            I.addNonUninvokableEffect((Ability) this.copyOf());
        } else if ((affected instanceof Item)
            && (!canBeUninvoked())
            && (msg.target() == affected)
            && (msg.targetMinor() == CMMsg.TYP_READ)
            && ((msg.targetMessage() == null) || (!msg.targetMessage().equals(CANCEL_WORD)))
            && (!(affected instanceof LandTitle))
            && (CMLib.flags().canBeSeenBy(this, msg.source()))
            && (((Item) affected).isReadable())
            && (((Item) affected).readableText() != null)
            && (((Item) affected).readableText().length() > 0)) {
            // first make sure the Item does not handle it,
            // since THIS item is in another language.
            msg.modify(msg.source(),
                msg.target(),
                msg.tool(),
                msg.sourceCode(),
                msg.sourceMessage(),
                msg.targetCode(),
                CANCEL_WORD,
                msg.othersCode(),
                msg.othersMessage());
            final Language L = (Language) msg.source().fetchEffect(ID());
            String str = ((Item) affected).readableText();
            if (str.startsWith("FILE=")
                || str.startsWith("FILE=")) {
                final StringBuffer buf = Resources.getFileResource(str.substring(5), true);
                if ((buf != null) && (buf.length() > 0))
                    str = buf.toString();
                else
                    str = "";
            }
            int numToMess = numChars(str);
            if (numToMess == 0)
                msg.source().tell(L("There is nothing written on @x1.", affected.name()));
            else {
                if (L != null)
                    numToMess = (int) Math.round(CMath.mul(numChars(str), CMath.div(100 - L.getProficiency(ID()), 100)));
                final String original = messChars(ID(), str, numToMess);
                str = scrambleAll(ID(), str, numToMess);
                msg.source().tell(L("It says '@x1'", str));
                if ((L != null) && (!original.equals(str)))
                    msg.source().tell(L("It says '@x1' (translated from @x2).", original, L.writtenName()));
            }
        }
    }
}
