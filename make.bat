REM Make sure you are building with java v1.6 or higher
REM SET Java_Home="C:\Program Files\Java\jdk1.6.0_01"
set CLASSPATH=.;%Java_Home%\lib\dt.jar;%Java_Home%\lib\tools.jar;.\lib\js.jar;.\lib\jzlib.jar
SET JAVACPATH="%Java_Home%\bin\javac" -g -nowarn -deprecation

IF "%1" == "docs" GOTO :DOCS

%JAVACPATH% com/planet_ink/fakedb/*.java
%JAVACPATH% com/planet_ink/game/Abilities/*.java
%JAVACPATH% com/planet_ink/game/application/*.java
%JAVACPATH% com/planet_ink/game/Areas/*.java
%JAVACPATH% com/planet_ink/game/Behaviors/*.java
%JAVACPATH% com/planet_ink/game/CharClasses/*.java
%JAVACPATH% com/planet_ink/game/Commands/*.java
%JAVACPATH% com/planet_ink/game/Common/*.java
%JAVACPATH% com/planet_ink/game/core/*.java
%JAVACPATH% com/planet_ink/game/core/collections/*.java
%JAVACPATH% com/planet_ink/game/core/database/*.java
%JAVACPATH% com/planet_ink/game/core/exceptions/*.java
%JAVACPATH% com/planet_ink/game/core/interfaces/*.java
%JAVACPATH% com/planet_ink/game/core/intermud/*.java
%JAVACPATH% com/planet_ink/game/core/intermud/cm1/*.java
%JAVACPATH% com/planet_ink/game/core/intermud/cm1/commands/*.java
%JAVACPATH% com/planet_ink/game/core/intermud/i3/*.java
%JAVACPATH% com/planet_ink/game/core/intermud/i3/net/*.java
%JAVACPATH% com/planet_ink/game/core/intermud/i3/packets/*.java
%JAVACPATH% com/planet_ink/game/core/intermud/i3/persist/*.java
%JAVACPATH% com/planet_ink/game/core/intermud/i3/server/*.java
%JAVACPATH% com/planet_ink/game/core/intermud/imc2/*.java
%JAVACPATH% com/planet_ink/game/core/smtp/*.java
%JAVACPATH% com/planet_ink/game/core/threads/*.java
%JAVACPATH% com/planet_ink/game/Exits/*.java
%JAVACPATH% com/planet_ink/game/Libraries/*.java
%JAVACPATH% com/planet_ink/game/Locales/*.java
%JAVACPATH% com/planet_ink/game/MOBS/*.java
%JAVACPATH% com/planet_ink/game/Races/*.java
%JAVACPATH% com/planet_ink/game/WebMacros/*.java
%JAVACPATH% com/planet_ink/game/Abilities/Archon/*.java
%JAVACPATH% com/planet_ink/game/Abilities/Common/*.java
%JAVACPATH% com/planet_ink/game/Abilities/Diseases/*.java
%JAVACPATH% com/planet_ink/game/Abilities/Druid/*.java
%JAVACPATH% com/planet_ink/game/Abilities/Fighter/*.java
%JAVACPATH% com/planet_ink/game/Abilities/interfaces/*.java
%JAVACPATH% com/planet_ink/game/Abilities/Languages/*.java
%JAVACPATH% com/planet_ink/game/Abilities/Misc/*.java
%JAVACPATH% com/planet_ink/game/Abilities/Paladin/*.java
%JAVACPATH% com/planet_ink/game/Abilities/Poisons/*.java
%JAVACPATH% com/planet_ink/game/Abilities/Prayers/*.java
%JAVACPATH% com/planet_ink/game/Abilities/Properties/*.java
%JAVACPATH% com/planet_ink/game/Abilities/Ranger/*.java
%JAVACPATH% com/planet_ink/game/Abilities/Skills/*.java
%JAVACPATH% com/planet_ink/game/Abilities/Songs/*.java
%JAVACPATH% com/planet_ink/game/Abilities/Specializations/*.java
%JAVACPATH% com/planet_ink/game/Abilities/Spells/*.java
%JAVACPATH% com/planet_ink/game/Abilities/SuperPowers/*.java
%JAVACPATH% com/planet_ink/game/Abilities/Tech/*.java
%JAVACPATH% com/planet_ink/game/Abilities/Thief/*.java
%JAVACPATH% com/planet_ink/game/Abilities/Traps/*.java
%JAVACPATH% com/planet_ink/game/Areas/interfaces/*.java
%JAVACPATH% com/planet_ink/game/Behaviors/interfaces/*.java
%JAVACPATH% com/planet_ink/game/CharClasses/interfaces/*.java
%JAVACPATH% com/planet_ink/game/Commands/interfaces/*.java
%JAVACPATH% com/planet_ink/game/Common/interfaces/*.java
%JAVACPATH% com/planet_ink/game/Exits/interfaces/*.java
%JAVACPATH% com/planet_ink/game/Items/Armor/*.java
%JAVACPATH% com/planet_ink/game/Items/Basic/*.java
%JAVACPATH% com/planet_ink/game/Items/ClanItems/*.java
%JAVACPATH% com/planet_ink/game/Items/interfaces/*.java
%JAVACPATH% com/planet_ink/game/Items/MiscMagic/*.java
%JAVACPATH% com/planet_ink/game/Items/BasicTech/*.java
%JAVACPATH% com/planet_ink/game/Items/CompTech/*.java
%JAVACPATH% com/planet_ink/game/Items/Software/*.java
%JAVACPATH% com/planet_ink/game/Items/Weapons/*.java
%JAVACPATH% com/planet_ink/game/Libraries/interfaces/*.java
%JAVACPATH% com/planet_ink/game/Libraries/layouts/*.java
%JAVACPATH% com/planet_ink/game/Libraries/mcppkgs/*.java
%JAVACPATH% com/planet_ink/game/Locales/interfaces/*.java
%JAVACPATH% com/planet_ink/game/MOBS/interfaces/*.java
%JAVACPATH% com/planet_ink/game/Races/interfaces/*.java
%JAVACPATH% com/planet_ink/game/WebMacros/grinder/*.java
%JAVACPATH% com/planet_ink/game/WebMacros/interfaces/*.java
%JAVACPATH% com/planet_ink/coffee_web/converters/*.java
%JAVACPATH% com/planet_ink/coffee_web/http/*.java
%JAVACPATH% com/planet_ink/coffee_web/interfaces/*.java
%JAVACPATH% com/planet_ink/coffee_web/server/*.java
%JAVACPATH% com/planet_ink/coffee_web/servlets/*.java
%JAVACPATH% com/planet_ink/coffee_web/util/*.java
%JAVACPATH% com/planet_ink/siplet/applet/*.java
%JAVACPATH% com/planet_ink/siplet/support/*.java

GOTO :FINISH

:DOCS

"%Java_Home%\bin\javadoc" -d .\docs -J-Xmx1024m -subpackages com.planet_ink.game

:FINISH
