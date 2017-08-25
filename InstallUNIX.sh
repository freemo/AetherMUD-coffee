#!/bin/sh
#The following either will echo (send to display), read (get in a variable) or sleep (wait for a #few seconds).
#Written by: Wolf a.k.a. TheOneWolf a.k.a. TheSimDude a.k.a. The_One a.k.a. Wolfgang Richter
#If you have a problem...e-mail the ghostbusters...I mean...e-mail wrichter@att.net...yeah :)
#VERSION: TEST5

echo "Hello, and welcome to the UNIX CoffeeMud automated installer."
sleep 1
echo "I am going to ask you a few questions, and as you answer them I will create your MUD."
sleep 1
echo "* MAKE SURE YOU HAVE WRITE PERMISSION TO mudUNIX.sh *"
sleep 1
echo "***** MAKE SURE YOU ARE EXECUTING THIS SCRIPT FROM THE MAIN COFFEEMUD DIR (not source dir)*****"

sleep 2

echo "Questions:"
echo "1. What is the location of JAVA V1.6+ (example: /opt/jdk1.6.0_09 - just to main dir NO TRAILING SLASH)?"
read Java_Home

echo "
2. Do you want to compile docs about the source code (not needed to run - javadoc etc.; more time consuming; you may need to be root...) ? [y/n]
"
read yesnodocs

echo "3. Do you want to compile the full program right now (say no if you're only doing the docs, or changing the name of your MUD)? [y/n]"
read yesnofullcompile

echo "3. What have you decided to name your MUD (you can change this later by editing the mudUNIX.sh file) ?"
read MUDname

echo "Alright, we're good to go, compiling will begin in 5 seconds."

sleep 5
#Next we will set up some needed variables for compiling.... and then run an if statement

JAVACPATH="$Java_Home/bin/javac -nowarn -g -deprecation -encoding UTF8 -classpath .:./lib/js.jar:./lib/jzlib.jar" 

if [ $yesnodocs = y ] ; then

echo "/nBeginning compile of the source docs...this may take awhile...you were warned!/n"
sleep 1

$Java_Home/bin/javadoc -d ./docs -J-Xmx256m -subpackages com.planet_ink.game

else

echo "OK no doc compiling for you...well its mainly for source developers anyways..."
echo "About to begin main compile....."
sleep 1

fi


if [ $yesnofullcompile = y ] ; then

