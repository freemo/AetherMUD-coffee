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
package com.planet_ink.coffee_mud.core.intermud.cm1;

import com.planet_ink.coffee_mud.core.CMClass;
import com.planet_ink.coffee_mud.core.intermud.cm1.commands.CM1Command;

import java.io.File;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;


@SuppressWarnings("unchecked")
public class CommandHandler implements Runnable {
    private static final Map<String, Class<? extends CM1Command>> commandList = new Hashtable<String, Class<? extends CM1Command>>();

    static {
        String className = CommandHandler.class.getName();
        String packageName = className;
        int x = packageName.lastIndexOf('.');
        if (x > 0)
            packageName = packageName.substring(0, x) + ".commands.";
        if (!className.startsWith("/"))
            className = "/" + className;

        className = className.replace('.', '/');
        className = className + ".class";

        final URL classUrl = CommandHandler.class.getClass().getResource(className);
        if (classUrl != null) {
            String temp = classUrl.getFile();
            if (temp.startsWith("file:")) {
                temp = temp.substring(5);
            }
            x = temp.lastIndexOf('/');
            if (x > 0) {
                final File dir = new File(temp.substring(0, x) + "/commands");
                if ((dir.exists()) && (dir.isDirectory())) {
                    for (final File F : dir.listFiles()) {
                        if (F.getName().endsWith(".class")
                            && (!F.getName().equals("CM1Command.class"))
                            && (F.getName().indexOf('$') < 0)) {
                            final String name = packageName + F.getName().substring(0, F.getName().length() - 6);
                            try {
                                AddCommand((Class<? extends CM1Command>) CMClass.instance().loadClass(name, true));
                            } catch (final Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    private final RequestHandler req;
    private String cmd;
    private String rest;

    public CommandHandler(RequestHandler req, String command) {
        this.req = req;
        final int x = command.indexOf(' ');
        if (x < 0) {
            cmd = command;
            rest = "";
        } else {
            cmd = command.substring(0, x).trim();
            rest = command.substring(x + 1).trim();
        }
    }

    private static final void AddCommand(Class<? extends CM1Command> c) throws InstantiationException, IllegalAccessException {
        final CM1Command c1 = CM1Command.newInstance(c, null, "");
        commandList.put(c1.getCommandWord(), c);
    }

    @Override
    public void run() {
        if (cmd.length() > 0) {
            try {
                if (cmd.equalsIgnoreCase("HELP")) {
                    if (rest.length() > 0) {
                        final Class<? extends CM1Command> commandClass = commandList.get(rest.toUpperCase().trim());
                        final CM1Command command = CM1Command.newInstance(commandClass, req, rest);
                        if ((command == null) || (!command.passesSecurityCheck(req.getUser(), req.getTarget())))
                            req.sendMsg("[FAIL UNKNOWN: " + rest.toUpperCase().trim() + "]");
                        else
                            req.sendMsg("[OK " + command.getCommandWord() + " " + command.getHelp(req.getUser(), req.getTarget(), rest) + "]");
                    } else {
                        final StringBuilder str = new StringBuilder("[OK HELP");
                        for (final String cmdWord : commandList.keySet()) {
                            final Class<? extends CM1Command> commandClass = commandList.get(cmdWord);
                            final CM1Command command = CM1Command.newInstance(commandClass, req, "");
                            if (command.passesSecurityCheck(req.getUser(), req.getTarget()))
                                str.append(" " + cmdWord);
                        }
                        str.append("]");
                        req.sendMsg(str.toString());
                    }
                    return;
                }
                final Class<? extends CM1Command> commandClass = commandList.get(cmd.toUpperCase().trim());
                final CM1Command command = CM1Command.newInstance(commandClass, req, rest);
                if ((command == null) || (!command.passesSecurityCheck(req.getUser(), req.getTarget())))
                    req.sendMsg("[FAIL UNKNOWN " + cmd.toUpperCase().trim() + "]");
                else
                    command.run();
            } catch (final java.io.IOException ioe) {
                req.close();
            }
        }
    }
}
