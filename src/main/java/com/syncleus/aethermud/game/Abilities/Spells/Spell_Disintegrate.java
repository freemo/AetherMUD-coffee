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
package com.syncleus.aethermud.game.Abilities.Spells;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.DeadBody;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.HashSet;
import java.util.List;


public class Spell_Disintegrate extends Spell {

    private final static String localizedName = CMLib.lang().L("Disintegrate");

    @Override
    public String ID() {
        return "Spell_Disintegrate";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_MALICIOUS;
    }

    @Override
    protected int canTargetCode() {
        return CAN_ITEMS | CAN_MOBS;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SPELL | Ability.DOMAIN_EVOCATION;
    }

    @Override
    public int overrideMana() {
        return 100;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        final Physical target = getAnyTarget(mob, commands, givenTarget, Wearable.FILTER_ANY);
        if (target == null)
            return false;
        final List<DeadBody> DBs = CMLib.utensils().getDeadBodies(target);
        for (int v = 0; v < DBs.size(); v++) {
            final DeadBody DB = DBs.get(v);
            if (DB.isPlayerCorpse()
                && (!DB.getMobName().equals(mob.Name()))) {
                mob.tell(L("You are not allowed to destroy a player corpse."));
                return false;
            }
        }

        if ((target instanceof Item) && (!CMLib.utensils().canBePlayerDestroyed(mob, (Item) target, true))) {
            mob.tell(L("You are not powerful enough to disintegrate @x1.", target.name(mob)));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        boolean success = false;
        int affectType = CMMsg.MSG_CAST_VERBAL_SPELL;
        if (!(target instanceof Item)) {
            if (!auto)
                affectType = affectType | CMMsg.MASK_MALICIOUS;
        }
        int levelDiff = target.phyStats().level() - (mob.phyStats().level() + (getXLEVELLevel(mob) / 2));
        if (target instanceof MOB)
            levelDiff += 6;
        if (levelDiff < 0)
            levelDiff = 0;
        success = proficiencyCheck(mob, -(levelDiff * 15), auto);

        if (auto)
            affectType = affectType | CMMsg.MASK_ALWAYS;

        if (success) {
            final Room R = mob.location();
            final CMMsg msg = CMClass.getMsg(mob, target, this, affectType, L(auto ? "" : "^S<S-NAME> point(s) at <T-NAMESELF> and utter(s) a treacherous spell!^?") + CMLib.protocol().msp("spelldam2.wav", 40));
            if ((R != null) && (R.okMessage(mob, msg))) {
                R.send(mob, msg);
                if (msg.value() <= 0) {
                    final HashSet<DeadBody> oldBodies = new HashSet<DeadBody>();
                    for (int i = 0; i < R.numItems(); i++) {
                        final Item I = R.getItem(i);
                        if ((I instanceof DeadBody)
                            && (I.container() == null))
                            oldBodies.add((DeadBody) I);
                    }

                    if (target instanceof MOB) {
                        if (((MOB) target).curState().getHitPoints() > 0)
                            CMLib.combat().postDamage(mob, (MOB) target, this, (((MOB) target).curState().getHitPoints() * 100), CMMsg.MASK_ALWAYS | CMMsg.TYP_CAST_SPELL, Weapon.TYPE_BURSTING, L("^SThe spell <DAMAGE> <T-NAME>!^?"));
                        if (((MOB) target).amDead())
                            R.show(mob, target, CMMsg.MSG_OK_ACTION, L("<T-NAME> disintegrate(s)!"));
                        else
                            return false;
                    } else
                        R.show(mob, target, CMMsg.MSG_OK_ACTION, L("<T-NAME> disintegrate(s)!"));

                    if (target instanceof Item)
                        ((Item) target).destroy();
                    else // destroy any newly created bodies
                    {
                        for (int i = 0; i < R.numItems(); i++) {
                            final Item I = R.getItem(i);
                            if ((I instanceof DeadBody)
                                && (I.container() == null)
                                && (!oldBodies.contains(I))
                                && (!((DeadBody) I).isPlayerCorpse())) {
                                I.destroy();
                                break;
                            }
                        }
                    }
                    R.recoverRoomStats();
                }

            }

        } else
            maliciousFizzle(mob, target, L("<S-NAME> point(s) at <T-NAMESELF> and utter(s) a treacherous but fizzled spell!"));

        // return whether it worked
        return success;
    }
}
