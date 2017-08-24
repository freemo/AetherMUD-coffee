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
package com.planet_ink.coffee_mud.Abilities.Druid;

import com.planet_ink.coffee_mud.Abilities.interfaces.Ability;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.interfaces.Environmental;
import com.planet_ink.coffee_mud.core.interfaces.Physical;
import com.planet_ink.coffee_mud.core.interfaces.Tickable;

import java.util.List;


public class Chant_Rockthought extends Chant {
    private final static String localizedName = CMLib.lang().L("Rockthought");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Rockthought)");
    CMMsg stubb = null;

    @Override
    public String ID() {
        return "Chant_Rockthought";
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
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_ENDURING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    public void executeMsg(Environmental host, CMMsg msg) {
        if ((affected instanceof MOB)
            && (stubb == null)
            && (msg.amISource((MOB) affected))
            && (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS)
            && (msg.othersCode() != CMMsg.NO_EFFECT)
            && (msg.othersMessage() != null)
            && (msg.othersMessage().length() > 0)))
            stubb = msg;
        super.executeMsg(host, msg);
    }

    @Override
    public boolean okMessage(Environmental host, CMMsg msg) {
        if ((affected instanceof MOB)
            && (stubb != null)
            && (!CMath.bset(msg.sourceMajor(), CMMsg.MASK_ALWAYS)
            && (!stubb.equals(msg)))) {
            // this can cause all kinds of potential problems ..
            // the number of checks to get around them probably isn't worth the cost.
        }
        return super.okMessage(host, msg);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if ((affected instanceof MOB)
            && (stubb != null)
            && (((MOB) affected).location() != null)
            && (((MOB) affected).location().okMessage(affected, stubb)))
            ((MOB) affected).location().send((MOB) affected, stubb);
        return super.tick(ticking, tickID);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final MOB target = this.getTarget(mob, commands, givenTarget);
        if (target == null)
            return false;

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto) | CMMsg.MASK_MALICIOUS, auto ? "" : L("^S<S-NAME> chant(s) at <T-NAMESELF>!^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                if (msg.value() <= 0) {
                    stubb = null;
                    success = maliciousAffect(mob, target, asLevel, 20, CMMsg.MSK_CAST_VERBAL | CMMsg.TYP_MIND | (auto ? CMMsg.MASK_ALWAYS : 0)) != null;
                    if (success) {
                        if (target.isInCombat())
                            target.makePeace(true);
                        target.location().show(target, null, CMMsg.MSG_OK_VISUAL, L("<S-NAME> look(s) stubborn."));
                    }
                }
            }
        } else
            return maliciousFizzle(mob, target, L("<S-NAME> chant(s) at <T-NAMESELF>, but nothing happens."));

        // return whether it worked
        return success;
    }
}
