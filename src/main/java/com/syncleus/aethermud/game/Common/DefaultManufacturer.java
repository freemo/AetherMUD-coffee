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
package com.syncleus.aethermud.game.Common;

import com.syncleus.aethermud.game.Common.interfaces.Manufacturer;
import com.syncleus.aethermud.game.Items.interfaces.Technical;
import com.syncleus.aethermud.game.Items.interfaces.Technical.TechType;
import com.syncleus.aethermud.game.Libraries.interfaces.MaskingLibrary;
import com.syncleus.aethermud.game.Libraries.interfaces.XMLLibrary.XMLTag;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.CMObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DefaultManufacturer implements Manufacturer {
    protected String name = "ACME";
    protected byte maxTechLevelDiff = 10;
    protected byte minTechLevelDiff = 0;
    protected double efficiency = 1.0;
    protected double reliability = 1.0;
    protected String rawItemMask = "";
    protected Set<TechType> types = new HashSet<TechType>();
    protected MaskingLibrary.CompiledZMask compiledItemMask = null;

    @Override
    public String ID() {
        return "DefaultManufacturer";
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public CMObject newInstance() {
        try {
            return getClass().newInstance();
        } catch (final Exception e) {
            return new DefaultPoll();
        }
    }

    @Override
    public void initializeClass() {
    }

    @Override
    public int compareTo(CMObject o) {
        return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));
    }

    @Override
    public CMObject copyOf() {
        try {
            return (Manufacturer) this.clone();
        } catch (final CloneNotSupportedException e) {
            return newInstance();
        }
    }

    @Override
    public byte getMaxTechLevelDiff() {
        return maxTechLevelDiff;
    }

    @Override
    public void setMaxTechLevelDiff(byte max) {
        this.maxTechLevelDiff = max;
    }

    @Override
    public byte getMinTechLevelDiff() {
        return minTechLevelDiff;
    }

    @Override
    public void setMinTechLevelDiff(byte min) {
        this.minTechLevelDiff = min;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public double getEfficiencyPct() {
        return efficiency;
    }

    @Override
    public void setEfficiencyPct(double pct) {
        efficiency = CMath.div(Math.round(pct * 100), 100.0);
    }

    @Override
    public double getReliabilityPct() {
        return reliability;
    }

    @Override
    public void setReliabilityPct(double pct) {
        reliability = CMath.div(Math.round(pct * 100), 100.0);
    }

    @Override
    public String getItemMaskStr() {
        return rawItemMask;
    }

    @Override
    public boolean isManufactureredType(Technical T) {
        if (T == null)
            return false;
        if (types.contains(TechType.ANY))
            return true;
        for (final TechType type : types) {
            if (type == T.getTechType())
                return true;
        }
        return false;
    }

    @Override
    public String getManufactureredTypesList() {
        return CMParms.toListString(types);
    }

    @Override
    public void setManufactureredTypesList(String list) {
        final Set<TechType> newTypes = new HashSet<TechType>();
        for (final String s : CMParms.parseCommas(list, true)) {
            final TechType t = (TechType) CMath.s_valueOf(TechType.class, s.toUpperCase().trim());
            if (t != null)
                newTypes.add(t);
        }
        this.types = newTypes;
    }

    @Override
    public MaskingLibrary.CompiledZMask getItemMask() {
        return compiledItemMask;
    }

    @Override
    public void setItemMask(String newMask) {
        if ((newMask == null) || (newMask.trim().length() == 0)) {
            newMask = "";
            rawItemMask = newMask.trim();
            compiledItemMask = null;
        } else {
            rawItemMask = newMask.trim();
            compiledItemMask = CMLib.masking().getPreCompiledMask(rawItemMask);
        }
    }

    @Override
    public String getXml() {
        final StringBuilder xml = new StringBuilder("");
        xml.append("<NAME>" + CMLib.xml().parseOutAngleBrackets(name) + "</NAME>");
        xml.append("<MAXTECHDIFF>" + maxTechLevelDiff + "</MAXTECHDIFF>");
        xml.append("<MINTECHDIFF>" + minTechLevelDiff + "</MINTECHDIFF>");
        xml.append("<EFFICIENCY>" + efficiency + "</EFFICIENCY>");
        xml.append("<MASK>" + CMLib.xml().parseOutAngleBrackets(getItemMaskStr()) + "</MASK>");
        xml.append("<TYPES>" + getManufactureredTypesList() + "</TYPES>");
        xml.append("<RELIABILITY>" + reliability + "</RELIABILITY>");
        return xml.toString();
    }

    @Override
    public void setXml(String xml) {
        final List<XMLTag> xpc = CMLib.xml().parseAllXML(xml);
        setName(CMLib.xml().restoreAngleBrackets(CMLib.xml().getValFromPieces(xpc, "NAME")));
        setMaxTechLevelDiff((byte) CMath.s_short(CMLib.xml().getValFromPieces(xpc, "MAXTECHDIFF")));
        setMinTechLevelDiff((byte) CMath.s_short(CMLib.xml().getValFromPieces(xpc, "MINTECHDIFF")));
        setEfficiencyPct(CMLib.xml().getDoubleFromPieces(xpc, "EFFICIENCY"));
        setReliabilityPct(CMLib.xml().getDoubleFromPieces(xpc, "RELIABILITY"));
        setItemMask(CMLib.xml().restoreAngleBrackets(CMLib.xml().getValFromPieces(xpc, "MASK")));
        setManufactureredTypesList(CMLib.xml().getValFromPieces(xpc, "TYPES"));
    }
}
