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
import com.syncleus.aethermud.game.Items.interfaces.Container;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.PackagedItems;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Libraries.interfaces.XMLLibrary;
import com.syncleus.aethermud.game.Libraries.interfaces.XMLLibrary.XMLTag;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.Log;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.ItemPossessor;

import java.util.List;
import java.util.Vector;


public class GenPackagedVariety extends GenItem implements PackagedItems {
    protected byte[] readableText = null;
    protected int numberOfItemsInPackage = 0;

    public GenPackagedVariety() {
        super();
        setName("a package of things");
        basePhyStats.setWeight(150);
        setDisplayText("a package sits here.");
        setDescription("");
        baseGoldValue = 5;
        basePhyStats().setLevel(1);
        setMaterial(RawMaterial.RESOURCE_WOOD);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "GenPackagedVariety";
    }

    @Override
    public String readableText() {
        return readableText == null ? "" : CMLib.encoder().decompressString(readableText);
    }

    @Override
    public void setReadableText(String text) {
        readableText = (text.trim().length() == 0) ? null : CMLib.encoder().compressString(text);
    }

    @Override
    public boolean packageMe(Item I, int number) {
        if ((I == null)
            || (I.amDestroyed()))
            return false;
        CMLib.utensils().disInvokeEffects(I);
        final String name = (number < 2) ? I.Name() : I.Name() + " (x" + number + ")";
        if (description().length() == 0)
            setDescription("The contents of the package appears as follows:\n\r" + name);
        else
            setDescription(description() + "\n\r" + name);
        basePhyStats().setWeight(basePhyStats().weight() + (I.basePhyStats().weight() * number));
        if (I.basePhyStats().height() > basePhyStats().height())
            basePhyStats().setHeight(I.basePhyStats().height());
        if (I.basePhyStats().level() > basePhyStats().level())
            basePhyStats().setLevel(I.basePhyStats().level());
        setBaseValue(baseGoldValue() + (I.baseGoldValue() * number));
        final StringBuffer itemstr = new StringBuffer("");
        itemstr.append("<PAKITEM>");
        itemstr.append(CMLib.xml().convertXMLtoTag("PINUM", "" + number));
        itemstr.append(CMLib.xml().convertXMLtoTag("PICLASS", CMClass.classID(I)));
        itemstr.append(CMLib.xml().convertXMLtoTag("PIDATA", CMLib.coffeeMaker().getPropertiesStr(I, true)));
        itemstr.append("</PAKITEM>");
        setNumberOfItemsInPackage(this.numberOfItemsInPackage() + number);
        setPackageText(packageText() + itemstr.toString());
        recoverPhyStats();
        return true;
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if ((msg.amITarget(this)
            || ((msg.tool() == this) && (msg.target() instanceof Container)))
            && ((getPackageFlagsBitmap() & PACKAGE_FLAG_TO_ITEMS_PROGRAMMATICALLY) == 0)
            && ((msg.targetMinor() == CMMsg.TYP_GET) || (msg.targetMinor() == CMMsg.TYP_DROP))) {
            ItemPossessor possessor = owner();
            if ((msg.targetMinor() == CMMsg.TYP_DROP) && (msg.target() instanceof Room))
                possessor = (Room) msg.target();
            List<Item> items = unPackage(Integer.MAX_VALUE);
            for (Item I : items)
                possessor.addItem(I, ItemPossessor.Expire.Player_Drop);
            destroy();
            return;
        }
        super.executeMsg(myHost, msg);
    }

    @Override
    public boolean isPackagable(List<Item> V) {
        if (V == null)
            return false;
        if (V.size() == 0)
            return false;
        return true;
    }

    @Override
    public Item peekFirstItem() {
        if (packageText().length() == 0)
            return null;
        final List<XMLLibrary.XMLTag> buf = CMLib.xml().parseAllXML(packageText());
        if (buf == null) {
            Log.errOut("Packaged", "Error parsing 'PAKITEM'.");
            return null;
        }
        final XMLTag iblk = CMLib.xml().getPieceFromPieces(buf, "PAKITEM");
        if ((iblk == null) || (iblk.contents() == null)) {
            Log.errOut("Packaged", "Error parsing 'PAKITEM'.");
            return null;
        }
        final String itemi = iblk.getValFromPieces("PICLASS");
        final Environmental newOne = CMClass.getItem(itemi);
        final List<XMLLibrary.XMLTag> idat = iblk.getContentsFromPieces("PIDATA");
        if ((idat == null) || (newOne == null) || (!(newOne instanceof Item))) {
            Log.errOut("Packaged", "Error parsing 'PAKITEM' data.");
            return null;
        }
        CMLib.coffeeMaker().setPropertiesStr(newOne, idat, true);
        return (Item) newOne;
    }

