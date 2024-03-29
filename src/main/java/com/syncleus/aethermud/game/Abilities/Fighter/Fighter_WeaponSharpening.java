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
package com.syncleus.aethermud.game.Abilities.Fighter;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.PhyStats;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMath;
import com.syncleus.aethermud.game.core.interfaces.Physical;
import com.syncleus.aethermud.game.core.interfaces.Tickable;

import java.util.List;


public class Fighter_WeaponSharpening extends FighterSkill {
    protected final static int TICKS_TO_SHARPEN = 8;
    private final static String localizedName = CMLib.lang().L("Weapon Sharpening");
    private static final String[] triggerStrings = I(new String[]{"WEAPONSHARPENING", "SHARPEN"});
    protected String displayString = "Sharpening";
    protected Item weapon = null;
    protected int damageBonus = 1;

    @Override
    public String ID() {
        return "Fighter_WeaponSharpening";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return L("(" + displayString + ")");
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
    protected int canAffectCode() {
        return Ability.CAN_ITEMS;
    }

    @Override
    protected int canTargetCode() {
        return Ability.CAN_ITEMS;
    }

    @Override
    public int maxRange() {
        return adjustedMaxInvokerRange(0);
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_WEAPON_USE;
    }

    @Override
    public int usageType() {
        return USAGE_MANA;
    }

    @Override
    public void setMiscText(String newMiscText) {
        super.setMiscText(newMiscText);
        if (newMiscText.length() > 0)
            damageBonus = CMath.s_int(newMiscText);
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (affected instanceof MOB) {
            final MOB mob = (MOB) affected;
            if ((weapon == null) || (weapon.owner() != affected) || (weapon.amDestroyed()) || (!CMLib.flags().isInTheGame(mob, true)) || (mob.location() == null)) {
                weapon = null;
                unInvoke();
                return false;
            }
            if ((this.tickDown % 2) == 0) {
                mob.location().show(mob, weapon, CMMsg.MSG_HANDS, L("<S-NAME> continue(s) sharpening <T-NAME> (@x1).", CMath.toPct(CMath.div((TICKS_TO_SHARPEN - tickDown + 1), TICKS_TO_SHARPEN))));
            }
        } else if (affected instanceof Item) {
            final Item weapon = (Item) affected;
            if ((weapon == null)
                || (weapon.amDestroyed())
                || (weapon.subjectToWearAndTear() && (weapon.usesRemaining() < 95))) {
                this.weapon = null;
                unInvoke();
                return false;
            }
        }
        return super.tick(ticking, tickID);
    }

    @Override
    public void unInvoke() {
        if ((affected instanceof MOB)
            && (weapon != null)
            && (!weapon.amDestroyed())) {
            final MOB mob = (MOB) affected;
            beneficialAffect(mob, weapon, 0, 0);
            final Ability A = weapon.fetchEffect(ID());
            if (A != null) {
                A.setMiscText(text());
                A.makeLongLasting();
            }
            weapon.recoverPhyStats();
            if (mob.location() != null)
                mob.location().recoverRoomStats();
            mob.tell(mob, weapon, null, L("You have finished sharpening <T-NAME>."));
            weapon = null;
        } else if ((affected instanceof Item)
            && (!((Item) affected).amDestroyed())
            && (((Item) affected).owner() instanceof MOB)) {
            final MOB M = (MOB) ((Item) affected).owner();
            if ((!M.amDead()) && (CMLib.flags().isInTheGame(M, true)) && (!((Item) affected).amWearingAt(Wearable.IN_INVENTORY)))
                M.tell(M, affected, null, L("<T-NAME> no longer seem(s) quite as sharp."));
        }
        super.unInvoke();
    }

    @Override
    public void affectPhyStats(Physical affected, PhyStats stats) {
        if ((affected instanceof Item) && (damageBonus > 0) && (((Item) affected).owner() instanceof MOB)) {
            stats.setDamage(stats.damage() + damageBonus);
            stats.addAmbiance("^w*^?");
        }
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if (mob.fetchEffect(ID()) != null) {
            mob.tell(L("You are already sharpening something."));
            return false;
        }
        final Item weapon = super.getTarget(mob, null, givenTarget, null, commands, Wearable.FILTER_ANY);
        if (weapon == null)
            return false;
        if (!(weapon instanceof Weapon)) {
            mob.tell(mob, weapon, null, L("<T-NAME> is not a weapon."));
            return false;
        }
        if (weapon.fetchEffect(ID()) != null) {
            mob.tell(mob, weapon, null, L("<T-NAME> is already sharp."));
            return false;
        }
        boolean isSharpenable;
        switch (((Weapon) weapon).weaponClassification()) {
            case Weapon.CLASS_AXE:
            case Weapon.CLASS_DAGGER:
            case Weapon.CLASS_EDGED:
            case Weapon.CLASS_SWORD:
            case Weapon.CLASS_POLEARM:
                isSharpenable = true;
                break;
            case Weapon.CLASS_FLAILED:
            case Weapon.CLASS_RANGED:
            case Weapon.CLASS_THROWN:
                switch (((Weapon) weapon).weaponDamageType()) {
                    case Weapon.TYPE_PIERCING:
                    case Weapon.TYPE_SLASHING:
                        isSharpenable = true;
                        break;
                    default:
                        isSharpenable = false;
                        break;
                }
                break;
            default:
                isSharpenable = false;
                break;
        }
        if (!isSharpenable) {
            mob.tell(mob, weapon, null, L("<T-NAME> can not be sharpened with this skill."));
            return false;
        }
        if ((weapon.subjectToWearAndTear()) && (weapon.usesRemaining() < 95)) {
            mob.tell(mob, weapon, null, L("<T-NAME> needs repairing first."));
            return false;
        }
        if ((!auto) && (mob.isInCombat())) {
            mob.tell(L("You are a bit too busy to do that right now."));
            return false;
        }
        final int bonus = (int) Math.round(CMath.mul(0.10 + (0.10 * getXLEVELLevel(mob)), weapon.phyStats().damage()));
        if (bonus < 1) {
            mob.tell(mob, weapon, null, L("<T-NAME> is too weak of a weapon to provide any more benefit from sharpening."));
            return false;
        }

        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final String str = auto ? L("<T-NAME> looks sharper!") : L("<S-NAME> start(s) sharpening <T-NAMESELF>.");
            final CMMsg msg = CMClass.getMsg(mob, weapon, this, CMMsg.MSG_NOISYMOVEMENT, str);
            if (mob.location().okMessage(mob, msg)) {
                displayString = "Sharpening " + weapon.name();
                mob.location().send(mob, msg);
                if (auto) {
                    beneficialAffect(mob, weapon, asLevel, 0);
                    final Ability A = weapon.fetchEffect(ID());
                    if (A != null) {
                        A.setMiscText("" + bonus);
                        A.makeLongLasting();
                    }
                    weapon.recoverPhyStats();
                    mob.location().recoverRoomStats();
                } else {
                    beneficialAffect(mob, mob, asLevel, TICKS_TO_SHARPEN);
                    final Fighter_WeaponSharpening A = (Fighter_WeaponSharpening) mob.fetchEffect(ID());
                    if (A != null) {
                        A.weapon = weapon;
                        A.setMiscText("" + bonus);
                    }
                }
            }
        } else
            return beneficialVisualFizzle(mob, weapon, L("<S-NAME> attempt(s) to tweak <T-NAME>, but just can't get it quite right."));
        return success;
    }

}
