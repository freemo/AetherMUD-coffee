# 6.0.0
    * Migrated to maven and removed ANT.
    * Removed all uses of "CoffeeMUD" and replaced with "AetherMUD".
# Build 5.9.6 Release ??/??/2017
   *. Sections in the default coffeemud.ini file were moved around.  Don't be alarmed. :)
   1. Hunger and Thirst system can now be tweeked.  See coffeemud.ini file
   2. Entering SHUTDOWN RESTART HARD will now attempt to run a restart.sh/restart.bat.
   3. GMODIFY now supports changing the CLASS of objects, including rooms.
   4. RESET RELEVEL tool added to refactor an areas level range.
   5. New skills: Studying, Tagging, Organizing, Dissertating
   6. Areas can now be unloaded.  And then loaded back again.  See UNLOAD and LOAD.
   7. New FORMULA_s in the coffeemud.ini for total and individual combat experience.
   8. Movement = weight/3 now required to push/pull, and the moves are consumed.

# Build 5.9.5 Release 04/10/2017
   *. Database Schema Updated for CMGRAC.  See the Installation Guide about running DBUpgrade.
   1. New chants: Phosphorescence, Land Legs, BurrowSpeak, Call Companions, Animal Companion
   2. Anarchy clan govt type now has Clan Experience spell as an instant benefit.
   3. Pirates no longer regain lost limbs after death.
   4. New resource: DIRT.  Mass Grave now populates its room with the stuff.
   5. GModify now supports query-able stat "CLASSTYPE"
   6. New Skills: Wilderness Sounds, Hardiness, Stonecunning, Keenvision, Burrow Hide
   7. New Skills: Cultural Adaptation, Fast Slinging, Sling Proficiency, Diligent Studying
   8. New Prayers: Taint of Evil, Disown
   9. Centaur gets Hardiness, Drow adds Light Sensitivity and Taint of Evil, 
  10. Duergar get -10% XP, Dwarfs get Stonecunning, Elfs Keenvision
  11. Races can now have a XP adjustment, see Archon's Guide
  12. New Language: WormSpeak
  13. Gnomes get BurrowSpeak and Burrow Hide, Goblins +5% XP, Half Elfs Cultural Adaptation
  14. Racial cultural abilities now support level and auto-gain/qualify.
  15. New Spells: Darkness Globe, Untraceable, Astral Step
  16. Achievement type GOTITEM now supports NUM argument.
  17. New Properties: Prop_OpenCommand, Bad Reputation, Slow Learner
  18. ZapperMask now supports IFSTAT, CLASSTYPE, SUBNAME, WEAPONTYPE, WEAPONCLASS, WEAPONAMMO
  19. 14 new Proficiency Skills, for overriding Class Restrictions
  20. Halflings get Fast Slinging, Sling Proficiency, Humans get Diligent Studying
  21. New Skills: Devour Corpse, Tail Swipe, Long Breath, Eagle Eyes, Vicious Blow, Mind Suck
  22. Ogres got Bad Reputation, Gnoll gets Devour Corpse, Svirfneblin gets Untraceable
  23. Githyanki get Astral Step, Lizard Men get Tail Swipe, Long Breath, MindFlayer get Mind Suck
  24. Aarakocran gets Eagle Eyes, Merfolk get Land Legs, Orc gets Vicious Blow
  25. New Standard Race: Pixie
  26. (Internal) Char Stats now supports crit damage bonuses for weapons and magic
  27. Races and Char Class can be disabled, finally, from the ini/mudgrinder.  See DISABLE=
  28. Internally disabled Races/Char Classes can be enabled from the ini/mudgrinder.  See ENABLE= 
  29. Some subtle new GenAbility features: uninvoke script trigger, uninvoking effects
  30. Minstrels now get pan pipes as standard outfit
  31. Trap_CaveIn allows overriding of the messaging.
  32. ShipWright now supports building and demolishing doors on ships.
  33. Reset genmixedracebuilds will now rebuild all the generic mixed races you have.
  34. Archons can now do CREATE MIXEDRACE raceid1 raceid2
  35. New common skills: Composting, Tanning, Fish Lore, Baiting
  36. Builders can now use LIST GENSTATS <class or item> to view named generic stats. 
  37. Universal Starting Items can now be distinguished by race.
  38. New GenExit/GenPortal tags, and new NOWEAR flag for items in MUDPercolator
  39. Some of the one-off Racial Categories have been consolidated.
  40. CommonSpeaker behavior now allows a language argument.
  41. New standard Locales: LongRoad, LongerRoad, LongestRoad - cost more movement
  42. New Achievement triggers: playerborn, births, racebirth, playerbornparent
  43. Babies born as players no longer get bonuses per se -- only through acheivements. 
  44. New INI entry: PROWESSOPTIONS, for controlling how combat/armor prowess is displayed.
  45. New Archon Skill: Infect, for giving mobs/players random diseases.
  46. Unpaid property taxes now generate both tell and email warnings.
  47. INVENTORY command now has LONG argument, similar to LONGLOOK.
  48. New Skills: Boulder Throwing 
  49. GMCP changes: char.login, char.items.contents, room.mobiles, room.items.*, room.players
  50. Sailor behavior now supports aggro mask and relative level checks.
  51. You can now create unlinked exits in the MUDGrinder.
# Build 5.9.4 Released 08/03/2016
   1. Qualify now shows language limits
   2. New Prop_NoTeleport exceptions
   3. ALIAS commands now supports "noecho" prefix to prevent echos.
   4. New property: Prop_HereEnabler, like the other Prop_Here* and Prop_*Enabler props.
   5. New Archon ability: Matrix Possess
   6. Common Skills now support INFO argument to get info on recipe items.
   7. New Area type: SubThinInstance, for making thin instance clones of existing areas.
   8. EQUIP LONG now shows all items being worn and tattoos, even at other layers.
   9. CoffeeMud now supports Twitter.  See the end of the Installation Guide.
  10. Prop_StatTrainer has new parameter: BASEVALUE, for making it more flexible.
  11. Prop_StatAdjuster has new parameter: ADJMAX, for giving mobs more POWAH!
  12. New Spell: Spell_Planeshift, for going to other planes of existence
  13. Prop_AbsorbDamage has several new parameters, and can now effect rooms/areas
  14. Clerics and Druids also get planar travelling spells: Plane Walking and Planar Travel
  15. New racial ability for horse-like races: Buck
  16. Achievements command has lots of new options
  17. The Skills Report on the Statistics MUDGrinder page can now group by name, type, domain.
  18. New Achievement trigger, FACTIONS (with an S), for counting groups of factions.
  19. Several new REMORTRETAIN options for expanding or fine tuning the remort process.
  20. New Dragonbreath parameters and types
  21. Races and Clan Governments now support parameters for granted abilities.
  22. New internal mob saving throws (more damage mitigators/enhancers), including weapon types.
  23. Bunch of new races for the outer planes.
  24. New Thief Skills: Superstitious, Rope Swing, Improved Boarding, Locate Alcohol, Hold Your Liquor
  25. Papermaking now supports containers, for gift bags, paper sacks, etc.
  26. MORGUE/DEATH/BODY/START rooms support levels and masks now.
  27. The Swim skill, when used as a racial ability, does not have a usage cost any more.
  28. Lassos and Nets no longer do physical damage
  29. Internal DB: CMCLAS field in CMCHAR table expanded to 250 chars. An optional DB upgrade!
  30. New ZapperMasks: +ISHOME -ISHOME, for masking mobs who are away from their areas
  31. Prop_*Adjuster now supports multiplying values, see help on Prop_HaveAdjuster
  32. Concierge behavior can now create portals if you want.
  33. Stolen property (property taken from a home or owned ship) cannot be sold to shopkeepers.
  34. New config option and Command: NOBATTLESPAM, for getting only damage summaries.
  35. New auto-diseases: Sea Sickness, Scurvy
  36. Sailing Ship now have TENDER command to extend gangplanks between peaceful ships
  37. New Skills: Sea Legs, Ride the Rigging, Belay, Buried Treasure, Wenching
  38. New Skills: Treasure Map, Walk the Plank, Sea Mapping, Plunder, Sea Charting
  39. New Skills: Dead Reckoning, Sea Navigation, Scuttle, Fence Loot, Pet Spy
  40. New Skills: Pirate Familiar, Pub Contacts, Combat Repairs, Foul Weather Sailing
  41. New Skills: Pay Off, Pet Steal, Merchant Flag, Pieces of Eight, Articles
  42. New Skills: Ramming Speed, Smuggler's Hold, Hide Ship, Intercept Ship, Await Ship
  43. New Skills: Mast Shot, Warning Shot, Silent Running, Water Tactics, Trawling
  44. New Skills: Diving, Siege Weapon Specialization, Deep Breath, Avoid Currents
  45. If it's not already obvious, new char class in beta: Pirate
  46. Internal: Web Server upgraded to rev 2.4 (SSL fixes and Protocol switching)
  47. Mundane STAT command now supports HEALTH, RESISTS, ATTRIBUTES
  48. New Spell: Lighthouse   
  49. DAYSCLANOVERTHROW separated from DAYSCLANDEATH -- see INI file.
  50. New Skills: Naval Tactics, Salvaging, Sea Maneuvers, Crows Nest, Hire Crewmember
  51. New Skills: Morse Code, Stowaway
  52. Can now LIST AREATYPES
  53. Individual diseases can now be disabled from the DISABLE= entry in the INI file.
  54. New Commoner class in beta: Sailor  
  55. Patroller now works with Sailing Ships.
  56. New CONFIG option: TELNET-GA, and CONFIG command can now be used to toggle them all.
  57. New Expertises: Ranged Sailing, Reduced Sailing, Power Sailing, Extended Sailing
  58. New Achievements for Pirate, Sailor, and Mer
# Build 5.9.3 Released 05/08/2016
   1. Follower behavior now has a few more options.
   2. STAT [MOBNAME] will now give full editor stats also, and 
   3. STAT [ITEMNAME/AREANAME/EXITNAME/ROOMNAME] now gives editor stats without editing
   4. New Locales: WaterSurfaceColumn and UnderWaterColumnGrid, and Salt water varieties.
   5. LIST TIMEZONES can now show all your areas grouped by common calendar.
   6. Smelting recipes are now editable from MG and CL.
   7. Prop_OpenPassword can now have a language qualifier
   8. LIST AREA SHOPS will now show local shop inventories with prices
   9. Archon QUESTS [QUEST NAME] and LIST QUESTWINNERS can now show all winners of quests.
  10. New CHANNEL flag: NOLANGUAGE, to turn off foreign languages when using the channel.
  11. LIST QUESTNAMES can show a map between quest names (ids) and display names.
  12. LIST ABILITYDOMAINS will show a list of, well, ability domains
  13. ID on private property will now show size and features, as will VIEW of titles on shopkeepers.
  14. Scripting trigger matches P syntax now truly is Precise (noteable bug fix).
  15. Conquerable will now transfer private property to conqueror, see new OWNERSHIP flag.
  16. Prop_Lot*ForSale and Prop_RoomPlus now support grid-connecting the walls of rooms for looping.
  17. New Scriptable commands: MPOLOADSHOP and MPMLOADSHOP, and funcs: SHOPHAS, SHOPITEM, NUMITEMSSHOP
  18. New Achievements trigger: CLASSLEVELSGAINED -- several more achievements that use it also.
  19. MudChat now supports talking spontaneously on a schedule, and responses may contain scriptable funcs.
  20. Trailto now accepts full tracking flag set as arguments, as well as other new tweek settings
  22. Rules for mixing races can now be tweeked a little.  See coffeemud.ini RACEMIXING.
  21. Can now add/edit items directly to shopkeeper inventory from MUDGrinder!!!
  22. New item type: GenSiegeWeapon, for combat between ships
  23. New item type: GenGrapples, for creating a portal between two ship bridges
  24. New behavior: Sailor, for allowing mobs to sail and fight on the big ships
  25. New Common Skill: Siegecraft, for making siege weapons for ship combat
  25. Sailing Ship combat is in beta, see help SAILING and help SHIP COMBAT
  26. New disable flag: FOODROT for disabling the automatic raw food rottability
# Build 5.9.2 Released 02/09/2016
   1. Apprentice now gets bonus common skill.
   2. Prop_ItemBinder can now bind to a group.
   3. POSSESS can now target a room or area
   4. Can now edit recipes of Construction, Masonry, Excavation, Landscaping, etc from CL or MG
   5. Save command will now prompt for confirmation with delta message.  
   6. New channel flag: ACCOUNTOOCNOADMIN - for a more nuanced Account Name-based channel
   7. Metacraft can now craft everything from specific skills.  See help.
   8. New Chants: Bloody Water, Find Driftwood, Filter Water, Aquatic Pass, Sense Water, Summon Coral, Flood
   9. New Chants: Underwater Action, Water Hammer, Drown, Call Mate, Summon School, High Tide, Land Lungs
  10. New Chants: Reef Walking, Capsize, Feeding Frenzy, Calm Seas, Summon Jellyfish, Flippers, Waterguard
  11. New Chants: Sift Wrecks, Favorable Winds, Tide Moon, Tidal Wave, Predict Tides, Tsunami, Whirlpool
  12. New races: SmallFish, Angelfish, Merfolk, Selkie, Swordfish, Dolphin, Seal, Walrus, Whale
  13. New language: Aquan
  14. New Druid Shapeshift forms: Fish and Sea Mammal (I bet you saw this coming)
  15. Archon AutoInvoke now saves settings.  Default no-invoke list now in lists.ini
  16. CoffeeTable statistics will now track activity by Area also.
  17. Ship Title copies can now be purchased and traded, just like land titles.
  18. CATALOG command can now be used to list categories.
  19. Anchors Down on sailing ships now prevents WaterCurrent effects (duh!)
  20. Sailing Ships now get area weather messages and affects
  21. Default autoreaction shopkeepers will now adjust prices based on faction.
  22. Legal system now supports punishment caps on repeat crimes.
  23. New Locale type: Whirlpool.
  24. Autoreaction ranges adjusted, and aggressive tagged to only affect near levels
  25. New Aggressive/MobileAgressive flag: CHECKLEVEL, see help on those behaviors.
  26. Archon wands/staffs now have GAIN ability to grant spells
  27. Yet more Druid stuff: Sea Lore, Summon Chum, and Water Cover
  28. New Druid sub-class: Mer (skill-only at this point, but mostly tested and working)
  29. Golem races can now see in the dark.
  30. Shopkeepers will now include the size of bulk sales in devalue rates. 
  31. GMODIFY change parms ADDABILITY, ADDEFFECT, ADDBEHAVIOR now support parms in parenthesis ().
      e.g. CHANGE=ADDABILITY=Prop_ReqSafePet(MSG=no!)
  32. Any item can now appear 'compressed' in room desc.  See item editors.
  33. Any container can have directly accessible contents.   See item editors.
  34. Races now support natural immunities (mostly for disease).  MG and CL editor support added.
  35. Added the rest of the Tech resource types .. mind your items with custom resource defs!
  36. Some new tech power generator types, and new fuelless engine type and options. Also, Light Switch!
  37. Internal: "ShipTech" Package renamed to "CompTech" to reflect reality that not all components are for ships.
  38. MOTD can now review previous news, and EMAIL BOX and MOTD PREVIOUS supports message limits
  39. Total Minutes played now recorded along with time for Player Leveling Stats
  40. ColorSet can be used to set any channel color now.
  41. Unattackable mobs no longer count in area statistics.
  42. Space Ship Shield generator done, mudgrinder and cl editor included.
  43. New resources added: salt and spice
# Build 5.9.1 Released 01/04/2016
   1. "Who accounts", used by an archon, can quickly list the account name of each player online.
   2. New Achievement event "GOTITEM", and new rewards (see new stats below and achievements.ini)
   3. New Player stats for Bonus char creation points, common skill limits
   4. New Account stats for Bonus char per account, chars online, char creation points, common skill limits
   5. New Property: Prop_ItemBinder, for binding items to char, acct, clan. How did that get missed all these years?
   6. New version of Siplet (the web client).  Now uses WebSockets for streaming joy.
   7. New disease: Sleepwalking.  Very rare, but can be caught through extreme fatigue.
   8. New Beastmaster / Ranger skill: Animal Bonding.
   9. Masonry/Construction now have their own recipe files.  Not much you can do with them, but still better than code.
  10. Artisans now get bonus xp for crafting items, with some minor anti-botting measures in.
  11. Due to a strange accident, lots of Wizard-class bugs fixed.  It's still an unavailable class.
  12. New Property: Prop_LotForSale, like Prop_LotsForSale, but you only have to buy the property once.
  13. New Property: Prop_RoomPlusForSale, like Prop_RoomForSale, but you are allowed to expand it for free.
  14. Prop_ReqCapacity now has a new argument to interact with Prop_LotForSale, and special circumstances for it.
  15. New property building skills: Landscaping, Excavation, and Unknown/Unfinished skills: Welding, Irrigation
  16. Lots of bugs fixed.
# Build 5.9.0 Released 12/20/2015
   1. Web Server upgraded to 2.3
   2. coffeemud.ini entry FLEE has new (blank) option to basically disable fleeing and break a bunch of skills.
   3. Scriptable: When reading script vars, scopes local->quest bound->global will be checked in order.
   4. New DEBUG flag -- INPUT, for debugging ALL user input in string form
   5. New Property: Prop_RestrictSkills - for limiting skill use by location, or as a curse affect.
   6. New package application: VFShell, a command line tool for access full coffeemud file system without the mud.
   7. Basic MCP support added to CoffeeMud.  ZMUD Editor package supported. mcppkgs directory for others.
   8. New DISABLE flag: HYGEINE, for disabling the natural hygenic system -- the magic version left intact.
   9. New support for a permanent ip blocking system.  See BLACKLISTFILE in coffeemud.ini
  10. Ability-crimes in the legal system now supports skill types and skill domains.  See laws.ini.
  11. Scriptable EXECMSG/CNCLMSG now supports specified major/minor codes.  See Scriptable Guide.
  12. STAT command now supports LEVELTIMES to view when a player leveled up from the command line.
  13. Tweek to prompts for SimpleMU, plus some prompt behavior settings.  See coffeemud.ini PROMPTBEHAVIOR.
  14. Can now temp add/remove misc DISABLE flags from Control Panel "Switches" screen.
  15. PUT can now target non-container items. Mentioned because such a low-lvl change requires watching.
  16. Internal/Scripting -- the COMMANDFAIL message type implemented for most basic player commands.
  17. Items can now have slots -- see help Prop_ItemSlot and Prop_ItemSlotFiller for more information.
  18. New DISABLE flag: ANSIPROMPT for character creation tweaking.
  19. New Achievements system: see /resources/achievements.txt, MUDGrinder, or the GameBuildersGuide for more info.
  20. New Misc Property: AutoStack -- for automatically stacking/packaging identical items.
  21. New command for muds using the account system: SWITCH, for quickly switching between other players on the same acct.
  22. The Deviations command now shows mob money deviations.
  23. Mundane STAT command now supports experience point stats. 
  24. Made some of the weather effects spam slightly more frequent, and added a msg for cold and heat.
  25. Mana/Health/Move recovery formulas tweaked with caps.  See coffeemud.ini
  26. Lots of refactoring done.  Sorry about that.  This is a big part of the reason for the minor version number change.
  27. Most of the CONFIG toggle commands now have optional OFF parameter to force them off.
  28. When Lay Traps/Set Snare is used to set the same trap on the same object, and that trap is sprung, it will reset it.
  29. Added some color to the Group command so health/mana/moves can be monitored at a glance.
  30. New Necromancer prayers: Mass Grave, Designation.
  31. Web Servers can now be stopped/started at runtime with CREATE WEBSERVER [name] and DESTROY WEBSERVER [name]
  32. Apprentices can no longer gain experience beyond that necessary for their next level.
  33. Prop_Resistance (and the other resistance props) now supports ability types and domains.
  34. New player command: Account, for seeing account info
  35. New command: Remort.  Yes, CoffeeMud now has a more traditional remort system. See coffeemud.ini, and the Achievement tie-in.
  36. Accounts now support tattoos at that level.  All the tattoo properties have been adjusted accordingly.
  37. New Zapper Mask: ACCCHIEVES for filtering account achievements.  TATTOO is for player achievements.
  38. Scriptable now has HASACCTATTOO and MPACCTATTOO for adjusting and checking account-level tattoos.
  39. Note: "titles.txt" has been renamed to "titles.ini".  The system will still load the old name, but be aware!
  40. New Zapper Mask: ANYCLASSLEVEL for filtering players by the levels in previous classes.
  41. You can now turn off post office mail forwarding with FORWARD STOP.
  42. Import/Export now supports the Catalog.
  43. Programmer's Guide updated with information about the Database Tables.
  44. New Archon Skill: Accuse -- for creating on-the-fly criminals out of anyone anywhere.
  45. Leiges no longer have to be online to gain experience from vassals.
  46. TaxiBehavior/Concierge now has more features!  See their help entries.
  47. Mages and Shaman now qualify for Alchemy 
# Build 5.8.5 Released 12/23/2014
   1. Sailing ship creation and distribution now actually works.
   2. Sailing Ships are now only as fast as their item's "ability" score. (1 means 1 move per tick, etc)
   3. Sailing Ships now properly count as Property for the purposes of certain spells and skills.
   4. Shipwright can retitle/redescribe sailing ship rooms now.
   5. Improvement to context-usage when targeting items, mobs, or exits, e.g.: look item.3
   6. MUDGrinder item adder/editor will now only show options appropriate to the area theme -- keep this in mind! 
   7. There is now a mundane/player version of the STAT command for -- wait for it -- viewing character stats
   8. Several electronic/tech/space editor fields finally added to MUDGrinder item editor.
   9. ShopKeeper view now shows more useful information about ships for sale.
  10. New resource: aluminum
# Build 5.8.4 Released 12/01/2014
   *. Database Schema Updated for CMBKLG and CMCLIT.  See the Installation Guide about running DBUpgrade.
   1. Channels now save their back messages to the database for perpetual enjoyment. See DISABLE,CHANNELBACKLOG in coffeemud.ini.
   2. Bit geeky, but exposed the Cross Class skill analysis and Recovery Rates analysis from MUDGrinder (char class and control panel respec)
   3. Scroll Scribing is now available to low level mages and arcanists for limited scroll making, and new transcribing feature.
   4. Bankers can now handle deposited containers as a single deposited object.  Players can now have their safety deposit box type thingys.
   5. Weaving has gotten a little recipe love; not much, but a little.
   6. New Delver chant: Magma Cannon
   7. Broken limbs are now a thing, but not as severe as amputation.  Falling and taking damage will cause it. Bandaging and healing can help.
   8. New Charlatan skills: Break A Leg, Monologue, Cast Blocking, Strike The Set, Upstage, Exit Stage Left, Curtain Call, Ad Lib
   9. New Expertise: Acting
  10. New Alterer spells: Polymorph Object, Magic Bullet, Flame Arrow, Shape Object, Keen Edge, Fabricate
  11. New Illusionist spells: Color Spray, Disguise Undead, Disguise Self, Disguise Other, Simulacrum, Invisibility Sphere
  12. New Abjurer spell: Anti-Plant Shell
  13. New Evoker spells: Purge Invisibility, Helping Hand, Produce Flame, Forceful Hand, Pocket
  14. New Conjurer spells: Watchful Hound, Insect Plague
  15. New Cleric spells: Death Guard, Death Knell, Sense Injury, Anti Undead Field, Hold Undead, Incite the Dead
  16. More Cleric spells: Mercy, Awaken, Dream Feast, Corpse Walk, True Resurrection, Devourer Curse, Snake Staff
  17. Even More Cleric spells: Protection from Outsiders, Unholy Portent, Joyous Rapture, Protection from Curses, Bloatbomb
  16. New Oracle spells: Sense Faithful, Speak with Dead
  17. New Purist spells: Judgement, Piety Curse, Sanctimonious
  18. New Shaman spells: Refuge
  19. New Assassin skills: Two Dagger Fighting, Cut Throat, Dagger Defense, Deep Cut, Dagger Specialization
  20. Tons of new animal noise languages added, and tacked onto various races as racial abilities. New MudChat possibilities for animals!
  21. New Ranger skills: Camouflage, Repel Vermin (chant), Delay Poison, Sense Snares and Pits, Set Snare, Air Wall
  22. More Ranger skills: Speak with Animals, Hunters Endurance, Bow Specialization, Fierce Companions
  23. Trap and Bombs can now be utilized as useable Abilities -- weird, but there ya go.
  24. New Paladin skills/prayers: Resurrect Mount, Hammer of Light, Abiding Aura, Paladin's Mount, Command Horse, Heal Mount, Holy Strike
  25. New Fighter skills: Toughness, Toughness II, Toughness III, Desperate Moves
  26. Beyond new skills, mage specialists and some clerics have had their spell lists tinkered with to make them a bit better
  27. STAT command now allows CHARSTATS for some number table happiness.
  28. AUTOINVOKE command now allows masks for multiple skills
  29. All Fighter classes now get a nice little XP bonus for winning duels (and not between two chars on same account!).
  30. Although somewhat Beta-ish, there are now GenSailingShip items for large multi-room sailing ships.  See Archon's Guide.
  31. New Locales: Sea Port, for Sailing Ship sales locations, and WoodenDeck for non-resource outdoor room, OceanGrid, and OceanThinGrid.
# Build 5.8.3 Release 10/27/2014
   *. Database Schema Updated for CMCHAR.CMDESC and CMCHAR.CMCHID.  See the Installation Guide about running DBUpgrade.
   1. (FakeDB): DBUpgrade is now application independent, and can handle big tables better.  See installation guide for new recommended usage.
   2. Generic Character Classes now support starting money values. (Standard support requires code).
   3. Socials action cost can now be controlled and tweeked from ini file/control panel.
   4. MiniWebServer renamed to CoffeeWebServer -- so many path/name changes! Also upgraded to 2.2.
   5. Clan Position-based effects/abilities implemented. Standard Powers also still apply.
   6. Random Generator now supports post-processing tags in XML, MERGE parameters and NODETYPE_[dir] tags. See example.xml re-organization.
   7. Portals can now be entered using the CLIMB skill, and their put, mount, and dismount strings are modifiable.
   8. New Property: Prop_Climbable - use for making rooms, exits, or portals require climbing (and benefit from ropes and ladders).
   9. New Guide in guides/randomareas.html.  Also, random races are easier to build now.
  10. New Assassin skill: Scratch, for doing very very little damage without entering combat.
  11. Emoter behavior's "inroom" parameter changed to comma delimited -- it just wasn't working the other way.
  12. Scriptable GET_PROG is now functional on container items, as it will match the item being retreived, not the container.
  13. Default XP chart (lists.ini) modified to make low levels faster, and level off around 5k per level.
  14. WanderHomeLater property expanded into more usefulness -- about 8 new parameters added.
  15. FireBuilding now gained by all classes at level 1.
  16. QUESTS command, used by an admin, can target other players to get info about them.  See AHELP QUESTS for more info on this.
  17. New property Prop_Uncampable -- prevents a mob or room from respawning if non-admin players are present.
  18. VIEW will now notify users if the armor they are interested in is too large or too small for them to wear.
  19. Player description 256 char limit is now gone.  New limit is 128k, expandable in code.
  20. QUALIFY command now has CRAFTING and NON-CRAFTING optional parameters.
  21. RESET command can now be used to reset player/account passwords using RESET PASSWORD <name> syntax, assuming both cmdplayers and reset security.
  22. STAT [player name] and MODIFY PLAYER [name] will now show common account information.  It was so annoying not having this.
  23. Account Manager in MUDGrinder now has a page to create massive numbers of accounts.  Done for a teacher who emailed me once.
  24. Races now include an isRideable flag to help inform mob class choice during character creation.  Make rideable players!
  25. To go along with the above: Centaur is now a playable race.
  26. New Ranger chant: Resuscitate Companion
  27. New GENERATEable themes in /resources/randreas/example.xml: forest, jungle, desert, swamp, mountains, snow, plains
  28. New Behavior: TaxiBehavior.  Also, Concierge has more optional arguments.
  29. Geeky MOBPROG change: can now Read variables in the global scope from script in a local scope when variable is not defined locally. No writing!
  30. Item container types now have their "defaultsClosed" property modifiable, instead of implied.
  31. Lock command will now close the thing first, if it's not closed.
  32. Expanded GMCP support to include char.items package, support for mal-formed core.hello, room.wrongdir, and a few others.
  33. New Armor-Item type: StdThinArmor and GenThinArmor -- see Archon's Guide for more information.
  34. Jewelmaking can now make jewelry that Requires piercings from BodyPierce.  Works off new class GenEarring.
  35. ScrimShaw got some serious love, becoming only common skill that can make doppleganger items at high levels.
  36. Prop_Doppleganger now supports ASMATERIAL argument to alter the way stats are calculated.
  37. New expertise: Shadow -- for lower cost sneaking and hiding
  38. Wimpy hit point score goes up and down with level hit point gains, maintaining roughly same percentage between levels.
  39. New Misc effect: Loyalty, for making mobs return to and follow players.  Prayer "Feed the Dead" and others will use it to protect companions.
  40. New Beastmaster chant: Give Life -- similar to feed the dead, leading perhaps to an animal follower role for that class.
  41. New Archon command: EVERY, similar to AT, but allows applying a command to multiple rooms, and multiple targets in each room.
  42. (Internal) Adjusted caps on spell effects based on level is now about 20 min, and about an hour after max adjustments. (Mostly affects Archons)
