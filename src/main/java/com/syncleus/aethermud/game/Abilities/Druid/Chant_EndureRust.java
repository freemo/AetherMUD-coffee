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
package com.syncleus.aethermud.game.Abilities.Druid;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Chant_EndureRust extends Chant {
    private final static String localizedName = CMLib.lang().L("Endure Rust");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Endure Rust)");
    protected Set<Environmental> dontbother = new HashSet<Environmental>();

    @Override
    public String ID() {
        return "Chant_EndureRust";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return localizedStaticDisplay;
    }

    @Override
    protected int canAffectCode() {
        return CAN_MOBS | CAN_ITEMS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_MOBS | CAN_ITEMS;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_BENEFICIAL_OTHERS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_PRESERVING;
    }

    @Override
    public void unInvoke() {
        if ((affected instanceof MOB) && (canBeUninvoked()))
            ((MOB) affected).tell(L("Your rust endurance fades."));
        super.unInvoke();
    }

    @Override
    public boolean okMessage(Environmental host, CMMsg msg) {
        if ((((msg.target() == affected) && (affected instanceof Item))
            || (msg.target() instanceof Item) && (affected instanceof MOB) && (((MOB) affected).isMine(msg.target())))
            && (msg.targetMinor() == CMMsg.TYP_WATER)) {
            if (!dontbother.contains(msg.target())) // this just makes it less spammy
            {
                final Room R = CMLib.map().roomLocation(affected);
                dontbother.add(msg.target());
                if (R != null)
                    R.show(msg.source(), affected, CMMsg.MSG_OK_VISUAL, L("<T-NAME> resist(s) the oxidizing affects."));
            }
            return false;
        }
        return super.okMessage(host, msg);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Physical target = this.getAnyTarget(mob, commands, givenTarget, Wearable.FILTER_ANY);
        if (target == null)
            return false;
        if (target instanceof Item) {
        } else if (target instanceof MOB) {
        } else {
            mob.tell(L("This chant won't affect @x1.", target.name(mob)));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);

        if (!success) {
            return beneficialWordsFizzle(mob, target, L("<S-NAME> chant(s) to <T-NAMESELF>, but fail(s)."));
        }
        final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> chant(s) to <T-NAMESELF>, causing a rust proof film to envelope <T-HIM-HER>!^?"));
        if (mob.location().okMessage(mob, msg)) {
            dontbother.clear();
            mob.location().send(mob, msg);
            beneficialAffect(mob, target, asLevel, 0);
        }
        return success;
    }
}
