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
package com.syncleus.aethermud.game.Behaviors;

import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Behaviors.interfaces.Behavior;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Exits.interfaces.Exit;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.*;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.Enumeration;
import java.util.List;


@SuppressWarnings("rawtypes")
public class Sounder extends StdBehavior {
    protected static int UNDER_MASK = 1023;
    protected static int TICK_MASK = 65536;
    protected static int ROOM_MASK = 32768;
    protected int minTicks = 23;
    protected int maxTicks = 23;
    protected int tickDown = (int) Math.round(Math.random() * (maxTicks - minTicks)) + minTicks;
    protected int[] triggers = null;
    protected String[] strings = null;
    protected CMMsg lastMsg = null;
    protected boolean oncePerRound1 = false;
    public Sounder() {
        super();
        minTicks = 23;
        maxTicks = 23;
        tickReset();
    }

    @Override
    public String ID() {
        return "Sounder";
    }

    @Override
    protected int canImproveCode() {
        return Behavior.CAN_ITEMS | Behavior.CAN_MOBS | Behavior.CAN_ROOMS | Behavior.CAN_EXITS | Behavior.CAN_AREAS;
    }

    @Override
    public String accountForYourself() {
        return "triggered emoting";
    }

    protected void tickReset() {
        tickDown = (int) Math.round(Math.random() * (maxTicks - minTicks)) + minTicks;
    }

