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
package com.syncleus.aethermud.game.Items.Weapons;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.MiscMagic.StdWand;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Items.interfaces.Wand;
import com.syncleus.aethermud.game.Items.interfaces.Weapon;
import com.syncleus.aethermud.game.Items.interfaces.Wearable;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMProps;
import com.syncleus.aethermud.game.core.CMStrings;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class GenStaff extends GenWeapon implements Wand {
    protected String secretWord = CMProps.getAnyListFileValue(CMProps.ListFile.MAGIC_WORDS);
    protected int maxUses = Integer.MAX_VALUE;

    public GenStaff() {
        super();

        setName("a wooden staff");
        setDisplayText("a wooden staff lies in the corner of the room.");
        setDescription("");
        secretIdentity = "";
        basePhyStats().setAbility(0);
        basePhyStats().setLevel(0);
        basePhyStats.setWeight(4);
        basePhyStats().setAttackAdjustment(0);
        basePhyStats().setDamage(4);
        setUsesRemaining(0);
        baseGoldValue = 1;
        recoverPhyStats();
        wornLogicalAnd = true;
        material = RawMaterial.RESOURCE_OAK;
        properWornBitmap = Wearable.WORN_HELD | Wearable.WORN_WIELD;
        weaponDamageType = TYPE_BASHING;
        weaponClassification = Weapon.CLASS_STAFF;
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "GenStaff";
    }

    @Override
    public int maxUses() {
        return maxUses;
    }

    @Override
    public void setMaxUses(int newMaxUses) {
        maxUses = newMaxUses;
    }

    @Override
    public boolean isGeneric() {
        return true;
    }

    @Override
    public int value() {
        if ((usesRemaining() <= 0)
            && (readableText.length() > 0)
            && (this.getSpell() != null))
            return 0;
        return super.value();
    }

    @Override
    public String readableText() {
        return readableText;
    }

    @Override
    public void setReadableText(String text) {
        readableText = text;
        secretWord = StdWand.getWandWord(readableText);
    }

    @Override
    public String secretIdentity() {
        String id = super.secretIdentity();
        final Ability A = getSpell();
        if (A != null)
            id = "'A staff of " + A.name() + "' Charges: " + usesRemaining() + "\n\r" + id;
        return id + "\n\rSay the magic word :`" + secretWord + "` to the target.";
    }

    @Override
    public Ability getSpell() {
        return CMClass.getAbility(readableText());
    }

    @Override
    public void setSpell(Ability theSpell) {
        readableText = "";
        if (theSpell != null)
            readableText = theSpell.ID();
        secretWord = StdWand.getWandWord(readableText);
    }

    @Override
    public String magicWord() {
        return secretWord;
    }

    @Override
    public void waveIfAble(MOB mob, Physical afftarget, String message) {
        StdWand.waveIfAble(mob, afftarget, message, this);
    }

    @Override
    public boolean checkWave(MOB mob, String message) {
        return StdWand.checkWave(mob, message, this);
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        final MOB mob = msg.source();
        switch (msg.targetMinor()) {
            case CMMsg.TYP_WAND_USE:
                if (msg.amITarget(this) && ((msg.tool() == null) || (msg.tool() instanceof Physical)))
                    StdWand.waveIfAble(mob, (Physical) msg.tool(), msg.targetMessage(), this);
                break;
            case CMMsg.TYP_SPEAK:
                if ((msg.sourceMinor() == CMMsg.TYP_SPEAK) && (!amWearingAt(Wearable.IN_INVENTORY))) {
                    boolean alreadyWanding = false;
                    final List<CMMsg> trailers = msg.trailerMsgs();
                    if (trailers != null) {
                        for (final CMMsg msg2 : trailers) {
                            if ((msg2.targetMinor() == CMMsg.TYP_WAND_USE)
                                && (msg2.target() == this))
                                alreadyWanding = true;
                        }
                    }
                    final String said = CMStrings.getSayFromMessage(msg.sourceMessage());
                    if ((!alreadyWanding) && (said != null) && (checkWave(mob, said)))
                        msg.addTrailerMsg(CMClass.getMsg(msg.source(), this, msg.target(), CMMsg.NO_EFFECT, null, CMMsg.MASK_ALWAYS | CMMsg.TYP_WAND_USE, said, CMMsg.NO_EFFECT, null));
                }
                break;
            default:
                break;
        }
        super.executeMsg(myHost, msg);
    }
    // wand stats handled by genweapon, filled by readableText
}
