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
package com.planet_ink.coffee_mud.Areas;

import com.planet_ink.coffee_mud.Areas.interfaces.Area;
import com.planet_ink.coffee_mud.Common.interfaces.TimeClock;
import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.interfaces.CMObject;

import java.util.Enumeration;


public class StdTimeZone extends StdArea {
    public StdTimeZone() {
        super();

        myClock = (TimeClock) CMClass.getCommon("DefaultTimeClock");
    }

    @Override
    public String ID() {
        return "StdTimeZone";
    }

    @Override
    public CMObject copyOf() {
        final CMObject O = super.copyOf();
        if (O instanceof Area)
            ((Area) O).setTimeObj((TimeClock) CMClass.getCommon("DefaultTimeClock"));
        return O;
    }

    @Override
    public TimeClock getTimeObj() {
        return myClock;
    }

    @Override
    public void setName(String newName) {
        super.setName(newName);
        myClock.setLoadName(newName);
    }

    @Override
    public void addChild(Area area) {
        super.addChild(area);
        area.setTimeObj(getTimeObj());
        for (final Enumeration<Area> cA = area.getChildren(); cA.hasMoreElements(); )
            cA.nextElement().setTimeObj(getTimeObj());
    }
}
