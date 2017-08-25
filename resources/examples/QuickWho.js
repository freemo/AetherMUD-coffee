//extends com.planet_ink.game.Commands.StdCommand
var CMLib = Packages.com.planet_ink.game.core.CMLib;
var CMParms = Packages.com.planet_ink.game.core.CMParms;

function ID() {
    return "QuickWho";
}

var commands = CMParms.toStringArray(CMParms.makeVector("QUICKWHO"));

function getAccessWords() {
    return commands;
}

function execute(mob, commands, x) {
    var e;
    var M;
    for (e = CMLib.players().players(); e.hasMoreElements();) {
        M = e.nextElement();
        if (M != null)
            mob.tell(M.Name());
    }
    return true;
}
