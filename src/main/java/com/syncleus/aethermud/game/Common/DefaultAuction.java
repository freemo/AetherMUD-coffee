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
package com.planet_ink.game.Common;

import com.planet_ink.game.Areas.interfaces.Area;
import com.planet_ink.game.Common.interfaces.AuctionData;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMProps;
import com.planet_ink.game.core.CMath;
import com.planet_ink.game.core.interfaces.CMObject;


public class DefaultAuction implements AuctionData {
    public Item auctioningI = null;
    public MOB auctioningM = null;
    public MOB highBidderM = null;
    public String currency = "";
    public double highBid = 0.0;
    public double bid = 0.0;
    public double buyOutPrice = 0.0;
    public int state = -1;
    public long tickDown = 0;
    public long start = 0;
    public String auctionDBKey = "";

    @Override
    public String ID() {
        return "DefaultAuction";
    }

    @Override
    public String name() {
        return ID();
    }

    @Override
    public CMObject newInstance() {
        try {
            return getClass().newInstance();
        } catch (final Exception e) {
            return new DefaultAuction();
        }
    }

    @Override
    public int compareTo(CMObject o) {
        return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));
    }

    @Override
    public void initializeClass() {
    }

    @Override
    public CMObject copyOf() {
        try {
            return (DefaultAuction) this.clone();
        } catch (final CloneNotSupportedException e) {
            return newInstance();
        }
    }

    @Override
    public int daysRemaining(MOB mob, MOB mob2) {
        if (System.currentTimeMillis() >= tickDown)
            return 0;
        Area A = CMLib.map().getStartArea(mob);
        if (A == null)
            A = CMLib.map().getStartArea(mob2);
        long daysRemain = tickDown - System.currentTimeMillis();
        daysRemain = Math.round(Math.floor(CMath.div(CMath.div(daysRemain, CMProps.getMillisPerMudHour()), A.getTimeObj().getHoursInDay())));
        return (int) daysRemain;
    }

    @Override
    public int daysEllapsed(MOB mob, MOB mob2) {
        if (System.currentTimeMillis() < start)
            return 0;
        Area A = CMLib.map().getStartArea(mob);
        if (A == null)
            A = CMLib.map().getStartArea(mob2);
        long daysRemain = System.currentTimeMillis() - start;
        daysRemain = Math.round(Math.floor(CMath.div(CMath.div(daysRemain, CMProps.getMillisPerMudHour()), A.getTimeObj().getHoursInDay())));
        return (int) daysRemain;
    }

    @Override
    public Item getAuctionedItem() {
        return auctioningI;
    }

    @Override
    public void setAuctionedItem(Item auctioningI) {
        this.auctioningI = auctioningI;
    }

    @Override
    public MOB getAuctioningMob() {
        return auctioningM;
    }

    @Override
    public void setAuctioningMob(MOB auctioningM) {
        this.auctioningM = auctioningM;
    }

    @Override
    public MOB getHighBidderMob() {
        return highBidderM;
    }

    @Override
    public void setHighBidderMob(MOB highBidderM) {
        this.highBidderM = highBidderM;
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    @Override
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public double getHighBid() {
        return highBid;
    }

    @Override
    public void setHighBid(double highBid) {
        this.highBid = highBid;
    }

    @Override
    public double getBid() {
        return bid;
    }

    @Override
    public void setBid(double bid) {
        this.bid = bid;
    }

    @Override
    public double getBuyOutPrice() {
        return buyOutPrice;
    }

    @Override
    public void setBuyOutPrice(double buyOutPrice) {
        this.buyOutPrice = buyOutPrice;
    }

    @Override
    public int getAuctionState() {
        return state;
    }

    @Override
    public void setAuctionState(int state) {
        this.state = state;
    }

    @Override
    public long getAuctionTickDown() {
        return tickDown;
    }

    @Override
    public void setAuctionTickDown(long tickDown) {
        this.tickDown = tickDown;
    }

    @Override
    public long getStartTime() {
        return start;
    }

    @Override
    public void setStartTime(long start) {
        this.start = start;
    }

    @Override
    public String getAuctionDBKey() {
        return auctionDBKey;
    }

    @Override
    public void setAuctionDBKey(String auctionDBKey) {
        this.auctionDBKey = auctionDBKey;
    }
}
