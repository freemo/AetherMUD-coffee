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
package com.planet_ink.game.Items.Armor;

import com.planet_ink.game.Items.Basic.StdItem;
import com.planet_ink.game.Items.interfaces.Armor;
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.MOBS.interfaces.MOB;


public class StdThinArmor extends StdItem implements Armor {
    int sheath = 0;
    short layer = 0;
    short layerAttributes = 0;
    public StdThinArmor() {
        super();

        setName("a piece of armor");
        setDisplayText("a piece of armor here.");
        setDescription("Thick padded leather with strips of metal interwoven.");
        properWornBitmap = Wearable.WORN_EYES;
        wornLogicalAnd = false;
        basePhyStats().setArmor(1);
        basePhyStats().setAbility(0);
        baseGoldValue = 150;
        setUsesRemaining(100);
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "StdThinArmor";
    }

    @Override
    public void setUsesRemaining(int newUses) {
        if (newUses == Integer.MAX_VALUE)
            newUses = 100;
        super.setUsesRemaining(newUses);
    }

    @Override
    public short getClothingLayer() {
        return layer;
    }

    @Override
    public void setClothingLayer(short newLayer) {
        layer = newLayer;
    }

    @Override
    public short getLayerAttributes() {
        return layerAttributes;
    }

    @Override
    public void setLayerAttributes(short newAttributes) {
        layerAttributes = newAttributes;
    }

    @Override
    public boolean canWear(MOB mob, long where) {
        if (where == 0)
            return (whereCantWear(mob) == 0);
        if ((rawProperLocationBitmap() & where) != where)
            return false;
        return mob.freeWearPositions(where, getClothingLayer(), getLayerAttributes()) > 0;
    }

    @Override
    public SizeDeviation getSizingDeviation(MOB mob) {
        return SizeDeviation.FITS;
    }

    @Override
    public boolean subjectToWearAndTear() {
        return false;
    }

    @Override
    public String secretIdentity() {
        String id = super.secretIdentity();
        if (phyStats().ability() > 0)
            id = name() + " +" + phyStats().ability() + ((id.length() > 0) ? "\n\r" : "") + id;
        else if (phyStats().ability() < 0)
            id = name() + " " + phyStats().ability() + ((id.length() > 0) ? "\n\r" : "") + id;
        return id + "\n\r" + L("Base Protection: @x1", "" + phyStats().armor());
    }
}