# Build 5.8.2 Release 6/13/2014
   0. Database Schema Updated! Run DBUpgrade, OR manually alter table CMCHCL to add CMCLSTS (see appropriate schema file)
   1. CM1Server has "FILE" command now for directory browsing/manipulation
   2. GenAbility additions: can now alter default duration of effect skills, and whether the damage/healing/extra casts happen periodically.
   3. Added an example of a "Channeling" mechanic to spells, though it's pointless with the default combat system. See GenAbility and Spell_ChannelingMissiles/
   4. Coffeemud now has a Char/Account approval system. Use expiration system w/trialdays of 0. Archons are notified of expired/unapproved users.
   5. Clan-Player level PVP ranking support in clans.  CLANKILLS and CLANPVPKILLS commands added.  ClanData WebMacro capable of the same.
   6. Clan forums and web sites supported -- see CLANWEBSITES and CLANFORUMDATA in coffeemud.ini file.  Still thinking about how clan leaders could modify it.
   7. Fighter classes now have armor & shield specializations, plus new expertises (Focuses) to allow wearing/wielding of slightly higher level items
   8. Players and Accounts now track PVPs, Rooms explored, mins played, xp & qp gained, and quests completed. Main web page or TOP command to see winners.
   9. Embedded WebServer upgraded to 2.1
  10. Regen rate formulas are now in INI/control panel. New formula allows faster recovery, esp at higher levels.
  11. PROMPT command now allows you to include current and max action points per tick %p and %P
  12. A simple fantasy name generator is now included for account and character names.
  13. New combat system: TURNBASED -- rather simple and beta-y for now.  Comments/Suggestions welcome.
  14. New disable flag: ALL_AGEING to disable the ageing system
  15. Script commands requiring location specifiers (mpat, mpgoto, mptransfer, mpechoat, etc) can find mobile mobs better using new
      syntax with the AT (@) sign.  For example, mptransfer $n hassan@midgaard#3001
  16. New Fighter skill: Weapon Sharpening (for a small damage bonus)
  17. Containers of all sorts now expose modifiable "open delay ticks".
  18. Localization has been amended, and partially redone.  See the FAQ for more info. The new scheme should make CM much more localizeable in the future.
  19. GMODIFY now supports change=destroy=true to obliterate objects matched!
  20. Scriptable MPOLOAD, MPMLOAD, and MPOLOADROOM can now source from a autogen-formatted xml file (such as those for the GENERATE) command.
  21. Random generation system (GENERATE command) now supports random races! See example.xml for more details.
  22. Follower behavior has new flag: NOFOLLOWERS
  23. Obesity can now also be cured by simple starvation.
  24. Can now disable factions that have already been spread amongst the mobs using DISABLE= entry in coffeemud.ini file.
   *. --- FYI Tech Stuff ---
  25. Ship Component Tech install skill (AstroEngineering) done-ish... like all tech, it's subject to change without notice.
  26. Area editor for planets now includes space positioning editors, including relative positioning calculators.
  27. List command now includes SPACE, SPACESHIPS, SPACESHIPAREAS, MOONS, PLANETS, and STARS 
  28. Create/Modify/Destroy can now be used to create and alter objects in outer space.
  29. Several new "shortcut" item and area types for space: moon, gasgiant, star, dwarfstar, massivestar, planet, moonlet, asteroid, etc.
  30. MoneyChanger behavior can now discount currency by its distant origin (farther planet is from origin of currency, less its worth).
# Build 5.8.1 Release 3/14/2014
   0. **** Database Schema Changes Were Made **** Make sure you follow the Installation Guide to upgrade your database. 
   1. QuestManager "STEP"ping feature now supports "STEP AUTO" to have steps proceed based on duration instead of MOBPROG triggers.
   2. Areas and Rooms now support "atmosphere" to designate what players have available for breathing.
   3. Rooms "climate" is now an editable feature, usually inherited from the area Climate, but modifiable at will.
   4. Races now designate what sort of resource they can breathe, which means GenRaces have a new field you can edit.
   5. New Archon command: JCONSOLE, for playing around with javascript commands (and accessing your muds running systems!!)
   6. New Forum features: All Forums Search, and better threading for all searches.
   7. Can now disable CharStats (attribute) display
   8. New WebMacros: IsDisabled, CatalogCatNext
   9. New Artisan skills: Master Drilling, M. Farming, M. Fishing, M. Foraging, M. Mining, M. Shearing, M. Chopping, M. Butchering
  10. More New Artisan Skills: Master Cooking, M. FoodPrep, M. Baking, M. Distilling -- make more than one dish at a time!
  11. Split command will now double as GET [AMOUNT] from [BUNDLE] (kept getting requests to add a split command, though GET worked).
  12. And yet more Artisan Goodness: Animal Husbandry -- lots of race baby naming and aging-system tweaking behind this simple funness.
  13. Crafting Skill owners can now LIST ALL to see what recipes are coming down the road.
  14. Catalog items and mobs can now be flagged in categories (does not change the global unique name rule). 
  15. Catalog items and mobs can now be accessed via the VFS at /resources/catalog/mobs/.. and /resources/catalog/items/.. as CMARE files.
  16. Some new multiclassing system options:  NO-BASE, NO-SUBONLY, NO-[X], and NO-GRP-[X] -- see the coffeemud.ini
  17. SHELL command now (finally) supports diff (thanks google code!).  
  18. Similar to #15, the entire world map, w/ cmare files, is in VFS at /resources/map/... along with editable properties files.
  19. (Tech) numerous gun and shield types created -- most of them templates for a murder-motel-type game
  20. PRIVATE setting in coffeemud.ini expanded to allow moving players and area objects between hosts.
  21. You can now YELL in a direction, which expands by 1 the number of rooms you are heard in, and which way.
  22. IMPORT now accepts zip files containing *.cmare files.
  23. Say command: if a player speaks same language as person being addressed is speaking, they will try to speak it.  
      Also, untargeted speaking, when only one other person is in the room, will assume to be addressing them. New DISABLE=AUTOLANGUAGE
  24. (Tech) New TriCorder item (portable computer).  Also, internally, class names and locations are shifting as tech takes shape.
  25. New GENERATEable area target in /resources/randreas/example.xml: random_dungeon (rand level, theme, aggro, size, etc)
  26. For those using account system, can now register for an account from ANY page in the forum system (really an oversight)
  26. New GENERATEable themes in /resources/randreas/example.xml: town, orcs, trolls, and goblins.  Also, lots of work done to fill it all out.
  27. New DISABLE flags: IMC2, I3 -- alternative means to turn them off.  At runtime, it puts those systems into idle.
  28. Another DISABLE flag: NEWCHARACTERS -- used for account-based muds to turn off (or not) char creation when NEWPLAYERS (accts) are.
  29: Disguise now checks badnameslist
  30. IndoorWaterSurface now generates IndoorUnderWaterGrid - would normally shy from this as a backward compatibility issue, but its an oversight.
  31. New locales: IndoorUnderWaterGrid, IndoorUnderWaterThinGrid, and IndoorWaterThinSurface classes
  32. Can now export/import entire accounts
  33. Quests will now save/restore their suspended status.
  34. Characters logged out for over 1 mud year will not have their age advance more than 1 year.  See SLOW_AGEING DISABLE flag to revert to previous alg.
  35. Archons can now use MODIFY command to set one/all skill proficiencies to a set value.
  36. Prop_Doppleganger has new parms to help you fine-tune who the mob considers for part of duplication.
  37. Emote now has the ability to add special codes to alter how your emote is seen in context.
# Build 5.8.0.1 Release 7/22/2013
  Bug fix release -- removed Go-Ahead chars for tinyfugue
# Build 5.8.0 Release 7/21/2013
   1. CoffeeMud and WebServer are now integrated -- this means a much faster and better web service all around.
   ^^^Check out the altered WebServer guide, and make sure you integrate the pub. ini, admin.ini, and common.ini if you customized them.
   2. Character Classes now use formulas for mana, movement, and hit point gains/losses.
   3. Character Class race/stat requirements can now be modified separately from qualifications mask.  Race qualifications shown in help now.
   4. Changed the wysiwyg forum editor to one that's also chrome compatible (nicEdit)
   5. Generic Abilities will now import/export when races that use them export
   6. (Internal) New factions now default into /resources/factions -- legacy locations will work too though.
   7. New Channel flag (ACCOUNTOOC) to force account names instead of channel names into messages.
   8. (Internal) randomdata.xml now at /resources/randareas/example.xml -- update your random area files and random Areas!
   9. MUD Logging to separate files now has more options, such as rolling logs based on entries or bytes, etc.
  10. Web Forum system now supports participation by empty Accounts (if using the account system), and web subscribe/unsubscribe
  11. Consolidated Session and Tick threads: check new coffeemud.ini file for new MAXWORKERTHREADS and MINWORKERTHREADS. Major drop in thread usage!
  12. MUDGrinder Control Panel now supports Debug flags, and the whole flag business has been put into its own "Tab"
  13. Web Server now supports @for?@ @next@ loops! See the web server guide for usage, or checkout an example in control.cmvp
  14. MUDGrinder Control Panel "Channels" editor is now muchfffffff friendlier and easy to use.
  15. CoffeeMud now supports MSDP as specified at the tintin sourceforge web site
  16. Siplet now also supports MSDP, in an undocumented, testable sorta way.  Usage \MSDP ([JSON OBJECT]).  Example: \MSDP {"LIST":"LISTS"}
  17. The "sessions" archon command has been replaced with "list sessions".  On the bright side, all of the LIST [X] parms now have their own help entries.
  18. JavaScript embedded in Scriptable scripts can now access all the Scriptable commands and functions from within JavaScript.  See the Scriptable Guide.
  19. CoffeeMud now has a working computer console, working power source for the computer, and working example software.  New command: TYPE for interacting.
      (No documentation, MUDGrinder support, and limited editor supp for Tech so far.  To use computer, create elecpanel, put battery inside, then activate computer)
  20. Bards songs/dances/plays now show an ambiance during performances (depending on the viewers ability to hear, of course).
  21. MUDGrinder Factions Manager now allows pre-loaded faction toggling.
  22. The changelog.txt has been reformatted, and is slightly easier to read now!
  23. CoffeeMud now supports GMCP according to CMUD and Aardwolf specs. SHELL .edit command supports GMCP editor -- I'll look for other places for it later.
  24. Siplet now also supports GMCP, in an undocumented, testable sorta way.  Usage \GMCP [PACKAGE] ([JSON OBJECT]).  Example: \GMCP core.supports.set ["char 1"]
  25. Scriptable now has an "arrive_prog" to handle situations with mobile mobs.
  26. (Internal) the entire login, char creation/account creation process is now asynchronous
  27. Thiefs have new "small" pit traps that, unlike normal pit traps, only trigger when you move through a room instead of when you enter.
  28. New INI entry 'DEFAULTPARENTAREA' for automatically giving new areas (or existing ones) that use the global clock to a default parent area.
  29. CoffeeMud will now convert MSP tags to MXP SOUND tags if the session supports both, allowing us to hear sounds in CMUD/ZMUD once again.
  30. New commands for moving in space ships: foreward, portside, aft, above, below, and starboard. Compass versions included of course.
  31. When entering "?" in the Affects/Behaviors command line modifier, it will now highlight the appropriate abilities/behaviors. It's awesome!
  32. New Property: Prop_ImproveGather, for modifying the yield of gathering skills based on masks
# Build 5.7.10 Release 2/17/2013 (* DB CHANGE!) (** Thanks to TGMUD for publishing their changelogs!!!)
   0. Database Schema Updated! Run DBUpgrade, OR create table CMCHCL (see appropriate schema file), then:
   INSERT INTO CMCHCL VALUES (SELECT USERID, CMCLAN, CMCLRO from CMCHAR where CMCLAN != ''); // lastly:
   ALTER TABLE CMCHAR DROP COLUMN CMCLAN; ALTER TABLE CMCHAR DROP COLUMN CMCLRO;   
   1. (Minor) ResourceOverride is now an ActiveTicker for better control over its behavior.  Also, room types bug fixed.
   2. New INI entries for whitelisting over ip/login/newplayer blocks: WHITELISTIPSCONN, WHITELISTLOGINS, WHITELISTIPSNEWPLAYERS
   3. Special GET case: Can now get parts of a bundle from a container -- get 2 oak from wagon will break a 2# bundle from inside in a wagon.
   4. Carpentry skills list underwent a little makeover.
   5. I3 LISTEN/SILENCE can be used by i3 admins to test out other channels... if you turn one on, be sure to turn it off before you leave!
   6. (internal) XMLManager optomized for More Speed!
   7. Missing INI entry (WRNMSGS) now added and documented.
   8. New Property (Prop_RoomList) -- more lightweight way to keep rooms from going dark at night (pun intended).
   9. New Skills: Urban Tactics, Pressure Points, Hideout
  10. New Clan Trophies: Most Members, and Highest Median Member Level
  11. Pregnancy will fail if either participant is exhausted or below 1/2 movement.  A message was added so the players know.
  12. Arcanists can LOOK at wand charges at level 30.
  13. Common Skills now support "STOP" to cancel.
  14. Troll regeneration moved from the MOB to the Race
  15. InstantDeath behavior now allows a zappermask, also New Property (Prop_InstantDeath) -- more lightweight than the behavior.
  16. New race: Slime, and racial category to go with it
  17. New spells: Acid Spray, Mass Hold
  18. Some common skills can now be done sitting now.
  19. Bandaging also restores a few hit points.
  20. CombatAbilities and its derivatives (Bardness/Clericness/etc..) will adjust base char stats of mobs to reflect class, unless NOSTATS in the parms.
  21. Players Teaching Players is now more beneficial for both.  Also, (internally), Teach is now StdMOB managed, as it should be.
  22. Potions/Pills/Scrolls/Powders now support parameters for spells on them.
  23. Scriptable: get_prog now applies to getting items from containers on the ground.
  24. Druids can now shapeshift into lower versions of their existing shapeshift forms.
  25. Char Creation Stat picker now has Random Roll option.
  26. POSE can now be turned off with NOPOSE
  27. The POLL system now supports 1 vote per account for those using the account system.
  28. Prop_TrashCan supports a DELAY parameter
  29. New formulas for PVP combat -- INI/Control Panel support.  The defaults basically remove level fudging.
  30. (Internal) New experiments in Fully Async sessions continues apace.  Converted Logout and Quit.
  31. New prayer: Retribution (neutral Blade Barrier for Missionaries & Shaman)
  32. POUR command can now be used to activate a potion on an item (for example, enchant weapon potion) (like thrown powders)
  33. About two dozen thief skills have been finished and assigned to various classes. Skill_EscapeBonds renamed to Struggle Bonds.
  34. Potions and Pills are more scaled to their levels.
  35. Normally mobile mobs will walk by hidden/invis exits now (unless they can see hidden, etc..)
  36. Death room (DEATH) coffeemud.ini entry now supports MORGUE to keep players where they died.
  37. (Internal) Expertises have been optimized such that players only store the highest version.  Functionally identical, but with new editor expression.
  38. Clans and Governments now support categories, scripts, and visibility flags, and mobs may belong to more than one, if setup properly. 
  39. New INI entries: MAXCLANCATS and PUBLICCLANCATS for controlling player access to clan gvts by category
  40. Player passwords can now be optionally hashed into the DB -- see HASHPASSWORDS in coffeemud.ini
  41. If you've been having MXP problems with ZMUD/CMUD or Siplet, those problems are over.
  42. New private Government type (Profession) for class clans.  Make sure you add CLASSCLAN to the new MAXCLANCATS ini entry.
  43. PlayerClass (the class used when you disable CLASSES) now uses a programmatically defined skill tree.
# Build 5.7.9 Release 09/10/2012
   1. FakeDB now supports getResultSetMetaData, making room for some sort of sql browser feature
   2. CoffeeMud (actually fakedb) is now build compatible with JDK 1.7
   3. Fixed a nasty bug in CMParms -- you do not want 5.7.8!
# Build 5.7.8 Release 09/09/2012
   1. Fixed the statistics bug
   2. Fixed a bug with variables in Scriptable MPTITLE command
   3. Fixed numerous prayers that did not respect elemental immunity/resistance
   4. Arcanist Spellcrafted abilities persist over reboots
   5. Children now track their number of high-level parents to which benefits are tied to, and can repick their stats and class.
# Build 5.7.7 Release 08/31/2012
   1. MUDGrinder area editor has easier to manage parent/child areas & subops -- the old multiselect thing was unbearable.
   2. GenBook now uses the Message Maker (from the SHELL command) to add book chapters.
   3. (Internal) Database connections can now be kept alive with pings.  See new coffeemud.ini entry: DBPINGINTERVALMINS
   4. Most crafting skills now support a LEARN command to gain the recipes to certain found items.
   5. Players can now spend points to create their new character stats, if you let them.  See coffeemud.ini entry: STARTSTAT
   6. You can now define the training point costs to stat changes with STATSCOST in your coffeemud.ini file
   7. Change the items all characters get at creation time with STARTINGITEMS in your coffeemud.ini file
   8. Can now COPY areas
   9. LIST command now supports AREAS for admins -- choose your own columns or sortings
  10. IMPORTANT: previously, mobs would never wander OUT of a room where an admin is -- now they also won't wander INTO a room where an admin is
  11. Construction and Masonry skills now support building stairs to create new floors -- rooms must still be purchased.
  12. Children now inheret the languages of their parents
  13. Players can now sit from a sleeping position (not sure how that got missed).
  14. SHOWDAMAGE flag in your coffeemud.ini file now supports SOURCE and TARGET options
  15. CLANVOTE option, long supported in the codebase, is now exposed in the coffeemud.ini file
  16. (Internal) System can now cache commonly used world searches.  See NOCACHE in coffeemud.ini to turn it off!
  17. Bugs fixed in conventional manner.
# Build 5.7.6 Release 04/23/2012
   1. New coffeemud.ini entries: COMMONCOST, SKILLCOST, LANGCOST.  Old entries, COMMONTRAINCOST, etc.. RETIRED.
   2. (Internal) fakedb now supports dup key detection for insert statements, DBUpgrade now more forgiving of dup keys.
   3. New command: AUTOMAP, for toggling AWARERANGE
   4. Import command now Sortof supports at least one type of .wld/.zon/.mob/.obj format areas.
   5. StdAutoGenInstance - Can now insert scriptable variables into autogen variable definitions, and more level variables. 
   6. New Conjurer Spell: Flaming Sword
   7. CoffeeMud now supports background colors through native ^ escape codes.  See programmer's guide/ahelp colors for more.
   8. Also support for fore/background 256 color support through native ^ escape codes.  As above, check the guides.
   9. Stuff: Shutdown command can now be put on an auto-remind timer, new sample js script FindRawMaterial, new internal property "Truce"
  10. MODIFY AREA Can now modify areas outside of those areas, as well as change their state by adding PASSIVE, ACTIVE, etc.
  11. NPC def hit point formula lowered slightly, some slight skill adjustments to decrease mage armor & increase thief dmg.
  12. New Property: Prop_AddDamage (no, really this time)
  13. New disposition: Unattackable -- effectively deprecates Prop_SafePet
# Build 5.7.5 Release 11/05/2011
   1. CoffeeMud now supports html emails, after a fashion
   2. New command: DIG, for digging holes and putting stuff in them.  Gem Digging now uses GDIG as its command word.
   3. You can now manage MOTD/NEWS using LIST NEWS and CREATE/MODIFY NEWS.  New News is now in journal SYSTEM_NEWS
   4. New Abjurer spell: Kinetic Bubble
   5. First addition to stock raw resource list in forever (dragonscales, white gold, etc..)
   6. GMODIFY can now alter room/locale-type objects as well (global room title changes anyone?)
   7. MUDGrinder supports editing/deleting multiple rooms using cntrl-click.
   8. New Jester skills: Satire, Quick Change, Center of Attention
   9. New Oracle Prayers: Divine Guidance, Prophecy, Seekers Prayer, Soul Peering, Lower Law, Omnipresence, Above the Law
  10. New Diviner Spells: Solve Maze, Group Status, Detect Weaknesses, Prying Eye, Telepathy, Natural Communion, Arms Length
      Spying Stone, Hear Thoughts, Know Fate, Divining Eye, Spotters Orders, Find Directions, Death Warning
  11. Players can now use COMMANDS CLEAR to clear out their personal command queue.
  12. Siplet should be back to 100%, or as close as the new tech will allow.
  13. New Archon skill: Injure -- for creating instant injuries (useful for testing, or a nasty punishment).
