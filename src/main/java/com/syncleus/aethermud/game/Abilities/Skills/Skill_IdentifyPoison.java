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
package com.syncleus.aethermud.game.Abilities.Skills;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Drink;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;


public class Skill_IdentifyPoison extends StdSkill {
    private final static String localizedName = CMLib.lang().L("Identify Poison");
    private static final String[] triggerStrings = I(new String[]{"IDPOISON", "IDENTIFYPOISON"});

    @Override
    public String ID() {
        return "Skill_IdentifyPoison";
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
        return Ability.CAN_ITEMS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public String[] triggerStrings() {
        return triggerStrings;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_POISONING;
    }

    public List<Ability> returnOffensiveAffects(Physical fromMe) {
        final Vector<Ability> offenders = new Vector<Ability>();

        for (final Enumeration<Ability> a = fromMe.effects(); a.hasMoreElements(); ) {
            final Ability A = a.nextElement();
            if ((A != null) && ((A.classificationCode() & Ability.ALL_ACODES) == Ability.ACODE_POISON))
                offenders.addElement(A);
        }
        return offenders;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Item target = getTarget(mob, mob.location(), givenTarget, commands, Wearable.FILTER_UNWORNONLY);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        final List<Ability> offensiveAffects = returnOffensiveAffects(target);

        if ((success) && ((offensiveAffects.size() > 0)
            || ((target instanceof Drink) && (((Drink) target).liquidType() == RawMaterial.RESOURCE_POISON)))) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, CMMsg.MSG_DELICATE_SMALL_HANDS_ACT | (auto ? CMMsg.MASK_ALWAYS : 0), auto ? "" : L("^S<S-NAME> carefully sniff(s) and taste(s) <T-NAME>.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                final StringBuffer buf = new StringBuffer(L("@x1 contains: ", target.name(mob)));
                if (offensiveAffects.size() == 0)
                    buf.append(L("weak impurities, "));
                else
                    for (int i = 0; i < offensiveAffects.size(); i++)
                        buf.append(offensiveAffects.get(i).name() + ", ");
                mob.tell(buf.toString().substring(0, buf.length() - 2));
            }
        } else
            beneficialWordsFizzle(mob, target, auto ? "" : L("<S-NAME> sniff(s) and taste(s) <T-NAME>, but receives no insight."));

        // return whether it worked
        return success;
    }
}