    @Override
    public boolean areAllItemsTheSame() {
        return false;
    }

    @Override
    public List<Item> unPackage(int number) {
        final List<Item> V = new Vector<Item>();
        int numberInPackage = numberOfItemsInPackage();
        if (number >= numberInPackage)
            number = numberInPackage;
        if (number <= 0)
            return V;
        Item firstItem = null;
        final List<XMLLibrary.XMLTag> buf = CMLib.xml().parseAllXML(packageText());
        StringBuilder newXml = new StringBuilder("");
        if (buf != null) {
            for (int p = 0; p < buf.size(); p++) {
                final XMLTag iblk = buf.get(p);
                if ((iblk != null) && (iblk.contents() != null) && (iblk.tag().equals("PAKITEM"))) {
                    if (number <= 0)
                        newXml.append("<PAKITEM>" + iblk.value() + "</PAKITEM>");
                    else {
                        final int numOfThese = iblk.getIntFromPieces("PINUM");
                        final String itemi = iblk.getValFromPieces("PICLASS");
                        final Environmental newOne = CMClass.getItem(itemi);
                        final List<XMLLibrary.XMLTag> idat = iblk.getContentsFromPieces("PIDATA");
                        if ((idat != null) && (newOne != null) && (newOne instanceof Item)) {
                            Item I = (Item) newOne;
                            CMLib.coffeeMaker().setPropertiesStr(newOne, idat, true);
                            for (int i = 0; i < numOfThese; i++) {
                                if (number <= 0) {
                                    newXml.append("<PAKITEM>");
                                    newXml.append(CMLib.xml().convertXMLtoTag("PINUM", "" + (numOfThese - i)));
                                    newXml.append(CMLib.xml().convertXMLtoTag("PICLASS", itemi));
                                    newXml.append(CMLib.xml().convertXMLtoTag("PIDATA", iblk.getValFromPieces("PIDATA")));
                                    newXml.append("</PAKITEM>");
                                } else {
                                    I = (Item) newOne.copyOf();
                                    if (basePhyStats().weight() > I.basePhyStats().weight())
                                        basePhyStats().setWeight(basePhyStats().weight() - I.basePhyStats().weight());
                                    if (baseGoldValue() > I.baseGoldValue())
                                        setBaseValue(baseGoldValue() - I.baseGoldValue());
                                    if (firstItem instanceof Container)
                                        I.setContainer((Container) firstItem);
                                    if (firstItem == null)
                                        firstItem = I;
                                    number--;
                                    numberInPackage--;
                                    V.add(I);
                                }
                            }
                        }
                    }
                }
            }
        }
        setNumberOfItemsInPackage(numberInPackage);
        if (numberInPackage <= 0) {
            destroy();
            return V;
        }
        recoverPhyStats();
        return V;
    }

    @Override
    public String packageText() {
        return CMLib.xml().restoreAngleBrackets(readableText());
    }

    @Override
    public void setPackageText(String text) {
        setReadableText(CMLib.xml().parseOutAngleBrackets(text));
        CMLib.flags().setReadable(this, false);
    }

    @Override
    public int numberOfItemsInPackage() {
        if (this.numberOfItemsInPackage <= 0) {
            final List<XMLLibrary.XMLTag> buf = CMLib.xml().parseAllXML(packageText());
            if (buf != null) {
                for (int p = 0; p < buf.size(); p++) {
                    final XMLTag iblk = buf.get(p);
                    if ((iblk != null) && (iblk.contents() != null))
                        this.numberOfItemsInPackage += iblk.getIntFromPieces("PINUM");
                }
            }
        }
        return numberOfItemsInPackage;
    }

    @Override
    public void setNumberOfItemsInPackage(int number) {
        numberOfItemsInPackage = number;
    }

    @Override
    public int getPackageFlagsBitmap() {
        return basePhyStats().ability();
    }

    @Override
    public void setPackageFlagsBitmap(int bitmap) {
        basePhyStats().setAbility(bitmap);
        phyStats().setAbility(bitmap);
    }
}
