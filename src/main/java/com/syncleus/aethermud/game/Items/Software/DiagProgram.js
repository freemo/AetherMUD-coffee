/*
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
//extends com.syncleus.aethermud.game.Items.Software.GenSoftware
//implements com.syncleus.aethermud.game.Items.interfaces.ArchonOnly

function ID() {
    return "ShipDiagProgram";
}

var lib = Packages.com.syncleus.aethermud.game.core.CMLib;

function newInstance() {
    var newOne = this.super$newInstance();
    newOne.setName("a diag program disk");
    newOne.setDisplayText("a small disk sits here.");
    newOne.setDescription("It appears to be a diagnostics terminal program.");
    return newOne;
}

function getParentMenu() {
    return "";
}

function getInternalName() {
    return "SCRIPTDIAG";
}

function isActivationString(word) {
    return this.isCommandString(word, false);
}

function isDeActivationString(word) {
    return this.isCommandString(word, false);
}

function isCommandString(word, isActive) {
    if (!isActive) {
        return word.toUpperCase() == "DIAGTERM";
    }
    else {
        return true;
    }
}

function getActivationMenu() {
    return "DIAGTERM: Internal computer systems diagnostics termanal";
}

function onActivate(mob, message) {
    this.addScreenMessage("Diagnostics Computer Terminal Activated");
    this.setCurrentScreenDisplay("Diagnostics Computer Terminal Activated\n\rEnter a JavaScript command to use.");
}

function onTyping(mob, message) {
    var cmd = '' + message;
    var x = cmd.indexOf('`');
    while (x >= 0) {
        cmd = cmd.substr(0, x) + '\'' + cmd.substr(x + 1);
        x = cmd.indexOf('`')
    }
    this.addScreenMessage("Input : " + cmd);
    try {
        this.addScreenMessage("Output: " + eval(cmd));
    }
    catch (err) {
        this.addScreenMessage("Error : " + (err ? err.message : '?'));
    }
}
