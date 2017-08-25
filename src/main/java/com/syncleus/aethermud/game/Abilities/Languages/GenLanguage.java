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
import com.syncleus.aethermud.game.Libraries.interfaces.XMLLibrary;
import com.syncleus.aethermud.game.core.*;
import com.syncleus.aethermud.game.core.CMClass.CMObjectType;
import com.syncleus.aethermud.game.core.interfaces.CMObject;
import com.syncleus.aethermud.game.core.interfaces.Environmental;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;


public class GenLanguage extends StdLanguage {
    private static final Hashtable<String, Object[]> vars = new Hashtable<String, Object[]>();
    private static final int V_NAME = 0;//S
    private static final int V_WSETS = 1;//L<S[]>
    private static final int V_HSETS = 2;//H<S,S>
    private static final int V_HELP = 3;//S
    private static final int NUM_VS = 4;//S
    private static final String[] CODES = {"CLASS",//0
        "TEXT",//1
        "NAME",//2S
        "WORDS",//2S
        "HASHEDWORDS",//2S
        "HELP",//27I
    };
    public String ID = "GenLanguage";
    public GenLanguage() {
        super();
    }

    private static final Object[] makeEmpty() {
        final Object[] O = new Object[NUM_VS];
        O[V_NAME] = "a language";
        O[V_WSETS] = new Vector<String[]>();
        O[V_HSETS] = new Hashtable<String, String>();
        O[V_HELP] = "<ABILITY>This language is not yet documented.";
        return O;
    }

    private static final Object V(String ID, int varNum) {
        if (vars.containsKey(ID))
            return vars.get(ID)[varNum];
        final Object[] O = makeEmpty();
        vars.put(ID, O);
        return O[varNum];
    }

    private static final void SV(String ID, int varNum, Object O) {
        if (vars.containsKey(ID))
            vars.get(ID)[varNum] = O;
        else {
            final Object[] O2 = makeEmpty();
            vars.put(ID, O2);
            O2[varNum] = O;
        }
    }

    @Override
    public String ID() {
        return ID;
    }

    @Override
    public String Name() {
        return name();
    }

