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
package com.planet_ink.coffee_mud.Abilities.Properties;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Abilities.interfaces.TriggeredAffect;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Common.interfaces.Clan;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMStrings;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.collections.Pair;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Prop_ItemBinder extends Property implements TriggeredAffect {
    protected BoundTo to = BoundTo.CHARACTER;
    protected BoundOn on = BoundOn.PICKUP;
    protected String boundToName = "";
    protected String msgStr = defaultMessage();

    @Override
    public String ID() {
        return "Prop_ItemBinder";
    }

    @Override
    public String name() {
        return "Allows an item to be bound to player, account, or clan.";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_ITEMS;
    }

    @Override
    public void setMiscText(String text) {
        String boundTo = CMParms.getParmStr(text, "TO", "CHARACTER");
        to = (BoundTo) CMath.s_valueOf(BoundTo.class, boundTo.toUpperCase().trim());
        for (BoundTo b : BoundTo.values()) {
            if (b.name().startsWith(boundTo.toUpperCase().trim()))
                to = b;
        }
        if (to == null)
            to = BoundTo.CHARACTER;

        String boundOn = CMParms.getParmStr(text, "ON", "PICKUP");
        on = (BoundOn) CMath.s_valueOf(BoundOn.class, boundOn.toUpperCase().trim());
        for (BoundOn b : BoundOn.values()) {
            if (b.name().startsWith(boundTo.toUpperCase().trim()))
                on = b;
        }
        if (on == null)
            on = BoundOn.PICKUP;

        boundToName = CMStrings.deEscape(CMParms.getParmStr(text, "BOUND", ""));
        msgStr = CMParms.getParmStr(text, "MESSAGE", defaultMessage());
        super.setMiscText(text);
    }

    protected String defaultMessage() {
        return "<O-NAME> flashes and flies out of <S-HIS-HER> hands!";
    }

    @Override
    public long flags() {
        return Ability.FLAG_ZAPPER;
    }

    @Override
    public int triggerMask() {
        return TriggeredAffect.TRIGGER_GET;
    }

    @Override
    public String accountForYourself() {
        if (boundToName.length() > 0)
            return "Bound to: " + boundToName;
        return "Binds to " + this.to.toString().toLowerCase() + " on " + this.on.toString().toLowerCase();
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (affected == null)
            return false;

        final MOB mob = msg.source();
        if (mob.location() == null)
            return true;

        if (msg.amITarget(affected)) {
            switch (msg.targetMinor()) {
                case CMMsg.TYP_HOLD:
                    break;
                case CMMsg.TYP_WEAR:
                    break;
                case CMMsg.TYP_WIELD:
                    break;
                case CMMsg.TYP_GET:
                case CMMsg.TYP_EAT:
                case CMMsg.TYP_DRINK:
                    if (this.boundToName.length() > 0) {
                        boolean zap = true;
                        switch (to) {
                            case CHARACTER:
                                if (this.boundToName.equals(msg.source().Name()))
                                    zap = false;
                                break;
                            case GROUP: {
                                final List<String> names = CMParms.parse(this.boundToName);
                                if (names.contains(msg.source().Name()))
                                    zap = false;
                                break;
                            }
                            case ACCOUNT:
                                if ((msg.source().playerStats() != null)
                                    && (msg.source().playerStats().getAccount() != null)
                                    && (this.boundToName.equals(msg.source().playerStats().getAccount().getAccountName())))
                                    zap = false;
                                break;
                            case CLAN: {
                                final Pair<Clan, Integer> pI = msg.source().getClanRole(this.boundToName);
                                if (pI != null)
                                    zap = false;
                                break;
                            }
                        }
                        if (zap) {
                            mob.location().show(mob, null, affected, CMMsg.MSG_OK_ACTION, msgStr);
                            return false;
                        }
                    }
                    break;
                default:
                    break;
            }
        } else if ((this.boundToName.length() > 0)
            && (msg.tool() == affected)
            && (msg.sourceMinor() == CMMsg.TYP_SELL)) {
            msg.source().tell(L("@x1 is bound to @x2, and can not be sold.", affected.name(), this.boundToName));
            return false;
        }
        return true;
    }

    final String getBindyName(final MOB mob) {
        switch (to) {
            case CHARACTER:
                return mob.Name();
            case GROUP: {
                final Set<MOB> grp = mob.getGroupMembers(new HashSet<MOB>());
                final List<String> grpMemberNames = new ArrayList<String>();
                for (MOB M : grp)
                    grpMemberNames.add(M.Name());
                return CMParms.combineQuoted(grpMemberNames, 0);
            }
            case ACCOUNT:
                if ((mob.playerStats() != null)
                    && (mob.playerStats().getAccount() != null))
                    return mob.playerStats().getAccount().getAccountName();
                break;
            case CLAN: {
                final Clan C = CMLib.clans().findRivalrousClan(mob);
                if (C != null)
                    return C.getName();
                break;
            }
        }
        return "";
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (msg.amITarget(affected)) {
            switch (msg.targetMinor()) {
                case CMMsg.TYP_HOLD:
                case CMMsg.TYP_WEAR:
                case CMMsg.TYP_WIELD:
                case CMMsg.TYP_EAT:
                case CMMsg.TYP_DRINK:
                    if ((this.boundToName.length() == 0)
                        && (on == BoundOn.EQUIP)) {
                        this.boundToName = getBindyName(msg.source());
                        if (this.boundToName.length() > 0)
                            super.miscText = text() + " BOUND=\"" + CMStrings.escape(this.boundToName) + "\"";
                    }
                    break;
                case CMMsg.TYP_GET:
                    if ((this.boundToName.length() == 0)
                        && (on == BoundOn.PICKUP)) {
                        this.boundToName = getBindyName(msg.source());
                        if (this.boundToName.length() > 0)
                            super.miscText = text() + " BOUND=\"" + CMStrings.escape(this.boundToName) + "\"";
                    }
                    break;
                default:
                    break;
            }
        }
    }

    protected enum BoundTo {
        CHARACTER,
        ACCOUNT,
        GROUP,
        CLAN
    }

    protected enum BoundOn {
        PICKUP,
        EQUIP
    }
}
