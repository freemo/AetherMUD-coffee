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
package com.syncleus.aethermud.game.Abilities.Prayers;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Abilities.interfaces.Trap;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Prayer_GuardianHearth extends Prayer {
    private final static String localizedName = CMLib.lang().L("Guardian Hearth");
    private final static String localizedStaticDisplay = CMLib.lang().L("(Guardian Hearth)");
    protected static HashSet<Integer> prots = null;

    @Override
    public String ID() {
        return "Prayer_GuardianHearth";
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
        return Ability.ACODE_PRAYER | Ability.DOMAIN_WARDING;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    protected int canAffectCode() {
        return CAN_ROOMS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_ROOMS;
    }

    @Override
    protected int overrideMana() {
        return Ability.COST_ALL;
    }

    @Override
    public long flags() {
        return Ability.FLAG_NEUTRAL;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((affected == null) || (!(affected instanceof Room)))
            return super.okMessage(myHost, msg);

        if (prots == null) {
            prots = new HashSet<Integer>();
            final int[] CMMSGMAP = CharStats.CODES.CMMSGMAP();
            for (final int i : CharStats.CODES.SAVING_THROWS())
                if (CMMSGMAP[i] >= 0)
                    prots.add(Integer.valueOf(CMMSGMAP[i]));
        }
        final Room R = (Room) affected;
        if (((msg.tool() instanceof Trap)
            || (prots.contains(Integer.valueOf(msg.sourceMinor())))
            || (prots.contains(Integer.valueOf(msg.targetMinor()))))
            && (msg.target() instanceof MOB)
            && ((msg.source() != msg.target()) || (msg.sourceMajor(CMMsg.MASK_ALWAYS)))) {
            final Set<MOB> H = ((MOB) msg.target()).getGroupMembers(new HashSet<MOB>());
            for (final Object element : H) {
                final MOB M = (MOB) element;
                if ((CMLib.law().doesHavePriviledgesHere(M, R))
                    || ((text().length() > 0)
                    && ((M.Name().equals(text()))
                    || (M.getClanRole(text()) != null)))) {
                    R.show(((MOB) msg.target()), null, this, CMMsg.MSG_OK_VISUAL, L("The guardian hearth protect(s) <S-NAME>!"));
                    break;
                }
            }
        }
        return super.okMessage(myHost, msg);
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Physical target = mob.location();
        if (target == null)
            return false;
        if (target.fetchEffect(ID()) != null) {
            mob.tell(L("This place is already a guarded hearth."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob, target, auto), auto ? "" : L("^S<S-NAME> @x1 to guard this place.^?", prayForWord(mob)));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);
                setMiscText(mob.Name());

                if ((target instanceof Room)
                    && (CMLib.law().doesOwnThisProperty(mob, ((Room) target)))) {
                    final String landOwnerName = CMLib.law().getPropertyOwnerName((Room) target);
                    if (CMLib.clans().getClan(landOwnerName) != null)
                        setMiscText(landOwnerName);
                    target.addNonUninvokableEffect((Ability) this.copyOf());
                    CMLib.database().DBUpdateRoom((Room) target);
                } else
                    beneficialAffect(mob, target, asLevel, 0);
            }
        } else
            beneficialWordsFizzle(mob, target, L("<S-NAME> @x1 to guard this place, but <S-IS-ARE> not answered.", prayForWord(mob)));

        return success;
    }
}