    @Override
    public void setParms(String newParms) {
        super.setParms(newParms);
        final List<String> emote = CMParms.parseSemicolons(newParms, true);
        triggers = new int[emote.size()];
        strings = new String[emote.size()];

        if (emote.size() > 0) {
            String s = emote.get(0);
            minTicks = 23;
            minTicks = CMParms.getParmInt(newParms, "min", minTicks);
            maxTicks = 23;
            maxTicks = CMParms.getParmInt(newParms, "max", maxTicks);
            if ((minTicks != 23) || (maxTicks != 23))
                emote.remove(0);
            for (int v = 0; v < emote.size(); v++) {
                s = emote.get(v).trim();
                s = CMStrings.replaceAll(s, "$n", "<S-NAME>");
                s = CMStrings.replaceAll(s, "$N", "<S-NAME>");
                s = CMStrings.replaceAll(s, "$e", "<S-HE-SHE>");
                s = CMStrings.replaceAll(s, "$E", "<S-HE-SHE>");
                s = CMStrings.replaceAll(s, "$s", "<S-HIS-HER>");
                s = CMStrings.replaceAll(s, "$S", "<S-HIS-HER>");
                if (s.toUpperCase().startsWith("SOUND ")) {
                    s = s.substring(6).trim();
                    final int x = s.indexOf(' ');
                    if (x < 0)
                        continue;
                    final String y = s.substring(0, x);
                    if (!CMath.isNumber(y))
                        continue;
                    triggers[v] = TICK_MASK + CMath.s_int(y);
                    s = "^E" + s.substring(x + 1).trim() + "^?";
                    strings[v] = s;
                } else if ((s.toUpperCase().startsWith("GET "))) {
                    triggers[v] = CMMsg.TYP_GET;
                    strings[v] = s.substring(4).trim();
                } else if ((s.toUpperCase().startsWith("GET_ROOM "))) {
                    triggers[v] = CMMsg.TYP_GET | ROOM_MASK;
                    strings[v] = s.substring(9).trim();
                } else if ((s.toUpperCase().startsWith("EAT_ROOM "))) {
                    triggers[v] = CMMsg.TYP_EAT | ROOM_MASK;
                    strings[v] = s.substring(9).trim();
                } else if ((s.toUpperCase().startsWith("EAT "))) {
                    triggers[v] = CMMsg.TYP_EAT;
                    strings[v] = s.substring(4).trim();
                } else if ((s.toUpperCase().startsWith("PUSH_ROOM "))) {
                    triggers[v] = CMMsg.TYP_PUSH | ROOM_MASK;
                    strings[v] = s.substring(10).trim();
                } else if ((s.toUpperCase().startsWith("PUSH "))) {
                    triggers[v] = CMMsg.TYP_PUSH;
                    strings[v] = s.substring(5).trim();
                } else if ((s.toUpperCase().startsWith("PULL_ROOM "))) {
                    triggers[v] = CMMsg.TYP_PULL | ROOM_MASK;
                    strings[v] = s.substring(10).trim();
                } else if ((s.toUpperCase().startsWith("PULL "))) {
                    triggers[v] = CMMsg.TYP_PULL;
                    strings[v] = s.substring(5).trim();
                } else if ((s.toUpperCase().startsWith("SIT "))) {
                    triggers[v] = CMMsg.TYP_SIT;
                    strings[v] = s.substring(4).trim();
                } else if ((s.toUpperCase().startsWith("SIT_ROOM "))) {
                    triggers[v] = CMMsg.TYP_SIT | ROOM_MASK;
                    strings[v] = s.substring(9).trim();
                } else if ((s.toUpperCase().startsWith("DROP "))) {
                    triggers[v] = CMMsg.TYP_DROP;
                    strings[v] = s.substring(5).trim();
                } else if ((s.toUpperCase().startsWith("DROP_ROOM "))) {
                    triggers[v] = CMMsg.TYP_DROP | ROOM_MASK;
                    strings[v] = s.substring(10).trim();
                } else if ((s.toUpperCase().startsWith("WEAR "))) {
                    triggers[v] = CMMsg.TYP_WEAR;
                    strings[v] = s.substring(5).trim();
                } else if ((s.toUpperCase().startsWith("WEAR_ROOM "))) {
                    triggers[v] = CMMsg.TYP_WEAR | ROOM_MASK;
                    strings[v] = s.substring(10).trim();
                } else if ((s.toUpperCase().startsWith("OPEN "))) {
                    triggers[v] = CMMsg.TYP_OPEN;
                    strings[v] = s.substring(5).trim();
                } else if ((s.toUpperCase().startsWith("OPEN_ROOM "))) {
                    triggers[v] = CMMsg.TYP_OPEN | ROOM_MASK;
                    strings[v] = s.substring(10).trim();
                } else if ((s.toUpperCase().startsWith("CLOSE "))) {
                    triggers[v] = CMMsg.TYP_CLOSE;
                    strings[v] = s.substring(6).trim();
                } else if ((s.toUpperCase().startsWith("CLOSE_ROOM "))) {
                    triggers[v] = CMMsg.TYP_CLOSE | ROOM_MASK;
                    strings[v] = s.substring(11).trim();
                } else if ((s.toUpperCase().startsWith("HOLD "))) {
                    triggers[v] = CMMsg.TYP_HOLD;
                    strings[v] = s.substring(5).trim();
                } else if ((s.toUpperCase().startsWith("HOLD_ROOM "))) {
                    triggers[v] = CMMsg.TYP_HOLD | ROOM_MASK;
                    strings[v] = s.substring(10).trim();
                } else if ((s.toUpperCase().startsWith("WIELD "))) {
                    triggers[v] = CMMsg.TYP_WIELD;
                    strings[v] = s.substring(6).trim();
                } else if ((s.toUpperCase().startsWith("WIELD_ROOM "))) {
                    triggers[v] = CMMsg.TYP_WIELD | ROOM_MASK;
                    strings[v] = s.substring(11).trim();
                } else if ((s.toUpperCase().startsWith("DRINK "))) {
                    triggers[v] = CMMsg.TYP_DRINK;
                    strings[v] = s.substring(6).trim();
                } else if ((s.toUpperCase().startsWith("DRINK_ROOM "))) {
                    triggers[v] = CMMsg.TYP_DRINK | ROOM_MASK;
                    strings[v] = s.substring(11).trim();
                } else if ((s.toUpperCase().startsWith("MOUNT "))) {
                    triggers[v] = CMMsg.TYP_MOUNT;
                    strings[v] = s.substring(6).trim();
                } else if ((s.toUpperCase().startsWith("MOUNT_ROOM "))) {
                    triggers[v] = CMMsg.TYP_MOUNT | ROOM_MASK;
                    strings[v] = s.substring(11).trim();
                } else if ((s.toUpperCase().startsWith("REMOVE "))) {
                    triggers[v] = CMMsg.TYP_REMOVE;
                    strings[v] = s.substring(7).trim();
                } else if ((s.toUpperCase().startsWith("REMOVE_ROOM "))) {
                    triggers[v] = CMMsg.TYP_REMOVE | ROOM_MASK;
                    strings[v] = s.substring(12).trim();
                } else if ((s.toUpperCase().startsWith("PORTAL_ENTER "))) {
                    triggers[v] = CMMsg.TYP_ENTER;
                    strings[v] = s.substring(13).trim();
                } else if ((s.toUpperCase().startsWith("PORTAL_ENTER_ROOM "))) {
                    triggers[v] = CMMsg.TYP_ENTER | ROOM_MASK;
                    strings[v] = s.substring(18).trim();
                } else if ((s.toUpperCase().startsWith("PORTAL_EXIT "))) {
                    triggers[v] = CMMsg.TYP_LEAVE;
                    strings[v] = s.substring(12).trim();
                } else if ((s.toUpperCase().startsWith("PORTAL_EXIT_ROOM "))) {
                    triggers[v] = CMMsg.TYP_LEAVE | ROOM_MASK;
                    strings[v] = s.substring(17).trim();
                } else if ((s.toUpperCase().startsWith("DAMAGE "))) {
                    triggers[v] = CMMsg.TYP_DAMAGE;
                    strings[v] = s.substring(7).trim();
                } else if ((s.toUpperCase().startsWith("DAMAGE_ROOM "))) {
                    triggers[v] = CMMsg.TYP_DAMAGE | ROOM_MASK;
                    strings[v] = s.substring(12).trim();
                } else if ((s.toUpperCase().startsWith("FIGHT "))) {
                    triggers[v] = CMMsg.TYP_WEAPONATTACK;
                    strings[v] = s.substring(6).trim();
                } else if ((s.toUpperCase().startsWith("FIGHT_ROOM "))) {
                    triggers[v] = CMMsg.TYP_WEAPONATTACK | ROOM_MASK;
                    strings[v] = s.substring(11).trim();
                }
            }
        }
        tickReset();
    }

