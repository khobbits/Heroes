Version 1.3.0

==== **Bug Fixes:** ====

* /bind now properly allows usage of shortened skill name identifiers (ablade, parrow, mshield, etc)
* Fixed issue with bukkit permissions not being set if Permissions3 wasn't detected
* SuperPerms is now fully supported - permission skills now work at all times
* Permissions for classes are now checked after the hero is loaded - this means that classes can now grant permissions for other classes, or themselves
* Player's displaynames will be updated properly when they choose a new path or reset.
* Player HP is now ceiled when synched preventing high HP classes from dying before they actually hit 0 HP. - for real
* creatures added in 1.8 will no longer crash the effect system
* Player's no longer get XP for killing themselves
* XP is only granted for Survival game-mode (creative mode will not award XP)
* Creature effects should no longer crash the effect manager
* Tamed Pets & Summons will now award Experience for their kills as intended
* Player inventory now synchs properly when an item is removed
* Satiated state will now regenerate a % of a hero's HP as intended, rather than the default half-heart (configurable)
* Crafting Xp is now awarded each time an item is crafted regardless of Cursor status
* Crafting XP is no longer awarded with a full inventory when shift-clicking to craft
* Arrow damage is now altered for bow draw - minimum damage is 1/3rd what is set in the configuration
* PUMPKIN is now an allowed armor type as intended
* SpoutUI displays have been disabled as the SpoutAPI is still changing (with plans to re-add them)

==== **General:** ====

* Dual Classes!
** Classes can now be tagged as primary: true or secondary: true
** players can only have the class selected once even if it works for both.
** skills that both classes share should take the 'better' of each classes settings
* Permission-Skills MUST now be added to the new permission-skills.yml
** You must define them as you would normal skills
** Names are case sensetive
** usage, permissions, must be defined at the minimum
** to add a permission skill to a class you must still add the name and level node to the class that contains it
* Uses ExperienceBar built into MC
* Added /hero admin level - command to adjust a player's level directly.
* /hero admin exp - is now additive and supports negative values. (it no longer sets the players xp to the value)
* Classes can now have multiple parents via Strong and Weak parent settings
* Classes must have 'user-class: true' to be added to the permission 'heroes.classes.*' 
* Class configurations are now split into their own files.
** Please use the Class Reformater to convert
** BACKUP FIRST!
* ExperienceOrbs will no longer drop by default - they can be turned back on in the config, but they wont do anything while heroes is active
* Hero recovery items have been removed - all items are now dropped to the world instead - players will need to recover their items before you update, they will become inaccessible
* Economy Support has been re-written - now supports iCo4/5/6 + Bose + EssentialsEco
* Spout is no longer used for inventory restrictions
** players will be able to equip the item, but if they try to use it it will be removed.  
** Armor will be removed if the player takes damage.
* Added option to prevent players from switching classes till they have mastered their current one.
* Added option to prevent max tiered players from switching classes.
* Food & Saturation levels will now be set to max (20) when a hero levels up.
* Most Environmental Damages are now percentage based:
** FALL damage: .05 for 5% health per 1 meter dropped, etc.
** SUFFOCATION damage: .05 is vanilla 
** DROWNING damage: .1 is vanilla
** STARVATION damage: .05 is vanilla
** FIRE damage: .2 by default
** LAVA damage: .2 by default
* Resistances are now checked internally in the HeroDamageListener
* pvpLevelRange setting limits how close in level players must be to pvp
** uses total levels in all parent classes in addition to the hero's current class
** will use the larger of the primary or secondary class

==== **API:** ====

* SkillManager - skills are now loaded into the SkillManager rather than the CommandHandler
* Skills are now loaded on Demand (only when a class has them)
* New SkillType - STEALTHY - can be used without breaking Invisibility - though the skills sideeffects may break it (like damage)
* CombustEffect - it's used for tracking who last applied FireTicks to a player
* FormEffect - only 1 can be active a time. work like 'stances'
* ImbueEffect - like FormEffects but are weapon related to prevent classes from stacking multiple weapon buffs at one time
* SafeFallEffect is now universal - the damagemanager now checks if the hero has the effect before allowing fall damage
* InvisibleEffect is now universal - it will automatically break on damage & use events
* SneakEffect is now universal
* getRemaingTime added to expirable effects
* TargettedSkills now have built-in - PvP/Party/Summon checking.
* PeriodicEffect is now PeriodicExpirableEffect - there is now a non-expirable PeriodicEffect
* All effects now have applyTime
* setIdentifiers for skills now uses VarArgs, please adjust accordingly (it's unecessary but cleans up the look)
* Effects can now have mobEffects (potion effects) use addMobEffect(id, ticks, amplitute, faked)
** Faked mobEffects are only sent to the player and are not resolved server-side
* Player - Location death map has been exposed.  It is now in the Util class instead of being only in the Revive skill
* Fall damage can now be adjusted before the heroes damage listener is called, not just cancelled
* New EffectTypes for tagging effects as Resist_TYPE - they will block any harmful skill of the type.
* WATER_BREATHING effecttype for tagging an effect as providing water breathing
* INVIS & SNEAK effecttypes 

==== **Skills:** ====

* All:
** Skills that add FireTicks should now award XP from deaths caused by Fire.
** Stun effects now cause the victim's screen to wobble
** Poison & Disease effects now turn the victims health-bar yellow
** delay - all skills that are used via command/binding can now be given a 'warmup' - the time is in milliseconds
** Summons now clear properly when changing classes.
* Confuse
** Screen now wobbles when under this skill's effect
* Excavate - (NEW!)
** Allows the player to instant break dirt type blocks (sand, gravel, etc)
** Works with any drop modification plugins, and Logging
* Herbalism - (NEW!)
** Passive skill that gives double-drops for herb related stuff
** Also allows players to have a chance to pickup dead shrubs & tall-grass
* Jump
** Now requires the player to be on Solid ground when used - this also means they can't be in a vehicle
** Has an option to turn air-jumping on/off
* Mining - (NEW!)
** Passive skill that has a chance to give double-drops when mining ores
** Gives a very small chance to drop a random ore from just stone.
* One - (READDED!)
** Provides a speed boost to the caster
* Repair - (NEW!)
** Allows a user to repair their items 
** Configurable levels to gain repairing of different item groups (weapon, armor, shears, fishing-rod, etc)
* Slow - (NEW!)
** Slows down the target's movement speed
** Also slows the targets's swing speed
* Smoke
** Now uses InvisibleEffect - will be removed if the player takes damage or interacts with the world
* Web
** Now makes a diamond shape rather than a +. Will extend 2 blocks each direction, it now also extends down 1 block in case the target square was in the air.
* Wisdom - (NEW!)
** Group buff that increases mana regeneration by a set multiplier.
* Wolf
** Fixed Wolves in unloaded chunks not despawning properly when a player exits
** Now reset properly when a player changes out of a class that has wolves
* Woodcutting - (NEW!)
** Passive skill that has a chance to give double-drops when chopping logs