# Build 5.7.4 Release 8/08/2011
   1. New coffeemud.ini entries: MINSESSIONTHREADS, MAXSESSIONTHREADS for managing player I/O thread usage.
   2. Converted Siplet Applet in the default web site to an ajax-based http macro.  Damn you browser security for killing my applet!
   3. New Fighter skill: Subdue
   4. New Command: DUEL -- for safe, healthy, player PVP duels, even on non-pvp or optional pvp systems.
   5. Immunities property supports legal immunity now
   6. Player corpses are now saved across reboots (if they haven't decayed)
   7. Rhino has been upgraded to 1.7R3
# Build 5.7.3 Release 7/14/2011
   1. **** Database Schema Changes Were Made **** Make sure you follow the Installation Guide to upgrade your database. 
   2. NAWS fully implemented now.. most charts and lists will now resize themselves to fit.  LINEWRAP now applies mostly to defaults.
   3. New Property: Prop_Socials -- for making socials contextually available (with items, in rooms, etc)
   4. Generic Languages can now be created online and within the MUDGrinder.  See Builders Guide.
   5. Generic Crafting Skills can now be created online and within the MUDGrinder. See Builders Guide.  
   6. All-Qualifys list can now be modified from MUDGrinder and Command Line. See Builders Guide.
   7. Channels definition in coffeemud.ini file can now include override color codes for each channel.
   8. Numerous random bugs fixed, with special thanks to frugality200
# Build 5.7.2 Release 3/31/2011
   1. New Area Type: StdAutoGenInstance, for creating random area instances based on random area generator
   2. Player WHERE now displays a more informative table
   3. AREAS command now has sorting options.
   4. LISTS.INI changed to include weather, sun movement, and hit/damage messages.  
   5. Each entry in each list inside LISTS.INI can now hold numerous variable entries for variety now! See the file for examples.
   6. MUDGrinder File Manager now has resizeable text area, and will let you choose which filesystem to save your file to (local/vfs/both)
   7. Forums are now compatible with SMTP mailing lists.. including very simple threading.  If performance is good, why not move Yahoo groups to one?  
   8. When using account system, account creation proceeds directly to new character creation.
   9. I3/Intermud3 now supports finger (through WHOIS), and will auto-restart if pinging the router is no longer responsive after an hour.
  10. MUDGrinder now has a Ability Components editor
  11. Can now delete a journal entirely from the MUDGrinder; also Clan Journals should clear out correctly when Clans are deleted.
  12. Can now DISABLE the Java-coded races or classes from the DISABLE entry in the INI file.  Quick way to drop the stock classes or races!
  13. Skill Recipes now have components, on MUDGrinder and Command Line
# Build 5.7.1 Release 3/12/2011
   1. Bug Fix worth mentioning: custom mxp images restored, fakedb typo
   2. AutoMapping system available -- see AWARERANGE in INI file and Control Panel
   3. Can now create/destroy/list debugflag and disableflag (same as debug= disable= in coffeemud.ini file -- changes are TEMPORARY!)
   4. Channel Commands by themselves now automatically show last X messages where X<=5
   5. Channel LAST (previous) messages now append an ellapsed time since the message was sent.
# Build 5.7.0 Release 3/10/2011
   1. Can now enter I3 RESTART or IMC2 RESTART to reconnect to the intermuds
   2. Language: Undercommon
   3. Property/skill: TemporaryAffects (for timer-dependent behaviors/properties)
   4. Property: Prop_LanguageSpeaker, Prop_ModFaction
   5. INI entry: WIZLISTMASK -- WIZLIST command modified to support it.
   6. MUDGrinder Map editor now has a Scripts editor button on the map screen.
   7. Internal: Lots of refactoring done. Lots.
   8. INI entries: OBJSPERTHREAD, TICKTIME, MILLISPERMUDHOUR -- control time and thread spawning!
   9. Web & SMTP server INI files have some tweeking options for performance (MAXTHREADS/REQUESTTIMEOUTMINS)
  10. New Scriptable Commands: MPPOSSESS, MPSETINTERNAL, MPSPEAK
  11. MERGE command can now be used to merge different database instances of areas/rooms/etc..
  12. Ability INI entries: MAXCOMMONSKILLS, MAXCRAFTINGSKILLS, MAXNONCRAFTINGSKILLS, MAXLANGUAGES, WALKCOST, RUNCOST
  13. Added an all-skills configuration file: /resources/skills/allqualifylist
  14. IP service (CM1Server) -- a new, more formal-syntax mud interface -- see guides/CM1ServerGuide.html
  15. DISABLE flag: LOGOUTS (keeps characters in game world even if they log out)
  16. Another playable race: Duergar
  17. Apprentice can now play only level 1 -- we had too many people playing all the way to 5 and regretting it.
  18. IMPORT now supports areatype parameter for converting to different types.
  19. MODIFY [TYPE] [NAME] [STAT] [VALUE] command syntax has had its first expansion since CM 1.0!
  20. CLAN ROLE IDs renumbered (internal).  System will attempt to fix most values, but enchanters/treasurers will be confused.
  21. ACT_PROG P clarified in the Scriptable document.
  22. New DISABLE flags in coffeemud.ini: DARKNIGHTS, DARKWEATHER
  23. EMAILREQ (player emails) can now be set to disabled in coffeemud.ini file.
  24. FakeDB now supports PreparedStatements, and Apache Derby support is now complete. 
  25. Skill Recipes: Skill Component entries can now be substituted for ingredient amount in skill recipes. Make items from other items!
  26. WebMacro NumPlayers supports ALL, TOTAL, TOTALCACHED, and ACCOUNTS.  See Web Guide for more info.
  27. New command: POSE, with new filter INI entry for it: POSEFILTER.  DISABLEd in coffeemud.ini file by default.
  28. Old accounts will now be purged when they run out of characters. See INI entry: ACCOUNTPURGE
  29. New Scriptable Functions: ISSPEAKING, ISCONTENTS, WORNON
  30. Clan types (governments) are now configurable with clangovernments.xml, create, modify, destroy, mudgrinder
  31. Racial Effects are now implemented as inexpensively as possible.  See genraces in command line & mudgrinder.
  32. Clans (via their governments) now support custom help entries, clan effects, and clan abilities.
  33. COMPARE command can now be used to compare container capacity
  34. Can now create exception lists for DEFCMDTIME and DEFABLETIME in coffeemud.ini file/control panel.
  35. Commands LOAD/UNLOAD now can be done manually by players for ranged weapons requiring ammunition.
  36. Affect now shows disposition, and time remaining on spells.
  37. Gathering skills are a 1/3 more stingy, and Crafting skills sort their lists by level now.
  38. Foreign CharSet localization support (char names, lists.ini (file loading/saving in general), web file editor, misc problems...)
# Build 5.6.2 Release 4/25/2010 * Bug Fix build only
# Build 5.6.1 Release 4/22/2010 * Bug Fix build only
# Build 5.6.0 Release 4/18/2010  * Database Schema Changed -- See Installation Guide
   1. New Property (From 5.x snippets): Prop_IceBox (thx Robert!)
   2. CharStats can now be customized with ADDCHARSTAT_ in coffeemud.ini file.
   3. New Commands (From 4.x snippets): Experience, Wealth (thx again Robert)
   4. RawMaterial can now be customized with ADDMATERIAL_ in coffeemud.ini file.
   5. Wearable can now be customized with ADDWEARLOC_ in coffeemud.ini file.
   6. Only Artisan's learn the Master common skills now.  Other classes have their common skill levels lowered.
   7. New language: Sign Language
   8. New ZapperMask(s): -CHANCE
   9. New Property(s): PresenceReaction (for new faction reaction system, etc.)
  10. New Mage Spell(s): Awe Other, Shrink Mouth (-- it's back! where did it go, anyway?)
  11. Factions now have new mob modification system similar to affects/behaviors: reactions
  11. New automated MOB reaction system -- see coffeemud.ini under REACTION, and the guide under FACTIONS for more info.
  12. Factions now have lots of new triggers: KILL, BRIBE, TALK, MUDCHAT, ARRESTED
  13. Faction triggers support parameters; see one of the new faction.ini files for details
  14. New Prayer for Healers: Divine Constitution
  15. STAT commands now supports: FACTIONS, ROOMSEXPLORED, AREASEXPLORED, WORLDEXPLORED
  16. PROMPT now supports new codes to display all manner of personal variables.
  17. Abilities/GenAbilities now support delays between allowed invocations
  18. New Thief Skill: Turf War!
  19. Run-time compiled java from LOAD command will now display any compile errors to user.
  20. Socials now support various item-type target options.
  21. New Multi-Character account system -- see COMMONACCOUNTSYSTEM in ini file.
  a. Can now LIST, CREATE, DESTROY, and MODIFY ACCOUNTS
  b. For transition muds: char selection IMPORT command lets players import their old chars to new accounts.
  c. EXPIRATION system works with accounts, when common account system is enabled. 
  22. New command: LOGOUT/LOGOFF -- exits back to login prompt/char selection
  23. New INI entry: EXVIEW, for selecting types of exit views for the players
  24. XP Bonuses for killing HL targets will only apply if player/mob close to highest level players level.
  25. New Forum system -- see FORUMJOURNALS entry in the coffeemud.ini file
  26. BIG fakedb upgrade: indexing, multi-keys, ORDER BY, LIKE, COUNT, complex WHERE conditions, optomizations. ** Real DB is still better.
  27. Raw material resources can now have effects (for eat/drink/whatever)
  28. CL Editor for weapons/armor will now automatically rebalance damage/value/etc whenever level is changed.
  29. Web Catalog: can substitute a user-selectable column (damage, basegold, etc) and sort by it. 
  30. Two new NOCACHE entries in coffeemud.ini file: FILERESOURCES, CATALOG (for you memory conscious people)
  31. Internal: slight improvement to db connection management that should improve memory use.
  32. MUDSTATE variable in coffeemud.ini file has changed.  Please update your ini files!
  33. CLASSSYSTEM has been expanded with another apprentice-based set of choices.
  34. New command: MPRUN -- for Archons to run Scriptable scripts, and for use in automated testing.
  35. Help on classes and races give much of same details as found on web now.
  36. Channel HELP entries are automatically generated.
  37. CoffeeMud's bugs were all cured, at public expense.
# Build 5.5.5 Release January 3rd, 2010
   1. New INI entries: DEFAULTPLAYERFLAGS, FORMULA_* for combat formulas, RECOVERRATE
   2. New DISABLE flags: AUTODISEASE, WEATHER, WEATHERNOTIFIES, WEATHERCHANGES
   3. RESET MOBSTATS and RESET ITEMSTATS have been documented for normalizing combat stats
   3. Character classes, and mob stats have been rebalanced along pre-3.0 lines. This makes hl PVP possible. 
      Thieves and Fighters are most altered classes.  
      Mobs are an average of players now.
      Combat Fudge Factors are more strict, effective, and rational.
      Bonuses from stats are capped by the players current Max Stat value.  Period.
      Use RESET MOBSTATS WORLD command to pull your mobs into compliance.  
      This will, as a side affect, reset all area content to db baseline. 
   4. CoffeeMud now supports critical hits for spells and physical damage: high int supports former, and high dex the later.
   5. New Zappermasks: +BASECLASS, -MOOD, +MOOD
   6. New DEBUG flag: HTTPACCESS, SMTPCLIENT
   7. SMTPSERVERNAME ini entry expanded to allow SMTP auth support.
   8. Siplet now supports multiple windows.
   9. Players can now get HELP on deities by name.
  10. Rhino (Javascript library) upgraded to 1.7R2
  11. Several new Oracle Prayers added for sensing the skills of others
  12. Behaviors ItemRefitter,ItemMender,ItemIdentifier have COST parameter now.
  13. New DISABLE flags: EQUIPSIZE, AUTOPURGE, MSSP
  14. WEAR command now supports specified wear locations.
  15. New Locales: CaveGrid, WetCaveGrid
  16. Note (coffeemud.ini): I3EMAIL and I3STATE changed to ADMINEMAIL and MUDSTATE
  17. CoffeeMud now supports MSSP request at login (not the telnet code version -- kept colliding with mccp in tintin)
  18. STAT command now supports titles.
  19 Dye now supports light and bright colorings. Various other recipes trickled in here and there.
  20. Wand magic words are now from lists.ini -- your players will need to relearn their trigger words.
  21. New Scriptable command: MPMONEY, MPHEAL
  22. Remorted chars get a single random max stat bonus point, instead of one to everything.
  23. CharClasses now support max/caps in common skills, crafting skills, non-crafting common skills, and languages.
  24. Injury text is a bit more decorated now. (Thanks gmdenna!)
  25. Many many more mxp images scrounged up, and several old ones replaces with better ones.  There is a light at the end of the tunnel!
# Build 5.5.2 Release December 12, 2008
   1. Journal Show All Messages allows batch processing now
   2. I3 will now properly respond to chan-user-req, and has a new default server in the ini
   3. trailto supports nohomes flag now.
   4. Crafting Expertises are no longer such a hideous hit on the time required
   5. Wimpy can now accept a percentage of hit points as an argument
   6. Messing up a mending now results in a partial mend. 
   7. Clarify Scroll now has a ceiling, to help balance against mages. 
   8. New Shaman Prayer: Cleanliness
   9. Replay will now show the commands you entered also.
  10. New Command Journal option: confirm
  11. New Zapper Mask: GROUPSIZE
  12. Catalog System has lots of new features -- faster, usage counters, & auto updating your map
  13. Catalog command has new parameters: scan, dbscan, overlook, dboverlook
  14. New player command: PAGEBREAK.   Also, pagebreak and linewrap added to CONFIG command.
      -- You can also enter x, quit, or exit at the pagebreak prompt to stop output.
  15. LIST LOG now streams its data for extreme speed.
  16. Players more than 10X fatigued will have a 1% chance/tick to fall under the sleep spell.
  17. New Spells: Dispel Divination, Teleport Object
  18. Players will be notified when users on FRIENDS list log off, just like logins.
  19. FieryRoom Behavior tick parms apply to damage now, instead of room destruction (other parms for that anyway).
  20. CL Player Editor now allows Quest-Scripts to be Removed manually (to fix players with bad quest scripts attached to them)
  21. MG Catalog Manager will now show "most popular" area for stuff, and allows sorting by column
  22. Common text files (intro.txt, newchar.txt, rules.txt, etc) are sent through webmacro system now -- add macros!
  23. Behavior MobReSave now saves/restores a mobs charstats.
  24. Thief Mark skill can now unmark, BodyPiercing can now "heal" piercings, 
    You can Leech yourself now, always Hide from sleeping mobs, and assassinate packs a larger punch.
  25. If a cage containing a mob is opened and left open -- the creature will now escape in 30 ticks (2 min)!
  26. New Prayer: Sense Alergies 
  27. Internal: Some time/CPU control has been implemented for skills that require scanning the whole map (like Teleport
    or Gate, etc.  This means that for large maps, players may notice those skills have gotten much "slower", though 
    in reality, it will help your mud support more people doing such things.
  28. New Disable Flags: DBERRORQUESTART, DBERRORQUE, CATALOGCACHE
  29. New LIST parameter: SCRIPTS (for finding script usages and scriptables in your world)
  30. Help money/currency/etc will show local currency.  help world currency shows all active currencies.
  31. Scriptable codes $h/$H $s/$S added to support him/her. LOG command also changed to prevent spoofing.
  32. Quests now save their authors, if the questmaker is used.
  33. Cooking will now give slightly more specific names to the final recipe (Fried Water Fowl instead of Fried Poultry, etc)
  34. Channel socials can now target Cataloged mobs (targetting other out-of-room mobs would have required a world scan)
  35. CoffeeTables STATS system will now gather statistics on quests, though some stats require mobprog "hooks" to gather.
    The templates for the questmaker have been updated.
  36. Necromancers will now be notified of the deaths of mobs nearby and of other players online.
  37. Players will now get a message if they attempt to put on armor outside their class restrictions.  Messages are in lists.ini
  38. ShopKeepers can now sell multiple-types of items.  Conflicting types are resolved when set.
  39. CommandJournals now support SCRIPT= flag to allow you to scrip entries into your journal.  An example for BUG is included.
  40. New Death/Flee consequence: ASTRAL_RES -- same as ASTRAL, but players can use ENTER [body] to self-res.
  41. New Area type: StdThinInstance.  Yep, CoffeeMud supports real-life instances now! Check out the Archon Guide for more info.
  42. CharClass ability mappings now support "maximum proficiency" values per skill.
  43. New un-qualified-for Skill: Flee -- just calls the Flee command, but can be used as a replacement of the command if set up properly.
  44. New un-qualified-for Common Skill: ScrollScribing -- inferior to the scribe skill, but ties into metacrafting!
  45. Random object generator done.  See HELP GENERATE, and play around with it.  Let me know if this is interesting in any way.
  46. EXPORT command now supports single players by name.
  47. * I changed the name of a few of the library interfaces, so make sure you follow the 
      part of the upgrade instructions regarding deleting your old "com" directory before 
      running the new build.
  48. CoffeeMud-bug values dropped 700 points in overnight debugging.
# Build 5.5.1 Released November 13, 2008 
   1. couple of bug fixes in the new features
# Build 5.5.0 Released November 9, 2008 - svn revision 2128
   1. MUD supports multiple ini files & "hosts" at boot time, see new INI entry PRIVATERESOURCES and Inst. Guide
   2. New Quest script command: SET PERSISTANCE to force quests to make numerous start attempts
   3. New Scriptable commands: MPPROMPT, MPCONFIRM, MPCHOOSE (read guide first before using!)
   4. Change INI entry: RUNWEBSERVERS, now allows you to specify the servers to boot
   5. New INI entry: STARTSTAT, to override the new player char stats "rolling" with a set value.
   6. New INI entry: CHARCREATIONSCRIPTS, to add new stuff to the character creation process
   7. New INI entry: MAXITEMSHOWN, to help beautify player home rooms with hundreds of items
   8. New DISABLE INI flags: ALLERGIES, THREADTHREAD, JOURNALTHREAD, MAPTHREAD, PLAYERTHREAD, SESSIONTHREAD, STATSTHREAD
   9. New INI entries: EXTVAR_ to add your own custom variables to a limited set of classes
  10. Internal: Several code shuffles to support the new multi-hosts stuff.
  11. New MUDGrinder Editor: Common Skill Recipes (new security code: CMDRECIPES)
  12. METACRAFT command can now have its creations be targeted.
  13. New CLAN type: FAMILY
  14. Apprentices may not go beyond level 5 (by default)
  15. Character Classes can now have a level cap specified.
  16. New Thief skills: Disassemble traps, Improved Caltrops, Slick Caltrops
  17. Factions can now have auto-applies affects/behaviors (auto-aggressive mobs based on faction, anyone?)
  18. Players now have their followers listed on the web page
  19. New resource: lists.ini, and ini entry LISTFILE for various combat, exp, and other modifiable settings.
  20. Internal: lots of new Libraries/ created out of disparate code, to support feature #1.
  21. New LIST type: helpfilerequests, for parsing help-file misses (if you are recording them).
  22. New INI entries: CHARSETINPUT, CHARSETOUTPUT for manipulating default IO char sets.
  23. In the works: random area/room/mob/item/etc generator.  GENERATE command is disabled atm. 
  24. New WebMacro ThinPlayerData created to make the Players MUDGrinder lister go faster.
  25. Food types now have BITE field to designate how many times EAT can be used against it.
  26. LOTS more skills/spells/etc have their castingQuality methods set: casting mobs just got smarter!
  27. Children will now be tattooed with their parentage.
  28. Internal: Lots of javadoc comments added.
  29. Internal: Scriptable reworked to do run-time parse caching to increase speed significantly.
  30. New Zappermask: QUESTWIN
  31. Term of old bugs has expired.  New bugs set to take office early next year.
# Build 5.4.0 Released June 21, 2008
   1. Internal: Command execution now knows some circumstances surrounding that execution.
   2. Internal: Environmental objects now sold internal MOBPROG Scripts.
   3. New Command: AUTORUN - a player will always be running around
   4. New Command: QUESTS - for listing, getting details on, and dropping saved quests
   5. WILLQUALIFY Command now accepts skill-types parameter
   6. Snoop/AS no longer functions for TELL receptions, TELL last and EMAIL reads
   7. Consider command can now be used to recommend healing for friendlies.
   8. New Command: Catalog, for managing mob/item catalog lists, and item auto-drop tables
   9. AFFECTS/AUTOAFFECTS can now be used by admins on a specified target.
  10. RESET command now supports REJUV parameter
  11. CREATE command directly supports catalog items/mobs
  12. GO command now supports OUT parameter
  13. Can now modify player clans/deities
  14. Internal: Scripting engine removed from Behavior to Common class, Scriptable is now a wrapper
  15. New Grinder Editor: Deities
  16. New Grinder Editor: Factions
  17. New Grinder Editor: CharClasses
  18. New Grinder Editor: Socials
  19. New Grinder Editor: AutoTitles
  20. New Grinder Editor: Catalog (mobs, items, item auto-drops)
  21. New Grinder Editor: Polls
  22. New Grinder Editor: Holidays
  23. New Grinder Editor: Races
  24. New Grinder Editor: GenAbilities
  25. Packages/Bundles now better preserve unique resources
  26. New Property: Prop_ItemNoRuin (purpose self explanatory :)
  27. Old Questmaker templates reworked and renamed Competitive
  28. Several new QuestMaker templates for persistant quests that players can complete at leisure.
  29. QuestManager now supports temporary Enable/Disable of quests
  30. Journal Browser now supports searches, and list ALL entries
  31. Internal: Languages now implement Language interface, StdLanguage base class
  32. GenCharClasses, Races, Abilities can now have custom HELP entries
  33. New Paladin aura: Purity
  34. New Archon skill: Stinkify
  35. Quests now have friendly display names, ability to run indefinitely, new STEP BACK and GIVE SCRIPT commands
  36. Default Prompt is now in the INI file
  37. Player image paths reimplemented to be more ZMUD friendly
  38. Expiration times are now in the INI file
  39. New Race: Svirfneblin
  40. New Script funcs: DATETIME, ISODD, QUESTSCRIPTED, QUESTROOM
  41. New Script cmds: MPSCRIPT, MPREJUV
  42. List command now supports parameters for list LOG to lessen the load
  43. Unlike prices, bugs are (hopefully) in decline.
# Build 5.3.0 Released February 22, 2007
   1. New MOB-Type(s) StdAuctioneer/GenAuctioneer to support multi-game-day auctions.
   2. New coffeemud.ini entry AUCTIONRATES to support both AUCTION command, and new Auctioneer settings with charges, costs, and limits.
   3. New Command BID for placing bids with StdAuctioneer/GenAuctioneer
   4. Command/Channel AUCTION changed a bit in its syntax: Prop_Auction has been eliminated from the codebase.
   5. Command LIST now supports a name filter in parameters
   6. By default, Areas now support a PASSIVE state -- if a player hasn't been in an area for 30 mins, all mobility and aggro behavior stops until a player re-enters the area.  This can be disabled from the coffeemud.ini DISABLE flags.  Has cut CPU usage on my own mud in HALF!
   7. When compositing a journal message or email, a TO field that starts with MASK= will now make it only visible to those who meet the ensueing zapper mask settings.
   8. Archons using the AREAS command will see areas color-coded to reflect active/passive states (neat way to see where your players have been)
   9. BUY/BID commands now supports .N format (BUY sword.3, etc)
  10. CoffeeMud has a QuestMaker now with over a dozen templates, available from MUDGrinder Quest Manager.  New META-QUESTSCRIPT language written to support it.
  11. CREATE QUEST (no parameters) now launches the command-line QuestMaker
  12. GO <exit name> is apparantly a new feature. :/
  13. New Scriptable function: INAREA, also new Scriptable variable $b
  14. ItemGenerator behavior now supports MAXDUPS parameters
  15. Conquerable now announces control point changes to players in the area
  16. Quest Scripts now supports an embedded file format -- keep EVERYTHING related to your quest in one file! Even the Scriptable behavior given as part of a quest can refer to files embedded in the quest script.
  17. The MUDGrinder File Manager got a cosmetic and interface tweak.  It's much easier to use now (I think)
  18. Command-Line journal commands can now be used to directly transfer msgs from one to another.  I forget the syntax -- check the HELP. :)
  19. A SKYSIZE (from coffeemud.ini file) of a negative number will create the appropriately sized skys/depths, but hide the exits.
  20. We now support multiple/random intro.txt files and intro.jpg files.. just put multiples of them in the same directory with different numbers before the extension (intro1.txt, intro23432.txt, intro283.jpg) and the system will select between them
  21. New internal-use Property: QuestBound.  Makes resetting a room/mob in use by a quest automatically STOP the quest.
  22. New Property: Prop_Artifact, for creating one-of-a-kind items that are carefully maintained and monitored by the system.
  23. Thief Conning now takes time to complete.
  24. Internal: web server threads are now monitored (like all others) and killed when overdue for completion.
  25. New global item-looting entry in coffeemud.ini called ITEMLOOTPOLICY -- very powerful for limiting/controlling looted items globally.
  26. New standard Exit type: UnseenWalkway
  27. Common crafting skills LIST command can now have simple word mask.  Also, Crafting skills allow player to win an XP-BONUS "lottery" if it determines that an item crafted was not found in the room after it was added.
  28. Infamous bugs found dead.  Paternity has not yet been established.
# Build 5.2.11 Released Jan 7, 2007
   1. New language user output parsing abilities.. see resources/translation*.properties
   2. New language user input parsing abilities.. see resources/parser*.properties
   3. New Chant (internal) Druidic Connection -- to support new no-kill xp stuff
   4. Druids now get xp from leveling animal followers, forcing weather calmness, and filling an area with their plants over time
   5. Aggressive behavior(s) now support MESSAGE= parm to set the mobs attack challenge
   6. New Scriptable trigger: KILL_PROG
   7. New Scriptable functions: STRCONTAINS, ISBIRTHDAY, INROOM, ISRECALL, MOOD
   8. New Scriptable command: SWITCH/ENDSWITCH
   9. Archon command ANNOUNCETO now supports HERE param
  10. New Property: Prop_Adjuster -- for modifying the stats of something directly
  11. Properties *SpellCast now supports NOUNINVOKE to make spells uninvoable
  12. ScrollCopy renamed to Memorize, and now gives xp for use by Mages
  13. PROMPT command now supports codes for tanks name, and health str
  14. REPORT ALL will show all skills/spells/stats/affects/etc
  15. Areas now support override economic flags.. designate economy at the Area/Parent Area level!
  16. Deities now support Cleric-Only blessing/curse designations
  17. StdMap display size now tied to player column-width settings
  18. New resource: cranberries
  19. GenSuperPill is now an Archon item, and Player-Changing Scriptable commands are now logged when executed
  20. Expertises now listed on WILLQUAL and on Class-Info pages on the built-in web site
  21. Can now destroy TICKS (ticking objects)
  22. CommandJournals now supports TRANSFER parameter to quickly transfer entries from one journal to another
  23. Internal: *all* zapper-masks are now compiled and cached for performance.  We no longer have string-parsed interpreted mask checks.
  24. Can now SAVE [PLAYERNAME] directly (yes, that should have been in 1.0 -- sue me)
  25. RESET ROOM/AREA commands now give an emote to players in the area/room
  26. New MUDGrinder editor: Clan Editor
  27. Internal: classes can now have their own custom mana requirements, and ability cost requirements for skills.  Not exposed to GenClasses yet.
  28. Behavior GetsAllEquipped can now be used as an auto-looting behavior
  29. Fighter get non-kill xp when their clan conquers an area that they are in at the time.
  30. Rangers now get xp from leveling animal followers
  31. Bards get exploration XP, including a bump for finding a new pub
  32. Gaolers get extra xp whenever they make someone scream
  33. Clerics can extra xp for Christening children
  34. Trappers get extra xp for selling animals to shops
  35. Druids get extra xp for freeing animals from cities(shops) into the country
  36. Really kindof a bug fix, but CMFS is now TRULY case-insensitive -- even on unix systems!
  37. A surge of new bug-destroying troops was ordered into the code.
# Build 5.2.10 Released Dec 11, 2006
   1. Internal: New Libraries: Law
   2. New Behavior: Nanny -- for easily automating watching over children, mounts, or followers for gold by the hour.
   3. New Scriptable trigger: IMASK_PROG (a mask prog that only responds to scripted mob)
   4. New Scriptable function: ISLIKE (for doing zapper mask checks on object)
   5. New Scriptable commands: MPUNLOADSCRIPT (an unload for scriptable files only); MPSTEPQUEST
   6. Scriptable change: MPGSET/MPSETting a mobs level will rescore it, changing race resizes it
   7. MudChat now has a "Quest" syntax for additing entries
   8. Arrest changes: trespassing now also includes being a foreign aggro mob not following a player
   9. Arrest changes: being wanted for resisting arrest now starts the "copkiller" effect (police don't follow normal procedure)
  10. Behavior MOBTeachers now teach Expertises
  11. Quest changes: now support multi-step quests, spawned quests, multi-quest spawned scripts, AREAGROUPs, and GIVE STATs
  12. Internal: New message type: LIFE (is sent when mobs are rejuved or initially brought into a map)
  13. GTELL now supports socials/emotes, and automatically emotes a LEVEL msg when a group member levels
  14. NOFOLLOW (by itself) no longer un-follows all group members, so it can be used to limit FUTURE followings only.
  15. The despotic Clan type "Clan" renamed to "Gang" to differentiate it from the more generic "Clan" name.
  16. Dozens of new skill, chant, prayer -domains implemented
  17. SNOOPED players will be tagged with their snoopitude names
  18. We now have a AUTO-player-TITLING system -- just use CREATE TITLE to get started
  19. I3 now connects to the new server, and their are coffeemud.ini entries to specify servers
  20. Two new log types -- kill log and combat log -- turn these on if you have lots of disk space!
  21. CoffeeMud has an IDLE timer system now -- settable through coffeemud.ini file
  22. ShopKeepers can now have price adjustments to items based on zapper masks of items.  Called PriceFactors
  23. new coffeemud.ini entry: MAXCLANMEMBERS - limit the number of members of player driven clans
  24. Generic Abilities -- yes, create a new skill/spell/property/whatever from the comfort of your command line editor.  use CREATE ABILITY to get started.
  25. Generic Races - CREATE RACE <RACEID> can now be used to CONVERT existing standard races to genraces.  This is a very faithful copy system.
  26. Generic Classes - CREATE CLASS <CLASSID> can now be used to CONVERT existing standard char classes to gencharclasses.  Not as faithful a copy as genraces due to the complexity of gencharclass code.
  27. Saving a room with items riding rideables will have ride-status be rememberd and restored on room reload, including riding items like wagons on horzes.
  28. Most java classes can be reloaded at run-time; the LOAD command will unload existing classes from the classloader first, so UNLOAD not necessary.  This includes .class files, .js (javascript) classes, and if setup correctly, .java files will be recompiled and loaded.
  29. As mentioned previously -- scripts directory eliminated and language scripts folded back into java code.  This is a linguistic surrender to a massive codebase that I can't maintain in an abstract language-scripted form.
  30. DESTROY now supports THREAD type -- doesn't always work for all threads though as thread.stop() is deprecated..
  31. CoffeeMud now has numerous skill-tied Expertises, as well as ability to define/destroy them online -- use CREATE/LIST EXPERTISE to get started
  32. can now LIST AREARESOURCES to scan current resource settings for area rooms
  33. can now LIST CONQUEREd to review area conquest details
  34. LIST RESOURCE <specific resource name> will now show the CONTENT of that resource
  35. New command: LEARN -- combines GAIN with TRAIN
  36. Expertises are now GAINED and TEACHed instead of TRAINed
  37. Online COMPONENT editor improved to be more like other online editors.
  38. All skill-listing commands, such as CHANTS/PRAYERS/SKILLs supports domain parameter, and skill-name parameters now
  39. ANNOUNCEMSG (no parm) now reveals existing announce message
  40. Don't normally announce bugs, but the one related to the MUDGRinder menu for non-archons was a doozy. Some may look at it now and wonder at all the "new" editors in there.
  41. can now TRANSFER large chunks of items from one place to another.
  42. Prop_SpellAdder (and derivatimes -- PropFightSpellCast, etc -- supports NOUNINVOKE flag now)
  43. Several new DEBUG coffeemud.ini entries for your amusement
  44. New Prayer: ROT (counter to good prayer)
  45. CoffeeMud now stores backup of player kids in database. Resurrect prayer can be used to bring then back, esp. when used by an Archon
  46. New Fighter Skill: Armor Tweaking
  47. Songs, Dances, Plays all now support multi-room effects which are exploited only by expertises -- still a cool feature
  48. Several new MOODs -- and improvements to old ones
  49. New Thief skills (though Archon only for the moment): Thief_IdentifyTraps, Thief_DisablingCaltrops, Thief_Autocaltrops, Thief_SetDecoys, Thief_Footlocks, Thief_DazzlingCaltrops, Thief_AutoMarkTraps, Thief_AutoDetectTraps
  50. Several new zapper masks: +-DAY, +-WEATHER, +-SILLFLAG +-MAXCLASSLEVELS
  51. Areas now have something called a Blurb flag -- not sure what its good for yet, but they are there for flagging areas and/or adding to area help files with custom state information about the area.
  52. HELP <socialname> now works
  53. BLOOD liquid types will now rot
  54. New resource type: BEANS
  55. Weapon change: magical bonus (+1, +2, etc) is now calculated as part of the attack/damage bonus of the weapon instead of purely as a bonus on the user of the weapon.
  56. Archon wands/staffs now both have level x up/down ability as well as RESTORE ability
  57. Can now delete directories in MUDGrinder File Manager
  58. MUDGrinder FLAT-Map area editor uses a new algorithm for placing rooms that GREATLY improves readability
  59. Yet another step taken towards the "Multi-Host" MUD abilities -- still not there by a longshot, but more work done in that direction.
  60. Mechanism for how standard races set their "default stats" (the way birds have an int of 1, for instance) was changed to be a bit more flexible, to allow higher stats in extreme circumstances
  61. Really a consequence of the GenRace changes, but auto-Mixed races (from breeding) will be a much more accurate mix of the races now.
  62. Prancer class renamed to Dancer
  63. Special/Reserved Quest called holidays can be added to/modified on an area-by-area basis using LIST/CREATE/MODIFY HOLIDAY <name>
# Build 5.2.3 - Release Oct 15, 2006
   1. Internal: all environmental classes can now more easily support extra-saved fields.  Still requires some programming work, but most is done for you.  See Programmer's Guide under STAT.
   2. Manipulating non-edible/drinkable raw resources will now auto-bundle, and unbundle them using normal GET [NUM] [TARGET} syntax.
   3. Channels now support new PLAYERREADONLY flag, so that mobs can talk on them, but players can not.
   4. AFTER command now supports LIST parameter.
   5. UNLOAD command now supports USER parameter (for decaching users)
   6. TICKTOCK now supports SAVETHREAD and UTILITHREAD parms to force those background tasks to go.
   7. GOTO command now supports PREVIOUS parameter
   8. *new* command: MOOD .. a cute roleplaying thing we thought up.
   9. Conquest changes: if an area is taken from one clan by another, the conquering clan can not raze the previous clans items for 3 days, giving the previous clan time to challenge the conquest.  During this period, if the new clan loses the area, it will revert to the previous clan.
  10. Archons can now conquer areas for their own clans by proclamation to the flag: I HEREBY DECLARE THIS AREA
  11. New item type: GenFatWallpaper (wallpaper with a display text)
  12. New item type: GenMobilePortal (a GenPortal that redirects a paired portal at its destination room back to itself)
  13. Wand of the Archon can now level or delevel a particular number (level x up, level x down)
  14. Two new scriptable commands: MPLOG, MPCHANNEL
  15. Legal change: if a cityguard is killed, the cityguards will follow a diff. procedure in arresting the killer.
  16. Scavenger behavior now supports a place for them to dump their trash loads.
  17. Other scriptable changes: playerstats now included in the mpstat commands, mpable commands now include expertises.
  18. New disable flags: fatgrids, fatareas, logins
  19. LOTS of skills now have their castingquality implemented, and so will be used more intelligently by mobs in combat.
  20. Several thief skills now have their expertises implemented.
  21. New common skill: bandaging, to cure bleeding and limb damage (but not limb loss, of course).
  22. New combat sub-system: Bleeding.  INI file support added 
  23. New zapper mask: +HOME
  24. More stuff added to mudgrinder control panel.
  25. Bugs detonated underground, mysteriously.  Wild claims about bug strength forthcoming.
# Build 5.2.1 - Released Sep 09, 2006
   1. New scriptable function: Math
   2. INI entried PLAYERDEATH and FLEE have a new option "OUT" for making
   folks go unconscious. You can also include your own math fomulas for
   exp loss, and they both let you include multiple penalties now.
   3. New INI entry: MOBDEATH -- technically the same options as the
   other two, but I only mention OUT and DEATH.
   4. START_, MORGUE_, and DEATH_ rooms can now include class entries.
   5. MUDGrinder now includes a player editor under the Player Manager.
   6. Scriptable STAT/GSTAT/MPGSET/etc can now include mob state and
   base-state entries.
   7. A few bugs dutifully informed of their transgression, judged by a
   ruthless programmer, and sentenced to a quick death.
# Build 5.2.0 - Released Sep 04, 2006
  Well, here it is, in all its splendor.
  Expect another build in a week -- I'm gonna let this one shake down and then send it to all the secondary download areas also.
  Not that I'm expecting any bugs, but with new stuff, you never ever know.
   1. New example mystery called "murdermystery" for Midgaard. Easy to adapt it for any other area, of course.
   2. Clerics can now convert mobs, create churches, and hold "services"
      defined by their deities.
   3. New clan type: THEOCRACY -- has strict membership and hierchy rules, but allows areas to be conquered with conversions, and restricts church building to the clans deity. Also, item control points dont matter for theocracy.
   4. CombatAbilities behavior now allows specific skills to be allowed or disallowed specifically for combat.
   5. New behavior: Concierge, will tell players how to get to places, optionally for a price.
   6. New prayers: dark senses, religious doubt (internal use only), Infuse Balance
   7. New behavior; QuestChat, allows chat flags to prevent re-saying the same things again, and allows dynamic building of chat data.
   8. Scriptable changes: New triggers CNCLMSG_PROG and EXECMSG_PROG, catch alls for trigger. Also new message: HUH
   9. Scriptable changes: MPEXP now allows a % of exp tnl to be given, and scriptable scripts can now be dynamically added to from quest scripts
  10. Scriptable changes: MPOLOAD can now use INTO syntax to load containers, MPQSET,QVAR command for changing/accessing quest objects
  11. Quest Manager has been extended QUITE a bit to support mysteries..
      lost of new settings, and some subtle differences in the way it works.
  12. New Bard skill: BEFRIEND, for making groups.
  13. New command: QUESTWINS -- show which quests the player is listed as having won/completed.
  14. REPORT command now lets you report your statistics.
  15. New chant: Sense Fluids -- for finding fluids for the drilling skill
  16. WIZEMOTE - can nw emote to clans
  17. CoffeeMud now supports Spell/Skill/etc Components -- use the CREATE COMPONENT command to specify new ones.
  18. New archon skill: FREEZE for making players stay put
  19. BANISH can now specify an amount of time for the player to remain banished.
  20. Prop_Doppleganger can now apply to items.
  21. Prop_Hidden can now specify unlocatable.
  22. New disease: Alzheimers
  23. New common skill: Shearing
  24. Two new example scripts: one to accuse all room entrants of heresy, and a javascript script to clear back channels
  25. New CoffeeMud.INI option: INTRODUCTIONSYSTEM -- enough said?
  26. New coffeemud.ini options: MANACOST and MINMANACOST now allow you to specify overrides for specific abilities.
  27. CoffeeMud will now, by default, prompt users for a reason for retiring, or quitting before their first hour is up. There are DISABLE entries for this feature, however.
  28. MUDGrinder Journal-reader has been enhanced for usability
  29. NOFOLLOW command can now be used to force someone to stop following you, as can REBUKE.
  30. New Archon STAT parameter: COMBAT -- for seeing how a players eq affects their stats.
  31. When a mob or player flees form another, the mob/player fled from gets an exp prize.
  32. LIST command now supports listing players by IP for determining alts.
  33. Channels now supports CLANALLYONLY flag
  34. Containers and portals now auto-close and auto-lock like exits.
  35. All the bugs thats fit to squish!
# Build 5.1.5 - Released Jul 26, 2006
  MCCP is now disabled in the coffeemud. ini file by default. What I thought was a really frisky bug in CoffeeMud turned out to be a problem in the zlib.jar file we are using. The bug causes player output lockup about once per day on my mud. I recommend you disable it until I can replace the library.
  Also, I did get primary hacking on the Mystery Engine completed, but will have to wait for the next build to see the debugged version with lots of example mystery quests.
  As always, this build is in the "stable" directory.
   1. HELP changes: VISIBLE command is better documented in relavant spells/skills, RANGE entry added.
   2. A players HIDE score is now shown in SCORE when hidden.
      Observation score (see hidden) is also shown when avail.
   3. Behaviors Aggressive, MobileAggressive, WimpyAggressive, VeryAggressive now have MISBEHAVE flag.
   4. Scriptable changes: $0..$9 are now valid temporary variables for holding all possible strings -- they are local to a single trigger.
   5. Scriptable changes: We now have FOR loops!! (use the temporary variables described above)
   6. Scriptable functions: QUESTPOINTS, QVAR, TRAINS, PRACS
   7. Scriptable methods: MPQUESTPOINTS, QVAR, MPTRAINS, MPPRACS, MPARGSET, MPLOADQUESTOBJ, MPQSET
   8. Scriptable changes: MPENABLE now supports ++, -- for adjusting proficiency
   9. RECORD command can now be used when player is offline, and will resume when they logoff/logon.
  10. New Thief skills: Hide Other, Woodland Creep
  11. (Internal): The words proficient/proficiency now spelled correctly in the source code.
  12. Bankers can now loan money to players who provide collateral, at an interest rate.
  13. New command: BORROW -- to support the Bankers.
  14. New Behavior QuestChat -- a MudChat for dynamicly built and/or one-time responses.
  15. Can now disable HUNGER/THIRST through the coffeemud.ini file.
  16. MUDGrinder now has Bank Accounts browser under Player Management, and Banned List is under P.M. as well.
  17. More bugs fixed than stars in the sky.
# Build 5.1.4 - Released Jun 26, 2006
   1. New Archon Skill: RECORD
   2. Several new Neutrality Chants: Natural Communion, Star Gazing, Deep Thoughts, and Plant Self 3. Educations have been renamed to Expertises -- there are new ones in the works, but not quite ready...
   4. Behavior ResourceOverride can now be applied to Areas.
   5. New Scriptable Triggers: SOCIAL_PROG, LOOK_PROG, LLOOK_PROG, OPEN_PROG, CLOSE_PROG, LOCK_PROG, UNLOCK_PROG
   6. New Scriptable Functions: EXP, NUMPCSAREA
   7. More New Scritpable stuff: $c, $C codes.
   8. POSSESS command will carry your security flags with you if done while SYSMSGS are on (see HELP POSSESS)
   9. New Properties: PROP_COMBATADJUSTER (mostly internal), PROP_NOORDERING, PROP_REQSTAT, PROP_TRAINER, SoundEcho
  10. New Thief Skills: Conceal Item, Silent Drop
  11. New Specialization: STAFF_SPECIALIZATION (given to Druids)
  12. Several new MXP images (thanks Nic!)
  13. New Command Line Clan Editor
  14. Clans can now empart a Character Class to those who join it, as well as a specifyable position (member, enchanter, etc).
  15. Common Skills now show % done to the player.
  16. Properties that give magic for worn items can be set to disable themselves when multi-layer enabled (to prevent abuse)
  17. CoffeeMud.ini DISABLE flag can now include MXP, MSP, CLASSTRAINING, NEWPLAYERS
  18. Missing HELP entries can now be channeled to the log or their own file. Also, an unmatched HELP command will now give a HELPLIST on the word.
  19. Several SHELL commands now support -R switch for recursive deleting, copying, whatever. Also, new MOVE shell command.
  20. Can now CREATE USER (Players) from the command line.
  21. New GenBelt item, and newbies get a real-live sheath in their OUTFIT now.
  22. New? Item GenBook (not really new, just stolen from the snippets upload -- thanks!)
  23. New container type: footwear
  24. The Nessness behaviors (Mageness, Fighterness, Druidness, etc..) now alter HP and stats of mobs to reflect class.
  25. Expertise macros added for the web site.
  26. Skills which are prereqs for other skills will now list that fact in the help files.
  27. New Database flag in coffeemud.ini file: DBREUSE, for those having trouble with MySQL in Windows XP.
  28. New Channels INFO flag for the ini file: WARRANTS
  29. Dadgummit! I laid down for core for a player/clan debt system and never used it anywhere. Ugh...
  30. For convenience, several references added to the Guides section: Reference. html
  31. New DEBUG flag: DBROOMPOP
  32. Guides were mostly cosmetically redone. Thanks for this donation!
  33. So many bugs were fixed, it would hurt us both to list them.
# Build 5.1.3 - Released Apr 24, 2006
   1. Armor can now be layered (more than one thing worn on a location)
   2. Armor can be layered and see-through (like cloth armband around a shirt sleeve)
   3. Armor can now be layered and multi-worn (like bracelets on a wrist, or badges on a shirt)
   4. Prop_WearAdjuster/Prop_WearEnabler have a new flag: LAYERED to limit layered magic armaments
   5. Common skills updated for layered clothing -- thanks Tim!
   6. New player race: Drow (still in beta though -- probably more will be done with it): Thanks Lee!
   7. Racketeering now prevents other thieves from harassing your protected shopkeepers: Thanks again Lee!
   8. Better support for Oracle is on the way -- partially there this release, but not all the way there. Thanks Aristotle!
   9. Spell_MagicItem now has protections for layered clothing (to prevent abuse)
  10. Not new, but in better shape: educations for using common skills to create enhanced items
     -- mobteachers especially are more helpful. :)
  11. Evil clerics now OUTFIT with swords instead of maces
  12. New resource: cheese. :/
  13. TELL now respects invisibility
  14. New Zapper masks: ADJSTR, ADJDEX, ADJCON, etc.. for adjusted/current stat checking
  15. A tall helping of bugs gobbled up.
# Build 5.1.2 - Released Apr 04, 2006
  numerous important bug fixes, as a few remaining problems with thin areas are shaken out. Even if you don't use thin areas, you'll need the fixes, since they also resolved issues with Grids.
  The only real new feature is the implementation of Educations. In this case, for the common skills.
  And, of course, assorted other fixes.
# Build 5.1.0 - Released Mar 23, 2006
  Here it is, and every bit as late as I promised. Hopefully something in there yall will like. As always, there are INI changes to watch out for, and another library JAR file has been added which needs to be included in your startup script (mud.bat or whatever).
   1. New Area types. These include grid type areas, uncached (thin) areas, and combinations of both. (see Archons Guide) 2. MUDGrinder support for "grid" area types -- supports "painting"
   huge swabs of rooms at once.
   3. Languages being spoken can now cause problems for ORDERing, and Shopkeeper/Banker/Postman interactions.
   4. Serious optomizations in all tracking-related behaviors and skills.
   5. New command: Replay (sort of a command-line scrollbar) 6. Many common skills now have attribute and other skill and proficiency requirements. This is the stick -- the next version will have tons of enhancements to them through Education flags.
   7. Default non-combat commands & skills/round changed from 4 to 10 and 4 to 6 respectively (in INI file).
   8. A first in the history of CoffeeMud! An INI entry has been REMOVED (MINMOVETIME). It's redundant now with the round timing code.
   9. New INI entry: AUTOAREAPROPS for automatically adding properties or behaviors to new/old Areas.
  10. Disease Obesity has been toned down slightly. No pun intended.
  11. Some networking issues with the HTTP/web server have been worked out. Pretty important bug fix.
  12. Prop_ReqCapacity has a new parameter: indoor 13. Skills/Spells/etc can now have requirements that include: other skills, proficiency in other skills, and ANY other Zapper masks.
  These requirements can be built on a class-by-class basis instead of being global to a skill.
  14. ItemGenerator behavior now weighs its random selections by value (more valuable == less likely) 15. MudChat behavior can now have combat-only message/response handling.
  16. You can now export and import areas from the MUDGrinder.
  17. RUNning is now faster than walking around, though its more expensive of movement.
  18. Player Records now have a "notes" field you can jot misc stuff into about them.
  19. Quests (the automated of course) can now have a priority/run level set to prevent them from overlapping.
  20. Quests can also have minimum players online, which can be narrowly defined using a player zappermask if you like.
  21. CoffeeMud now supports MCCP (text compression), and it supports it reliably and well. There is another jar file in the LIB directory to make sure is in the classpath, however.
  22. DEFAULT PROMPT now includes the combat opponents health text.
     It was removed from its previous place. Old players will have to update their prompts.
  23. Due to the mudmagic.com coding context, CoffeeMud now has a configurable and expandable Extended English Parsing system. You can enter all kinds of complex things like: get the third axe ; get the axe and the sword ; get the sword and wield it ; get sword, axe, and helmet and put all in the bag 24. Zapper mask now includes SKILLS (including optional proficiency checks), EDUCATIONS, SECURITY flags (but only if they are defined in your ini file) 25. (Internal) CoffeeMud is now FAR stingier with threads, and has been drastically optimized for speed without costing another byte in memory (heck, perhaps using even less in the process).
  26. If your muds been up long enough, you can actually start reviewing your players leveling-dates from the MUDGrinder Player List.
  27. Siplet also supports MCCP now, and has had a few Firefox/Browser related issues addressed.
  28. The MUDGrinder main menu has been shuffled a tiny bit. A minor change to the Threads listing makes it a bit more useful.
  29. A whole circus of bugs tamed, and hopefully as few new ones introduced as possible.
# Build 5.0.1 - Released Jan 18, 2006
   A few bug fixes as 5. 0 is preped for public announcement.
# Build 5.0.0 - Released Jan 14, 2006
   This is mostly just from memory. ..
   1. Packages have been moved around, fakedb files are now in a different place, and there have been INI file changes -- follow the Installation Guide upgrade instructions closely!
   2. Some speed and memory usage enhancements.
   3. Smoking can give you cancer now.
   4. New CoffeeMud file system, with new command SHELL and new MUDGrinder File Browser to support it. Remember that "." is a shortcut for the SHELL command. Also remember that all your files must be in the coffeemud installed directory now! Also, new security flags, VFS: and FS: implemented to allow you to give file level or directory level access to builders. Do Help CMFS for more info.
   5. Better builder reporting in the log.
   6. CoffeeMud classloader will now load JavaScript ".js" classes also.
      See the Programmer's Guide for more information.
   7. Many, but not all, interfaces have been commented. Generating javadocs might actually be useful!
   8. New Behaviors: ItemGenerator, and RandomItems
   9. Metacraft has some more parameters to extend its usefulness
  10. New actions/time-based commands support. INI entries DEFCOMCMDTIME and DEFCMDTIME and DEFCOMABLETIME and DEFABLETIME to support it. My own players preferred a lower DEFABLETIME -- you might want to tinker with that setting to taste (I set mine from .25 to .1)
  11. Trailto command has more options now.
  12. New INI entries: LIBRARY, COMMON to support extended engine library classpaths
  13. INI entry change: COMBATSYSTEM now has new entry: MANUAL.
  Internally, abilities now have preinvoke methods, and commands have preexecute.
  14. Lots of other stuff I can't remember, and with a 15megabyte change file, I may never know about.
  15. Somewhere, there are fixed bugs.
# Build 4.7.13 - Release Dec 1, 2005
  Somewhat early build as I figure out what's next.
  **VERY IMPORTANT** No DB changes, but launching the mud now requires
  that you include lib/js. jar in your classpath. All of the sample
  batch files have been adjusted accordingly. There are also a INI
  entries to watch out for if you upgrade.
   1. New global color code: ^J for weather messages.
   2. Siplet now detects browser java installation and gives a message
      for those without.
   3. Scriptable changes: NUMMOBSROOM accepts a name parameter, new
      MPRESET command, new <SCRIPT> command (see 7)
   4. More interesting information about death is recorded in DeadBody
      objects now, with GenEditor support.
   5. New memory management tool: LIST OBJCOUNTERS
   6. Relationship between Hide and See Hidden altered. Success at
      either is now modified by level, skill, 2 new saving throws (save vs
      detection, save vs overlooking), height, dexterity, wisdom.
   7. Scriptable behavior, Quest engine, web pages all support
      Javascripting. New INI entry controls security for Scriptable
      behavior.
   8. New command: JRUN, for executing external-file javascript, for
      batch or other purposes.
   9. Skill Usage report added to the Statistics tables in the MUDGrinder
  10. Lots of default MXP images are included in this build.. almost
      all races, but some resources as well. Almost all were collected by
      the wonderous Jordana.
  11. The formula used to determine hits and misses in combat has been
      altered to correct some imbalances and improve gameplay.
  12. Two weapon fighting altered to make it desirable for players,
      while (hopefully) maintaining balance.
  13. Injury system now includes a minimum level to lose a body part,
      specifyable in the INI file.
  14. Internal: Most shopkeeper code from Merchant and StdShopKeeper
      consolodated into CoffeeShop object and CoffeeShops library
  15. Merchant common skill can now be used as a property also. See
      AHELP MERCHANT for more info, but basically you can make anything
      from an item, to a room, to a whole area into a shop.
  16. FLEE command can now be used to end combat when all your foes are
      bound, sleeping, or held.
  17. Scriptable: Previously mentioned changes to speech_prog and
      regmask_prog. See yahoo support group.
  18. New command: GMODIFY, which allows searches and replaces in
      objects by stat fields.
  19. Locksmith skill now supports DELOCK parameter.
  20. 1/2 cup of debug labor sprikled within 1 foot radius of entire
      bug mound. Approx 1 qt of water per cup of debugging.
# Build 4.7.12 - Release Nov 12, 2005
  Not a long list, but a fun one. I've spent almost the entire last
  three weeks working on Siplet -- the built in java applet-based
  client. It's now very close to where I want it... at least close
  enough that I managed to put SOME time in on the engine. :)
   1. ALLSKILLS Security flag added as an alternative means of giving
   admins all skills short of making Archons or new classes.
   2. CoffeeMud SMTP server will now react more favorably to HTML-
   formatted emails, provided they also include a text part.
   3. Emoter behavior now supports socials proper.
   4. New text file: mxp_images.ini which, because I don't have sample
      images yet, doesn't do much. It's an empty template for all the
      default MXP compliant images that can be used in-game whenever an
      object-specific image isn't provided by builders.
   5. LIST RACECATS command added, along with LIST MATERIALS.
   6. CoffeeMud now does a bit more TELNET-code negotiation, and does it
      in a nicer fashion. Special thanks to Alan Wood for providing the
      code that inspired it. The only functional bonus, per se, is that
      people using straight TELNET will get character echo so they can see
      what they type.
   7. New player command: VISIBLE, which also works for Archons WIZI
      purposes.
   8. Siplet client now supports as much of the MXP protocol as
      CoffeeMud dishes out. :)
   9. New property: Prop_RoomUnmappable
  10. *Modified* Scriptable commands: MPECHOAT, NUMMOBROOM -- now both
      much more powerful
  11. FakeDB now supports a WHERE clause with other comparisons aside
      from just =: >, <, <=, >=, and <>
  12. New spells: Word of Recall, Mana Shield, Minor Mana Shield, and
      Major Mana Shield
  13. <internal> Shutdown app now works using an escape sequence, so
      the new Shutdown app is NOT backwards compat with old servers.
  14. Quests can now have static start dates, both real-life dates, and
      mud-dates.
  15. StdThinGridVacuum has been reactivated. I think I finally got
      its problems licked.
  16. New intro screen for MXP client users -- they will see intro.jpg
      in addition to intro. txt
  17. A growth of malignant bugs was eradiated.
