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
package com.syncleus.aethermud.game.Items.MiscMagic;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.Basic.GenDrink;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.Potion;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class GenMultiPotion extends GenDrink implements Potion {
    public GenMultiPotion() {
        super();

        material = RawMaterial.RESOURCE_GLASS;
        setName("a flask");
        basePhyStats.setWeight(1);
        setDisplayText("A flask sits here.");
        setDescription("A strange flask with stranger markings.");
        secretIdentity = "";
        baseGoldValue = 200;
        liquidType = RawMaterial.RESOURCE_DRINKABLE;
        recoverPhyStats();
    }

    @Override
    public String ID() {
        return "GenMultiPotion";
    }

    @Override
    public boolean isGeneric() {
        return true;
    }

    @Override
    public boolean isDrunk() {
        return (readableText.toUpperCase().indexOf(";DRUNK") >= 0);
    }

    @Override
    public void setDrunk(boolean isTrue) {
        if (isTrue && isDrunk())
            return;
        if ((!isTrue) && (!isDrunk()))
            return;
        if (isTrue)
            setSpellList(getSpellList() + ";DRUNK");
        else {
            String list = "";
            final List<Ability> theSpells = getSpells();
            for (int v = 0; v < theSpells.size(); v++)
                list += theSpells.get(v).ID() + ";";
            setSpellList(list);
        }
    }

    @Override
    public void setLiquidType(int newLiquidType) {
        liquidType = newLiquidType;
    }

    @Override
    public String secretIdentity() {
        return StdScroll.makeSecretIdentity("potion", super.secretIdentity(), "", getSpells());
    }

    @Override
    public int value() {
        if (isDrunk())
            return 0;
        return super.value();
    }

    @Override
    public String getSpellList() {
        return readableText;
    }

    @Override
    public void setSpellList(String list) {
        readableText = list;
    }

    @Override
    public List<Ability> getSpells() {
        return StdPotion.getSpells(this);
    }

    @Override
    public void setReadableText(String text) {
        readableText = text;
        setSpellList(readableText);
    }

    @Override
    public void drinkIfAble(MOB owner, Physical drinkerTarget) {
        final List<Ability> spells = getSpells();
        if (owner.isMine(this)) {
            if ((!isDrunk()) && (spells.size() > 0)) {
                final MOB caster = CMLib.map().getFactoryMOB(owner.location());
                final MOB finalCaster = (owner != drinkerTarget) ? owner : caster;
                boolean destroyCaster = true;
                for (int i = 0; i < spells.size(); i++) {
                    final Ability thisOneA = (Ability) spells.get(i).copyOf();
                    if ((drinkerTarget instanceof Item)
                        && ((!thisOneA.canTarget(drinkerTarget)) && (!thisOneA.canAffect(drinkerTarget))))
                        continue;
                    int level = phyStats().level();
                    final int lowest = CMLib.ableMapper().lowestQualifyingLevel(thisOneA.ID());
                    if (level < lowest)
                        level = lowest;
                    caster.basePhyStats().setLevel(level);
                    caster.phyStats().setLevel(level);
                    thisOneA.invoke(finalCaster, drinkerTarget, true, level);
                    Ability effectA = drinkerTarget.fetchEffect(thisOneA.ID());
                    if ((effectA != null) && (effectA.invoker() == caster))
                        destroyCaster = false;
                }
                if (destroyCaster)
                    caster.destroy();
            }

            if ((liquidRemaining() <= thirstQuenched()) && (!isDrunk()))
                setDrunk(true);
        }
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((msg.amITarget(this))
            && (msg.targetMinor() == CMMsg.TYP_DRINK)
            && (msg.othersMessage() == null)
            && (msg.sourceMessage() == null))
            return true;
        else if ((msg.tool() == this)
            && (msg.targetMinor() == CMMsg.TYP_FILL))
            return true;
        return super.okMessage(myHost, msg);
    }

    @Override
    public void executeMsg(final Environmental myHost, final CMMsg msg) {
        if (msg.amITarget(this)) {
            final MOB mob = msg.source();
            switch (msg.targetMinor()) {
                case CMMsg.TYP_DRINK:
                    if ((msg.sourceMessage() == null) && (msg.othersMessage() == null)) {
                        drinkIfAble(mob, mob);
                        if (isDrunk()) {
                            mob.tell(L("@x1 vanishes!", name()));
                            destroy();
                        }
                        mob.recoverPhyStats();
                    } else {
                        msg.addTrailerMsg(CMClass.getMsg(msg.source(), this, msg.tool(), CMMsg.NO_EFFECT, null, msg.targetCode(), msg.targetMessage(), CMMsg.NO_EFFECT, null));
                        super.executeMsg(myHost, msg);
                    }
                    break;
                default:
                    super.executeMsg(myHost, msg);
                    break;
            }
        } else if ((msg.tool() == this) && (msg.targetMinor() == CMMsg.TYP_FILL) && (msg.target() instanceof Physical)) {
            if ((msg.sourceMessage() == null) && (msg.othersMessage() == null)) {
                drinkIfAble(msg.source(), (Physical) msg.target());
                if (isDrunk()) {
                    msg.source().tell(L("@x1 vanishes!", name()));
                    destroy();
                }
                msg.source().recoverPhyStats();
                ((Physical) msg.target()).recoverPhyStats();
            } else {
                msg.addTrailerMsg(CMClass.getMsg(msg.source(), msg.target(), msg.tool(), CMMsg.NO_EFFECT, null, msg.targetCode(), msg.targetMessage(), CMMsg.NO_EFFECT, null));
                super.executeMsg(myHost, msg);
            }
        } else
            super.executeMsg(myHost, msg);
    }
    // stats handled by gendrink, spells by readabletext
}