    protected void emoteHere(Room room, MOB emoter, String emote) {
        if (room == null)
            return;
        final Room oldLoc = emoter.location();
        if (emoter.location() != room)
            emoter.setLocation(room);
        final CMMsg msg = CMClass.getMsg(emoter, null, CMMsg.MSG_EMOTE, emote);
        if (room.okMessage(emoter, msg)) {
            for (int i = 0; i < room.numInhabitants(); i++) {
                final MOB M = room.fetchInhabitant(i);
                if ((M != null)
                    && (!M.isMonster())
                    && (CMLib.flags().canSenseMoving(emoter, M)))
                    M.executeMsg(M, msg);
            }
        }
        if (oldLoc != null)
            emoter.setLocation(oldLoc);
    }

    public void doEmote(Tickable ticking, String emote) {
        MOB emoter = null;
        emote = CMStrings.replaceAll(emote, "$p", ticking.name());
        emote = CMStrings.replaceAll(emote, "$P", ticking.name());
        if (ticking instanceof Area) {
            emoter = CMClass.getMOB("StdMOB");
            emoter.setName(ticking.name());
            emoter.charStats().setStat(CharStats.STAT_GENDER, 'N');
            for (final Enumeration r = ((Area) ticking).getMetroMap(); r.hasMoreElements(); ) {
                final Room R = (Room) r.nextElement();
                emoteHere(R, emoter, emote);
            }
            emoter.destroy();
        } else if (ticking instanceof Room) {
            emoter = CMClass.getMOB("StdMOB");
            emoter.setName(ticking.name());
            emoter.charStats().setStat(CharStats.STAT_GENDER, 'N');
            emoteHere((Room) ticking, emoter, emote);
            emoter.destroy();
        } else if (ticking instanceof MOB) {
            emoter = (MOB) ticking;
            if (!canFreelyBehaveNormal(ticking))
                return;
            emoteHere(((MOB) ticking).location(), emoter, emote);
        } else {
            if ((ticking instanceof Item) && (!CMLib.flags().isInTheGame((Item) ticking, false)))
                return;
            final Room R = getBehaversRoom(ticking);
            if (R != null) {
                emoter = CMClass.getMOB("StdMOB");
                emoter.setName(ticking.name());
                emoter.charStats().setStat(CharStats.STAT_GENDER, 'N');
                emoteHere(R, emoter, emote);
                emoter.destroy();
            }
        }
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if (((--tickDown) <= 0)
            && (!CMSecurity.isDisabled(CMSecurity.DisFlag.EMOTERS))
            && ((!(ticking instanceof MOB)) || (canFreelyBehaveNormal(ticking)))) {
            tickReset();
            for (int v = 0; v < triggers.length; v++)
                if ((CMath.bset(triggers[v], TICK_MASK))
                    && (CMLib.dice().rollPercentage() < (triggers[v] & UNDER_MASK))) {
                    doEmote(ticking, strings[v]);
                    break;
                }
        }
        oncePerRound1 = false;
        return true;
    }