# Build 4.7.11 - Released Oct 28, 2005
   1. Date/Time records for each level a player gains, including their creation data/time, are now kept.
   2. New Zapper checks: season, month, hour
   3. FAQ in Archon Guide removed and replaced with new Builders FAQ in the guides section. Thanks Jeremy!
   4. WHO command extended with optional PLAYERKILL parameter
   5. Individual Abilities and Commands can now be disabled via the INI file, if for some reason you want to do that.
   6. New Clan trophy (Most Rival player-kills). Damn, don't think I tested that.... :/
   7. Looking up into or across SKY areas can now tell you of mobs and items "out there".
   8. New Archon LIST entries: JOURNALS, TITLES
   9. Can now destroy entire journals with DESTROY JOURNAL
  10. ShopKeepers can now enforce a level range on items bought with new RANGE prejudice factor.
  11. Command Line Socials editor totally rewritten.
  12. Socials can now include arbitrary second parameter... not just SELF, <T-NAME>, but ALL or any other second word.
  13. New player command: AUTOAFFECTS
  14. ShopKeepers will now accept items to sell from property owners if their "home room" is player or clan property. Proceeds from such sales go directly into the property owners local bank accounts, if any.
  15. Several more commands have been "scriptified" -- thanks again to Gianluca for his continued hard work.
  16. Prop_Retainable has been greatly enhanced -- it is practically a new property, and makes player owned payed-one-time laborers much easier to handle.
  17. New player command: ALIAS
  18. New web player menu: Player Access, which gives players their char stats and other goodies, including email.
  19. EMAIL command has been rewritten to allow accessing player email even with AUTOFORWARD flag is off.
  20. AddFile web macro now has WEBIFY MacParm to translate color and other MUD codes.
  21. Regional Awareness skill now shows maps in color! woohoo!
  22. PK rule changes: players can not turn PK flag off, or log off within a few minutes of a fight with another player.
  23. New behavior: ScriptableEverymob, for having ALL mobs in a given area share a Scriptable script.
  24. Socials now also include MSP sound files. Numerous socials have been outfitted -- THANKS JORDANA!!!!
  25. Weather events, and numerous previously silent spells now have accompanying files -- thanks AGAIN to Jordana!
  26. Common Skills now are mostly outfitted with sounds.
  27. CoffeeMud now has an official web-based client available to Internet Explorer users via your internal web servers public pages. No MXP support yet, but it does have MSP sound support, ANSI support, and "up arrow" command memory.
  28. A horde of marauding bugs beaten back with the ingenious use of banana peels and cat droppings.
# Build 4.7.10 - Release Oct 11, 2005
  Bug fixes to StdShopkeeper and BaseGenerics.
# Build 4.7.9 - Released Oct 10, 2005
  here were a few really nasty bugs discovered soon after the release of
  4. 7.8. This build corrects them.
# Build 4.7.8 - Released Oct 07, 2005
   1. Built in channels WIZINFO and CLANTALK are *GONE*. They have been
      replaced with new CHANNELS flags in the INI file. Please see the INI
      file for more information on this new feature.
   2. Channels can now be made read only, and can display lots of new
      information they couldn't before.
   3. Those with CLOAKING ability can now auto-cloak at login by putting
      their login name followed by a space and a '!' character during login.
   4. Unless disabled, the system will now keep track of every room a
      player has visited. It will color exits accordingly (see INI file),
      and will report the % of the current area and world visited in the
      WHERE command.
   5. Many properties Prop_*SpellCast, Prop_*Adjuster, Prop_*Enabler,
      Prop_*Resister, etc. . may now include an optional zapper mask to
      determine if the described bonuses will apply to the current
      user/wearer/rider/owner/whatever. This is great for making armor
      that only works when all pieces are worn, or axes with bonuses only
      for dwarves, etc.
   6. Readable items may now be written/read in different languages.
      SPEAK command will choose what language will be WRITTEN in.
   7. New unlisted command: Test -- written just for automatiting the
      testing of all the property changes mentioned in #5.
   8. Light sources can now be handled in limited ways, even in the dark.
   9. Looking at a rideable will now list the people riding it.
  10. WillQualify command will now report which skills are
      automatically gained.
  11. A name mask can now be given to the IMPORT command when importing
      player lists.
  12. genCharClass command line editor FINALLY supports the
      availability flag properly. This was really a bug fix, but well
      worth mentioning.
  13. Journal-type items can now have an administrator listed in their
      parameters along with REPLY, READ, and WRITE requirements.
  14. ANNOUNCE command changes: announce alone defaults to ALL.
      ANNOUNCETO syntax required to direct an announcement. ANNOUNCEMSG
      allows admins to customize the string outputted when they use the
      command.
  15. Color of fight text now differs for strings reflecting your own
      attacks versus those of others. ColorSet expanded accordingly.
  16. AFK command now has an optional parameter for when people try to
      do TELLs to you.
  17. Highly dangerous new Archon command: PAUSE. Can be directed to
      stop thread/ticking of individual objects, OR THE ENTIRE MUD!!!!!
  18. Internal: Gianluca managed to scriptify the BaseGenerics.java
      file -- all the command line editors basically. What a feat....
  19. Internal: New string tag: <S-NAMENOART> <T-NAMENOART> <O-
      NAMENOART> to automatically remove any prefix english articles from
      names before displaying them.
  20. New command: PACKAGE. Just read the help -- it's purpose and use
      will be apparant.
  21. New Scriptable command: MPSTOP, to hault mob fighting and common
      skill using.
  22. New autoconfig flag for players: COMPRESS. A complement to or
      alternative to BRIEF.
  23. "$" character can now be used by players to anchor both the
      beginning AND end of strings. example: $exact name match only$ 24. Journal replies are now date/time stamped.
  25. Password command now requires your old password.
  26. Maximum rate of skill improvement slowed from 1/minute to 1/5
      minutes.
  27. LOTS of new MXP tags added, especially to SCORE, archon WHERE
  28. new INI entries: MAXCONNSPERIP, MAXNEWPERIP to control
      multiplaying and new-player spamming
  29. File Browser in MUDGrinder now has filename search, and file
      search features
  30. Archons can now see the mundane WHERE text by doing WHERE !
  31. sumbugzfixt
# Build 4.7.7 - Released Sep 24, 2005
  Mostly a bug-fix build, though I did find a little time for some
  tinkering. Several class files were dropped, so make sure you follow
  the upgrade rules regarding deleting your com path.
   1. Better reporting of map load errors.
   2. Optimization of XML parser -- might be a little speed bump in
      there somewhere.
   3. The commands IDEA, BUG, TYPO, TASK, and ASSIST are ALL
      DEPRECATED. They are GONE!
   4. New INI entry: COMMANDJOURNALS, replaced all the above commands.
   5. New and Updated Items: PlayingCard, HandOfCards, DeckOfCards. New
      Behavior to demonstrate them: PokerDealer
   6. PKILL rule change: *BOTH* clans must be at war with each other,
      directly or indirectly, to override PKILL flag.
   7. Minor additions to Quest Manager selection abilities, esp with set
      locale, set room, and set area.
   8. EXAMINE can now be used to glean new information on mobs and
      players.
   9. MUDZapper now supports ITEMS. This was HUGE! Several new zapper
      fields added to support this.
  10. Admin WHERE command can now include zapper masks in your searches. 11. New application: Shutdown, allows you to shutdown coffeemud
      gracefully from the command line. See Installation Guide for more
      info.
  12. New non-qualified Prayer (for deities perhaps): Refresh
  13. Internal: ServiceEngine now supports a startTickDown method with
      VARIABLE tick rate.
  14. Character Classes can now have several class-level dependent
      names. GenCharClass support added.
  15. Character Classes can now grant users class-level dependent
      Security codes and Groups. GenCharClass support added.
  16. A snow-capped goat playground MOUNTAIN of bugs fixed.
# Build 4.7.6 - Released Aug 26, 2005
  *  A New Table was added to the Database ** If you intend to use
  the new polling feature, make SURE you review the Installation Guide
  instructions regarding upgrading a database-change version before
  downloading and installing build 4. 7.6
  I'd like to give KUDOs to Gianluca of Italy. He's been working with
  me on "scriptizing" CoffeeMud to support other languages. That means
  you'll find several more entries in the EN_TX scripts area of the
  system.
  This is a fun build I think -- been pretty busy the last few weeks.
   1. AHELP command and MUDGrinder Archon Help topics now actually
      favors the archon help files.
   2. New Scriptable trigger: LEVEL_PROG. Also, LOGIN_PROG triggers
      optomized to run almost instantaneously now.
   3. The MATE SELF social now may make the player go blind.
   4. A summary of the total value of each currency is now listed in the
      money section of inventory.
   5. Items and MOBs in room listings are now always capitalized -- ok,
      pretty trivial, but I'm padding the list anyway.
   6. New Prayer: Divine Favor -- not qualified for by any cleric --
      intended as a deity blessing.
   7. The Chicken Race now lays eggs instead of just the Chicken MOB
   8. Reply now has its own zapper mask in StdJournal/GenJournal
   9. DoorwayGuardian behavior now has NOSNEAK parm, and ability to
      specify the displayed text.
  10. New Scriptable command: MPNOTRIGGER
  11. New Scriptable functions: VALUE, CURRENCY
  12. New QuestScript command: RESET (for rooms or areas)
  13. Fighter GOUGE skill will now damage an eye, called strike
      improved, internal improvements to Injury prop.
  14. New misc property: addictions. very infrequent random affliction
      from (currently) drinking or smoking.
  15. Newly on the market rooms in a Prop_LotsForSale/Prop_RoomsForSale
      situation will now remove door locks.
  16. Prop_NoChannel now has lots and lots of options it didn't have
      before.
  17. New command: EXAMINE. Takes a tick to execute, but gives lots of
      new info based on players int.
  18. New application: OffLine -- run this if your mud is down for DB
      or other maint. Uses text/down.txt
  19. EMAILREQ ini entry now has new possible value: PASSWORD
  20. New feature: Player POLLING. Use CREATE POLL. Also new security
      string POLLS, and new command: POLL.
  21. New INI optionmization to go with the above: DISABLE=POLLCACHE
      (for low memory systems w/ lots of polls and users)
  22. Poison and Alchoholic nature transferred with the FILL/POUR
      command. How did that get missed?
  23. Can now fill a drink glass from a container that contains liquid
      items (like iron pot after distilling).
  24. A few assorted bugs were crushed up against the windshield of
      circumstance.