$JAVACPATH com/planet_ink/fakedb/*.java
$JAVACPATH com/planet_ink/game/Abilities/*.java
$JAVACPATH com/planet_ink/game/application/*.java
$JAVACPATH com/planet_ink/game/Areas/*.java
$JAVACPATH com/planet_ink/game/Behaviors/*.java
$JAVACPATH com/planet_ink/game/CharClasses/*.java
$JAVACPATH com/planet_ink/game/Commands/*.java
$JAVACPATH com/planet_ink/game/Common/*.java
$JAVACPATH com/planet_ink/game/core/*.java
$JAVACPATH com/planet_ink/game/core/database/*.java
$JAVACPATH com/planet_ink/game/core/exceptions/*.java
$JAVACPATH com/planet_ink/game/core/interfaces/*.java
$JAVACPATH com/planet_ink/game/core/intermud/*.java
$JAVACPATH com/planet_ink/game/core/intermud/cm1/*.java
$JAVACPATH com/planet_ink/game/core/intermud/cm1/commands/*.java
$JAVACPATH com/planet_ink/game/core/intermud/i3/*.java
$JAVACPATH com/planet_ink/game/core/intermud/i3/net/*.java
$JAVACPATH com/planet_ink/game/core/intermud/i3/packets/*.java
$JAVACPATH com/planet_ink/game/core/intermud/i3/persist/*.java
$JAVACPATH com/planet_ink/game/core/intermud/i3/server/*.java
$JAVACPATH com/planet_ink/game/core/intermud/imc2/*.java
$JAVACPATH com/planet_ink/game/core/smtp/*.java
$JAVACPATH com/planet_ink/game/core/threads/*.java
$JAVACPATH com/planet_ink/game/core/collections/*.java
$JAVACPATH com/planet_ink/game/Exits/*.java
$JAVACPATH com/planet_ink/game/Libraries/*.java
$JAVACPATH com/planet_ink/game/Locales/*.java
$JAVACPATH com/planet_ink/game/MOBS/*.java
$JAVACPATH com/planet_ink/game/Races/*.java
$JAVACPATH com/planet_ink/game/WebMacros/*.java
$JAVACPATH com/planet_ink/game/Abilities/Archon/*.java
$JAVACPATH com/planet_ink/game/Abilities/Common/*.java
$JAVACPATH com/planet_ink/game/Abilities/Diseases/*.java
$JAVACPATH com/planet_ink/game/Abilities/Druid/*.java
$JAVACPATH com/planet_ink/game/Abilities/Fighter/*.java
$JAVACPATH com/planet_ink/game/Abilities/interfaces/*.java
$JAVACPATH com/planet_ink/game/Abilities/Languages/*.java
$JAVACPATH com/planet_ink/game/Abilities/Misc/*.java
$JAVACPATH com/planet_ink/game/Abilities/Paladin/*.java
$JAVACPATH com/planet_ink/game/Abilities/Poisons/*.java
$JAVACPATH com/planet_ink/game/Abilities/Prayers/*.java
$JAVACPATH com/planet_ink/game/Abilities/Properties/*.java
$JAVACPATH com/planet_ink/game/Abilities/Ranger/*.java
$JAVACPATH com/planet_ink/game/Abilities/Skills/*.java
$JAVACPATH com/planet_ink/game/Abilities/Songs/*.java
$JAVACPATH com/planet_ink/game/Abilities/Specializations/*.java
$JAVACPATH com/planet_ink/game/Abilities/Spells/*.java
$JAVACPATH com/planet_ink/game/Abilities/SuperPowers/*.java
$JAVACPATH com/planet_ink/game/Abilities/Tech/*.java
$JAVACPATH com/planet_ink/game/Abilities/Thief/*.java
$JAVACPATH com/planet_ink/game/Abilities/Traps/*.java
$JAVACPATH com/planet_ink/game/Areas/interfaces/*.java
$JAVACPATH com/planet_ink/game/Behaviors/interfaces/*.java
$JAVACPATH com/planet_ink/game/CharClasses/interfaces/*.java
$JAVACPATH com/planet_ink/game/Commands/interfaces/*.java
$JAVACPATH com/planet_ink/game/Common/interfaces/*.java
$JAVACPATH com/planet_ink/game/Exits/interfaces/*.java
$JAVACPATH com/planet_ink/game/Items/Armor/*.java
$JAVACPATH com/planet_ink/game/Items/Basic/*.java
$JAVACPATH com/planet_ink/game/Items/ClanItems/*.java
$JAVACPATH com/planet_ink/game/Items/interfaces/*.java
$JAVACPATH com/planet_ink/game/Items/MiscMagic/*.java
$JAVACPATH com/planet_ink/game/Items/BasicTech/*.java
$JAVACPATH com/planet_ink/game/Items/CompTech/*.java
$JAVACPATH com/planet_ink/game/Items/Software/*.java
$JAVACPATH com/planet_ink/game/Items/Weapons/*.java
$JAVACPATH com/planet_ink/game/Libraries/interfaces/*.java
$JAVACPATH com/planet_ink/game/Libraries/layouts/*.java
$JAVACPATH com/planet_ink/game/Libraries/mcppkgs/*.java
$JAVACPATH com/planet_ink/game/Locales/interfaces/*.java
$JAVACPATH com/planet_ink/game/MOBS/interfaces/*.java
$JAVACPATH com/planet_ink/game/Races/interfaces/*.java
$JAVACPATH com/planet_ink/game/WebMacros/grinder/*.java
$JAVACPATH com/planet_ink/game/WebMacros/interfaces/*.java
$JAVACPATH com/planet_ink/coffee_web/converters/*.java
$JAVACPATH com/planet_ink/coffee_web/http/*.java
$JAVACPATH com/planet_ink/coffee_web/interfaces/*.java
$JAVACPATH com/planet_ink/coffee_web/server/*.java
$JAVACPATH com/planet_ink/coffee_web/servlets/*.java
$JAVACPATH com/planet_ink/coffee_web/util/*.java
$JAVACPATH com/planet_ink/siplet/applet/*.java
$JAVACPATH com/planet_ink/siplet/support/*.java

else

echo "What!? No main compile for you...maybe you just wanted to compile the docs? Or change your MUD's name...any ways...I'll make this more intuitive in the future!"
fi
echo "Writing your new mudUNIX.sh..."
rm mudUNIX.sh
echo "#You should really input a name for your MUD below...." >> mudUNIX.sh
echo "#Before using this on a UNIX machine, you must 'chmod 755 mudUNIX.sh' to make this file executable by the UNIX machine" >> mudUNIX.sh
echo "#FYI - the nohup command will make a nohup.out file, usually in the CofferMud (directory where you start this from) directory - it will log the server messages..." >> mudUNIX.sh
echo "" >> mudUNIX.sh
echo "nohup $Java_Home/bin/java -classpath \".:./lib/js.jar:./lib/jzlib.jar\" -Xms129m -Xmx256m com.planet_ink.game.application.MUD \"$MUDname\" &" >> mudUNIX.sh
chmod 755 mudUNIX.sh
echo "Your mudUNIX.sh script has been written."
echo "To change memory or other settings, you must MANUALLY edit mudUNIX.sh after every time you run this script."
echo "Would you like to start your mud up? [y/n]"
read startyeanay

if [ $startyeanay = y ] ; then
sleep 1
echo "OK your MUD is now starting..."
sh mudUNIX.sh

else
echo "Alright, MUD not starting now, if you wish to start it later just do sh mudUNIX.sh"
echo "Hope you enjoy CoffeeMud!"
fi
