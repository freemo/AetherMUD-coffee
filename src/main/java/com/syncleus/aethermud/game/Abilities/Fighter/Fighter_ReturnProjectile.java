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
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Items.interfaces.*;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.Races.interfaces.Race;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.ItemPossessor;
import com.syncleus.aethermud.game.core.interfaces.Tickable;


public class Fighter_ReturnProjectile extends FighterSkill {
    private final static String localizedName = CMLib.lang().L("Return Projectile");
    public boolean doneThisRound = false;

    @Override
    public String ID() {
        return "Fighter_ReturnProjectile";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public String displayText() {
        return "";
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_OK_SELF;
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_MOBS;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    public boolean isAutoInvoked() {
        return true;
    }

    @Override
    public boolean canBeUninvoked() {
        return false;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_SKILL | Ability.DOMAIN_EVASIVE;
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if (!super.okMessage(myHost, msg))
            return false;

        if (!(affected instanceof MOB))
            return true;

        final MOB mob = (MOB) affected;
        if (msg.amITarget(mob)
            && (!doneThisRound)
            && (CMLib.flags().isAliveAwakeMobileUnbound(mob, true))
            && (msg.targetMinor() == CMMsg.TYP_WEAPONATTACK)
            && (msg.tool() instanceof Weapon)
            && ((((Weapon) msg.tool()).weaponClassification() == Weapon.CLASS_RANGED)
            || (((Weapon) msg.tool()).weaponClassification() == Weapon.CLASS_THROWN))
            && (!(msg.tool() instanceof Electronics))
            && (mob.rangeToTarget() > 0)
            && (mob.charStats().getBodyPart(Race.BODY_HAND) > 1)
            && ((mob.fetchAbility(ID()) == null) || proficiencyCheck(mob, -85 + mob.charStats().getStat(CharStats.STAT_DEXTERITY) + (2 * getXLEVELLevel(mob)), false))
            && (mob.freeWearPositions(Wearable.WORN_HELD, (short) 0, (short) 0) > 0)) {
            Item w = (Item) msg.tool();
            if ((((Weapon) w).weaponClassification() == Weapon.CLASS_THROWN)
                && (msg.source().isMine(w))) {
                if (!w.amWearingAt(Wearable.IN_INVENTORY))
                    CMLib.commands().postRemove(msg.source(), w, true);
                CMLib.commands().postDrop(msg.source(), w, true, false, false);
            } else if ((w instanceof AmmunitionWeapon) && ((AmmunitionWeapon) w).requiresAmmunition()) {
                final Weapon neww = CMClass.getWeapon("GenWeapon");
                String ammo = "";
                if (neww instanceof AmmunitionWeapon)
                    ammo = ((AmmunitionWeapon) w).ammunitionType();
                if (ammo.length() == 0)
                    return true;
                if (ammo.endsWith("s"))
                    ammo = ammo.substring(0, ammo.length() - 1);
                ammo = CMLib.english().startWithAorAn(ammo);
                neww.setName(ammo);
                neww.setDisplayText(L("@x1 sits here.", ammo));
                neww.setUsesRemaining(1);
                neww.setMaterial(((Weapon) w).material());
                neww.setWeaponClassification(Weapon.CLASS_THROWN);
                neww.setWeaponDamageType(((Weapon) w).weaponDamageType());
                neww.basePhyStats().setWeight(1);
                neww.setBaseValue(0);
                neww.setRanges(w.minRange(), w.maxRange());
                neww.recoverPhyStats();
                w = neww;
                mob.location().addItem(neww, ItemPossessor.Expire.Player_Drop);
            }
            if (mob.location().isContent(w)) {
                final CMMsg msg2 = CMClass.getMsg(mob, w, msg.source(), CMMsg.MSG_GET, L("<S-NAME> catch(es) the <T-NAME> shot by <O-NAME>!"));
                if (mob.location().okMessage(mob, msg2)) {
                    mob.location().send(mob, msg2);
                    if (mob.isMine(w))
                        CMLib.combat().postAttack(mob, msg.source(), w);
                    doneThisRound = true;
                    helpProficiency(mob, 0);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean tick(Tickable ticking, int tickID) {
        if (tickID == Tickable.TICKID_MOB)
            doneThisRound = false;
        return super.tick(ticking, tickID);
    }
}
