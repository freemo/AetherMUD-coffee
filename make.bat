REM Make sure you are building with java v1.6 or higher
REM SET Java_Home="C:\Program Files\Java\jdk1.6.0_01"
set CLASSPATH=.;%Java_Home%\lib\dt.jar;%Java_Home%\lib\tools.jar;.\lib\js.jar;.\lib\jzlib.jar
SET JAVACPATH="%Java_Home%\bin\javac" -g -nowarn -deprecation

IF "%1" == "docs" GOTO :DOCS

%JAVACPATH% com/syncleus/aethermud/fakedb/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/*.java
%JAVACPATH% com/syncleus/aethermud/game/application/*.java
%JAVACPATH% com/syncleus/aethermud/game/Areas/*.java
%JAVACPATH% com/syncleus/aethermud/game/Behaviors/*.java
%JAVACPATH% com/syncleus/aethermud/game/CharClasses/*.java
%JAVACPATH% com/syncleus/aethermud/game/Commands/*.java
%JAVACPATH% com/syncleus/aethermud/game/Common/*.java
%JAVACPATH% com/syncleus/aethermud/game/core/*.java
%JAVACPATH% com/syncleus/aethermud/game/core/collections/*.java
%JAVACPATH% com/syncleus/aethermud/game/core/database/*.java
%JAVACPATH% com/syncleus/aethermud/game/core/exceptions/*.java
%JAVACPATH% com/syncleus/aethermud/game/core/interfaces/*.java
%JAVACPATH% com/syncleus/aethermud/game/core/intermud/*.java
%JAVACPATH% com/syncleus/aethermud/game/core/intermud/cm1/*.java
%JAVACPATH% com/syncleus/aethermud/game/core/intermud/cm1/commands/*.java
%JAVACPATH% com/syncleus/aethermud/game/core/intermud/i3/*.java
%JAVACPATH% com/syncleus/aethermud/game/core/intermud/i3/net/*.java
%JAVACPATH% com/syncleus/aethermud/game/core/intermud/i3/packets/*.java
%JAVACPATH% com/syncleus/aethermud/game/core/intermud/i3/persist/*.java
%JAVACPATH% com/syncleus/aethermud/game/core/intermud/i3/server/*.java
%JAVACPATH% com/syncleus/aethermud/game/core/intermud/imc2/*.java
%JAVACPATH% com/syncleus/aethermud/game/core/smtp/*.java
%JAVACPATH% com/syncleus/aethermud/game/core/threads/*.java
%JAVACPATH% com/syncleus/aethermud/game/Exits/*.java
%JAVACPATH% com/syncleus/aethermud/game/Libraries/*.java
%JAVACPATH% com/syncleus/aethermud/game/Locales/*.java
%JAVACPATH% com/syncleus/aethermud/game/MOBS/*.java
%JAVACPATH% com/syncleus/aethermud/game/Races/*.java
%JAVACPATH% com/syncleus/aethermud/game/WebMacros/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/Archon/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/Common/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/Diseases/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/Druid/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/Fighter/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/interfaces/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/Languages/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/Misc/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/Paladin/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/Poisons/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/Prayers/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/Properties/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/Ranger/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/Skills/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/Songs/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/Specializations/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/Spells/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/SuperPowers/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/Tech/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/Thief/*.java
%JAVACPATH% com/syncleus/aethermud/game/Abilities/Traps/*.java
%JAVACPATH% com/syncleus/aethermud/game/Areas/interfaces/*.java
%JAVACPATH% com/syncleus/aethermud/game/Behaviors/interfaces/*.java
%JAVACPATH% com/syncleus/aethermud/game/CharClasses/interfaces/*.java
%JAVACPATH% com/syncleus/aethermud/game/Commands/interfaces/*.java
%JAVACPATH% com/syncleus/aethermud/game/Common/interfaces/*.java
%JAVACPATH% com/syncleus/aethermud/game/Exits/interfaces/*.java
%JAVACPATH% com/syncleus/aethermud/game/Items/Armor/*.java
%JAVACPATH% com/syncleus/aethermud/game/Items/Basic/*.java
%JAVACPATH% com/syncleus/aethermud/game/Items/ClanItems/*.java
%JAVACPATH% com/syncleus/aethermud/game/Items/interfaces/*.java
%JAVACPATH% com/syncleus/aethermud/game/Items/MiscMagic/*.java
%JAVACPATH% com/syncleus/aethermud/game/Items/BasicTech/*.java
%JAVACPATH% com/syncleus/aethermud/game/Items/CompTech/*.java
%JAVACPATH% com/syncleus/aethermud/game/Items/Software/*.java
%JAVACPATH% com/syncleus/aethermud/game/Items/Weapons/*.java
%JAVACPATH% com/syncleus/aethermud/game/Libraries/interfaces/*.java
%JAVACPATH% com/syncleus/aethermud/game/Libraries/layouts/*.java
%JAVACPATH% com/syncleus/aethermud/game/Libraries/mcppkgs/*.java
%JAVACPATH% com/syncleus/aethermud/game/Locales/interfaces/*.java
%JAVACPATH% com/syncleus/aethermud/game/MOBS/interfaces/*.java
%JAVACPATH% com/syncleus/aethermud/game/Races/interfaces/*.java
%JAVACPATH% com/syncleus/aethermud/game/WebMacros/grinder/*.java
%JAVACPATH% com/syncleus/aethermud/game/WebMacros/interfaces/*.java
%JAVACPATH% com/syncleus/aethermud/web/converters/*.java
%JAVACPATH% com/syncleus/aethermud/web/http/*.java
%JAVACPATH% com/syncleus/aethermud/web/interfaces/*.java
%JAVACPATH% com/syncleus/aethermud/web/server/*.java
%JAVACPATH% com/syncleus/aethermud/web/servlets/*.java
%JAVACPATH% com/syncleus/aethermud/web/util/*.java
%JAVACPATH% com/syncleus/aethermud/siplet/applet/*.java
%JAVACPATH% com/syncleus/aethermud/siplet/support/*.java

GOTO :FINISH

:DOCS

"%Java_Home%\bin\javadoc" -d .\docs -J-Xmx1024m -subpackages com.syncleus.aethermud.game

:FINISH
