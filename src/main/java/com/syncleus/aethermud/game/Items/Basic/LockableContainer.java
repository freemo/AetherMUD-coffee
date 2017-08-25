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
package com.planet_ink.game.Items.Basic;

import com.planet_ink.game.Items.interfaces.RawMaterial;


public class LockableContainer extends StdContainer {
    public LockableContainer() {
        super();
        hasALid = true;
        isOpen = false;
        hasALock = true;
        isLocked = true;
        setMaterial(RawMaterial.RESOURCE_OAK);
    }

    @Override
    public String ID() {
        return "LockableContainer";
    }

}
