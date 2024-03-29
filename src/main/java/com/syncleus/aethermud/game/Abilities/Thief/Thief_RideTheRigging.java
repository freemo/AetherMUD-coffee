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
package com.syncleus.aethermud.game.Abilities.Thief;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Areas.interfaces.Area;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.BoardableShip;
import com.syncleus.aethermud.game.Items.interfaces.SailingShip;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.PhysicalAgent;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class Thief_RideTheRigging extends ThiefSkill {
    private final static String localizedName = CMLib.lang().L("Ride The Rigging");
    private static final String[] triggerStrings = I(new String[]{"RIDERIGGING", "RIDETHERIGGING"});

    @Override
    public String ID() {
        return "Thief_RideTheRigging";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_THIEF_SKILL | Ability.DOMAIN_SEATRAVEL;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int usageType() {
        return USAGE_MOVEMENT;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Room R = mob.location();
        if (R == null)
            return false;
        if ((!(R.getArea() instanceof BoardableShip))
            || (!(((BoardableShip) R.getArea()).getShipItem() instanceof SailingShip))
            || ((R.domainType() & Room.INDOORS) != 0)) {
            mob.tell(L("You must be on the deck of a ship to ride the rigging to another ship."));
            return false;
        }
        final BoardableShip myShip = (BoardableShip) R.getArea();
        final SailingShip myShipItem = (SailingShip) myShip.getShipItem();
        if ((myShipItem == null)
            || (!(myShipItem.owner() instanceof Room))
            || (!CMLib.flags().isWateryRoom((Room) myShipItem.owner()))) {
            mob.tell(L("Your ship must be at sea to ride the rigging to another ship."));
            return false;
        }
        final Room fightR = (Room) myShipItem.owner();
        final PhysicalAgent targetShipItem = myShipItem.getCombatant();
        if (!(targetShipItem instanceof SailingShip)) {
            mob.tell(L("Your ship must be targeting another ship to ride the rigging to it."));
            return false;
        }
        int distance = myShipItem.rangeToTarget();
        if ((distance < 0) && (!auto)) {
            mob.tell(L("Your ship needs to get closer to the target to ride the rigging."));
            return false;
        }
        if (!CMLib.flags().isStanding(mob) && (!auto)) {
            mob.tell(L("You need to stand up!"));
            return false;
        }
        Physical target = targetShipItem;

        Area targetArea = ((BoardableShip) targetShipItem).getShipArea();
        List<Room> choices = new ArrayList<Room>();
        for (Enumeration<Room> r = targetArea.getProperMap(); r.hasMoreElements(); ) {
            final Room R2 = r.nextElement();
            if ((R2 != null)
                && ((R2.domainType() & Room.INDOORS) == 0)
                && (R2.domainType() != Room.DOMAIN_OUTDOORS_AIR))
                choices.add(R2);
        }

        if (choices.size() == 0) {
            mob.tell(L("You don't see any deck on that ship to land on!"));
            return false;
        }

        Room targetRoom = choices.get(CMLib.dice().roll(1, choices.size(), -1));

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        boolean success = proficiencyCheck(mob, -((distance - 1) * 30), auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, auto ? CMMsg.MSG_OK_ACTION : CMMsg.MSG_THIEF_ACT, auto ? "" : L("<S-NAME> climb(s) onto the rigging and rides it over to <T-NAME>!"));
            if (fightR.okMessage(mob, msg)) {
                fightR.send(mob, msg);
                final CMMsg enterMsg = CMClass.getMsg(mob, targetRoom, this, CMMsg.MSG_ENTER, null, CMMsg.MSG_ENTER, null, CMMsg.MSG_ENTER, L("<S-NAME> swing(s) onto the ship from @x1!", myShipItem.name()));
                final CMMsg leaveMsg = CMClass.getMsg(mob, R, this, CMMsg.MSG_LEAVE, L("<S-NAME> swing(s) away on the rigging."));
                if (R.okMessage(mob, leaveMsg) && targetRoom.okMessage(mob, enterMsg)) {
                    if (mob.isInCombat()) {
                        //CMLib.commands().postFlee(mob,("NOWHERE"));
                        mob.makePeace(false);
                    }
                    R.send(mob, leaveMsg);
                    targetRoom.bringMobHere(mob, false);
                    targetRoom.send(mob, enterMsg);
                    mob.tell(L("\n\r\n\r"));
                    CMLib.commands().postLook(mob, true);
                }
            }
        } else {
            if (distance < 2)
                return beneficialVisualFizzle(mob, target, auto ? "" : L("<S-NAME> climb(s) on the rigging and swing(s) around helplessly."));
            else {
                targetRoom = fightR;
                final CMMsg msg = CMClass.getMsg(mob, target, this, auto ? CMMsg.MSG_OK_ACTION : CMMsg.MSG_NOISYMOVEMENT, auto ? "" : L("<S-NAME> climb(s) onto the rigging and swing(s) toward(s) <T-NAME>!"));
                if (fightR.okMessage(mob, msg)) {
                    fightR.send(mob, msg);
                    final CMMsg enterMsg = CMClass.getMsg(mob, targetRoom, this, CMMsg.MSG_ENTER, null, CMMsg.MSG_ENTER, null, CMMsg.MSG_ENTER, L("<S-NAME> fall(s) into the water with a splash!"));
                    final CMMsg leaveMsg = CMClass.getMsg(mob, R, this, CMMsg.MSG_LEAVE, L("<S-NAME> swing(s) away on the rigging, but fall(s) into the drink."));
                    if (R.okMessage(mob, leaveMsg) && targetRoom.okMessage(mob, enterMsg)) {
                        if (mob.isInCombat()) {
                            //CMLib.commands().postFlee(mob,("NOWHERE"));
                            mob.makePeace(false);
                        }
                        R.send(mob, leaveMsg);
                        targetRoom.bringMobHere(mob, false);
                        targetRoom.send(mob, enterMsg);
                        mob.tell(L("\n\r\n\r"));
                        CMLib.commands().postLook(mob, true);
                    }
                }
            }
        }
        return success;
    }

}