    @Override
    public void executeMsg(Environmental E, CMMsg msg) {
        // this will work because, for items, behaviors
        // get the first tick.
        int lookFor = -1;
        if ((msg != lastMsg) && (!CMSecurity.isDisabled(CMSecurity.DisFlag.EMOTERS)))
            switch (msg.targetMinor()) {
                case CMMsg.TYP_OPEN:
                case CMMsg.TYP_CLOSE:
                    if ((msg.target() == E)
                        || ((!(E instanceof Item)) && (!(E instanceof Exit))))
                        lookFor = msg.targetMinor();
                    break;
                case CMMsg.TYP_GET:
                case CMMsg.TYP_PUSH:
                case CMMsg.TYP_PULL:
                case CMMsg.TYP_REMOVE:
                case CMMsg.TYP_WEAR:
                case CMMsg.TYP_HOLD:
                case CMMsg.TYP_WIELD:
                case CMMsg.TYP_EAT:
                case CMMsg.TYP_DRINK:
                case CMMsg.TYP_SIT:
                case CMMsg.TYP_SLEEP:
                case CMMsg.TYP_MOUNT:
                    if ((msg.target() == E) || (!(E instanceof Item)))
                        lookFor = msg.targetMinor();
                    break;
                case CMMsg.TYP_DROP:
                    if (((!(E instanceof Item)) || (msg.target() == E))
                        && (msg.target() instanceof Item))
                        lookFor = CMMsg.TYP_DROP;
                    break;
                case CMMsg.TYP_ENTER:
                    if ((msg.target() != null)
                        && (msg.target() == getBehaversRoom(E)))
                        lookFor = CMMsg.TYP_ENTER;
                    break;
                case CMMsg.TYP_LEAVE:
                    if ((msg.target() != null)
                        && (msg.target() == getBehaversRoom(E)))
                        lookFor = CMMsg.TYP_LEAVE;
                    break;
                case CMMsg.TYP_WEAPONATTACK:
                    if ((msg.target() != null)
                        && (msg.target() != E)
                        && ((msg.source() == E) || (msg.tool() == E) || (E instanceof Room) || (E instanceof Exit))
                        && (!oncePerRound1))
                        lookFor = CMMsg.TYP_WEAPONATTACK;
                    break;
                case CMMsg.TYP_DAMAGE:
                    if ((msg.target() != null)
                        && (msg.source() != E)
                        && ((msg.target() == E) || (msg.tool() == E) || (E instanceof Room) || (E instanceof Exit)))
                        lookFor = CMMsg.TYP_DAMAGE;
                    break;
            }
        lastMsg = msg;
        final Room room = msg.source().location();
        if ((lookFor >= 0)
            && (room != null)
            && ((!(E instanceof MOB)) || (lookFor == CMMsg.TYP_WEAPONATTACK)
            || (lookFor == CMMsg.TYP_DAMAGE)
            || (canFreelyBehaveNormal(E))))
            for (int v = 0; v < triggers.length; v++)
                if (((triggers[v] & UNDER_MASK) == lookFor)
                    && (!CMath.bset(triggers[v], TICK_MASK))) {
                    if (CMath.bset(triggers[v], ROOM_MASK)) {
                        final CMMsg msg2 = CMClass.getMsg(msg.source(), null, null, CMMsg.NO_EFFECT, CMMsg.NO_EFFECT, CMMsg.MSG_EMOTE, CMStrings.replaceAll(strings[v], "$p", E.name()));
                        msg.addTrailerMsg(msg2);
                    } else {
                        final CMMsg msg2 = CMClass.getMsg(msg.source(), null, null, CMMsg.MSG_EMOTE, CMMsg.NO_EFFECT, CMMsg.NO_EFFECT, CMStrings.replaceAll(strings[v], "$p", E.name()));
                        msg.addTrailerMsg(msg2);
                    }
                }
        super.executeMsg(E, msg);
    }
}