# Build 4.7.5 - Released Aug 15, 2005
  Here ya go-- Some fun tweeks in there for your power players and
  builders. Enjoy!
   1. Spells shrink and grow now counteract each other.
   2. Cost of common skills brought back up to previous 1 train level in
      default ini settings.
   3. New builder-journal-idea/bug/typo type thing: TASKS. Requires
      TASKS security flag.
   4. Prop_ReqCapacity has some new parameters to extend its usefulness.
      (Players, and Mobs)
   5. Clanresign will now force the players to lose any clan items they
      had to donation or oblivion.
   6. Clans must now give conquered mobs clan items to maintain
      control. Creates a new cold-war dynamic among clans.
   7. New clan item: membership card, is proof against pamphlets.
   8. New Scriptable prog type: REGMASK_PROG for regular expression
      matches.
   9. New Scriptable command: MPFACTION for changing faction
  10. New Scriptable functions: FACTION for reading faction, HASNUM for
     counting items, ISSERVANT
  11. Several spells/chants/etc that do ongoing damage will make
     themselves hostile to the caster.
  12. New legal penalty enhancements: fines, trial skipping, detaining,
     crime separation, and no-release from prison/detention.
  13. Skill class/categories/domains can now be made into blanket
     crimes of influence or skill use.
  14. Tax Collector enhanced to handle criminal fines.
  15. Mobile MOBs with clan-crafted "gathering items" will now
     assist "crafting item" mobs by taking needed resources to them.
  16. HelpList now includes description search.
  17. Lots of dead bugs, including the QUEEN!
# Build 4.7.4 - Released Jul 17, 2005
  Probably one of the smallest builds released. Normally one I would
  hold for another week or two, but for some of the bug fixes. The
  change file only had 3000 lines totally 136k on disk. The new stuff
  is really light, but here it is. ..
   1. Thief Tag Turf skill now lets them tag for their clan, and remove
      tags.
   2. Several of the home-protecting prayers and spells now have a clan
      component.
   3. Several of the ongoing damage spells/prayers/chants will keep the
      mob aggr to the caster so long as he/she is under its affect.
   4. (Internal) CMMap now has a registry system to receive special
      registered CMMsgs. Inspired by the Snippets code, though the imp I
      went with is more CM Compatible.
   5. ASSIST command and list added, based on snippets code (I forget
      whose -- someone should bow).
   6. Assassins Eye added as a clancraft item.
   7. Lots of new prayers: Cure Fatigue, Cure Exhaustion, Invigorate,
      Cause Fatigue, Cause Exhaustion, Enervate, Minor Infusion, Moderate
      Infusion, Major Infusion
   8. New Behavior: Evil Executioner. Also, both Good and Evil
      executioner now have player parm.
   9. New INI entry: EQVIEW, based on snippets code (I forget whose --
      someone else should bow)
  10. Doppleganger property now has MIN/MAX parms.
  11. New StdAbility protected method minCastWaitTime(), defaulting to
     0, now helps prevent spell spamming. Implementations include Healing
     Hands, and the several Cure prayers.
  12. An enormous cloud of millions of swarming bugs dissipated with
     the swirling winds of correction.
# Build 4.7.3 - Released Jul 04, 2005
   1. Secret temporary reset called "autoweather" (RESET AUTOWEATHER) to turn all your players autoweather flags on. This is a temporary feature to enable your players to better enjoy the new weather messages and bring them into line with the new defaults. New Muds do not have to use it.
   2. All weather strikes are now rendered mundane, so spell/magic immunity will not apply (sort of a bug fix, sort of a feature).
   3. New Clan Item: Donation List
   4. Wording changed for Portals so that you enter and leave them instead of "there".
   5. New Exit type: OpenNameable
   6. Web server now supports HTTP uploads! Woot! Only implementation is in the File Browser in MUDGrinder.
   7. Factions can now be hidden from FACTIONLIST command.
   8. New MOB types: StdPostman and GenPostman. DEPOSIT and WITHDRAW commands expanded to support them.
   9. ShopKeepers, Bankers, Postmen all have ignoremask to complement the prejudice. Includes new IGNOREMASK entry in the coffeemud.ini file.
  10. MOTD now reminds people of pending and new items in postal boxes.
  11. Prejudice expanded in the types of things you can mask.
  12. WeatherAffects a little lesh harsh on players now.
  13. Drippling globs of bugs siphoned from the code.
# Build 4.7.2 - Released Jun 16, 2005
   1. CONNSPAMBLOCK is now disableable from the INI file. This willmake the TestBang app easier to run.
   2. Players are now restricted on number of items they can carry, in addition to normal weight restrictions. CARRYALL security flag overrides. New Prompt flags support it as well.
   3. Bundling syntax is now uniform across all pertinant common skills.
   4. Scriptable variables now includes sir/madam in $y/$Y. Also, internally, <S-SIR-MADAM> will do the same.
   5. Two new diseases: Frost Bite, and Heat Exhaustion.
   6. Enhanced Behavior: WeatherAffects handles all measure of weather effects on players and the environment. See the behavior help for more info.
   7. New INI entry: AUTOWEATHERPARMS -- allows the WeatherAffects behavior to be added automatically to all areas, with the given parms (or not, of course).
   8. Internal changes: Behaviors can now be designated as "borrowed" like Abilities. Also, new preInvoke() method added to Abilities.
   9. AUTOWEATHER player flag is now automatically turned on for new users. The text output from autoweather is now no longer on movement from any room->any room, but only from indoors->outdoors, and as specified in the rumbleticks and botherticks parts of WeatherAffects.
  10. What can and can not be seen outdoors at night is now modified by the moon phase, assuming the moon is visible (cloud cover can affect this).
  11. Two new skills: Prayer Call Undead, and Spell Summon Companion
  12. Swimming is now at crawl-like speeds.
  13. New Zapper Check: WORN -- like ITEM, but only triggers if the item is worn, wielded, or held.
  14. Several creepy little bugs turned to jelly beneath my thumb.
# Build 4.7.1 - Released Jun 05, 2005
# Build 4.7.0 - Released Jun 05, 2005
   1. Clans can now be referenced by partial names in commands now.
   2. "here" is now a valid parm for WIZEMOTE
   3. Scriptable trigger changes: LOGIN_PROG, LOGOFF_PROG triggers.
   4. Scriptable functions: IPADDRESS([CODE] == [ADDRESS]), RAND0NUM([NUM]). Also, functions and variables can now be used in "." string parsing syntax.
   5. LIST THREADS functionality extended. Also, LIST THREADS SHORT syntax added.
   6. Command Line Faction File Editor implemented. CREATE/MODIFY/DESTROY commands support factions now.
   7. Archon Guide now has an entry on Factions.
   8. Journal Browser in MUDGrinder now keeps current journal on-screen when the list is long.
   9. Journal Browser includes EMAIL and TRANSFER buttons now.
  10. A huge hot steaming smelly pile of bugs carted away.
# Build 4.6.17 - Released May 06, 2005
  The Database Schema has changed Please see the Installation
  Guide for information on upgrading a database when the schema has
  changed.
  The INI file has new entries They are FACTIONS,
  ACCOUNTEXPIRATION, EXPCONTACTLINE, TRIALDAYS, and SOFTWARE. Please
  add them to your existing coffeemud. ini files, even if you don't mean
  to use either one.
   1. Babys now "soil" themselves.
   2. New Scriptable Commands: MPWALKTO (optomized version of MPTRACKTO), ISBEHAVE, and MPPLAYERCLASS.
   3. Internal: the spell specification in the crafting skill "text" files has changed slightly. An * now precedes a spell/parameter combo.
   4. New Item folder/group in the package and in the INI file: Software (functionality is still in the workd)
   5. New MXP: WHISPER tag.
   6. New commands: ACTIVATE/DEACTIVATE (again, functionlity is in que)
   7. Scriptable changes: Programs with item name masks can now accept item class id names as well (makes money easier to spot). Also, a BUY_PROG and SELL_PROG now exists.
   8. MUDGrinder Area editor got some 11 direction support and a minor face-lift (Thanks Jeremy!)
   9. ColorSet command now allows black backgrounds.
  10. New Skill: Unbinding
  11. New Archon Item: ArchonManual -- protected from non-archon builders in both the mudgrinder journal browser and command line.
  12. Factions implemented. FACTIONLIST command added to support them. Documentation is incomplete due to the fact that I'm still working on a command line faction editor. Alignment has been changed to a faction, and is defined in your resources directory in a file called alignment.ini. The resources/data directory also contains a reputation.ini file to use as-is or as a template for your own factions. Until the editor is done, read the ini files carefully for instruction. This is also brought to you, with several additions and a few more corrections from yours truly, by the letter J.
  13. New INI entries: ACCOUNTEXPIRATION, EXPCONTACTLINE, TRIALDAYS, along with a support command: expire. This feature is untested, but looked good. Also brought to you by the letter J.
  14. Zapper supports Faction-based masks, as does start and death room ini entries, btw.
  15. Grids now support 11 directions.
  16. Sounder Behavior has new PUSH/PULL triggers.
  17. Death/Flee INI options now include one to lose skill.
  18. Thieves Cant language filled out. Thanks to Xalan of Sancara.
  19. More bug fixes than digits in pi.
# Build 4.6.16 - Released Mar 04, 2005
   1. You can now BOOT by ip address.
   2. New Scriptable command: mpplayerclass
   3. New player configuration command: linewrap
   4. Followers now reappear, if possible, where they last were instead of always reappearing with their master.
   5. And, as always, a ton of fixed bugs.
# Build 4.6.15 - Released Feb 21, 2005
   1. EXPORT area data will now include any external resource files, such as scripts, cmare data files, and other external file data in the CMARE file generated. IMPORT will then recreate those files.
   2. New Item type: GenRecipe -- allows you to add to the recipe lists for common skills by giving players items which act at recipes/schematics of the item.
   3. New INI entry -- SMTPSERVERNAME. You can leave it blank unless, like me, you are suddently FORCED to use a relay server in order to send mail with CoffeeMuds SMTP subsystem.
   4. Getting your password incorrect more than 3 times will prompt the user to have their password emailed to them. This requires the SMTP feature, obviously.
   5. Multiple spells as well as spells with parameters can now all be given in a common skill recipe. Just separate everything with semicolons and the engine will sort it out.
   6. New languages: Druidic, Ignan
   7. Behavior FasterRoom has been RENAMED!!! It is now FasterRecovery, and can be applied to mobs, items, rooms, rideables, etc..
   8. Patroller behavior has been enhanced, GREATLY. It can now be effectively used on rideables to create caravans and ferries, especially when combined with Prop_TicketTaker. Patroller no longer requires every room along the way be given, and can correct its coarse if it gets off it.
   9. Items of all sorts can now be pushed and pulled around.
  10. Prompts come back more slowly during combat, cutting down on combat spam bigtime.
  11. Hygiene system implemented for players. Don't ask.
  12. 2 new Common Skills: Food Prep and Baking, to go along with Cooking.
  13. Herbology now gets its herb list from an external file in the resources/skills directory.
  14. Inter-grid exits are now included in export/cmare files.
  15. A heaping sloppy glob of bugs fixed.
# Build 4.6.14 - Released Feb 06, 2005
   1. Players are "warned" of possible weapon fumbling when they wield the weapon.
   2. Mageness, Druidness, Bardness, etc can be used to make multi-class mobs now.
   3. New RULES command and text file in the resources/text area.
   4. New Prayer: Aura of Fear
   5. New INI entry COLORSCHEME for defining a different default color scheme for the system.
   6. GEAS/ENSLAVE has been pretty much rewritten to tax system resources MUCH less.
   7. A bug fix worth mentioning: Cleric subclasses now have their proper skill lists, perhaps for the first time!
   8. New Property: Prop_LocationBound for keeping things/mobs really PUT
   9. MUDGrinder "flat map" format has been greatly optomized for good usage of screen real-estate.
  10. All common "gathering" skills now support bundling.
  11. New command: SMELL/SNIFF
  12. New Property: Prop_Smell to give things a smell that may not already have one. Can do limited emoting for mobs.
  13. New Currency system installed -- see the Archons Guide under Areas.
  14. Internal: MoneyUtils deleted and replaced with new BeanCounter class. Purge your old com directories when upgrading!
  15. MoneyChanger behavior extended to support his true calling
  16. Archons can now T)ransfer journal entries between journals.
  17. More bugs fixed, introduced, fixed again, reintroduced and made worse, and finally fixed than could possibly be imagined.
# Build 4.6.13 - Released Jan 09, 2005
  Fixed are bugs related to the Player Purging system, and bugs that caused all food resources to go "bad" almost immediately.
# Build 4.6.12 - Released Jan 07, 2005
   1. Age is now relevant to fertility.
   2. Uncooked foods now rot over time -- meat becomes poisonous, vegies unnutricious and yucky.
   3. Potions, Pills, etc now forbidden to hold Archon-only skills.
   4. New Spells: DayDream, Flagportaion, Enchant Arrows, Repairing Aura, Spider Climb, Mystic Shine, Wizards Chest
   5. New Chant: Strike Barren
   6. Prop_WeaponImmunity expanded in capability and performance.
   7. New DISABLE flags added in the INI entry: CLASSLESS, EXPLESS, LEVELESS. See the Archons Guide (FAQ) for more information on using these new flags.
   8. New SAVE parameter added to the INI file, for causing shutdown saves of rooms, or preventing other saves. Room item save flag also disables the junk-cleaning system.
   9. New Scriptable stuff: $d, $D, CLANDATA func, MPSETCLANDATA. INROOM func changed.
  10. New Commands: AUTOINVOKE, HELPLIST
  11. New EXPORT destination parameter: EMAIL (for coffeemud SMTP server users)
  12. New Prayer: Fortress
  13. Players and their children now have a chance to develop random allergies. Allergic reactions can range, depending on the players interaction with the allergen, from sneezing, to Hives, to Heartstopper poisoning.
  14. MXP problems with Linux fixed. I normally don't enumerate bug fixes here, but I know some of you were holding back on upgrading because of this. :(
  15. Hunger/Thirst and weight now have a different relationship.
  16. Races can now be designated classless, levelless, or expless as per the INI file entries.
  17. Classes can now be designated raceless, levelless, or expless as per the INI file entries.
  18. New INI entries: STARTHP, STARTMANA, STARTMOVE
  19. Classes and Races can now modify the starting hp, mana, and move for players.
  20. New Disease: Depression, caused by fallen comrades and pregnancy.
  21. Clan members now get titles for free.
  22. As always, several bugs were fixes. And hopefully not many new ones added. :/
# Build 4.6.11 - Released Dec 19, 2004
   1. CLANMORGUESET implemented.
   2. CLAN Trophy system implemented -- new INI entries available.
   3. Formations are now displayed in room view.
   4. New disease: obesity
   5. DIRECTIONS ini entry put in to support the 11 direction system, along with the 4 new commands.
   6. Limb injury system implemented, along with INI entries to support it. Integrated with Amputation system.
   7. Clan Equipment is no longer permanently disabled when held by non-clan members. Instead, it just goes inactive.
   8. A few minor bugs fixed in some of last builds features. :)
# Build 4.6.10 - Released Dec 12, 2004
  There is a new INI entry "MUDTHEME" which everyone MUST include in their on INI files and set to "1". Other than that, it serves no purpose as of yet... it's just a placeholder for features that exist merely as pipe-dreams.
  Those features which are not pipe-dreams are listed below:
   1. Improved docs for the new databases, thanks Mathew
   2. New StdItems DeckOfCards, PlayingCard -- just say "shuffle" to it
   3. MXP additions: Better exit, clanlist, channels, quest support, room and user list support
   4. GenPortals can now be designated to appear as "exits" in a room. See the Archon's Guide for more info on the new fields. GenPortals now implement the Exit interface also -- the consequences of that are as yet unclear, but could include some unexpected surprises.
   5. Internal: 10-directions stubbed in. All that's left is for someone to code the commands and call Directions.ReInitialize. Some extra coding is likely required, but at least there's a basis for others to play with.
   6. Better Smaug Scriptable support in IMPORT command.
   7. Magic Items now explode when they burn up.
   8. Pottery common skill can now make bricks. heh. :)
   9. New command: CLOAK -- wizinv without being unseen (still can't be seen on WHO, etc)
  10. Sessions and Where now included as cloaked. Higher or equal levels with CLOAK or WIZINV security flag can now see the lower.
  11. FROM syntax now in GET/PUT/etc.. commands for containers (get x from y)
  12. New spell: Scatter -- great for quests. :)
  13. I3 Targeted EMOTES now implemented (not really a bug since it was really never there)
  14. Public web page: Channel backlogs feature implemented. Two supporting WebMacros came with it.
  15. Diseases now come with difficulty levels which require higher level clerics to remove.
# Build 4.6.9 - Released Nov 28, 2004
  Just a couple of bug fixes that popped up.
# Build 4.6.8 - Released Nov 27, 2004
  *  This version only has MXP support implemented. The configuration file for it is located in resources/text/mxp.txt
  * The image download feature in ZMUD failed horribly for me, so I only inserted one image-ready tag (ScoreImg) in the codebase, and then commented out its implementation in mxp. txt so as not to crash ZMUD for other people as well. I notified ZMUD of the bug. Lord only knows if they care. :)
  * If there are any other bug fixes or changes, they weren't really worth mentioning. MXP was my only focus.
# Build 4.6.7 - 
  Often referred to as the "Ghost Version", this was a minor re-release of 4. 6.6 with one fix and one oversight in StdLawBook Support. Release Date Unkown. - JDVyska JDVyska (as per discussion with Bo)
# Build 4.6.6 - Released Nov 12, 2004
   1. Body Piercing has been significantly changed to make it BETTER!
   2. The command line now has a calendar editor for TimeZone/Planet type areas in the MODIFY AREA.
   3. The calendar for the default clock is no longer saved in the db, but always subject to the INI file (as it should be).
   4. 3 new common skills (thanks ThrinnTU): Costuming, MasterCostuming, MasterLeatherworking
   5. Most common skills now list the level of the items in their lists.
   6. The cost of wishing for levels is now 4 times greater.
   7. There is now BANNED SUBSTANCE law support.
   8. New Thief Skill: Identify Bombs
   9. New command: GCONSIDER
  10. Resources can now be unbundled with the GET X FROM Y syntax
  11. New Skills: Chirgury, Leeching
  12. New Disease: Leeches
  13. Rerolling method rewritten to eliminate lockup chances for any max values (which I rewrote again, but the idea came from Ashera).
# Build 4.6.5 - Released Oct 22, 2004
   1. Properties Have/Wear/Ride Zappers now have a MESSAGE parm.
   2. Emoter now has an inroom parameter.
   3. Default clock and calendar has been drastically modified to make mud-years only about 11 real-life days long. Please example the default coffeemud.ini changes for guidelines on adapting your own INI files.
   4. Races now have an aging chart to show how they progress through stages of life. Each stage can affect max-stats.
   5. Players now have ages and birthdays based on the default mud-calendar.
   6. Age ability can now be used to give mobs ages as well.
   7. New area type: TimeZone, to separate areas into time zones without making them on different planets. :)
   8. New secret spells: Youth, Torture, Blademouth, Limb Rack, Brainwash
   9. New skills: Enslave, Slave Trading, Torturesmith, Behead, Tar and Feature, Collect Bounty, JailKey, Arrest, Flay
  10. New Shopkeeper type: Slavetrader
  11. Less expensive stat gains above 22
  12. Bonuses to mana for primary stats above 21, and other bonuses for stats above 21 based on class.
  13. Night can now be effectively disabled from the INI file.
  14. Dress, feed, undress, mount, Push, and Pull can now be used on bound prisoners.
  15. New secret prayer: Stoning
  16. New commonder char class: Gaoler
  17. Players can now have visible tattoos that show up on EQ lists.
  18. New common skills: Tattooing, Body Piercing
  19. Arrest changes: executioner will announce executions over a channel, provided a default channel defined in INI file. They will also select Behead or Stoning if they have the skills.
  20. New temporary undocumented reset "genraceagingcharts" for correcting the aging charts of legacy genraces produced from pregnancy.
  21. Players can now have titles/suffixes granted by archons or scriptable. See the help on MPTITLE in the scriptable guide for more information on this feature. The new command TITLE lets the player select which title to use. Polymorphs and disguised can override titles.
# Build 4.6.4 - Released Oct 09, 2004
   1. Scriptable changes: new flags $w, $W. functions: deity, clan, clanrank
   2. Arrest-related skills now work fine when used on parent areas over several children areas.
   3. Prop_RoomForSale, Prop_RoomsForSale, Prop_LotsForSale, Prop_AreaForSale now support rental property automatically withdrawn from bank accounts with a RENTAL flag in the parameters.
   4. Arrest now includes a crime of robbing property, defined as taking something from private property when the owner is not present.
   5. Arrest now includes a sales tax applied to all shopkeeper goods in the area.
   6. Arrest now includes a local citizen tax setting for mobs with the Tax Collector behavior.
   7. Arrest now includes a property tax applied to the value of owned property. Money is automatically withdrawn from bank accounts, or may be paid to a tax collector.
   8. Arrest now includes a treasury room and container for all taxes collected.
   9. Arrest now includes a law against tax evasion
  10. Size of the list of entries in a journal can be controlled with JOURNALLIMIT ini entry. New features of journals include ability to filter on new msgs when listing, to read the next message, and to list old messages with an OLD parameter.
  11. Enough bug fixes and java optomizations to frighten small children.
# Build 4.6.2 - Released Sep 17, 2004
   1. New Archon command: AS. If used properly, can provide the means for maintaining player inventories, as well as player bank accounts.
   2. Special damage message for attacks which do no/negative damage.
   3. THROWing a non-weapon item at another mob provokes circumstantially, but does no damage.
   4. New Misc Magic item types: StdPowder, GenPowder. A magic item which emparts its affects only when thrown on a target.
   5. Spelled-up Weapons, Potions, Pills, Scrolls, etc.. all cast their spells at the level of the item involved instead of the target or user.
   6. MudChat can now accept, as a parameter, the name of it's chat.dat file to use.
   7. Scriptable CHANGE!! MPEXP no longer grants exp to entire group!
   8. Rebuke no longer requires one to be in the same room.
   9. Players served by vassals get/lose their exp ONLY if they are online at the time.
  10. New Archon command: metacraft
  11. Internal: StdAbility overrideMana() can now be coded to take percentages of mana/hp/move instead of flat amount. See Spell_Gate for how to take 90% of someones mana per use.
  12. Patroller now supports rideable items.
  13. Deaths now recorded by WIZINFO
  14. New Property: PROP_NEWDEATHMSG
  15. New Command: FORMATION, for setting the range of group members.
  16. New Disease: BLAHS -- can be caught by those who remain idle for more than 3 hours.
  17. New RESET parm: AREAINSTALL -- for adding behaviors/props to all your areas at the same time.
  18. New Behavior: BEHAVIOR_WEATHERAFFECTS -- an expension of PUDDLEMAKER with some added benefits. This behavior is where all other weather affects will eventually be implemented.
  19. New Items: StdLimb, GenLimb -- for now, only supports the creation of false limbs (glass eyes, wooden legs) by the common skills for those with amputated parts. Will eventually do more.
  20. And, as always, LOTS and LOTS of bugs fixed!
# Build 4.6.1 - Released Aug 27, 2004
   1. SESSIONS command now has a sorting parameter.
   2. Three new INI file entries: SAYFILTER, EMOTEFILTER, CHANNELFILTER. Also available from the Control Panel.
   3. Numerous player security holes plugged (thanks William -- wherever you are!)
   4. New INI entry: WIZINFONAMES, for adding exceptions to the sysop only rule for seeing the WIZINFO channel.
   5. New Property: Prop_LimitedItems, for restricting the number of an item of a particular name in the game. Each item must have the property -- please read the help on this before using!
   6. Archon Guide now has an Index! Woohoo!
   7. New Ranger skill: Regional Awareness (hey guys -- here's your overland map -- I'm gonna want feedback on it!!!)
   8. Follower behavior enhanced so that it can be placed on Items as well as MOBs.
   9. New "levels" of intoxication coded. I'm still not 100% happy with it, but it's a good start. At least 1 drink does not make you all the way plastered any more.
  10. Restoration prayer can now abort death from an Atemi Strike.
  11. As always, lots and lots and lots of bug fixes.
# Build 4.6.0 - Released Aug 22, 2004
   1. RESET MOBSTATS is back.
   2. Journals will now notify their owners, or those who enter the room (if ungettable) if there are new messages.
   3. New Behavior: TargetPlayer (thanks Tulath!)
   4. Area-affecting behaviors placed on parent areas will now also affect children areas: Conquerable, Emoters, Arrest, etc included among these.
   5. Higher level WIZINV can now see lower level WIZINV when in the same room.
   6. CoffeeMud map loads will now be uber faster for rooms with lots of items (player homes, etc)
   7. Player Tattoos can now be set to expire.
   8. Players about to be purged will get emailed now.
   9. Deviations has had a face life (thanks again Tulath!)
  10. Whole new class of Grids create: StdThinGrids, and their derivatives, are arbitrarily large Grids whose children are created only when needed and destroyed only when no longer needed. ThinGrids use a lot more CPU to get their work done, but use a LOT less memory. In fact, ThinGrids can be as large as you like without eating another byte. All standard skys and oceans are now ThinGrid derivatives to save space overall.
  11. The LINK command can now be used to link directly to and from the children rooms inside of Grids. Grid children are referenced as follows: AreaName#123#(x,y) Where 123 is the room number, and x and y are the coordines of the crid. For instance, link "AreaName#123(1,2) east" will now work to attach a room to a grid child. No MUDGrinder support for this feature, and no CREATE ROOM/CREATE EXIT support.
  12. New I3EMAIL INI entry.
  13. New Economy controls implemented -- see the new INI entries, as well as the new ShopKeeper fields for more info.
  14. As always, lots, and lots, and lots, and lots of bugs fixed!!
# Build 4.5.6 - Released Aug 05, 2004
  All the silly bugs discovered on its first day out, and a few of the new interesting bugs reported today: fixed.
# Build 4.5.5 - Released Aug 03, 2004
   1. TELL and GTELL now have TELL LAST x syntax to view a log of past conversations.
   2. Internal: All Common Skills are now qualified for in a single method in StdCharClass.java.
   3. Updates from Tulath of the following: FieryRoom, Spell_DemonGate, CorpseEater, Compare (sorry, no details)
   4. New FIGHT/DAMAGE triggers in the Sounder Behavior.
   5. Inventory command can now accept a mask.
   6. Follower Behavior now allows grouping or simple following according to a (zapper) mask
   7. Zapper checks can now include effect masks, for having many different things happen only if the mob is under the effect of a particular spell or skill.
   8. Updates from ThrinnTU of the following: Carpentry, Armorsmith
   9. New Properties: Prop_HaveEnabler, Prop_WearEnabler, Prop_RideEnabler -- to add skills/abilities when items are owned, worn, or ridden.
  10. New Common Skill: Master Armorsmith (ThrinnTU)
  11. New Property: Prop_AddDamage (nuff said)
  12. New MaxUses field for wands (Tulath)
  13. Forage skill extension for herbs: file resources/herbs.txt can contain lists of custom herb names from specific Locales. Each list is seperated by a blank line, and each item on each list is linefeed delimited. Each list starts with the class name of a locale (StdRoom, Plains, Woods, etc) and is followed by a list of possible herb names. Each herb name can be preceeded by a number and a space to denote its relative weight with respect to the others in the same list.
  14. New WebMacro: SystemFunction, for performing shutdowns, and Announces.
  15. List Deviations has been changed to a new archon command: Deviations.
  16. New Property: Prop_ClosedSeasons
  17. Lots of bugs fixed. :)
