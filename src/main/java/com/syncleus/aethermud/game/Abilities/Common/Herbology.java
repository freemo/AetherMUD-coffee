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
package com.planet_ink.game.Abilities.Common;

import com.planet_ink.game.Abilities.interfaces.Ability;
import com.planet_ink.game.Common.interfaces.CMMsg;
import com.planet_ink.game.Items.interfaces.Item;
import com.planet_ink.game.Items.interfaces.RawMaterial;
import com.planet_ink.game.Items.interfaces.Wearable;
import com.planet_ink.game.MOBS.interfaces.MOB;
import com.planet_ink.game.core.CMClass;
import com.planet_ink.game.core.CMLib;
import com.planet_ink.game.core.CMParms;
import com.planet_ink.game.core.Resources;
import com.planet_ink.game.core.interfaces.Physical;

import java.util.List;


public class Herbology extends CommonSkill {
    private final static String localizedName = CMLib.lang().L("Herbology");
    private static final String[] triggerStrings = I(new String[]{"HERBOLOGY"});
    protected Item found = null;
    protected boolean messedUp = false;

    public Herbology() {
        super();
        displayText = L("You are evaluating...");
        verb = L("evaluating");
    }

    @Override
    public String ID() {
        return "Herbology";
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
        return Ability.ACODE_COMMON_SKILL | Ability.DOMAIN_NATURELORE;
    }

    public String parametersFormat() {
        return "HERB_NAME";
    }

    @Override
    protected boolean canBeDoneSittingDown() {
        return true;
    }

    @Override
    public void unInvoke() {
        if (canBeUninvoked()) {
            if ((affected != null) && (affected instanceof MOB) && (!aborted) && (!helping)) {
                final MOB mob = (MOB) affected;
                if (messedUp)
                    commonTell(mob, L("You lose your concentration on @x1.", found.name()));
                else {
                    final List<String> herbList = Resources.getFileLineVector(Resources.getFileResource("skills/herbology.txt", true));
                    String herb = null;
                    while ((herbList.size() > 2) && ((herb == null) || (herb.trim().length() == 0)))
                        herb = herbList.get(CMLib.dice().roll(1, herbList.size(), -1)).trim().toLowerCase();

                    if (found.rawSecretIdentity().length() > 0) {
                        herb = found.rawSecretIdentity();
                        found.setSecretIdentity("");
                    }

                    commonTell(mob, L("@x1 appears to be @x2.", found.name(), herb));
                    String name = found.Name();
                    name = name.substring(0, name.length() - 5).trim();
                    if (name.length() > 0)
                        found.setName(name + " " + herb);
                    else
                        found.setName(L("some @x1", herb));
                    found.setDisplayText(L("@x1 is here", found.Name()));
                    found.setDescription("");
                    found.text();
                }
            }
        }
        super.unInvoke();
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (super.checkStop(mob, commands))
            return true;
        if (commands.size() < 1) {
            commonTell(mob, L("You must specify what herb you want to identify."));
            return false;
        }
        final Item target = mob.fetchItem(null, Wearable.FILTER_UNWORNONLY, CMParms.combine(commands, 0));
        if ((target == null) || (!CMLib.flags().canBeSeenBy(target, mob))) {
            commonTell(mob, L("You don't seem to have a '@x1'.", (commands.get(0))));
            return false;
        }
        commands.remove(commands.get(0));

        if ((target.material() != RawMaterial.RESOURCE_HERBS)
            || ((!target.Name().toUpperCase().endsWith(" HERBS"))
            && (!target.Name().equalsIgnoreCase("herbs")))
            || (!(target instanceof RawMaterial))
            || (!target.isGeneric())) {
            commonTell(mob, L("You can only identify unknown herbs."));
            return false;
        }
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        verb = L("studying @x1", target.name());
        displayText = L("You are @x1", verb);
        found = target;
        messedUp = false;
        if (!proficiencyCheck(mob, 0, auto))
            messedUp = true;
        final int duration = getDuration(15, mob, 1, 2);
        final CMMsg msg = CMClass.getMsg(mob, null, this, getActivityMessageType(), L("<S-NAME> stud(ys) @x1.", target.name()));
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            beneficialAffect(mob, mob, asLevel, duration);
        }
        return true;
    }
}
