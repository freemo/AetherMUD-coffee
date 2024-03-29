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
import com.syncleus.aethermud.game.Abilities.interfaces.ItemCraftor;
import com.syncleus.aethermud.game.Common.interfaces.CMMsg;
import com.syncleus.aethermud.game.Items.interfaces.Item;
import com.syncleus.aethermud.game.Items.interfaces.RawMaterial;
import com.syncleus.aethermud.game.Locales.interfaces.Room;
import com.syncleus.aethermud.game.MOBS.interfaces.MOB;
import com.syncleus.aethermud.game.core.CMClass;
import com.syncleus.aethermud.game.core.CMLib;
import com.syncleus.aethermud.game.core.interfaces.ItemPossessor;
import com.syncleus.aethermud.game.core.interfaces.Physical;

import java.util.List;


public class Chant_CrystalGrowth extends Chant {
    private final static String localizedName = CMLib.lang().L("Crystal Growth");

    @Override
    public String ID() {
        return "Chant_CrystalGrowth";
    }

    @Override
    public String name() {
        return localizedName;
    }

    @Override
    public int classificationCode() {
        return Ability.ACODE_CHANT | Ability.DOMAIN_ROCKCONTROL;
    }

    @Override
    protected int canAffectCode() {
        return 0;
    }

    @Override
    protected int canTargetCode() {
        return 0;
    }

    @Override
    protected int overrideMana() {
        return 50;
    }

    @Override
    public int abstractQuality() {
        return Ability.QUALITY_INDIFFERENT;
    }

    @Override
    public boolean invoke(MOB mob, List<String> commands, Physical givenTarget, boolean auto, int asLevel) {
        if ((mob.location().domainType() != Room.DOMAIN_INDOORS_CAVE)
            && ((mob.location().getAtmosphere() & RawMaterial.MATERIAL_ROCK) == 0)) {
            mob.tell(L("This magic will not work here."));
            return false;
        }
        final int material = RawMaterial.RESOURCE_CRYSTAL;
        if (!super.invoke(mob, commands, givenTarget, auto, asLevel))
            return false;

        // now see if it worked
        final boolean success = proficiencyCheck(mob, 0, auto);
        if (success) {
            final CMMsg msg = CMClass.getMsg(mob, null, this, verbalCastCode(mob, null, auto), auto ? "" : L("^S<S-NAME> chant(s) to the cave walls.^?"));
            if (mob.location().okMessage(mob, msg)) {
                mob.location().send(mob, msg);

                ItemCraftor.ItemKeyPair pair = null;
                int tries = 100;
                while ((pair == null) && (--tries > 0)) {
                    ItemCraftor A = null;
                    switch (CMLib.dice().roll(1, 10, 0)) {
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            A = (ItemCraftor) CMClass.getAbility("Blacksmithing");
                            break;
                        case 5:
                        case 6:
                        case 7:
                            A = (ItemCraftor) CMClass.getAbility("Armorsmithing");
                            break;
                        case 8:
                        case 9:
                        case 10:
                        default:
                            A = (ItemCraftor) CMClass.getAbility("Weaponsmithing");
                            break;
                    }
                    if (A != null)
                        pair = A.craftAnyItem(material);
                    if ((pair == null)
                        || (pair.item == null)
                        || (pair.item.phyStats().level() > mob.phyStats().level())) {
                        if (pair != null) {
                            if (pair.item != null)
                                pair.item.destroy();
                            if (pair.key != null)
                                pair.key.destroy();
                        }
                        pair = null;
                    }
                }
                if (pair == null) {
                    mob.tell(L("The chant failed for some reason..."));
                    return false;
                }
                final Item building = pair.item;
                final Item key = pair.key;
                mob.location().addItem(building, ItemPossessor.Expire.Resource);
                if (key != null)
                    mob.location().addItem(key, ItemPossessor.Expire.Resource);
                final Ability A2 = CMClass.getAbility("Chant_Brittle");
                if (A2 != null)
                    building.addNonUninvokableEffect(A2);

                mob.location().showHappens(CMMsg.MSG_OK_ACTION, L("a tiny crystal fragment drops out of the stone, swells and grows, forming into @x1.", building.name()));
                mob.location().recoverPhyStats();
            }
        } else
            return beneficialWordsFizzle(mob, null, L("<S-NAME> chant(s) to the walls, but nothing happens."));

        // return whether it worked
        return success;
    }
}