# Build 4.5.4 - Released Jul 04, 2004
   1. A few new tech item classes that do nothing as of yet.
   2. Prop_RideAdjuster now works for rideable mobs. Yes, that seems like a bug fix, but it really isn't as it required lots of new code.
   3. Several races enhanced with racial abilities (thanks ThrinnTU)
   4. Containers with capacity==0 no longer show contents message (unless there actually IS content)
   5. New item classes StdPerfume, GenPerfume, with support in the Apothecary skill.
   6. New Property: Prop_MOBEmoter -- just a property wrapper for the behavior, but it only works on mobs.
   7. Patroller behavior now works for items, and has a RIDEOK flag. We can now make rideable item busses and such.
   8. FasterRoom behavior now has lots more parms to make it more versatile.
   9. Prop_Retainable now has another parm to make it more versatile -- have mobs that require payment from a property owners bank account every few muddays or months!
  10. Go can be used as a synonym for Enter when targeting a rideable or portal.
  11. Lots and lots of bugs fixed. Get this build! You need it!
# Build 4.5.3 - Released Jun 17, 2004
   1. DOMAIN name is now stored in the main coffeemud.ini file for SMTP, IMC2, and I3. Please update your ini files!!
   2. Two new misc affects: Antidote and DiseaseCure, for making specific cures to specific poisons or diseases.
   3. Amputation, Pregnancy now show when LOOKed at.
   4. Construction and Masonry now accept help from other players/mobs.
   5. Failing to swipe/steal/etc can now possibly result in a pissed off mob. Chance goes up as the value of the swipe does.
   6. new Behavior: NastyAbilities for casting malicious spells that don't start fights.
   7. IMC2 Locate implemented, I3 Locate also documented. Either will do a locate on both. Also, IMC2 reconnection implemented.
   8. New Props: Prop_HereAdjuster and Prop_HereSpellCast for having rooms that affect players.
   9. Three new Archon skills: Wrath, Hush, and Banish.
  10. Poofs added for the Transfer command. Uses same POOFs editor.
  11. QUIET now has its own flag, and it also blocks tells.
  12. New smelting metal: Spidersteel, for an improved cloth for mages.
  13. BUY command can now target third parties: BUY "CURE POISON" FOR BOB
  14. Multi-births are now possible with Pregnancy. Non-intelligent children (int of 1) never become players. Children now inherent the alignment of their guardian.
  15. Numerous new variables for the prompt command covering time, weather, and the status of your tank.
  16. New Command: EMPTY, for transferring contents of containers to other containers, or inventory, or the ground.
  17. New Property: Prop_Weather, for forcing the weather in an area.
  18. Creating areas now gives a default room from command line! Modifying a room into a new area with no rooms will now re-number the room.
  19. New Commands: SHOW/DISPLAY, for letting someone see the description of an item in your inventory.
  20. New INI entry: COMBATSYSTEM. If set to QUEUE, will allow any commands (including KILL) to occupy a players speed/actions during combat.
  21. As always, lots and lots and lots of bugs fixed.
# Build 4.5.2 - Released Jun 08, 2004
   1. Certain additions to Sounder behavior.
   2. Diff reactions to being arrested for mobs (based on int and align).
   3. Summon is now protected for players.
   4. New Props: Prop_NoTelling, Enhanced prop: Prop_NoChannel
   5. All dynamic classes are now reloadable at runtime (woohoo!). Please remove newInstance() methods from your custom classes!
   6. IMC2 Network interface implemented. Instructions for joining are in the INI file.
   7. As always, several bugs fixed and holes filled.
# Build 4.5.1 - Released May 24, 2004
  Well, the docs are done, a few bugs were fixed, and TAKE was expanded to allow quest points to be taken. The most significant part of this build is that it's going out as a public release.
# Build 4.5.0 - Released May 21, 2004
   1. Tweek made to hp/mana/move gain formula to better differentiate the classes. Bring up localhost:27744/crossclass.cmvp for a chart.
   2. Scriptable.html guide almost done! Only the IF-Functions to go!
   3. Internal: Lots of new fields on the DeadBody interface.
   4. Sacrifice, Desececrate, Disintegrate, etc no longer affect player corpses.
   5. New help entries for clans, clan types.
   6. Scriptable: MPHIDE, MPUNHIDE, MPOPEN, MPCLOSE, MPLOCK, MPUNLOCK added. MPDAMAGE can now affect items.
   7. New information in the Installation Guide
   8. Archon Guide updated.
   9. Programming Guide updated
  10. Breeding system as it pertains to child->player process and naming altered. Children now must be named with Cristen prayer.
  11. Bestow Name Chant and Domesticate Skill can name animal pets.
  12. Brand new Security system! See AHELP SECURITY, and the coffeemud.ini file for more information. SubOps are gone. Getting above lastplayerlevel no longer imparts immortality per se. More!
  13. Where command has more parms now to narrow searches.
  14. New Commands: Restring, Poof, purge, Sessions, Trailto
  15. MUDGrinder now has improved security checking. Will allow access to any/all functionality specified from the new security system.
  16. CoffeeMud now has an online INI file editor in the Control Panel part of the MUDGrinder.
  17. SAY command altered so that it never targets items, and rarely targets mobs. SAYTO command *ADDED* to pick up the slack.
  18. Prop_AbsorbDamage added. Prop_WeaponImmunity expanded.
  19. Gate/Teleport spells now hit random room when they fail. Gate/Portal now take all mana to cast. Resurrect now only heals half exp.
  20. Calendar settings all moved to the INI file (and internally, into the timeclock objects).
# Build 4.3.0 - Released May 07, 2004
   1. Diseases should not cause fights any more, ever! (Unless you invoke one purposefully)
   2. Commands and Topics commands now have pauses, whether you want them or not.
   3. Can no longer use movement based skills if bound or held (unless the skill is escape bonds)
   4. Two new web macros: AddRandomFile and AddRandomFileFromDir
   5. GenMob XML, and room Descriptions can now have their memory caching feature turned off in the INI file. This will save some memory, but not much. It will definitely slow down your system. See the NOCACHE entry in the new coffeemud.ini
   6. Can now both import and export player data.
   7. Can now designate a race as unavailable to magic, unavailable as a player race, or both.
   8. Updates to several Common Skills, included Poses in Taxidermy
   9. Update to the ability to protect player Corpses. See the new INI entry CorpseGuard (thanks Jeremy!)
  10. Prop_TattooAdder expanded in capabilities
  11. Scriptable Changes: MASK/ACT_PROG will now mask the completed text view (without codes). Also, MPOLOADROOM added, MPM2I2M added. Also, ISABLE, ISOPEN, ISLOCKED added.
  12. Spell_BigMouth updated -- you can now eat anything that fits.
  13. Cage skill can now be used on ANYONE by an Archon to create mob items. Dropping them again reconstitutes.
  14. Clans now gain tax exp from other players who gain exp in their conquered areas.
  15. DESTROY now usable (in a very limited sense) by players.
  16. GIVE works more often when done by an Archon.
  17. DRESS always works when done by an Archon (be careful -- you can override clothing logic with it!)
  18. New Quest var: SET MOBGROUP
  19. Where command now MUCH more useful for Archons (and much slower :(
  20. Bless, infuse evil, infuse holiness and other prayers no longer fool zapper alignment checks. Just didn't pass the play testability -- kept conflicting with deities and their desire to keep pure clerics.
  21. Important changes to the instructions for the proper construction of Character Classes. The changes are necessary for certain bug fixes in mobs with class behaviors (fighterness, clericness, etc)
  22. Ammunition system changed. *ALL* ammunition must be GenAmmunition class now. See the Archons Guide for what new features we get from this class. (hint: magic arrows!) :)
  23. WE HAVE AN ARCHON SKILL It's called MULTIWATCH. Check it out. It's for all you archons who worry about multiplaying.
  24. Internal: for what its worth, resource items will now store the room type where they were acquired.
  25. Text file-based MOTD feature added (place your file in resources/text/motd.txt) (need to document that somewhere...)
  26. When using formal forms of commands: create item, copy, destroy, modify item, and are referring to an ITEM, you can now use "@" syntax to more accurately specify the item or items you are referring to.
  27. Mobs and Shopkeepers can now have randomly determined/variable equipment and stock. This has been implemented by using the REJUV field of items as a holder of percent chances of the item appearing. values of 0 still refer to 100%. If more than one item is being worn on the same location, the rejuv field will be used to determine exactly one of the items. RESET ROOM can be used to restore ALL items to a mobs inventory. The Archon MUST leave the room for a tick or two before the mob disgards the "loser" items. The MUDGrinder has been updated with this feature.
  28. Numerous scriptable triggers now work more as expected when an item is the host. (death prog, entry prog, etc..)
  29. BEGINNINGS of a Scriptable HTML doc are here.. it is not even a third done yet. Just the triggers in there so far, for what its worth.
  30. SUBSCRIBE/UNSUBSCRIBE responses are now in the email.ini file.
  31. Prop_ReqCapacity GREATLY expanded in capabilities
  32. Decay bug (which was really a zapper bug) fixed. Anything that used the -RACE or +RACENAME in zapper probably had not been working til now.
  33. New Programmer's Guide entries: Languages, Poisons, and Diseases!
# Build 4.2.8 - Released Apr 23, 2004
   1. ISMOON added to scripable
   2. SMTP Server fully functional. See guides/SMTPServer.html for more info. AUTOFORWARD command added.
   3. lots of bug fixes, esp to pregnancy related stuff
   4. tattoos now stored in user tables and mob xml. Prop_Tattoo now deprecated.
   5. player data now purged when player deleted (bank accounts, emails, private journal mail, etc).
   6. %I added to prompt
   7. can now export player data (can't import it yet)
   8. shopkeeper/banker LIST interface changed.
   9. Did I mention lots of bugs fixed. :)
# Build 4.2.6 - Released Apr 10, 2004
   1. Minor changes to some Clerics & the Beastmaster, a new Prayer ortwo.
   2. New Common Skill: Master Tailoring
   3. New Druid SubClass: Delver, with DOZENS of new chants.
   4. New Resources: trees, berries, fishes, stones, distributed among the locales.
   5. Internal: Several Common Crafting Skills now have auto-item-generation interfaces.
   6. can now Wish for Levels (for others) at ENORMOUS cost.
   7. Internal: Rooms can now have Bubble Effects,
   8. New command: WillQualify (thanks Tulath)
   9. SubClass Armor Restrictions system changed as mentioned previously.
  10. Prop_LangTranslator, and Prop_CommonTwister added
  11. SubArchon security holes fixed (Thanks Kelthan and his sneaky crew!)
  12. SMTP Server started. Still not quite there, but very very close -- next release for sure.
  13. Numerous, dozens, and otherwise LOTS of bugs fixed.
# Build 4.2.5 - Released Mar 26, 2004
   1. Statistics keeping system/reporting implemented. To view the statistics system, you can make use of the commandline STAT command, or use the Statistics page from the MUDGrinder.
   2. Boring Internal Change to save some memory: Numerous Hashtable usages changed to HashSets
   3. Dozens of new Thief and Fighter skills.
   4. Fighter & Thief subclasses rebalanced with new skills.
   5. Can now use CHANNELNAME last X syntax to view a short log of channel mesages.
   6. New Common Skill: Master Weaponsmithing. Few updates to other common skills as well.
   7. Perpetual diseases are now a thing of the past with temporary immunity following the survival of a disease.
   8. Alpha Base code for the espresso server -- leave turned off in your INI file for now.
   9. Prop_ScrapExplode added.
  10. A few assorted bugs fixed.
# Build 4.2.4 - Released Mar 13, 2004
   1. ClanWho command created.
   2. The end of children is now players (following birth and aging, 3 rl months?).
   3. Train gives a little more hit points/mana/movement every time.
   4. Score Items button in Grinder now takes any adjuster properties into account.
   5. Mobs can now leave a room during combat if all opponents are unable to sense or stop them.
   6. StdPortal/GenPortal item created
   7. For common skill bundles, you can now specify the size of your bundle.
   8. Areas can now be put in different time zones by making StdArea areas the children of area type stdplanet
   9. Areas can now be created using create area. Also, area time/date stored in DB now.. NO MORE TIME.TXT!!!
  10. Sounder now has HOLD and WIELD, HOLD_ROOM, and WIELD_ROOM
  11. Enemy flags in conquered areas can ONLY be picked up by a warring clan IF the warring clan conquers the area away from them.
  12. teleportation spells only work between areas with same time zone
  13. New Prop: Prop_AreaForSale
  14. New Two: Goblinese, Gigantic (Thanks ThrinnTU)
  15. Dozens of new Cleric prayers, and reworked Clerics to boot.
  16. +ITEM added to zapper
  17. Several new chants, and tweeked druid classes. A 5th class is in design right now.
  18. Marriage, etc.. allows joint account access, joint property ownership
  19. New MUDGrinder Control Panel
  20. Half/Mixed races now created with pregnancy.
# Build 4.2.3 - Released Mar 01, 2004
  Shameful to be building again so soon, but there are about 10 bugs fixed in this, 2-3 of which were pretty important. Part of the risks of downloading beta builds, of course, but I still like to upload stuff that works. :)
# Build 4.2.2 - Released Feb 28, 2004
   1. Quests will be aware of the objects used by other quests so as to prevent duplicates.
   2. Masterwork items added to common skills, and some damage adjustments made. Most likely we are heading to just plain Master crafting skills soon, however.
   3. DISABLE and DEBUG flags added to INI file. They should both normally be blank, so modifying your INI is not REALLY necessary.
   4. WizInv now disabled when a subop leaves their area, ditto sysmsgs.
   5. CONSUME_PROG added to Scriptable
   6. Half-Races (via GenRace) now autogenerated due to Pregnancy. The Half Races will split the human and non-human racial attributes right down the middle.
   7. Resurrection now restores lost experience from death.
   8. Link, Lock commands restored.
   9. Permanency Spell now really permanent, but drains permanent mana also.
  10. JewelMaking common skill expanded quite a bit. Drilling common skill added.
  11. Identify message for some magic items has been greatly enhanced (by better english translation of the HaveAdjuster/WearAdjuster parms).
  12. UseSpellCast, FightSpellCast, etc.. props now accept parameters for the spells if you need them.
  13. Prop_ModExperience created.
  14. Most magic or skill-summoned creatures no longer grant experience when killed.
  15. Archons will no longer EVER fumble their skills/spells, nor will they need to eat, nor will they ever get fatigued.
  16. Outfit control now added to GenRace.
  17. NOCOMMON switch added to MOBTeacher behavior.
  18. New Fighter Skill: Point Blank Shot
  19. Two new Classes: Apprentice and Artisan. Not too sure about the second one, but the first is a really cool idea that can be expanded on by muds who want everyone to come in on the same footing.
  20. GenCharClasses created. Although I personally did not expect much use from it, one of my builders suggested that these could be a great tool not only for CM customizers, but for builders who want to create skill templates for their mobs. Anyway, just thought I'd pass that thought on.
  21. And, as always, lots and lots and lots of bugs fixed.
# Build 4.2.1 - Released Feb 15, 2004
  All this has are 5-6 bug fixes over 4. 2.0, but they are worth getting if you were among the brave who downloaded 4.2.0.
# Build 4.2.0 - Released Feb 13, 2004
   1. Mages have been toned down quite a bit. Wish is more painful, permanency more permanent (but also more painful), and armor restrictions follow mages around should they wish themselves out of their class.
   2. FieryRoom integrated (thanks Tulath!)
   3. Zapper now includes area names zapper -AREA +AREA, etc..
   4. Abilities and Commands now have casting/execution times. 0=instant, 1=instant in peace/1 tick in combat, 2 or more=delay
   5. Skill/Spells/etc.. commands now accept a level parameter to get the top of the list.
   6. ShopKeepers are stingier buyers now (by default). Charisma is even more important when dealing with them.
   7. Affects now show in columns.
   8. Relationship between abilities and class armor/alignment restrictions has been changed. See post in this group from a few days ago for more info on this important balancing maneuver.
   9. GenMob default armor settings have been adjusted upwards (2fold).
  10. Weapon default damage settings adjusted slightly upwards.
  11. Neither of the above two affects existing map mobs.
  12. Body part requirements included in many skills.
  13. Internal: Commands classes have been retooled. Interface Affect replaced by CMMsg, Host by MudHost, ExternalPlay is gone, numerous util classes moved or renamed.
  14. Conquest behavior added.
  15. Ability to set up "area" channels created.
# Build 4.1.3 - Released Jan 21, 2004
   1. Scriptable MPECHO command no longer has a syntax for "private" echoing. This brings it into conformity with standards for this command.
   2. Catching a disease no longer automatically drops followers under most circumstances.
   3. ShopKeepers now have ability to charge quest points, exp, or money for items. See the Archons guide for more info on this.
   4. Bug: modify mob <mob name> where <mob name> is a player was not presenting the player editor, but was presenting the mob editor.
   5. Help entries for abilities now shows mana/movement cost for the person getting the help
   6. The training post cost for basic stats was modified. Training up to 18 costs 1 training point, up to 20 costs 2, up to 22 costs 3, up to 24 costs 4, and above that costs 5 training points.
   7. Base Max Stat INI entry created for adjusting the base 18 system
   8. Max Stats can now be adjusted by race or class.
   9. Areas that share law files in their Arrest behavior parm will now also share warrants (wanted in one place means wanted in the other also).
  10. ProtectedCitizen behavior accepts WANDER parameter.
  11. Items of certain materials can now float on water instead of always sinking. Special thanks to thrinntu for the intense research this required.
  12. InstallationGuide updated with donated unix mysql info. Thanks!
  13. Arrest behaviors with parameter "custom" can have non-ini file customizable laws and policies using the new GenLawBook item. GenLawBook item must have the name of the area with such an Arrest behavior set up.
  14. Mining now picks up coal (duh!)
  15. Foraging now picks up more (in volume) cloth material than foodstuffs.
  16. No more horrible death crys whenever a player is autopurged.
  17. Clan Enchanter position in Clans created. They are designated spenders of Clan Experience via the Clan Enchant spells.
  18. Bug: NP exception in Snatch skill fixed.
  19. ShopKeepers who sell land will now show only properties adjacent to available rooms, adjacent to rooms not for sale, or adjacent to rooms owned by the viewer of the list.
  20. Bug: Residual CONTROL trigger word was still in the Turn Undead skill. Was removed.
  21. Duration field in Smokables set through editor is now meaningful.
  22. Bug fixed which allowed Charlatans to disguise themselves as Archons.
  23. Bug: Smoke Rings was not gaining proficiency through practice. Fixed.
  24. Bug: Default Z level will remain selected in MUDGrinder now.
  25. Bug: Can no longer backstab with a flailed weapon.
  26. Bug: Dagger was overlooked as a handled weapon type in Cleric subclasses.
  27. Bug: Can no longer KNOCK on thin air.
  28. Areas can now have parents/children. Property/Behavior support for this is still iffy.
  29. Bug: ResetWhole behavior was just flat broke. Is fixed now.
  30. Bug: Cooking: certain recipes were broke due to parsing error. Fixed.
  31. Archons can now get privileged information when using CLANDETAILS command.
  32. DESTROY CLAN <name> now works.
  33. MODIFY CLAN <name> can now be used to change a clans status.
  34. MOBTeachers now respond to GAIN LIST command.
  35. Can now specify how much you want to smelt in the Smelting skill.
  36. Masonry skill can now build pools, crawlways, windows, and portcullis
  37. Bug: From name in Journal web macros is now fixed.
  38. ShopKeepers can now have LIMIT parameter in their prejudice mask to define an absolute limit to number of items shown on LIST.
  39. Bug: Can no longer use containers to get rid of undroppable items.
  40. Bug: Weaponsmith items with ' characters in their names were renamed to avoid bugs.
  41. Shutdowns are now much more chatty.
  42. Bug: NP exception in Flank skill fixed.
  43. Bug: Avoid Traps skill now gains proficiency over time as it ought.
  44. Following now use level difference instead of absolute levels to weight difficulty: Laughter Spell, Hold Spell, Flesh Stone, Dismissal, Disintegrate, Flesh Rock, Demonic Consumption, Treemorph, Hold Animal, Domesticate
  45. Races can now be designated fertile/infertile.
  46. Bug: Racial Categories Amphibian and Fish never ever drown or need to tread water.
  47. ObjectGuardian behavior now accepts SENTINEL parameter.
  48. Bug: Most Fake and Conjured Weapons, Food, Armor, etc.. no longer have monetary value.
  49. No longer have to swim from water->land.
  50. Bug: Looks like something was fixed in Prop_PracticeDummy, but not sure what.
  51. Bug: You can now wake up in a Prop_Crawlspace room.
  52. Can no longer scrap items on private property, and scrap duration is now a function of item weight.
  53. Bug: The "You stop working" message is now gone. It was actually a bug in Smoke Rings.
  54. Can now modify player clan roles from command line player editor.
  55. Bug: Yelling from silenced rooms is now fixed.
  56. Bug: Renamed items (as in Enlarge item spell) now display properly in rooms.
  57. Virtues of balsa and ironwood now in the help files for common skills.
  58. Clans with deleted or missing bosses will now have someone autopromoted to keep the clan going.
  59. Bug: Certain crafted items could previously be repeatedly removed from banks. This is now fixed.
  60. Bug: Can no longer Distract when on the ground.
  61. Bug: Can no longer Con someone while in combat with them.
  62. Bug: Analyze Mark now autogains in proficiency
  63. Bug: Detect Ambush was over enthusiastic in its reports.
  64. Bug: Linked Health can now only be cast on group members.
  65. The following spells can now have a player/mob name placed in their parameters in order to use as a kind of hard-coded charming/following: Charm Animal, Enthrall, Charm, Sermon
  66. Bug: Folks underwater will no longer get weather reports.
  67. Archons with playerkill on will always override player preferences.
  68. Bug: Players mob objects reported as not ticking will no longer be destroyed, but will only be logged out.
  69. New Archon command: AFTER
  70. A follower may not be ORDERed if you have your autoassist turned off.
  71. NOPROMPT parameter added to shutdown.
  72. The connection spam-blocker is now in place, as described in the group message previously posted.
  73. Prop_NoPurge can now be placed on items for the same affect.
  74. Limbs now affects wear positions to an extent.
  75. Bug: Problem with amputated limbs now affecting wear positions fixed.
  76. LONG parameter to EQUIPMENT command can now be used to see all available wear positions.
  77. Carpentry, Blacksmith, Scrimshaw, and most other common craft skills can now make Bundles. Bundled resources are automatically unbundled the next time you try to make anything out of the bundle.
  78. GenLawBook item done. Set the readable text to the name of an Area which has The Arrest Behavior with parameter: custom. The Law Book can then be used to modify laws and legal policies there.
  79. Bug: Charisma now restricts not just followers, but followers followers.
  80. Internal: Healing and Experience Gains are now message types. Cap on healing is 511 hp. No cap on XP.
  81. Undead are now harmed by healing spells and healed by harming spells (though they still get mad at you for casting harm spells at them). Undead now also have immunities in addition to their resistances.
  82. Can now specify a minimum elapsed time between moves using MINMOVETIME INI entry.
  83. Summon Steed and Summon Flyer can now create angry herds.
  84. Bug: Nondetection skill fixed.
  85. Floating Disc can now no longer target the ungettable.
  86. Following spells, when worn off, can anger the formerly affected: Enthrall, Charm, Friendship, Charm Animal
  87. Files can now be specified in the readable text of a readable item or exit using FILE= syntax.
  88. Bug: Animate prayers now create more proper undead, who are 1 level lower than their minimum, who have lasting stats, and proper limbs as the body they were made from.
  89. A lower mana limit MANAMINCOST can now be set in INI file. This was previously 5 mana for all spells.
  90. Mageness, Bardness, Druidness, and Clericness behaviors now accept parameters to determine the types of spell choices made by the caster.
  91. New Chant: Chant_EelShock, and Disease: Yawns; thanks Lee!
  92. Bug: Armor Rescore button fixed in MUDGrinder
  93. Automatic mana consumption when players are affected by spells can now be set up using MANACONSUMETIME, and MANACONSUMEAMT INI entries.
  94. New Prayer: Heal Undead
  95. Lots and lots and lots of new help entries to fill in some gaps noticed by players.
# Build 4.1.2 - Released Dec 21, 2003
   1. Negative numbers may be used as prices in GenShopKeepers to allow experience points or quest points to be spent.
   2. Some tweaks to Arrest in preperation for Conquest -- note that areas which use the same laws file will now also share warrants!
   3. ShopKeepers can now place limits on their listed inventory.
   4. Areas can now have parents and children. Complete Behavior/property compatibility is still unclear.
   5. Some tweaks and additions to a few common skills and behaviors.
   6. More bug fixes than you can shake a stick at.
# Build 4.1.1 - Released Dec 03, 2003
   1. (Might have also made the last build) Area SubOps now restricted from modifying/deleting players.
   2. Completed Clan system (do HELP CLANS) for more info. Important note! Old Clans will have found that their old Bosses are now degraded to Leaders due to the insertion of the Clan Enchanter position.
   3. New Clan Commands to support the new system include: CLANVOTE, CLANQUAL, CLANDECLARE, CLANTAX, and perhaps one or two more.
   4. New Clan coffeemud.ini entries: CLANVOTEO, CLANVOTED, CLANVOTER, CLANENCHCOST
   5. New Scriptable Functions: ISALIVE, ISPKILL, NAME
   6. Updates to the LockSmith Common Skill (allows one-sided locks now)
   7. New AUTOPURGE feature for players. See the AUTOPURGE INI entry. Also try out NOPURGE, list NOPURGE, destroy NOPURGE, etc..
   8. Zapper Masks are now recognized in the ini file for channels INSTEAD of straight levels. Update your INI files!!
   9. For you coders, the max-stats are now exposed to affectCharStats, so both race and class can now affect your maximum in any stat. The values are actually a relative adjustment from base 18.
  10. JewelMaking common skill extended a bit to allow mounting/encrusting.
  11. Masonry and Construction updated for making peep-holes (windows), and crawlways.
  12. Clan Enchantment spells courtesy of Jeremy V.
  13. GenTub item created for easy bath tubs.
  14. Scriptable STAT and GSTAT related functions can now dip into CharStats, PhyStats, and CharState (STRENGTH, SAVING THROWS, HIT POINTS, SENSESMASK, etc, etc) -- so those complaining that you couldn't get at that data can now have at it!
  15. Scriptable behavior may now be added to items or rooms.
  16. New Scriptable triggers: GET_PROG, PUT_PROG, WEAR_PROG, REMOVE_PROG, DROP_PROG -- obviously to support items, but they will work for Scriptable mobs as well.
  17. Lots of bug fixes, of course, though they tended to be more minor fixes, which is why I'm sticking with 4.1 as the official build for awhile.
