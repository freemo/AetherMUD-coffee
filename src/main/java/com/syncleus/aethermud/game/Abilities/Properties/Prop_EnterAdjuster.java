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
package com.syncleus.aethermud.game.Abilities.Properties;

import com.syncleus.aethermud.game.Abilities.interfaces.Ability;
import com.syncleus.aethermud.game.Abilities.interfaces.TriggeredAffect;
import com.syncleus.aethermud.game.CharClasses.interfaces.CharClass;
import com.syncleus.aethermud.game.Common.interfaces.AccountStats.PrideStat;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Common.interfaces.CharStats;
import com.syncleus.aethermud.game.Exits.interfaces.Exit;
import com.syncleus.aethermud.game.Libraries.interfaces.MaskingLibrary;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.CMParms;
import com.syncleus.aethermud.game.core.interfaces.Environmental;
import com.syncleus.aethermud.game.core.interfaces.Rideable;

import java.util.Vector;


public class Prop_EnterAdjuster extends Property implements TriggeredAffect {
    protected MaskingLibrary.CompiledZMask mask = CMLib.masking().createEmptyMask();
    protected String[] parameters = new String[]{"", ""};

    @Override
    public String ID() {
        return "Prop_EnterAdjuster";
    }

    @Override
    public String name() {
        return "Room entering adjuster";
    }

    @Override
    protected int canAffectCode() {
        return Ability.CAN_EXITS | Ability.CAN_ROOMS;
    }

    @Override
    public long flags() {
        return Ability.FLAG_ADJUSTER;
    }

    @Override
    public int triggerMask() {
        return TriggeredAffect.TRIGGER_ENTER;
    }

    @Override
    public void setMiscText(String newText) {
        super.setMiscText(newText);
        parameters = CMLib.masking().separateMaskStrs(text());
        if (parameters[1].trim().length() == 0)
            mask = CMLib.masking().createEmptyMask();
        else
            mask = CMLib.masking().getPreCompiledMask(parameters[1]);
    }

    @Override
    public String accountForYourself() {
        return Prop_HaveAdjuster.fixAccoutingsWithMask("Affects those who enter: " + parameters[0], parameters[1]);
    }

