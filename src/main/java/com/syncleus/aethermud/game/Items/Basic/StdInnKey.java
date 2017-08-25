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

import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.InnKey;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.ShopKeeper;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.Iterator;


public class StdInnKey extends StdKey implements InnKey {
    public ShopKeeper myShopkeeper = null;

    public StdInnKey() {
        super();
        setName("a metal key");
        setDisplayText("a small metal key sits here.");
        setDescription("It says it goes to room 1.");

        material = RawMaterial.RESOURCE_STEEL;
        baseGoldValue = 10;
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "StdInnKey";
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (tickID == Tickable.TICKID_ITEM_BOUNCEBACK) {
            this.destroyed = false;
            this.setContainer(null);
            if ((owner() != null) && (owner() == myShopkeeper))
                return false;
            if (owner() != null)
                removeFromOwnerContainer();
            if (myShopkeeper != null) {
                myShopkeeper.getShop().addStoreInventory(this); // makes a copy
                destroy();
            }
            return false;
        }
        return true;
    }

    @Override
    public void hangOnRack(ShopKeeper SK) {
        if (myShopkeeper == null) {
            myShopkeeper = SK;
            int y = 0;
            for (final Iterator<Environmental> i = SK.getShop().getStoreInventory(); i.hasNext(); ) {
                final Environmental E = i.next();
                if (E instanceof InnKey)
                    y++;
            }
            setName("key to room " + (y + 1));
            setDescription("The key goes to room " + (y + 1) + ", but will expire soon, so you better use it quickly! Give the key to your innkeeper, " + SK.name() + ", when you leave.");
            setMiscText("INN" + (y + 1));
        }
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        super.executeMsg(myHost, msg);
        if (((msg.targetMinor() == CMMsg.TYP_GIVE)
            || (msg.targetMinor() == CMMsg.TYP_SELL))
            && (myShopkeeper != null)
            && (msg.target() == myShopkeeper)
            && (msg.tool() == this)) {
            CMLib.threads().deleteTick(this, Tickable.TICKID_ITEM_BOUNCEBACK);
            myShopkeeper.getShop().addStoreInventory(this); //makes a copy
            destroy();
        }
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if (((msg.targetMinor() == CMMsg.TYP_GIVE)
            || (msg.targetMinor() == CMMsg.TYP_SELL))
            && (msg.target() instanceof ShopKeeper)
            && (myShopkeeper != null)
            && (msg.target() != myShopkeeper)
            && (msg.tool() == this)) {
            CMLib.commands().postSay((MOB) msg.target(), msg.source(), L("I'm not interested."), false, false);
            return false;
        } else if ((msg.sourceMinor() == CMMsg.TYP_GET)
            && (myShopkeeper != null)
            && (msg.tool() == myShopkeeper)
            && (msg.target() == this))
            CMLib.threads().startTickDown(this, Tickable.TICKID_ITEM_BOUNCEBACK, CMProps.getIntVar(CMProps.Int.TICKSPERMUDDAY));
        else if ((msg.sourceMinor() == CMMsg.TYP_ENTER)
            && (msg.target() instanceof Room)
            && (owner() == msg.source())
            && (msg.source().location() != null)
            && (((Room) msg.target()).getArea() != msg.source().location().getArea())
            && (super.miscText != null)) {
            final Area shopArea = CMLib.map().areaLocation(myShopkeeper);
            if ((shopArea == ((Room) msg.target()).getArea()) || (shopArea == null)) {
                if (super.miscText.startsWith("-"))
                    super.miscText = super.miscText.substring(1);
            } else if (!super.miscText.startsWith("-"))
                super.miscText = "-" + super.miscText;
        }
        return true;
    }
}