# Build 4.1.0 - Released Nov 20, 2003
   1. Some new Races
   2. StdSmokeable (GenPipe, GenCigar) created -- we can now smoke!
   3. SmokeRings common skill created to improve the smoking experience.
   4. Weak/No Swimmers and items will now SINK from water surfaces.
   5. Morgue rooms can now be set by deity
   6. If the subject of a journal message starts with MOTD, it will remain new for 24 hours.
   7. New wear position: ON_BACK
   8. Cityguards will now be able to Sap those they are subdueing, even those much higher level from them.
   9. Optional INI file setting LASTPLAYERLEVEL puts a ceiling on player levels gained through experience. If it is used, anyone given a level above that number by an Archon becomes immortal (can't be killed by ordinary means).
  10. Now slightly harder to lose limbs in combat.
  11. Prop_StatTrainer now accepts "NOTEACH" parameter. Property can now be used strictly for setting stats.
  12. Sitting (Tripped) mobs can now still attack, but at half rate (could not attack at all before). Sleep spell also less powerful.
  13. Player affects will now save. Added to Player editor.
  14. Player behaviors will now save. Added to Player editor.
  15. ClanID now settable from the Player editor.
  16. Deities will now more intelligently cast spells targeting items.
  17. Some Skills will now require movement instead of mana, or half of each.
  18. Generic Races created. Too much here to say -- best feature of this version. See the Archons guide for more info.
  19. Racial Abilities created. Second best feature! Some samples have been thrown into the standard races -- see Dragon for one.
  20. new Archon LIST parm: TRAILTO <room/area/roomid>
  21. new Archon LIST parm: COMMON (for common skills -- oversight)
  22. new Scriptable conditionals: ISSEASON, ISWEATHER, GSTAT, INCONTAINER
  23. new Scriptable commands: MPSAVEVAR, MPLOADVAR, MPGSET, MPENABLE, MPDISABLE
  24. Decay behavior now triggered by mounting as well.
  25. Three new DB Tables: CMPDAT, CMGRAC, CMCCAC.
  26. Slight fudge factor adjustment to combat to make high powered low level players have a harder time with much higher level mobs.
  27. Stoneskin and Magic Items spells toned down slightly.
  28. HELP entries for skills will show players their personal mana/etc cost.
  29. Training stats above 18 will now cost more than 1 train.
  30. TRESSPASS and PROTECTED entries in the laws.ini resource file (for arrest) are now zapper masks instead of race lists. If yours is custom, you need to update it!
  31. Banker data is no longer stored in CMJRNL -- it is stored in the new CMPDAT table. An Archon must enter 'reset bankdata [bank name]' to transfer your bank data from one table to the other. You need only ever do this once.
  32. New Property: AbilityImmunity (thanks Jeremy!)
  33. New Behavior: MOBHunter (thanks Jeremy!)
  34. Journal Web Macros now allow public reading.
  35. ProtectedCitizen now accepts a cityguard mask instead of simple name.
  36. New Behavior: ProtectedCitizens -- for areas
  37. New Behavior: CombatAssister -- mask catch all for same room mob assisting.
  38. Can now set individual prices on ShopKeeper goods. Use -1 to keep using the stock pricing system.
  39. New Archon List parm: ENVRESOURCE
  40. And, of course, lots and lots and lots of bug fixes.
# Build 4.0.11 - Released Nov 04, 2003
   1. Jeremy's MAPLEVEL fix was integrated.
   2. New Races: Centaur, Aarakocran, Gnoll
   3. New Resources: ENERGY, DUST, PLASTIC, NUTS, FLOWERS, RUBBER, EBONY, IVORY, WAX, BREAD, CRACKER, YEW, PIPEWEED
   4. DEITIES command now shows short list by default.
   5. Channels now support socials and emotes! Use CHANNEL :smile
   6. WIZEMOTE command created.
   7. New Web Macros: SocialTbl, DeityNext, DeityData, DeityID, AbilityPowersNext, AbilityCursesNext, AbilityBlessingNext
   8. New Player WHERE command -- shows the best areas to level in based on the players level and alignment.
   9. New Command: POUR
  10. New Deity updates, including CURSES and POWERS! Also, several new Deity triggers.
  11. "reset smalleropendoors" command added -- recommended for those using the old ROM imported areas -- it patches in a memory optomization for exits imported from ROM compatible areas. Only needs to be done once.
  12. New exit type: OpenDescriptable
  13. New spells: Enlightenment, Feign Invisibility (my fav), Flaming Ensnarement
  14. New Prayers: Philosophy, Atonement, Corruption, Moral Balance, Mass Cure Disease
  15. Nifty new Unix script support! Thanks Wolf!!!
  16. New INI file entry: FOLLOWLEVELDIFF
  17. There are now limits on what players can ORDER someone to do.
  18. Room title/descriptions can now vary by weather, time, season. See the Archon's Guide for more information.
  19. New xZapper checks: stats and multiclass checks.
  20. Mobile behavior now has a leash.
  21. New Behavior: MOBReSave
  22. List Deviations archon command created.
  23. Lots and lots and lots of bug fixes, including MovingRoom
# Build 4.0.10 - Released Oct 17, 2003
   1. by default, all mobs will have their NOTEACH flag on. To get a mob to teach, you must give the mob either MOBTeacher behavior, or Prop_StatsTrainer property.
   2. The Z-level mapping in the MUDGrinder is now optional.
   3. Oh, and a silly MOBTeacher bug was fixed...
# Build 4.0.9 - Released Oct 16, 2003
   1. Much more flexible character class editor part of the player editor from the command line. (modify playername)
   2. New Scriptable trigger: BRIBE_PROG
   3. New Scriptable command: BREAK
   4. More very important enhancements/bug fixes to the import *.are feature (the feature that lets us import rom/madrom/icey/circle/smaug/etc.. areas.
   5. New player death method on the ini file: ASTRAL. The player, to continue, must be resurrected with either the song of rebirth, or the prayer raise dead.
   6. Several new spells, most of them new "mass" spell, as well as fatigue, detect traps, and shockshield.
   7. New Behaviors: RandomTeleporter to make a mob that zaps around. Also RandomTraps to set random traps around an area.
   8. Yes, CombatAbilities (and by extension Clericness, Mageness, etc) was very broken. But it's fixed now.
   9. Several new races..
  10. Another new INI entry: PAGEBREAK for allowing global pause/page breaks for players. An individual player-setting for this feature will have to wait, however.
  11. New Deity trigger: reading (duh!! how did I miss this one?!?!)
  12. Grid rooms (GridLocales) will now link to other grids more intelligently.
  13. New StdJournal flag: PRIVATE to allow private-only journals.
  14. New Archon command: STAT
  15. Old Archon command TRANSFER now has new functionality.
  16. LIST SUBOPS will now list your area archons
  17. Area help is now more friendly to those trying to type for it.
  18. MUDGrinder map editor is now multi-level. Thanks Jeremy!
  19. For all zapper checks (all those properties/behaviors/whatever that allows masks), there is now +NAMES, +CLANS, and -CLANS to mask for clan membership.
  20. MOBTeacher behavior will now allow percentages.
  21. Prop_StatTrainer will now allow individual stat settings.
  22. Prop_Doppleganger will create mobs whose level reflects its enemies
  23. New Common Skill: Lacquerring
  24. Numerous old damage-based spells rebalanced for level.
# Build 4.0.7 - Released Oct 08, 2003
   1. Player bodies are now protected when using the morgue feature.
   2. BrotherHelper now accepts a parm to limit the fray.
   3. AT command added for Archons and area subops.
   4. LOTS and LOTS of new support for area importing added!!
   5. Two Weapon fighting changed as previously discussed.
   6. Assault/Murder rules changed as previously discussed. There is a new RESISTINGARREST crime in the laws.ini.
   7. Scriptable support for TATTOOs added. Also, better alternate format support for IF conditions added. I won't go into what that means in detail, but if you notice that mobprogs from other muds don't give as many errors.. that's why.
   8. Fixes in MOBEater and CombatAbilities (specifically with wand use) make those behaviors much more useful.
   9. Minor changes in rejuv rates for hp/mana/mov weigh more heavily now on stats than on level. Not changing this when coffeemud went from a 25 level system to a 100+ system was an oversight.
# Build 4.0.6 - Released Oct 02, 2003
  Yep -- more bug fixes, numerous new races added for madrom are file import support, as well as tinkerings here and there to make the system a happier one.
# Build 4.0.5 - Released Sep 15, 2003
   1. Spell Siphon added at Enchantment/17
   2. Archons will now find everything with Locate Object -- even unlocatable things.
   3. QUIET flag added to quest scripts to stifle certain log messages. It's undocumented I think.
   4. Area SubOps can now use export command, targeting the screen.
   5. RandomMonsters behavior can now accept XML directly in the parms.
   6. Decay behavior now accepts "zapper" masks for who may trigger it.
   7. Several (~10) spells/chants/songs/prayers renamed to make the help files happy. Their class names are the same, just their game-names changed.
   8. Dozens of spells/chants/etc. now have more "public" uninvocation messages than they did before. In other words, when a spell/etc wears off, more of them will make a public proclaimation of such instead of a private tell.
   9. CONSIDER command -- as player advances in level, the range of levels for each of the consideration messages widens.
  10. Detect Magic spell is now MUCH more powerful than it was! Check it out!
  11. Some tweeks to Falling affect that involved water.
  12. Prop_PeaceMaker can now affect mobs and accept personalized strings.
  13. Harpy replaces Griffon as top-level Avian for Druids
  14. Two new zapper masks added (-player -mob)
  15. ShopKeepers now exempt from the item GET level rule.
  16. CorpseEater behavior now accepts zapper masks.
  17. AntiVagrant now has ANYWHERE and KICKOUT flags.
  18. BOOT=mycoffeemud.ini command-line parameter now accepted. See installation guide for info on it.
  19. Several various bug fixes in various areas.
# Build 4.0.4 - Released Sep 19, 2003
  *  Like every version thus far, this one is a hodgepodge of various bug fixes we are finding as we play test. Nothing real serious.
  * The changes to Decay and RandomMonsters discussed in the group were worked in tho.
# Build 4.0.3 - Released Sep 15, 2003
  No need to wait . . there it is. Bug fixes, some playtest balancing stuff done to the druids (including adding a few more chants 
  to bring two of them up to speed), the new hp calculation mentioned previously (without comment), and yes, the channel 
  color thingy.
# Build 4.0.1 - Released Sep 10, 2003
# Build 4.0.0 - Released Sep 05, 2003
# Build 3.8.8.9 - Released Aug 29, 2003
  *  Only changes are the aforementioned genmob bug, and stretching the weather-bother from 90 secs to 6 mins (was annoying me).
  * The fix to the genmob was well worth tossing out another build, as this bug has been around for a few weeks now and was wreaking havoc.
# Build 3.8.8.8 - Released Aug 29, 2003
   1. Titles are now READABLE.
   2. WIZLIST created
   3. New Sculptibles (yea, that's minor -- but were really scrapping the bottom for new stuff to list...)
   4. New Scriptable commands/funcs: MPDAMAGE, MPTRACKTO, MPBEHAVE, MPUNBEHAVE, MPAFFECT, NUMITEMSMOB, MOBITEM, NUMITEMSROOM, NUMMOBSROOM, ROOMITEM
   5. New Chants/Spells/Prayers: Sense Sentience and Detect Sentience, Sanctify Room, Arcane Mark, Arcane Possession, Boomerang
   6. New INI Entries: COMMONTRAINCOST, COMMONPRACCOST, SKILLTRAINCOST, SKILLPRACCOST, LANGTRAINCOST, LANGPRACCOST
   7. New Behaviors: Wimpy, Sounder (CHECK THIS ONE OUT PEOPLE!!!!!)
   8. WIZINFO internal channel created
   9. Arrest Sentences are less when mobile is aggressive or a trouble maker (thiefness). This is intended to give newbies a break when arrested for assault after a thief or aggressive picks a fight with them.
  10. ARCHON GUIDE Updated (up to Items, I think)
  11. INSTALLATION GUIDE Updated (includes Upgrade guide)
  12. new DBUpgrade utility in the FakeDB package allows upgrading/copying CoffeeMud database from and to ANY versions(even 1.2).
  13. Prop_ClosedDayNight has LOTs of new parameters now making it MUCH more powerful. Check this out too!
  14. Languages have a translationHash(String language) method to return word for word translations.
  15. Bad weather will now (every 90 seconds or so) blurb to users who are outdoors.
  16. The True Sight spell can now see through disguises and polymorphs
  17. Import code now supports MADROM "sounds" codes, as well as ROM/ROM2/MADROM external mob prog script importing.
  18. Prop_RoomWatch created for watching events in rooms you aren't in.
  19. Prop_Transportable now supports rideables.
  20. and LOTS and LOTS and LOTS and LOTS of bug fixes. :)
# Build 3.8.8.7 - Released Aug 22, 2003
   1. Geas spell is done.
   2. The .cmare format for inter-area links changed ever so slightly. If you want to reimport the stock area files, you'll need to download them again.
   3. New Scriptable commands (forget their names -- have to do with iterating)
   4. *** Socials.java and Social.java have been moved to the common folder. This means you must blow away your old "com" directory when installing this new build.
   5. Default mud port changed to 5555 from 4444 -- note this in your MUD clients! (4444 will still work, it just makes a bad default port since it is shared with a virus)
   6. Parolees can no longer leave the area in which they were parolled to.
   7. REPORT expanded to include skills
   8. For those wishing to use JNT, "defaultShutdown()" method created in MUD.java.
   9. New debug info related to "tick" status (internal).
  10. RandomMonsters has a new LOCALE TYPE parameter (optional)
  11. LOTS and LOTS and LOTS and LOTS of bug fixes.
# Build 3.8.8.5 - Released Aug 15, 2003
   1. Memory optomizations: new INI entry COMPRESS. MOB interface changed.
   2. Friends/Ignore list moved from journal to cmchar database, so old friends/ignore won't work.
   3. Ability interface changed again (internal)
   4. Programming Guide has had some new sections added..
   5. Aggressive changes made (masks, not attacking when archon present, etc)
   6. Quests mob selection now has more sophisticated masks
   7. Called strike can now (secretly) go for the head on 50% hp hit.
   8. -names added to zapperCheck
   9. Items created with common skills credit their maker in secret identity.
  10. Artisan class created
  11. Functions now possible in Scriptable
  12. Minstrel subclass finished!
  13. MORGUE ini entry created
  14. can now modify users, including their stats, regardless of onlineness.
  15. Saving throws now saved in the database
  16. Scriptable can now include multiple load= statements.
# Build 3.8.8.4 - Released Aug 08, 2003
   1. SCAN parameter added to many common skills.
   2. Behavior ItemIdentifier now does much much more.
   3. Scriptable: FUNCTION_PROG, MPBEACON, MPCALLFUNC, and MPALARM added.
   4. TRANSFER Archon command added.
   5. Quests will now clean up any behaviors/abilities/affects added to map mobs or items
   6. WebMacros now documented in the WebServer guide
   7. AFK command added, and auto-AFK implemented.
   8. Behavior tutor added to the Programmer's Guide
   9. NOTRAIN command added
  10. (Internal) ability to have secret skills added. See Beastmaster for example of numerous "secret" skills. These are skills a class qualifies before, but no MOBTeacher teaches, and is never listed in QUAL or help.
  11. New spells added to help with protecting homes.
  12. attack/armor descriptions extended AGAIN to 500/600 pt system. Ugh!
  13. Main Menu button added to MUDGrinder area editor -- BTW the MUDGrinder has LOTS of bugs fixed, and it will even run a tad faster.
  14. affects list will now show what language you are speaking (if not common)
  15. Base MANA cost now in the coffeemud.ini file -- update your ini file!!
  16. GoodyBag updated -- get a new one!!
  17. Fatigue system implemented. Won't bother you like hunger/thirst will, and takes a LOOONG time without sleep to even show up.
  18. AUTODRAW will now also autosheath! Yea!
  19. LinkedWeather behavior added.
  20. Can now create dirs in scripts browser.
  21. Multiple gets/drops/etc... spam is now consolidated on display.
  22. Can now do get 15 pie to get fifteen pies, drop, give, etc...
  23. ReScore button added to item editor in MUDGrinder area editor.
  24. FRIENDS, IGNORE, and AUTONOTIFY commands added.
# Build 3.8.8.1 - Released Aug 01, 2003
   1. GenMobs now have their Hit Point modifier default to 11 instead of 0 -- very important!
   2. Corpses are now Generic Items, meaning all relevant info about them will be saved.
   3. Spell_Mirage can now accept a roomID parameter
   4. Most Admin Web macros no longer active before the MUD completes booting.
   5. Journal Browser in MUDGrinder completed.
   6. Player info in MUDGrinder now shows correct Armor, and MAX Damage amount.
   7. Wear-restrictions bug fixed in the Races. Sorry Druids!
   8. Quest Manager added to MUDGrinder
   9. Script/file editor added to Quest Manager. This is a secure system -- no worries! Not only is the file editor an Admin macro, requiring login, but it only allows browsing of MOUNTed directories.
  10. Prop_WeaponImmunity created.
  11. ShopKeeper's now give a lot more info on VIEW
  12. Regeneration property/skill now accepts parameters of excepted damage.
  13. POST fixed -- all Admin/MUDGrinder web macros now use POST to transmit data -- slightly more secure.
  14. Archon's will now take half the time to login.
  15. MOBTeacher mobs will now take an infintesimal time to load. This will improve your boot times bigtime!
  16. Armor/Combat descriptions now extended to a 400/500 point system. More descriptions added to compensate.
  17. Can now refill a lantern before it runs out.
  18. Command word parsing significantly changed: XCommands now have precedence. Ability evoking words will give way to socials or other commands if the mob does not have the skill. Exact wordings will always have precedence over partial wordings.
  19. Couple of new Scriptable functions: EVAL and NUMBER
  20. MSP question removed from character creation. Let them turn it on if they want it.
  21. Behaviors: ClanHelper, FaithHelper, BrotherHelper, and RaceHelper will now attack mobs if necessary.
  22. Behaviors: Aggressive, WimpyAggressive, MobileAggressive, and VeryAggressive now accept a parameter "MOBKILLER" to expose mobs to their wrath.
  23. New Arrest changes: laws.ini flag "ARRESTMOBS" exposes mobs to all laws except nudity and the armed citizen law.
  24. Weapon/Armor guide added to the Archon's Guide -- see Appendix C
  25. StdBanker's now default to CHARGE interest for holding money instead of paying it -- this is only a default. Your old GenBankers will be unaffected, and you can still change it to a positive interest rate.
  26. And, as always, numerous bugs fixed.
# Build 3.8.7.9 - Released Jul 25, 2003
   1. Redefinable ESCAPE codes created. See the new INI file.
   2. Items held and wielded may now be dropped or given, if specified by name (not included in ALL, etc)
   3. MSP Sound support, including SOUNDS/NOSOUNDS commands to turn them on/off.
   4. New Jester skills created. Jester class complete.
   5. Item interface (internal) remove() changed to unWear(), destroyThis() changed to destroy()
   6. MOB interface (internal) removeFromGame() and giveItem() added.
   7. Material bonuses will now be weighted based on worn location for Armorsmith. Material bonuses updated for all common skills that use them.
   8. Features list updated.
   9. SOUNDSPATH INI file entry added to support MSP download feature.
  10. Light Source durations extended.
  11. Instrument Making common skill created.
  12. AUTOIMPROVE command created.
  13. WebMacro support for HELP files created. See default public page and default admin pages for exampled.
  14. Resource Manager WebMacro created. See MUDGrinder.
  15. Player Browser WebMacros created. WARNING! Loading players, especially Archon's, can be SLOW! See the MUDGRinder.
  16. Banned List management WebMacros created. See MUDGrinder.
  17. WebMacros may now be embedded in each other. I'll update the Web Server doc eventually, but in short, it's done like this: @WebMacro?@@WebMacro2?@@@WebMacro3@@@ @@ @
  18. System Reports WebMacro created, along with Ticks reports. See the MUDGrinder!!
  19. Log file WebMacro created -- see default admin page.
  20. KNOCK command created
  21. Can now fish from any room with FISH as a resource.
  22. GateGard will now open doors to knocking. Also, ALLNIGHT and KEEPLOCKED parameters created to affect gategard behavior.
  23. Journal Browser WebMacros HALF done -- see the MUDGrinder for progress.
  24. Builder's FAQ just rolled into the Archon's Guide
  25. And last, but not least: Numerous and assorted bugs, all reported by this group, were fixed.
# Build 3.8.7.8 - Released Jul 18, 2003
   1. Web server now supports post, thanks to Jeremy.
   2. StdMaps (and GenMaps, and thus, Map Making) all support "sections" in the maps.
   3. MAJOR internal change to Environmental interface. Name() counterpart to name() created to reflect an unmodified name(). New methods support Disguise and other preexisting skills.
   4. Charlatan bard subclass created, along with Disguise, False Arrest, and lots of other fun skills.
   5. Internal changes to CharStats and Race interfaces to mask their names as well, using new method displayClassName
   6. Drunken language created, and appropriate spells/songs/poisons updated.
   7. Last call added to AUCTION, time shortened, and listing bug fixed.
   8. PuddleMaker behavior created.
   9. Scriptable: TIME_PROG, DAY_PROG created. ISTIME, ISDAY functions created.
  10. EnlargeRoom may now be applied to areas.
  11. Bug fixed in Traps, so that players cannot trap each other without playerkill flags
  12. Some new Druid chants created... control weather, tap grapevine, and others.
  13. Lasso's and Nets created (StdLasso, StdNet)!
  14. Weaving common skill created for lassos, ropes, and nets, etc..
  15. Oracle subclass updated for post level-30 skill bonuses (oversight...)
  16. MAJOR Internal change: ID() field will now ALWAYS return the class name of the object, even for Rooms and MOBS. roomID() field created for rooms, and Name() already supported MOBS.
  17. New spell (Shove) and barbarian skill (bullrush) created.
  18. Bard 30th level skill (Ode) created. Very fun. :)
  19. Armorsmith and similar skills updated so that armor awards reflect locations.
  20. Numerous common skills updated to better reflect material strengths and weights.
  21. Prancer Bard subclass created, along with his numerous Dances.
  22. Exp base award raised from 60 to 100. ANY COMMENTS on this??? Should it be in the INI file?
  23. Jester bard subclass started (about 95% there), with numerous new skills: joke, slapstick, pupeteer, lay minor traps, and many others...
# Build 3.8.7.7 - Released Jul 05, 2003
   1. Various bugs fixed.
   2. Fish caught with fishing will be gettable from a boat.
   3. EMAILREQ ini file entry added
   4. Archive File Name added to areas for making area backups easier.
   5. Rate of increase of amount needed per level decreased from 125-75.
   6. reRoll() method moved from CharStats to Race (internal)
   7. MPSTARTQUEST command added. (forgot to document! -- works like MPENDQUEST)
   8. Storage of dates in CMJRNL and CMCHAR table changed!!! The database has *CHANGED* However, users of fakedb will be unaffected.
   9. First new Bard subclass (Charlatan) started, but not completed. Some new skills came with him though.. like Songcraft, Shuffle, Warrants, Immitation, etc..
  10. AUCTION system created!
  11. GoldLooter and CorpseLooter behaviors added
  12. Prop_OpenPassword property created (but not tested yet) :/
# Build 3.8.7.5 - Released Jun 21, 2003
   1. Herbs resource added, and made beneficial to Cooking skill
   2. Couple bugs fixed involving the contraction of a cold.
   3. Other assorted bug fixes as always...
   4. New Common Skills: Herbology, Herbalism, Plant Lore
   5. Scores of new Druid chants! There are now more chants than prayers!
   6. Prompt color removed from colors list -- coloring of prompts made easier.
   7. Mana calculation changed again to give credit for levels taken in diff qualifying classes.
   8. New Properties to restrict/reward riding of Rideables
   9. Races now designate body parts for benefit of other code.
  10. 3 new Druid subclasses added.
  11. MOB Followers are now saved to database
  12. Archons now exempt from INstantDeath
  13. Key Ring container type created
  14. New Property "Prop_RoomView" for looking to other rooms easily
  15. (ys) joins (es) and (s) as special english text codes
  16. Damage now displayable as a number -- INI file entry supports this now
  17. Search Common skill created -- allows 3 seconds of detect hidden in same room
  18. Search Thief skill REPLACED! Replacing it is Thief Detection skill
  19. New Behavior: ProtectedCitizen for screaming city folk
  20. Mobile behavior now supports Locale parameters for setting roaming limits!
  21. Clan support (beta) added, along with Builder's guide entry, and StdClanMaster mob
  22. MPAT Script command now works properly
  23. Email address for players now saved
  24. Channels, TELL, REPLY, and GTELL commands now all subject to effects of language, and spells which affect language (Song of Babble, and spells of comprehension)
# Build 3.8.7.4 - Released Jun 06, 2003
   1. LIST USERS sorting ability now smarter for level/age
   2. Social bug fixed
   3. ;; bug fixed
   4. Traps help entries added
   5. Disease help entries added
   6. Mana requirements chart stretched a little more for all abilities
   7. New Behavior: ResourceOverride allows custom resources for ANY room!
   8. LOTS of changes to the ARREST Behavior. New LAWS.ini entries. Cityguards will now TAKE a person to jail, as well as release them from jail by taking them to a specified place. Also, more than one jail room may be specified!
   9. LAWS.INI file now fully documented
  10. New Common Skill Marketeering (Merchant) allows players to put their goods up for sale at any price.
  11. BUY, SELL, VALUE, LIST, VIEW, DEPOSIT, WITHDRAW now actually work as documented as far as parameters go. LIST will now list all other shopkeepers in the room, not including oneself.
  12. ActiveTicker bug fixed (thanks Jeremy)
  13. Base Druid class tinkered with a bit in preparation for new stuff coming next week.
# Build 3.8.7.3 - Released May 30, 2003
   1. Upgrade to the Emoter behavior
   2. Journal text fixed so that SQL statements won't be offended.
   3. Code (ys) joins (s) and (es) as "smart" suffixes to english words. (ys) will turn Study into either Study or Studies.
   4. Chance of catching several diseases lowered.
   5. Help files now loaded by directory instead of by enumerated list. All help files must end with .ini and must begin with "arc_" to be an archon help file.
   6. Mobs can now follow each other, but players still cannot follow mobs unless enchanted.
   7. Poison help added -- I'll do diseases & traps next time.
   8. WHO and I3 WHO now no longer lists possessed mobs
   9. Can now GIVE to shopkeepers
  10. Materials now listed in Armorsmith items
  11. Some tinkering with the HELP files for affects/behaviors in the MUDGrinder
  12. All class lists guarenteed sorted, and internal binary search now used for class lookups.
  13. Last room occupied now saved to DB
  14. WoodRoomMaze, WoodRoomGrid now created
  15. Quitting in combat now does a FLEE consequence first.
  16. Flee consequence now definable in the INI file.
  17. Grid room init fixed.
  18. Thief "Set Alarm" skill now uses a radiant algorithm instead of a tracking algorithm -- much better!
  19. UNLOAD can now be used to unload classes
  20. LOAD can now be used to load individual classes or directories
# Summary of all bug msgs for all builds
   1. And, as always, numerous bugs fixed.
   2. LOTS and LOTS and LOTS and LOTS of bug fixes.
   3. and LOTS and LOTS and LOTS and LOTS of bug fixes. :)
   4. More bug fixes than you can shake a stick at.
   5. Enough bug fixes and java optomizations to frighten small children.
   6. A few minor bugs fixed in some of last builds features. :)
   7. As always, several bugs were fixes. And hopefully not many new ones added. :/
   8. More bugs fixed, introduced, fixed again, reintroduced and made worse, and finally fixed than could possibly be imagined.
   9. A heaping sloppy glob of bugs fixed.
   10. And, as always, a ton of fixed bugs
   11. More bug fixes than digits in pi.
   12. A huge hot steaming smelly pile of bugs carted away.
   13. Several creepy little bugs turned to jelly beneath my thumb.
   14. Drippling globs of bugs siphoned from the code.
   15. An enormous cloud of millions of swarming bugs dissipated with the swirling winds of correction.
   16. Lots of dead bugs, including the QUEEN!
   17. A few assorted bugs were crushed up against the windshield of circumstance.
   18. A snow-capped goat playground MOUNTAIN of bugs fixed.
   19. sumbugzfixt
   20. A horde of marauding bugs beaten back with the ingenious use of banana peels and cat droppings.
   21. A growth of malignant bugs was eradiated.
   22. 1/2 cup of debug labor sprikled within 1 foot radius of entire bug mound. Approx 1 qt of water per cup of debugging.
   23. Somewhere, there are fixed bugs.
   24. A whole circus of bugs tamed, and hopefully as few new ones introduced as possible.
   25. A tall helping of bugs gobbled up.
   26. So many bugs were fixed, it would hurt us both to list them.
   27. More bugs fixed than stars in the sky.
   28. All the bugs thats fit to squish!
   29. A few bugs dutifully informed of their transgression, judged by a ruthless programmer, and sentenced to a quick death.
   30. Bugs detonated underground, mysteriously.  Wild claims about bug strength forthcoming.
# Build 3.8.7.2 - Released May 22, 2003
   1. Tweek to weight of items imported from .ARE files.
   2. Thief TRAPS debugged
   3. Thief BOMBS created, along with Bomb Making skill
   4. Lots of bugs fixed, including some with followers, and with Deity Blessings.
   5. CONFIG command created to review your flags
   6. Social added: MATE (removing this social will disable the whole sex system)
   7. Pregnancy and Aging added. Not an elaborate system, but perfectly harmless.
   8. Idle timer added, replaces location on the LIST SESSIONS report
   9. Players/mobs will not accept bombed or trapped items GIVEn to them (gives a purpose to the thief Plant skill).
  10. New Diseases added -- Disease system completely implemented. All diseases are now linked throughout the engine, and are catchable one way or another.
  11. Patroller behavior added
  12. TicketTaker property added
  13. BaseCharClass* web macros added
  14. PUT OUT (lightsource) will now work
# Build 3.8.7.1 - Released May 14, 2003
   1. Diseases (certain ones, at least) can now spread through infected meat or blood from butchered bodies. Dead bodies can also spread certain diseases.
   2. Numerous new Poisons. Making a poison is now easier! Just create something drinkable, and add the poison class as an affect. Look in coffee_mud/abilities/poisons (or LIST POISONS) for more info.
   3. Couple new alcohols as well (they are listed under poisons).
   4. Emoter features added.
   5. Four new thief subclasses (no help on them yet) -- arcanist, burglar, assassin, and trapper, in addition to the new improved Thief.
   6. Dozens and dozens of new thief and fighter skills to support the new thief subclasses. Just too many to mention! Argh!
   7. New common skills, apothecary, distilling.. at least I think they're new.
   8. The Mageness, Druidness, Fighterness.. etc behaviors can now accept subclass names as parameters.
   9. Start rooms fixed, Followers FINALLY debugged
  10. INI file entry for gen-editor type
  11. 'hello works like ' hello now.
  12. Climb now requires standing
  13. Dozens of new traps.