    @Override
    public String name() {
        return (String) V(ID, V_NAME);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String[]> translationVector(String language) {
        return (List<String[]>) V(ID, V_WSETS);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> translationHash(String language) {
        return (Map<String, String>) V(ID, V_HSETS);
    }

    @Override
    public CMObject newInstance() {
        try {
            final GenLanguage A = this.getClass().newInstance();
            A.ID = ID;
            return A;
        } catch (final Exception e) {
            Log.errOut(ID(), e);
        }
        return new GenLanguage();
    }

    @Override
    protected void cloneFix(Ability E) {
    }

    @Override
    public boolean isGeneric() {
        return true;
    }

    // lots of work to be done here
    @Override
    public int getSaveStatIndex() {
        return getStatCodes().length;
    }

    @Override
    public String[] getStatCodes() {
        return CODES;
    }

    @Override
    protected int getCodeNum(String code) {
        for (int i = 0; i < CODES.length; i++) {
            if (code.equalsIgnoreCase(CODES[i]))
                return i;
        }
        return -1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getStat(String code) {
        int num = 0;
        int numDex = code.length();
        while ((numDex > 0) && (Character.isDigit(code.charAt(numDex - 1))))
            numDex--;
        if (numDex < code.length()) {
            num = CMath.s_int(code.substring(numDex));
            code = code.substring(0, numDex);
        }
        switch (getCodeNum(code)) {
            case 0:
                return ID();
            case 1:
                return text();
            case 2:
                return (String) V(ID, V_NAME);
            case 3:
                if (num == 0) {
                    final List<String[]> words = (List<String[]>) V(ID, V_WSETS);
                    final StringBuilder str = new StringBuilder("");
                    for (final String[] wset : words) {
                        if (str.length() > 0)
                            str.append("/");
                        str.append(CMParms.toListString(wset));
                    }
                    return str.toString();
                } else if (num <= ((List<String[]>) V(ID, V_WSETS)).size())
                    return CMParms.toListString(((List<String[]>) V(ID, V_WSETS)).get(num - 1));
                else
                    return "";
            case 4:
                return CMParms.toKeyValueSlashListString((Map<String, String>) V(ID, V_HSETS));
            case 5:
                return (String) V(ID, V_HELP);
            default:
                if (code.equalsIgnoreCase("allxml"))
                    return getAllXML();
                break;
        }
        return "";
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setStat(String code, String val) {
        int num = 0;
        int numDex = code.length();
        while ((numDex > 0) && (Character.isDigit(code.charAt(numDex - 1))))
            numDex--;
        if (numDex < code.length()) {
            num = CMath.s_int(code.substring(numDex));
            code = code.substring(0, numDex);
        }
        switch (getCodeNum(code)) {
            case 0:
                if (val.trim().length() > 0) {
                    V(ID, V_NAME); // force creation, if necc
                    final Object[] O = vars.get(ID);
                    vars.remove(ID);
                    vars.put(val, O);
                    if (num != 9)
                        CMClass.delClass(CMObjectType.ABILITY, this);
                    ID = val;
                    if (num != 9)
                        CMClass.addClass(CMObjectType.ABILITY, this);
                }
                break;
            case 1:
                setMiscText(val);
                break;
            case 2:
                SV(ID, V_NAME, val);
                if (ID.equalsIgnoreCase("GenLanguage"))
                    break;
                break;
            case 3:
                if (num == 0) {
                    final String[] allSets = val.split("/");
                    final List<String[]> wordSets = new Vector<String[]>();
                    for (final String wordList : allSets)
                        wordSets.add(CMParms.parseCommas(wordList, true).toArray(new String[0]));
                    SV(ID, V_WSETS, wordSets);
                } else if ((num == ((List<String[]>) V(ID, V_WSETS)).size()) && (val.length() == 0))
                    ((List<String[]>) V(ID, V_WSETS)).remove(num - 1);
                else if (num <= ((List<String[]>) V(ID, V_WSETS)).size())
                    ((List<String[]>) V(ID, V_WSETS)).set(num - 1, CMParms.parseCommas(val, true).toArray(new String[0]));
                else if ((num == ((List<String[]>) V(ID, V_WSETS)).size() + 1) && (val.length() > 0))
                    ((List<String[]>) V(ID, V_WSETS)).add(CMParms.parseCommas(val, true).toArray(new String[0]));
                break;
            case 4:
                SV(ID, V_HSETS, CMParms.parseKeyValueSlashListString(val));
                break;
            case 5:
                SV(ID, V_HELP, val);
                break;
            default:
                if (code.equalsIgnoreCase("allxml") && ID.equalsIgnoreCase("GenLanguage"))
                    parseAllXML(val);
                break;
        }
    }

    @Override
    public boolean sameAs(Environmental E) {
        if (!(E instanceof GenLanguage))
            return false;
        if (!((GenLanguage) E).ID().equals(ID))
            return false;
        if (!((GenLanguage) E).text().equals(text()))
            return false;
        return true;
    }

    private void parseAllXML(String xml) {
        final List<XMLLibrary.XMLTag> V = CMLib.xml().parseAllXML(xml);
        if ((V == null) || (V.size() == 0))
            return;
        for (int c = 0; c < getStatCodes().length; c++) {
            if (getStatCodes()[c].equals("CLASS"))
                ID = CMLib.xml().restoreAngleBrackets(CMLib.xml().getValFromPieces(V, getStatCodes()[c]));
            else if (!getStatCodes()[c].equals("TEXT"))
                setStat(getStatCodes()[c], CMLib.xml().restoreAngleBrackets(CMLib.xml().getValFromPieces(V, getStatCodes()[c])));
        }
    }

    private String getAllXML() {
        final StringBuffer str = new StringBuffer("");
        for (int c = 0; c < getStatCodes().length; c++) {
            if (!getStatCodes()[c].equals("TEXT")) {
                str.append("<" + getStatCodes()[c] + ">"
                    + CMLib.xml().parseOutAngleBrackets(getStat(getStatCodes()[c]))
                    + "</" + getStatCodes()[c] + ">");
            }
        }
        return str.toString();
    }
}
