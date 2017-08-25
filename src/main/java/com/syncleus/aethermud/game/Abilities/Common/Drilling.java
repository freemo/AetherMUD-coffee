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
package com.syncleus.aethermud.game.Abilities.Common;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Container;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Drink;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Drilling extends GatheringSkill {
    private final static String localizedName = CMLib.lang().L("Drilling");
    private static final String[] triggerStrings = I(new String[]{"DRILL", "DRILLING"});
    protected Item found = null;
    protected String foundShortName = "";
    private Drink container = null;

    public Drilling() {
        super();
        displayText = L("You are drilling...");
        verb = L("drilling");
    }

    @Override
    public String ID() {
        return "Drilling";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_COMMON_SKILL | Ability.DOMAIN_GATHERINGSKILL;
    }

    @Override
    protected boolean allowedWhileMounted() {
        return false;
    }

    @Override
    public String supportedResourceString() {
        return "LIQUID";
    }

    protected int getDuration(MOB mob, int level) {
        return getDuration(35, mob, level, 10);
    }

    @Override
    protected int baseYield() {
        return 1;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((affected != null) && (affected instanceof MOB) && (tickID == Tickable.TICKID_MOB)) {
            final MOB mob = (MOB) affected;
            if (tickUp == 6) {
                if (found != null) {
                    commonTell(mob, L("You have found some @x1!", foundShortName));
                    displayText = L("You are drilling out some @x1", foundShortName);
                    verb = L("drilling out some @x1", foundShortName);
                    playSound = "drill.wav";
                } else {
                    final StringBuffer str = new StringBuffer(L("You can't seem to find anything worth drilling around here.\n\r"));
                    final int d = lookingFor(RawMaterial.MATERIAL_LIQUID, mob.location());
                    if (d < 0)
                        str.append(L("You might try elsewhere."));
                    else
                        str.append(L("You might try @x1.", CMLib.directions().getInDirectionName(d)));
                    commonTell(mob, str.toString());
                    unInvoke();
                }

            }
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void unInvoke() {
        if (canBeUninvoked()) {
            if (affected instanceof MOB) {
                final MOB mob = (MOB) affected;
                if ((found != null) && (!aborted) && (mob.location() != null)) {
                    int amount = ((found.material() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_CLOTH) ?
                        (CMLib.dice().roll(1, 10, 0) * (baseYield() + abilityCode())) :
                        (CMLib.dice().roll(1, 3, 0) * (baseYield() + abilityCode()));
                    if (amount > (container.liquidHeld() - container.liquidRemaining()))
                        amount = (container.liquidHeld() - container.liquidRemaining());
                    if (amount > ((Container) container).capacity())
                        amount = ((Container) container).capacity();
                    final CMMsg msg = CMClass.getMsg(mob, found, this, getCompletedActivityMessageType(), null);
                    msg.setValue(amount);
                    if (mob.location().okMessage(mob, msg)) {
                        String s = "s";
                        if (msg.value() == 1)
                            s = "";
                        msg.modify(L("<S-NAME> manage(s) to drill out @x1 pound@x2 of @x3.", "" + msg.value(), s, foundShortName));
                        mob.location().send(mob, msg);
                        for (int i = 0; i < msg.value(); i++) {
                            final Item newFound = (Item) found.copyOf();
                            final Room R = mob.location();
                            if (R == null)
                                break;
                            if (!dropAWinner(mob, newFound)) {
                                break;
                            }
                            if ((container != null)
                                && (container instanceof Container)) {
                                if (mob.isMine(container)) {
                                    CMLib.commands().postGet(mob, null, newFound, true);
                                    if (mob.isMine(newFound))
                                        newFound.setContainer((Container) container);
                                } else if (R.isContent((Item) container))
                                    newFound.setContainer((Container) container);
                            }
                        }
                    }
                }
            }
        }
        super.unInvoke();
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (super.checkStop(mob, commands))
            return true;
        bundling = false;
        if ((!auto)
            && (commands.size() > 0)
            && ((commands.get(0)).equalsIgnoreCase("bundle"))) {
            bundling = true;
            if (super.invoke(mob, commands, givenTarget, auto, asLevel))
                return super.bundle(mob, commands);
            return false;
        }

        final Item I = getTarget(mob, mob.location(), givenTarget, commands, Wearable.FILTER_ANY);
        if (I == null)
            return false;
        if ((!(I instanceof Container))
            || (((Container) I).capacity() <= ((Container) I).phyStats().weight())) {
            commonTell(mob, L("@x1 doesn't look like it can hold anything.", I.name(mob)));
            return false;
        }
        final int resourceType = mob.location().myResource();
        if ((!(I instanceof Drink)) || ((I.material() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_LIQUID)) {
            commonTell(mob, L("@x1 doesn't look like it can hold a liquid.", I.name(mob)));
            return false;
        }
        final List<Item> V = ((Container) I).getContents();
        if (((Drink) I).containsDrink()) {
            for (int v = 0; v < V.size(); v++) {
                final Item I2 = V.get(v);
                if ((I2.material() & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_LIQUID) {
                    if (I2.material() != resourceType) {
                        commonTell(mob, L("@x1 needs to have the @x2 removed first.", I.name(mob), I2.name(mob)));
                        return false;
                    }
                }
            }
            if (((Drink) I).liquidRemaining() > 0) {
                commonTell(mob, L("You need to empty @x1 first.", I.name(mob)));
                return false;
            }
        }

        verb = L("drilling");
        found = null;
        playSound = null;
        if (!confirmPossibleMaterialLocation(RawMaterial.MATERIAL_LIQUID, mob.location())) {
            commonTell(mob, L("You don't think this is a good place to drill."));
            return false;
        }
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        if ((proficiencyCheck(mob, 0, auto))
            && (((resourceType & RawMaterial.MATERIAL_MASK) == RawMaterial.MATERIAL_LIQUID))) {
            found = (Item) CMLib.materials().makeResource(resourceType, Integer.toString(mob.location().domainType()), false, null);
            foundShortName = "nothing";
            if (found != null)
                foundShortName = RawMaterial.CODES.NAME(found.material()).toLowerCase();
        }
        final int duration = getDuration(mob, 1);
        final CMMsg msg = CMClass.getMsg(mob, found, this, getActivityMessageType(), L("<S-NAME> start(s) drilling."));
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            container = (Drink) I;
            found = (Item) msg.target();
            beneficialAffect(mob, mob, asLevel, duration);
        }
        return true;
    }
}
