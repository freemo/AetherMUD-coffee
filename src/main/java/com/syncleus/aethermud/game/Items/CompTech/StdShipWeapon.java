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
package com.planet_ink.game.Items.CompTech;

import com.planet_ink.game.Areas.interfaces.Area;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.*;
import com.planet_ink.game.Libraries.interfaces.LanguageLibrary;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.collections.XVector;
import com.planet_ink.game.core.interfaces.Environmental;
import com.planet_ink.game.core.interfaces.ItemPossessor;
import com.planet_ink.game.core.interfaces.SpaceObject;
import com.planet_ink.game.core.interfaces.Tickable;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class StdShipWeapon extends StdElecCompItem implements ShipWarComponent {
    private final double[] targetDirection = new double[]{0.0, 0.0};
    private ShipDir[] allPossDirs = new ShipDir[]{ShipDir.FORWARD};
    private int numPermitDirs = 1;
    private int[] damageMsgTypes = new int[]{CMMsg.TYP_ELECTRIC};
    private volatile long powerSetting = Integer.MAX_VALUE;
    private volatile ShipDir[] currCoverage = null;
    private volatile Reference<SpaceShip> myShip = null;

    protected static void sendComputerMessage(final ShipWarComponent me, final String circuitKey, final MOB mob, final Item controlI, final String code) {
        for (final Iterator<Computer> c = CMLib.tech().getComputers(circuitKey); c.hasNext(); ) {
            final Computer C = c.next();
            if ((controlI == null) || (C != controlI.owner())) {
                final CMMsg msg2 = CMClass.getMsg(mob, C, me, CMMsg.NO_EFFECT, null, CMMsg.MSG_ACTIVATE | CMMsg.MASK_CNTRLMSG, code, CMMsg.NO_EFFECT, null);
                if (C.okMessage(mob, msg2))
                    C.executeMsg(mob, msg2);
            }
        }
    }

    @Override
    public String ID() {
        return "StdShipWeapon";
    }

    @Override
    public TechType getTechType() {
        return Technical.TechType.SHIP_WEAPON;
    }

    @Override
    public void setOwner(ItemPossessor container) {
        super.setOwner(container);
        myShip = null;
    }

    @Override
    public int powerNeeds() {
        return (int) Math.min((int) Math.min(powerCapacity, powerSetting) - power, (int) Math.round((double) powerCapacity * getRechargeRate()));
    }

    protected synchronized SpaceShip getMyShip() {
        if (myShip == null) {
            final Area area = CMLib.map().areaLocation(this);
            if (area instanceof SpaceShip)
                myShip = new WeakReference<SpaceShip>((SpaceShip) area);
            else
                myShip = new WeakReference<SpaceShip>(null);
        }
        return myShip.get();
    }

    @Override
    public ShipDir[] getPermittedDirections() {
        return allPossDirs;
    }

    @Override
    public void setPermittedDirections(ShipDir[] newPossDirs) {
        this.allPossDirs = newPossDirs;
    }

    @Override
    public int getPermittedNumDirections() {
        return numPermitDirs;
    }

    @Override
    public void setPermittedNumDirections(int numDirs) {
        this.numPermitDirs = numDirs;
    }

    @Override
    public int[] getDamageMsgTypes() {
        return damageMsgTypes;
    }

    @Override
    public void setDamageMsgTypes(int[] newTypes) {
        this.damageMsgTypes = newTypes;
    }

    protected ShipDir[] getCurrentCoveredDirections() {
        if (this.currCoverage == null) {
            final ShipDir[] permitted = getPermittedDirections();
            int numDirs = getPermittedNumDirections();
            if (numDirs >= permitted.length)
                currCoverage = getPermittedDirections();
            else {
                int centralIndex = CMLib.dice().roll(1, numDirs, -1);
                List<ShipDir> theDirs = new ArrayList<ShipDir>(numDirs);
                int offset = 0;
                final List<ShipDir> permittedDirs = new XVector<ShipDir>(permitted);
                permittedDirs.addAll(Arrays.asList(permitted));
                permittedDirs.addAll(Arrays.asList(permitted));
                while (theDirs.size() < numDirs) {
                    if (!theDirs.contains(permittedDirs.get(centralIndex + offset)))
                        theDirs.add(permittedDirs.get(centralIndex + offset));
                    if (!theDirs.contains(permittedDirs.get(centralIndex - offset)))
                        theDirs.add(permittedDirs.get(centralIndex - offset));
                    offset += 1;
                }
                currCoverage = theDirs.toArray(new ShipDir[theDirs.size()]);
            }
        }
        return currCoverage;
    }

    @Override
    public boolean okMessage(Environmental host, CMMsg msg) {
        if (!super.okMessage(host, msg))
            return false;
        return true;
    }

    @Override
    public boolean tick(final Tickable ticking, final int tickID) {
        if (!super.tick(ticking, tickID))
            return false;
        if ((ticking == this) && (tickID == Tickable.TICKID_BEAMWEAPON)) {
            this.destroy();
            return false;
        }
        return true;
    }

    @Override
    public void executeMsg(Environmental myHost, CMMsg msg) {
        super.executeMsg(myHost, msg);
        if (msg.amITarget(this)) {
            switch (msg.targetMinor()) {
                case CMMsg.TYP_ACTIVATE: {
                    final LanguageLibrary lang = CMLib.lang();
                    final Software controlI = (msg.tool() instanceof Software) ? ((Software) msg.tool()) : null;
                    final MOB mob = msg.source();
                    if (msg.targetMessage() == null) {
                        powerSetting = powerCapacity();
                    } else {
                        final String[] parts = msg.targetMessage().split(" ");
                        final TechCommand command = TechCommand.findCommand(parts);
                        if (command == null)
                            reportError(this, controlI, mob, lang.L("@x1 does not respond.", me.name(mob)), lang.L("Failure: @x1: control failure.", me.name(mob)));
                        else {
                            final Object[] parms = command.confirmAndTranslate(parts);
                            if (parms == null)
                                reportError(this, controlI, mob, lang.L("@x1 did not respond.", me.name(mob)), lang.L("Failure: @x1: control syntax failure.", me.name(mob)));
                            else if (command == TechCommand.POWERSET) {
                                powerSetting = ((Long) parms[0]).intValue();
                                if (powerSetting < 0)
                                    powerSetting = 0;
                                else if (powerSetting > powerCapacity())
                                    powerSetting = powerCapacity();
                            } else if (command == TechCommand.WEAPONTARGETSET) {
                                final SpaceObject ship = CMLib.map().getSpaceObject(this, true);
                                if (ship == null)
                                    reportError(this, controlI, mob, lang.L("@x1 did not respond.", me.name(mob)), lang.L("Failure: @x1: control syntax failure.", me.name(mob)));
                                else {
                                    targetDirection[0] = ((Double) parms[0]).doubleValue();
                                    targetDirection[1] = ((Double) parms[0]).doubleValue();
                                }
                            } else if (command == TechCommand.WEAPONFIRE) {
                                final SpaceObject ship = CMLib.map().getSpaceObject(this, true);
                                if (ship == null)
                                    reportError(this, controlI, mob, lang.L("@x1 did not respond.", me.name(mob)), lang.L("Failure: @x1: control syntax failure.", me.name(mob)));
                                else if (this.power < this.powerSetting)
                                    reportError(this, controlI, mob, lang.L("@x1 is not charged up.", me.name(mob)), lang.L("Failure: @x1: weapon is not charged.", me.name(mob)));
                                else {
                                    if (ship instanceof SpaceShip) {
                                        final ShipDir dir = CMLib.map().getDirectionFromDir(((SpaceShip) ship).facing(), ((SpaceShip) ship).roll(), targetDirection);
                                        if (CMParms.contains(getCurrentCoveredDirections(), dir))
                                            reportError(this, controlI, mob, lang.L("@x1 is not facing a covered direction.", me.name(mob)), lang.L("Failure: @x1: weapon is not facing correctly.", me.name(mob)));
                                    }
                                    SpaceObject weaponO = (SpaceObject) CMClass.getTech("StdSpaceTech");
                                    int damageMsgType = CMMsg.TYP_ELECTRIC;
                                    if (getDamageMsgTypes().length > 0)
                                        damageMsgType = getDamageMsgTypes()[CMLib.dice().roll(1, getDamageMsgTypes().length, -1)];
                                    switch (damageMsgType) {
                                        case CMMsg.TYP_COLLISION:
                                            weaponO.setName(L("a metal slug"));
                                            ((Technical) weaponO).setMaterial(RawMaterial.RESOURCE_STEEL);
                                            break;
                                        case CMMsg.TYP_ELECTRIC:
                                            weaponO.setName(L("an energy beam"));
                                            ((Technical) weaponO).setMaterial(RawMaterial.RESOURCE_ENERGY);
                                            break;
                                        case CMMsg.TYP_ACID:
                                            weaponO.setName(L("a disruptor beam"));
                                            ((Technical) weaponO).setMaterial(RawMaterial.RESOURCE_CHLORINE);
                                            break;
                                        case CMMsg.TYP_COLD:
                                            weaponO.setName(L("a distintegration beam"));
                                            ((Technical) weaponO).setMaterial(RawMaterial.RESOURCE_FRESHWATER);
                                            break;
                                        case CMMsg.TYP_FIRE:
                                            weaponO.setName(L("a photonic beam"));
                                            ((Technical) weaponO).setMaterial(RawMaterial.RESOURCE_ENERGY);
                                            break;
                                        case CMMsg.TYP_GAS:
                                            weaponO.setName(L("a particle beam"));
                                            ((Technical) weaponO).setMaterial(RawMaterial.RESOURCE_AIR);
                                            break;
                                        case CMMsg.TYP_LASER:
                                            weaponO.setName(L("a laser beam"));
                                            ((Technical) weaponO).setMaterial(RawMaterial.RESOURCE_ENERGY);
                                            break;
                                        case CMMsg.TYP_PARALYZE:
                                            weaponO.setName(L("a fusion beam"));
                                            ((Technical) weaponO).setMaterial(RawMaterial.RESOURCE_ENERGY);
                                            break;
                                        case CMMsg.TYP_POISON:
                                            weaponO.setName(L("a magnetic beam"));
                                            ((Technical) weaponO).setMaterial(RawMaterial.RESOURCE_ENERGY);
                                            break;
                                        case CMMsg.TYP_SONIC:
                                            weaponO.setName(L("a tight radio beam"));
                                            ((Technical) weaponO).setMaterial(RawMaterial.RESOURCE_ENERGY);
                                            break;
                                        case CMMsg.TYP_UNDEAD:
                                            weaponO.setName(L("an anti-matter beam"));
                                            ((Technical) weaponO).setMaterial(RawMaterial.RESOURCE_ANTIMATTER);
                                            break;
                                        case CMMsg.TYP_WATER:
                                            weaponO.setName(L("a graviton beam"));
                                            ((Technical) weaponO).setMaterial(RawMaterial.RESOURCE_ENERGY);
                                            break;
                                    }
                                    weaponO.setKnownSource(ship);
                                    long[] firstCoords = CMLib.map().moveSpaceObject(ship.coordinates(), targetDirection, ship.radius());
                                    weaponO.setCoords(firstCoords);
                                    weaponO.setRadius(10);
                                    weaponO.setDirection(targetDirection);
                                    weaponO.setSpeed(SpaceObject.VELOCITY_LIGHT);
                                    ((Technical) weaponO).setTechLevel(techLevel());
                                    ((Technical) weaponO).basePhyStats().setWeight(0);
                                    ((Technical) weaponO).phyStats().setWeight(0);
                                    ((Technical) weaponO).basePhyStats().setDamage(phyStats().damage());
                                    ((Technical) weaponO).phyStats().setDamage(phyStats().damage());
                                    CMLib.threads().startTickDown(weaponO, Tickable.TICKID_BEAMWEAPON, 10);
                                    CMLib.map().addObjectToSpace(weaponO, firstCoords);
                                }
                            } else
                                reportError(this, controlI, mob, lang.L("@x1 refused to respond.", me.name(mob)), lang.L("Failure: @x1: control command failure.", me.name(mob)));
                        }
                    }
                    break;
                }
                case CMMsg.TYP_POWERCURRENT:
                    break;
                case CMMsg.TYP_DEACTIVATE:
                    this.activate(false);
                    this.power = 0;
                    break;
            }
        }
    }

    @Override
    public boolean sameAs(Environmental E) {
        if (!(E instanceof StdShipWeapon))
            return false;
        return super.sameAs(E);
    }
}
