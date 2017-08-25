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
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class Engraving extends CommonSkill {
    private final static String localizedName = CMLib.lang().L("Engraving");
    private static final String[] triggerStrings = I(new String[]{"ENGRAVE", "ENGRAVING"});
    protected Item found = null;
    protected String writing = "";

    public Engraving() {
        super();
        displayText = L("You are engraving...");
        verb = L("engraving");
    }

    @Override
    public String ID() {
        return "Engraving";
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
        return Ability.ACODE_COMMON_SKILL | Ability.DOMAIN_CALLIGRAPHY;
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
                if (writing.length() == 0)
                    commonTell(mob, L("You mess up your engraving."));
                else {
                    String desc = found.description();
                    final int x = desc.indexOf(" Engraved on it are the words `");
                    final int y = desc.lastIndexOf('`');
                    if ((x >= 0) && (y > x))
                        desc = desc.substring(0, x);
                    found.setDescription(L("@x1 Engraved on it are the words `@x2`.", desc, writing));
                }
            }
        }
        super.unInvoke();
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (super.checkStop(mob, commands))
            return true;
        if (commands.size() < 2) {
            commonTell(mob, L("You must specify what you want to engrave onto, and what words to engrave on it."));
            return false;
        }
        Item target = mob.fetchItem(null, Wearable.FILTER_UNWORNONLY, commands.get(0));
        if ((target == null) || (!CMLib.flags().canBeSeenBy(target, mob))) {
            target = mob.location().findItem(null, commands.get(0));
            if ((target != null) && (CMLib.flags().canBeSeenBy(target, mob))) {
                final Set<MOB> followers = mob.getGroupMembers(new TreeSet<MOB>());
                boolean ok = false;
                for (final MOB M : followers) {
                    if (target.secretIdentity().indexOf(getBrand(M)) >= 0)
                        ok = true;
                }
                if (!ok) {
                    commonTell(mob, L("You aren't allowed to work on '@x1'.", (commands.get(0))));
                    return false;
                }
            }
        }
        if ((target == null) || (!CMLib.flags().canBeSeenBy(target, mob))) {
            commonTell(mob, L("You don't seem to have a '@x1'.", (commands.get(0))));
            return false;
        }
        commands.remove(commands.get(0));

        final Ability write = mob.fetchAbility("Skill_Write");
        if (write == null) {
            commonTell(mob, L("You must know how to write to engrave."));
            return false;
        }

        if ((((target.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_GLASS)
            && ((target.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_METAL)
            && ((target.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_ROCK)
            && ((target.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_PRECIOUS)
            && ((target.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_SYNTHETIC)
            && ((target.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_WOODEN)
            && ((target.material() & RawMaterial.MATERIAL_MASK) != RawMaterial.MATERIAL_MITHRIL))
            || (!target.isGeneric())) {
            commonTell(mob, L("You can't engrave onto that material."));
            return false;
        }
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;
        writing = CMParms.combine(commands, 0);
        verb = L("engraving on @x1", target.name());
        displayText = L("You are @x1", verb);
        found = target;
        if ((!proficiencyCheck(mob, 0, auto)) || (!write.proficiencyCheck(mob, 0, auto)))
            writing = "";
        final int duration = getDuration(30, mob, 1, 3);
        final CMMsg msg = CMClass.getMsg(mob, target, this, getActivityMessageType(), L("<S-NAME> start(s) engraving on <T-NAME>."));
        if (mob.location().okMessage(mob, msg)) {
            mob.location().send(mob, msg);
            beneficialAffect(mob, mob, asLevel, duration);
        }
        return true;
    }
}