# Build 3.8.7.0 - Released Apr 27, 2003
   1. You can no longer order a follower to attack a non-pkill player.
   2. Changes to the Gap exit, and weak bridge and ledge properties. They work much better now.
   3. New Property: Prop_UseSpellCast2 -- similar to Prop_UseSpellCast, but triggers differently. See help.
   4. Prop_MagicFreedom selects the room to echo errors into differently now.
   5. Numerous new diseases -- most "unlinked" with the engine, but will be linked in a future build. More on the way.
   6. Infectuous diseases now carry in corpse and butchered meat of a diseased corpse.
   7. Making too many room moves in a single 4 second period will now be more exhausting!
   8. GO, WALK, RUN, commands now accept numbers and multiple directions as parameters!
   9. Monk defensive bonus fixed.
  10. MOBTeacher will now evenly disperse total levels among the several classes, and now properly has all abilities to teach in multiple-class scenarios.
  11. Can now pick up very heavy things with Floating Disc.
  12. New spell: Elemental Storm
  13. New command: UNLOAD, for unloading help, all, and specific resources. Use LIST RESOURCES.
  14. A lot more data is stored in Resources now instead of in local classes to support UNLOAD.
  15. (Internal) Now using Enumerators instead of Iterators to get a little better performance out of accessing maps.
  16. (Internal) CMClass class lists now accessed by enumerators to better hide implementation (abilities are hashed now)
  17. New Behavior: RandomMonsters -- for generating random monsters in a room or area
  18. CREATE and MODIFY commands for Gen* objects no longer give a wizard, but a menu! Much cleaner and easier to use...
  19. (Internal) New Interfaces: Tickable, MsgListener, StatsAffecting used to merge different access interfaces. See programmers guide for more info on changes to standard boolean tick(int tickID) and boolean okAffect( and void affect( methods. Aside from updating your method signitures, you will NOT need to update your code! No functionality changes, just signiture changes that will make future implementations easier (and made some implementations in this build easier).
  20. EXTENSIVE extensions to the Scriptable behavior to support Quests.
  21. New QUESTS lists -- see HELP QUESTS. Commands Create, modify, destroy, save, and lists support the quest lists.
  22. New table ADDED: CMQUESTS. Users of fakedb need not worry, there were no changes to old tables!
  23. Archon staff/wand now requires an Archon to wield, and wielding alleviates hunger/thirst
  24. New Locale types: Road, RoadGrid, MountainsGrid, MountainSurfaceGrid
  25. Emoter behavior fixed.
# Build 3.8.6.8 - Released Apr 19, 2003
   1. Bugs fixed in the new Grid sizing stuff
   2. New Behavior: RaceHelper -- does exactly what ClanHelper USED to do (allow mobs to assist others of the same race)
   3. Changed Behavior: ClanHelper -- will no longer assist based on race, but on Clan allegance now. Can also be used to make mobs member of Clans.
   4. Tattoos now a part of the zapperCheck
   5. Flying is now difficult in storms
   6. Announce sysop command added
   7. Prop_SparringRoom added for deathless fighting by players
   8. MOBs with Mobile behavior now properly disperse in a grid
   9. Painting common skill added
  10. Jeweller shopkeeper fixed.
  11. New shopkeeper types added (mostly resource based)!
  12. Spell-domains can now be used in QUALIFY command
  13. Scrapping skill added
  14. Prop_SpellReflecting added for making spell-reflecting mobs and items
  15. Arches can now be made by the mason
  16. Weather can now cause minute rust on metal armor/weapons
  17. New Diseases added, MANY more to come!
  18. New Behavior: FaithHelper to have mobs help each other of the same faith. can also be used to give a mob Faith.
  19. Prop_Trashcan -- nuff said
  20. Linefeed bug fixed for telnet users of the mud
  21. New Chants: Alter Time, and Move Sky (changes day<->night)
  22. New Behavior: WaterCurrents
  23. New Behavior: InstantDeath
  24. New Fighter Skill: Opponent Knowledge
  25. WHISPER command added
  26. Listen thief skill updated to allow hearing speech in an adjacent room, or whispering in the same room.
  27. Yell now affects adjacent rooms
  28. New Skill: Haggle
  29. New Common Skill: Taxidermy
  30. GapExit created for crevasses narrow enough to jump
  31. BUG FIX: Aggressive zapperChecks were backwards... fixed now
  32. Prop_WeakBridge created for exits/rooms
  33. Prop_NarrowLedge created for exits/rooms
  34. Archon Staff and Wand of the Archons now alleviates hunger/thirst
# Build 3.8.6.7 - Released Apr 12, 2003
   1. Some tinkerings with intelligence requirements for gaining skills.
   2. Some tinkerings with the adjustedLevel of spells/prayers. Both of these benefit players.
   3. New DRESS command for putting clothing on followers
   4. New UNDRESS command for taking clothes off followers
   5. New FEED command for giving food/water to a follower
   6. Bankers no longer keep items given to them for deposit
   7. Internationalization Scripts directory/structure changed a bit so tha
   8. New Rideable Item type: WAGON! Use the MOUNT/DISMOUNT command to attach wagons to horses or other large rideable mounts. Then hope in and away you go!
   9. New Common Skill: Wainwright (For making wagons, chariots, etc..)
  10. Common Skills will now look for the BEST material to make a thing out of, instead of the first available material.
  11. Weapon damages updated in the Weaponmaking skill.
  12. Mime behavior created.
  13. (Internal) Socials are now Environmentals, which means you can know when a Social is being performed by looking at the message Tool.
  14. (Internal) Iterators now used for navigating CMMap things.
  15. New socials: pillow, smurf
  16. New Skill: Map Making (Bard)
  17. Grid rooms can now have customized dimensions, this includes any StdGrid derived locale type (StdMaze, WoodsGrid, WoodsMaze, etc..)
  18. Grinder and command-line support for #17 added.
  19. ** Scriptable Behavior has been debugged and (90%) tested. Documentation for this behavior has been added to the Archon Help files. Sample scripts provided! Your mobs are now scriptable!
  20. Certain races can now be designated as illegal in your Arrest areas (trespassers). See the default laws.ini for more info.
  21. ResetWhole behavior created -- allows you to make an Area or Room reset entirely on certain intervals, instead of on a mob-by-mob basis.
# Build 3.8.6.6 - Released Apr 07, 2003
   1. A dozen or so new Prayers
   2. Cleric subclasses are DONE: Templar, Doomsayer, Shaman, Oracle, Necromancer, Missionary, Purist, and Healer
   3. New ShopKeeper type: CLAND_SELLER -- only sells to clans (not to individuals)
   4. You now sit AT a table, not ON one (duh...)
   5. Bugs fixed in Arrest/Handcuff
   6. New common skill: FARMING
   7. ** Internationalization of CoffeeMud has gone through a proof of concept phaze. New Scripts utility to handle translation. New INI file entries specify which of a set of scripts files locates in your resources directory to use. So far, only the Movement.java class has been done. That might be all I'll ever do, hoping on help from others to do the rest. :)
   8. New/old command: BEACON. Allows you to change your start room, or to change the start room of someone else.
   9. The DESTROY command now understands ALL all. and .1, .2, .3 ..etc
  10. You can EXPORT room, area, or world items, mobs, weapons, or armor into SPECIAL CMARE files.
  11. You can now IMPORT items/mobs from the special item/mob CMARE files into your current room.
  12. MERGE command as discussed above.
  13. StdJournal and GenJournals can now have READ and WRITE requirements specified instead of using the level of the item alone. See the Archon's Guide for more info on how to do this.
  14. New property Prop_RoomsForSale allows you to sell a set of adjacent rooms as a group instead of individually.
# Build 3.8.6.5 - Released Mar 31, 2003
   1. DEITIES command to list all known deities, along with their requirements, blessings, and rituals.
   2. CLANACCEPT, CLANAPPLY, CLANDETAILS, CLANEXILE, CLANHOMESET
   3. CLANLIST, CLANREJECT, CLANRESIGN
   4. I3 SILENCE implemented, a fix put in I3 ADD (still does nothing!)
   5. Bugs fixed in MudChat
   6. Dozens of self-only spells reengineered to allow casting on others when done with a wand (or as a blessing!)
   7. Help entries for Mage subclasses added.
   8. Cleric Subclasses (so far: Templar, Shaman, Doomsayer, Necromancer)
   9. Help entries for above Cleric subclasses
  10. Most prayer casting strings modified to integrate the NAME of the god chosen by the cleric.
  11. Dozens of new prayers, and some renamed to take advantage of a new schema. Please clear out your olde COM path before applying the new version!
  12. SaveThread now watches TelnetSessions for inactivity! How did I miss that?!
# Build 3.8.6.3 - Released Mar 25, 2003
   1. Fixed some bugs in the new LADDER Rideable type.
   2. New THROW command to get ladder-types (ropes, etc) into pits to help comrads. The THROW command can also be used with THROWN weapons, as well as grenades at some point in the future.
   3. Ability to AUTODRAW the second weapon if you have Two Weapon Fighting
   4. Created MAXSTAT INI entry to allow more easily changing the starting state distribution for players.
   5. Created SKYSIZE INI entry to allow more easily changing the default dimension of EndlessSkys
   6. INI File entries ITEMS, MOBS, BEHAVIORS, ABILITIES, etc.. now REQUIRED! You should default them all to %DEFAULT%. See the coffeemud.ini file in the distribution for more information on this.
   7. Recursive bug fixed in sky creation that was filling up the stack whenever someone tried to load 14,000 rooms. :)
   8. FAKEDB ResultSet updated to implement getRow, beforeFirst, and last methods. Now functional!
   9. Bugs fixed in Swipe, Steal. Also, proficiency leak plugged in Swipe, Steal, Bribe.
  10. Entire WORLD can now be exported/imported as a .CMARE file.
  11. Unique World/Area/Room WEAPONS, ITEMS, ARMOR, MOBS can now be exported as a .CMARE file. They cannot be imported yet, however. Still trying to figure out how that is going to work.
  12. Feather Fall spell now traps attempts to pick up more than you can carry. This is the world's oldest CoffeeMud bug, kept in because of extreme popularity with players.
  13. Against my better judgement, Remove Curse now actually removes non-droppability and non-removability from items.
  14. Spell lists updated for the Import of ROM/CircleMUD/etc area files.
  15. RoomForSale will now update room MOBs on shutdown of system, provided the MOB is 1. not rejuvinating, and 2. has the room as a starting room.
  16. Prop_Retainable property added to make MOBs 1. not rejuvinating, 2. to make their current room the starting room, and 3. Cost more/less when purchased by shopkeepers. Recommend this be applied to MOBs sold by shopkeepers.
  17. Proficiency leak plugged in detect/remove traps.
  18. Bug fixed in grantAbilities of Mage class.
  19. LOTS more done on the Scriptable Behavior. Still havn't had a chance to test/debug it, but presumably it is done now (or, at least hacked).
  20. MUDGrinder's ability to properly identify mobs and items being edited has been greatly enhanced, thereby also enhancing it's general reliability.
# Build 3.8.6.2 - Released Mar 19, 2003
   1. *LOTS OF NEW DATABASE CHANGES!!! BEWARE*  IP address, clan name, and clan role to players, ride entry for mobs
   2. Big additions to DoorwayGuardian, HaveZapper, WearZapper, and all Aggressive behaviors: classes, races, gender, levels, and lots more can now be masked.
   3. MOBTeacher expanded to allow teaching of multi-class skills
   4. Prop_ReqEntry property replaces Prop_ReqClasses, Prop_ReqAlign, Prop_ReqRaces, Prop_ReqLevels, etc..
   5. Optimization: milli-longs now used instead of Calendars
   6. Optimization: compression of MOB miscText field to save memory
   7. DoorwayGuardians now guard more than just Doors...
   8. Help System for Spells, Skills, Songs, Chants, Prayers improved GREATLY -- now resembles the web sites more.
   9. New GoodyBag item contains a pill for almost anything an Archon may want to give a Player. Each item will replace itself once removed.
  10. New BOOT command to kick someone out of the game.
  11. New BAN command to ban user names and ip address. LIST BANNED and DESTROY BANNED also supported.
  12. Bug fixes, spelling corrections, blah blah blah..
  13. Some additions to the ORDER command to support clans.
  14. Optomization: Commands classes made static
  15. A few new fighter skills to support ranged weapons.
  16. A few new spells to make hungry and thirsty mages/archons happy.
  17. Also, fun new spell: Animate Weapon
  18. New Common Skill: Shipwright
  19. New Common Skill: Speculate
  20. Wake now accepts a mob parameter
  21. AUTOGUARD command/flag to say whether you follow your group members -- intended more for mob followers.
  22. New Common Skill: Paper Maker (makes blank scrolls too!)
  23. Common Skills now support sheaths: Armorsmith, Leatherworker, Tailor
  24. Common Skills now make specific item containers (like piggy banks that only hold coins, etc)
  25. Tents, Sleeping bags added to Tailor
  26. New Rideable type (LADDER) added to assist those with these items in going up climable surfaces.
# Build 3.8.6.1 - Released Mar 17, 2003
   1. There are around 50 new spells, most of which are specialist mage spells. The most amusing of these are Breadcrumbs, Big Mouth, Illusory Disease, and Add Limb.
   2. In addition there are dozens and dozens of bug fixes!
# Build 3.8.5.9 - Released Mar 13, 2003
   1. Create [new] form of the create command is gone. Replaced with the Copy command. Create still works in its other forms, however.
   2. Copy command allows creation of multiples!
   3. Fixed bugs in the new START room parameters!
   4. The new Cogniportive spell for making title's into transport wands.
   5. ShopKeepers can now cast spells on items you are wielding/holding.
   6. Arrest now has 4 levels of parole as a sentence.
   7. Public nudity can now be against the law.
   8. Walking around armed can now be against the law.
   9. Cityguards now dispense threats and warnings, instead of the judge.
  10. Public inebriation can now be against the law.
  11. Lots of bugs in Sneaking fixed.
  12. Spells cast on items now no longer save with the item. Spells added using the CREATE or MODIFY or MUDGRINDER still save though.
  13. Fixed bugs that made the order death/end of combat and fleeing occurred work correctly.
  14. Can now sneak past room/exit requirements unless NOSNEAK parameter included.
  15. NUMEROUS new Specialist Mage classes. Specialist-only spells are next in line.
  16. RACE entry now in the guide, and Classes updated.
  17. New Prayers added, and Cleric balanced out a bit.
  18. Bugs in I3 fixed.
  19. New I3 command replaces MUDLIST and I3CHANNELS, and adds others.
# Build 3.8.5.8 - Released Mar 10, 2003
   1. StdShopKeepers (in their misc text field) and GenShopKeepers (in a special prejudice field) will be able to modify the prices, up or down, of the goods they offer based on racial category or alignment, or just for everybody if you don't like the stock formula.
   2. Seperate start and death rooms will be specified in the INI file based on alignment or racial category.
   3. GateGuard and DoorwayGuardian can now modify their behavior based on the same criteria as the Prop_HaveZapper property (based on racial category, class, base class, or alignment). You'll be able to use these seperately or in combination to make gate guards more friendly, or outright mean.
   4. A new Behavior FasterRoom will be able to increase the number of rejuvinating ticks for mobs in the room or area affected by it. In addition, you can also set it up to increase the number of times the mobs in the rooms tick, which will affect spell effects, combat, and everything else that ticks. Either one or both of these can be set using the FasterRoom behavior.
   5. And lastly (I think), the mud will support listening on any number of specified ports.
# Build 3.8.5.7 - Released Mar 07, 2003
   1. New bubbleAffects boolean on the Ability interface to make Affects passed on by Items much more elegant.
   2. MASK_HURT bug fixed and improved to allow damage up to 1023.
   3. MudChat updated to allow includes (Thanks Jeremy!)
   4. Racial categories added to Race interface to allow Racial Enemy abilities for Rangers.
   5. Rangers and Druids no longer divide experience among animal followers, assuming they either deliver the killing blow, or are the "first player in line".
   6. Wish is now a 30th level Mage spell.
   7. Dual Parry is now spelled right. :)
   8. Cover Defence Fighter skill added.
   9. Dodge now only works against non-ranged/thrown weapons (melee).
  10. Craft Holy Avenger skill now 30th level Paladin
  11. Favored Enemy 1-4 skills added for Rangers.
  12. Woodland Sneak and Woodland Hide skills for Rangers.
  13. Woodland Lore skill added for Rangers
  14. BARBARIAN Class created!
  15. MONK Class created!
  16. Animal Frenzy now 30th level Ranger skill.
  17. GoodGuardian Behavior now a little more good
  18. Experience requirement now grows by only 25exp after 25th.
  19. Monk skills: Atemi Strike, Ax Kick, Body Flip, Cartwheel, Catch Projectile, Circle Parry, Circle Trip, Deflect Projectile, Flying Kick, Ki Strike, Knife Hand, Monkey Punch, Lightning Strike, Pin, Return Projectile created.
  20. Barbarian skills: War Cry, Battle Cry, Rally Cry, Charge, Shrug, Stone Body, Spring created.
  21. Bugs in Wind Gust and Repulsion fixed.
  22. ** Players with AUTOMELEE Off will now spend 25 movement and as many Attacks as required to retreat to the range of their wielded weapon. Very Cool! Archers are now much more viable!
  23. CHARGEN changed to GENCHAR to avoid confusion with CHARGE skill.
  24. Bard's may now use Non-Metal armors instead of Leather or worse.
  25. Massive bug fixed! Damage adjusted by skills/spells will now actually change the message to reflect new damage!
  26. OverTheLedge Locale created, thanks Jeremy!
  27. HELP for the TRAIN command updated for subclasses.
  28. Very high-point damage messages extended to 300 from 95.
  29. Programmer's Guide has a section on Character Classes now!
  30. Journal date bug fixed?
# Build 3.8.5.4 - Released Mar 03, 2003
  The only new features are the addition of the Amputation Ability, a few new Cleric Prayers (one of which restores Amuputated limbs), and the updated Programming Guide.
# Build 3.8.5.0 - Released Feb 24, 2003
   1. Added I3STATE INI file entry to get a more accurate display of your muds status on the I3 mud list.
   2. Added VIEW command for shopkeepers.
   3. ShopKeepers, Money Changers, and Bankers will give change as change bags. They will now consolodate their change bags with any you already have in your inventory. ShopKeepers will now also accept money directly from change bags for purchases!!!
   4. Changed the ^^ color code (stop reverse) to ^.
   5. Made ^^ a code for inserting a "^" into text
   6. About a gazillion bug fixes, including the shopkeeper translation bug.
   7. Implemented the SubClass notions mentioned previously. Two Database changes, both in CMCHAR: CMLEVL is now a string (50) and CMCLAS is now a longer string (200).
   8. Extended the TRAIN command to gain new classes. When a new class is gained, the user will start off level 0 of that class. They must level to become level 1 in their new class. They retain all previous skills, and qualification access to all previous class skills.
   9. Added some new INI entries: CLASSSYSTEM (to choose between a multi-class, subclass, or a straight one-class system), PLAYERKILL (to make playerkilling always on, never on, or optional), PLAYERDEATH (to make the death of a user cost a level, experience, or their players very soul), and EXPRATE - to change Poet's experience bonus/penalty rate.
# Build 3.8.0.0 - Released Feb 20, 2003
   1. Memory optomization of the classes (especially Abilities). CoffeeMud memory usage cut almost in half!
   2. A global News feature was added. Need to add instructions about that though -- basically, you just create a Journal (GenJournal item) and name it "CoffeeMud News". Writing on it will cause new messages
      to appear automatically for the players.
   3. InterMud3 support is pretty new, though that might have made 3.0...
   4. Player homes are now supported, through a new ShopKeeper type called LANDSELLER. Rooms in the same area as the shopkeeper with the Prop_RoomForSale or Prop_LotsForSale property will be available for
      purchase.
   5. Construction, Masonry, and LockSmith common skills were created to support player homes.
   6. Hireling Behavior created to support hiring people to follow you and obey ORDERs. HIRE and FIRE commands created to support this.
   7. PROMPT and COLORSET commands created to allow players to change their prompts and to personalize the color scheme! Beware! This added two new fields to the CMCHAR table!
   8. SHEATH and DRAW commands created to support a new property of containers: contain type. Containers may contain anything that will fit in them by default, but with this new property, containers can
      restrict what they can contain.
   9. AUTODRAW command created to support the above.
   10. Armor now extends Container, allowing armor to be a sheath.
   11. Numerous unremembered bug fixes. :)
# Build 3.0.0.0 - Released Nov 23, 2002
   1. ANSI support is new
   2. CoffeeMud now has a web server! Yipee!
   3. Add Area objects and Tables and Affect/Properties!
   4. Allow classes and directories to be assigned in the INI file in addition to the default path.
   5. Create a custom command ability, include ability to override existing commands!
   6. Add Notes abilities -- really need for bug fix reporting! Done this way: a Journal can be read, it will list the posts for a given Journal.
   7. Druid : a good new base class
   8. hit and miss strings in the Weapon. Natural Weapons can also be retreived from MOBs, or from Races by default.
   9. From Maree: (Game/Heath Texts), the descriptions for the health of a MOB should be moved to a text file (A resource?), that is loaded. The descriptions would be assigned to the MOB at build time, and should be assignable to GENMOBS via a new tag.
  10. ranged combat?!Yes!And thrown weapons
  11. And ranged pole-arms!
  12. Add CoffeeMud area files!can import and export rooms or areas
  13. Languages! Affects says and replies if you don't know a language spoken by the speaker.
  14. Make mountable MOBS, and mountable Items. Mounted animals will take half of all combat damage, will add to attack ability, and increase movement and will act as a container for the purposes of cargo. Mounted Items will not appear in inventory, and be restrained to a terrain type (air, water, etc). More than one person can mount an item (a boat).
  15. Armor damage/repair, especially cloth from fire, and metal armor from bludgeoning/leather armor from slashing.
  16. banks!(Well, the moneychanger behavior is good enough).
  17. exp from sac/des also based on level of the body. Set body levels based on char level.
  18. Serve and Rebuke commands. Any character may take on a leige. A Leige receives 10% of all experience gained by the vassels, providing incentive to assist the vassel.
  19. Weapon condition, sharpness, and bluntness Weapon repair and sharpening/blunting.
  20. Crawl command, and crawlspace property for rooms and exits
  21. Furniture.. chairs to sit in, beds to lie in, tables to put things on? It's all riding...
  22. Convert some properties to work with the new areas. Prop_NOMOB, etc..
  23. make room properties also work for exits
  24. Multi-Room Item Transporter. Have a TeleportingContainer that, whenever anything is inside it, it will send the items to the appropriate GateObject. The GateObject can be either a container or not. If not, the GateObject will drop its cargo on the floor. The TeleportingContainer and the GateObject will have identical MiscText fields to establish the Keys between them.
  25. Forget spell to make someone forget a skill they try, at random!
  26. Predict Weather spell
  27. Multi-Room Gate would work the same way. An Entry, Exit, and TwoWay type would be constructed. The Entry type would accept "ENTER" commands from MOBs, and drag all followers along, of course. The Following thing would be automatic, if an Exit and Direction extensions are used.
  28. genbed, gentable
  29. ranger skills: find water
  30. kill commmand to switch among targets fighting you
  31. Two-handed fighting
  32. NonVagrant behavior to wake up those sleeping on the streets
  33. Meditation for Mage should be like Endurance for Fighter.
  34. Add last-call date/time string to the "list users" command
  35. be able to drink or fill waterskin from freshwater locales
  36. PlayerKill flag, properties for nopkill areas, rooms, etc.. (and pkill ONLY areas, room, etc..)
  37. vassels command to list the ones who serve you (even those which are online)
  38. Distract ability (can't fight during) to make an enemies scores worse...
  39. Finish implementing height -- make armor that doesn't fit! Armor can init at height 0, and adjust to the very first wearer.
  40. make sure you can create pills that generate experience points
  41. thieves skills -- silent autoloot, silent autogold (thief will autoloot and autogold "off the top")
  42. die of hunger, thirst
  43. appraise skill to give weight, base value, and material of an item
  44. Lore skill for Bards.. allows an identify for them.
  45. Exit/Area/Room property to only let in mobs with certain tatoos (a property that keeps track of one or more tatoos). A property to grant a tatoo when a mob is killed, or an item picked up.
  46. add parameter to spells command to list by school
  47. More stuff for Fighters, especially Paladins and Rangers, who must use their trains for spells. Sweep would make a good Fighter ability, as would cleave!
  48. iron grip spell to thwart disarm
  49. Buy command for standard mobs, to facilitate trades.
  50. race based wearing restrictions, and a "become" method to enforce them (and clothing size);
  51. resources property for rooms
  52. HuntableAnimals that produce pieces of raw meat in inventory when killed. (Resources implemented instead)
  53. Hunt ability for rangers to find them.
  54. QUAL should only list what you DON'T have -- it's too long.
  55. 0-level abilities. Trade skills to produce mundane items. One would go to a "factory", where one could buy raw materials. Facilities available there would be used, along with your skill, to produce a finished product..e.g. a shirt, pants, minor weapons and armor, etc. Mining, Fishing, Woodcutting, etc.. to get raw materials. Cooking to improve the nourishment of Food objects (and remove rawness from Meat). The LeatherWorker ability can mend leather items, Tailor cloth items, etc..
  56. Sap skill for thieves to knock someone out who can't see them.
  57. Binding skill for thieves to bind someone who is asleep (knocked out).
  58. MUDGrinder web service
# Build 1.25(2.5) - Released Feb 21, 2002
# Build 1.1.0.0 - Released October, 2001
  ! AFFECT ANSI ARCHELP ARCTOPICS AREAS AUTOASSIST AUTOEXITS AUTOGOLD AUTOLOOT BACKSTAB BASH
  BERZERK BREAK BRIBE BUG BUY CAST CHANNELS CHECK CLIMB CLOSE COMMANDS COMPARE
  CONSIDER COPY CREATE CREDITS DESCRIPTION DESTROY DETRAP DIRT DISARM DOWN DRAIN DRINK
  DROP DUMPFILE EAST EAT EMOTE EQUIPMENT EXAMINE EXITS FILL FLEE FOLLOW GET
  GIVE GO GROUP GTELL HANDS HELP HIDE HOLD IMPORT INVENTORY KICK KILL
  LINK LIST LISTEN LOCK LOOK MODIFY NOANSI NOFOLLOW NORTH OBSERVE OPEN ORDER
  OUTFIT PASSWORD PEEK PICK POISON PRACTICE PRAY PRAYERS PULL PUSH PUT QUALIFY
  QUIET QUIT READ RECALL REMOVE REPLY REPORT RESCUE RESET REST REVOKE SAVE
  SAY SCORE SCROLLCOPY SEARCH SELL SING SKILLS SLEEP SNEAK SOCIALS SONGS SOUTH
  SPELLS SPLIT STAND STEAL SWIM SWIPE SYSMSGS TAKE TEACH TELL TOPICS TRACK
  TRAIN TRAP TRIP TURN UNLINK UNLOADHELP UNLOCK UP VALUE WAKE WEAR WEST
  WHO WHOIS WHOMP WIELD WIMPY WRITE XML YELL
# Build 1.0.0.0 - Released Mar 23, 2001
  ! AFFECT AREAS BACKSTAB BASH BERZERK BREAK BRIBE BUG BUY CAST CHECK
  CLIMB CLOSE COMMANDS COMPARE CONSIDER COPY CREATE CREDITS DESCRIPTION DESTROY DETRAP DIRT
  DISARM DOWN DRINK DROP EAST EAT EMOTE EQUIPMENT EXAMINE EXITS FILL FLEE
  FOLLOW GET GIVE GO GROUP GTELL HANDS HELP HIDE HOLD INVENTORY KILL
  LIST LISTEN LOCK LOOK MODIFY NOFOLLOW NORTH OBSERVE OPEN ORDER PASSWORD PEEK
  PICK POISON PRACTICE PRAY PRAYERS PULL PUSH PUT QUIET QUIT READ RECALL
  REMOVE REPLY REPORT RESCUE REST REVOKE SAVE SAY SCORE SCROLLCOPY SEARCH SELL
  SING SKILLS SLEEP SNEAK SOCIALS SONGS SOUTH SPELLS SPLIT STAND STEAL SWIPE
  SYSMSGS TAKE TEACH TELL TRACK TRAIN TRAP TRIP TURN UNLOCK UP WAKE
  WEAR WEST WHO WHOIS WHOMP WIELD WIMPY WRITE
# Project officially started Feb 24, 2000