    @Override
    public boolean okMessage(final Environmental myHost, final CMMsg msg) {
        if ((affected != null)
            && (((msg.targetMinor() == CMMsg.TYP_ENTER) && ((affected instanceof Room) || (affected instanceof Exit)))
            || ((msg.targetMinor() == CMMsg.TYP_SIT) && (affected == msg.target()) && (affected instanceof Rideable)))
            && ((mask == null) || (CMLib.masking().maskCheck(mask, msg.source(), true)))) {
            final MOB mob = msg.source();
            final Vector<Ability> theSpells = new Vector<Ability>();
            int del = parameters[0].indexOf(';');
            while (del >= 0) {
                final String thisOne = parameters[0].substring(0, del);
                if ((thisOne.length() > 0) && (!thisOne.equals(";"))) {
                    Ability A = CMClass.getAbility(thisOne);
                    if ((A != null) && (!CMLib.ableMapper().classOnly("Archon", A.ID()))) {
                        A = (Ability) A.copyOf();
                        theSpells.addElement(A);
                    }
                }
                parameters[0] = parameters[0].substring(del + 1);
                del = parameters[0].indexOf(';');
            }
            if ((parameters[0].length() > 0) && (!parameters[0].equals(";"))) {
                Ability A = CMClass.getAbility(parameters[0]);
                if (A != null) {
                    A = (Ability) A.copyOf();
                    theSpells.addElement(A);
                }
            }
            for (int i = 0; i < theSpells.size(); i++) {
                final Ability thisOne = (Ability) theSpells.elementAt(i).copyOf();
                thisOne.invoke(mob, mob, true, 0);
            }

            mob.basePhyStats().setAbility(mob.basePhyStats().ability() + CMParms.getParmPlus(parameters[0], "abi"));
            mob.basePhyStats().setArmor(mob.basePhyStats().armor() + CMParms.getParmPlus(parameters[0], "arm"));
            mob.basePhyStats().setAttackAdjustment(mob.basePhyStats().attackAdjustment() + CMParms.getParmPlus(parameters[0], "att"));
            mob.basePhyStats().setDamage(mob.basePhyStats().damage() + CMParms.getParmPlus(parameters[0], "dam"));
            mob.basePhyStats().setDisposition(mob.basePhyStats().disposition() | CMParms.getParmPlus(parameters[0], "dis"));
            mob.basePhyStats().setLevel(mob.basePhyStats().level() + CMParms.getParmPlus(parameters[0], "lev"));
            mob.basePhyStats().setRejuv(mob.basePhyStats().rejuv() + CMParms.getParmPlus(parameters[0], "rej"));
            mob.basePhyStats().setSensesMask(mob.basePhyStats().sensesMask() | CMParms.getParmPlus(parameters[0], "sen"));
            mob.basePhyStats().setSpeed(mob.basePhyStats().speed() + CMParms.getParmPlus(parameters[0], "spe"));
            mob.basePhyStats().setWeight(mob.basePhyStats().weight() + CMParms.getParmPlus(parameters[0], "wei"));
            mob.basePhyStats().setHeight(mob.basePhyStats().height() + CMParms.getParmPlus(parameters[0], "hei"));

            mob.baseCharStats().setStat(CharStats.STAT_CHARISMA, mob.baseCharStats().getStat(CharStats.STAT_CHARISMA) + CMParms.getParmPlus(parameters[0], "cha"));
            mob.baseCharStats().setStat(CharStats.STAT_CONSTITUTION, mob.baseCharStats().getStat(CharStats.STAT_CONSTITUTION) + CMParms.getParmPlus(parameters[0], "con"));
            mob.baseCharStats().setStat(CharStats.STAT_DEXTERITY, mob.baseCharStats().getStat(CharStats.STAT_DEXTERITY) + CMParms.getParmPlus(parameters[0], "dex"));
            String val = CMParms.getParmStr(parameters[0], "gen", "").toUpperCase();
            if ((val.length() > 0) && ((val.charAt(0) == 'M') || (val.charAt(0) == 'F') || (val.charAt(0) == 'N')))
                mob.baseCharStats().setStat(CharStats.STAT_GENDER, val.charAt(0));
            mob.baseCharStats().setStat(CharStats.STAT_INTELLIGENCE, mob.baseCharStats().getStat(CharStats.STAT_INTELLIGENCE) + CMParms.getParmPlus(parameters[0], "int"));
            val = CMParms.getParmStr(parameters[0], "cla", "").toUpperCase();
            if (val.length() > 0) {
                final CharClass C = CMClass.findCharClass(val);
                if ((C != null) && (C.availabilityCode() != 0))
                    mob.baseCharStats().setCurrentClass(C);
            }
            val = CMParms.getParmStr(parameters[0], "rac", "").toUpperCase();
            if ((val.length() > 0) && (CMClass.getRace(val) != null)) {
                final int oldCat = mob.baseCharStats().ageCategory();
                mob.baseCharStats().setMyRace(CMClass.getRace(val));
                mob.baseCharStats().getMyRace().startRacing(mob, false);
                if (mob.baseCharStats().getStat(CharStats.STAT_AGE) > 0)
                    mob.baseCharStats().setStat(CharStats.STAT_AGE, mob.baseCharStats().getMyRace().getAgingChart()[oldCat]);
            }
            mob.baseCharStats().setStat(CharStats.STAT_STRENGTH, mob.baseCharStats().getStat(CharStats.STAT_STRENGTH) + CMParms.getParmPlus(parameters[0], "str"));
            mob.baseCharStats().setStat(CharStats.STAT_WISDOM, mob.baseCharStats().getStat(CharStats.STAT_WISDOM) + CMParms.getParmPlus(parameters[0], "wis"));
            if (CMParms.getParmPlus(parameters[0], "lev") != 0)
                mob.baseCharStats().setClassLevel(mob.baseCharStats().getCurrentClass(), mob.baseCharStats().getClassLevel(mob.baseCharStats().getCurrentClass()) + CMParms.getParmPlus(parameters[0], "lev"));

            mob.baseState().setHitPoints(mob.curState().getHitPoints() + CMParms.getParmPlus(parameters[0], "hit"));
            mob.curState().setHunger(mob.curState().getHunger() + CMParms.getParmPlus(parameters[0], "hun"));
            mob.curState().setMana(mob.curState().getMana() + CMParms.getParmPlus(parameters[0], "man"));
            mob.curState().setMovement(mob.curState().getMovement() + CMParms.getParmPlus(parameters[0], "mov"));
            mob.curState().setThirst(mob.curState().getThirst() + CMParms.getParmPlus(parameters[0], "thi"));

            mob.setPractices(mob.getPractices() + CMParms.getParmPlus(parameters[0], "prac"));
            mob.setTrains(mob.getTrains() + CMParms.getParmPlus(parameters[0], "trai"));
            final int qp = CMParms.getParmPlus(parameters[0], "ques");
            if (qp != 0)
                mob.setQuestPoint(mob.getQuestPoint() + qp);
            if (qp > 0)
                CMLib.players().bumpPrideStat(mob, PrideStat.QUESTPOINTS_EARNED, qp);
            final int newMoney = CMParms.getParmPlus(parameters[0], "coin");
            if (newMoney != 0)
                CMLib.moneyCounter().setMoney(mob, CMLib.moneyCounter().getMoney(mob) + newMoney);
            final int exp = CMParms.getParmPlus(parameters[0], "expe");
            if (exp > 0)
                CMLib.leveler().postExperience(mob, null, null, exp, false);
            mob.recoverCharStats();
            mob.recoverPhyStats();
            mob.recoverMaxState();
            CMLib.utensils().confirmWearability(mob);
        }
        return super.okMessage(myHost, msg);
    }
}
