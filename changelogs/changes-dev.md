Version dev

==== **Bug Fixes:** ====

* /bind now properly allows usage of shortened skill name identifiers (ablade, parrow, mshield, etc)
* Fixed issue with bukkit permissions not being set if Permissions3 wasn't detected
* SuperPerms is now fully supported - permission skills now work at all times
* Player's displaynames will be updated properly when they choose a new path or reset.
* Player HP is now ceiled when synched preventing high HP classes from dying before they actually hit 0 HP. - for real
* creatures added in 1.8 will no longer crash the effect system
* Player's no longer get XP for killing themselves
* XP is only granted for Survival game-mode (creative mode will not award XP)
* Creature effects should no longer crash the effect manager
* Fixed SpoutUI hooking
* Tamed Pets & Summons will now award Experience for their kills as intended
* Player inventory now synchs properly when an item is removed
* Satiated state will now regenerate a % of a hero's HP as intended, rather than the default half-heart (configurable)
* Crafting Xp is now awarded each time an item is crafted regardless of Cursor status
* Crafting XP is no longer awarded with a full inventory when shift-clicking to craft
* Arrow damage is now altered for bow draw - minimum damage is 1/3rd what is set in the configuration

==== **General:** ====

* Uses ExperienceBar built into MC
* Added /hero admin level - command to adjust a player's level directly.
* /hero admin exp - is now additive and supports negative values. (it no longer sets the players xp to the value)
* Classes can now have multiple parents via Strong and Weak parent settings
* Classes must have 'user-class: true' to be added to the permission 'heroes.classes.*' 
* ExperienceOrbs will no longer drop by default - they can be turned back on in the config, but they wont do anything while heroes is active
* Hero recovery items have been removed - all items are now dropped to the world instead - players will need to recover their items before you update, they will become inaccessible
* Economy Support has been re-written - now supports iCo4/5/6 + Bose + EssentialsEco
* Spout is no longer used for inventory restrictions, 
** players will be able to equip the item, but if they try to use it it will be removed.  
** Armor will be removed if the player takes damage.
* Added option to prevent players from switching classes till they have mastered their current one.
* Added option to prevent max tiered players from switching classes.
* Food & Saturation levels will now be set to max (20) when a hero levels up.
* Some Damages are now percentage based:
** FALL damage: .05 for 5% health per 1 meter dropped, etc.
** SUFFOCATION damage: .05 is vanilla 
** DROWNING damage: .1 is vanilla
** STARVATION damage: .05 is vanilla
** LAVA damage: .2 by default (20%)
* Resistances are now checked internally in the HeroDamageListener

==== **API:** ====

* New 
** SkillManager - skills are now loaded into the SkillManager rather than the CommandHandler
** Skills are now loaded on Demand (only when a class has them)
** CombustEffect - it's used for tracking who last applied FireTicks to a player
** FormEffect - only 1 can be active a time. work like 'stances'
** ImbueEffect - like FormEffects but are weapon related to prevent classes from stacking multiple weapon buffs at one time
* SafeFallEffect is now universal - the damagemanager now checks if the hero has the effect before allowing fall damage
* InvisibleEffect is now universal - it will automatically break on damage & use events
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

==== **Skills:** ====

* All:
** Skills that add FireTicks should now award XP from deaths caused by Fire.
** Stun effects now cause the victim's screen to wobble
** Poison & Disease effects now turn the victims health-bar yellow
** delay - all skills that are used via command/binding can now be given a 'warmup' - the time is in milliseconds
** Summons now clear properly when changing classes.
* Become Death - (NEW!)
** You become like a zombie, and no longer need to breath air
** Undead will not target you for the duration of the skill
* Berserk - (NEW!)
** Uses new FormEffect
** Causes the hero to deal more physical damage, but take more damage.
* Bite
** Now also applies a Bleed effect
* Blaze
** Will no longer skip targeting all enemies if one of them can't be targetted.
* Cleave - (NEW!)
** Short range melee attack, that requires a weapon.
** Damages all enemies near your target for weapon damage * multiplier
* Confuse
** Screen now wobbles when under this skill's effect
* Deconstruct
** Fixed exploit that allowed a player to deconstruct items over the damage threshold
* Deep Freeze
** No longer double-messages when the effect shatters.
* Disarm - (NEW!) - thanks Kostronor!
** Disarms your opponent for the duration and prevents them from re-equipping weapons.
* Endurance - (NEW!)
** Uses new FormEffect
** causes the hero to take less damage from all sources, and deal less physical damage
* Forage - (NEW!) - thanks jetfan
** Player can forage for items
** items retrieved will be based on biome type the player is currently in
** default items will be added to all biomes
* ForcePull
** Pulls the target to the ground right in front of the caster, rather than a fixed amount of distance
* Garrote - (NEW!)
** May only be used while stealthed or invisible
** Silences the target and deals damage
** Requires the skill to be bound to string
* Gift - (NEW!)
** Sends an item you're holding to another player
* Herbalism - (NEW!)
** Passive skill that gives double-drops for herb related stuff
** Also allows players to have a chance to pickup dead shrubs & tall-grass
* IceArrow - (NEW!) - thanks jetfan
** Imbues your arrows with ice, causing them to deal weapon damage + slow your enemy for a limited time.
* Impale - (NEW!)
** Does weapon damage to the target at medium range
** Launches the target into the air.
* Jump
** Now requires the player to be on Solid ground when used - this also means they can't be in a vehicle
** Has an option to turn air-jumping on/off
* Mining - (NEW!)
** Passive skill that has a chance to give double-drops when mining ores
** Gives a very small chance to drop a random ore from just stone.
* One - (READDED!)
** Provides a speed boost to the caster
* Plague
** Fixed bug that caused plague to use the original duration rather than the remaining spell time when spreading to nearby enemies
* Purge - (NEW!)
** Acts as an Area of effect targeted purge.
** Still requires a target like dispel, or will target self if cast with no target
* Quake - (NEW!)
** Deals damage in a radius from where the hero lands from a fall.
** damage is a % of the damage taken by the hero
* Quicken - (NEW!)
** Long duration movement speed increase
** Removes/Cancels when the player takes damage
** Party-wide buff
* Slow - (NEW!)
** Slows down the target's movement speed
** Also slows the targets's swing speed
* Smoke
** Now uses InvisibleEffect - will be removed if the player takes damage or interacts with the world
* SoulBond - (NEW!)
** Allows the caster to absorb some damage from the target, allowing the target to live longer
* SoulLeech - (NEW!)
** Slowly drains your enemies health, when the effect expires it heals you for a percentage of the damage done
* StealEssence - (NEW!) - thanks Multi
** Steals random beneficial effects from the target up to the specified number
* TransmuteOre - (RENAME!)
** XMuteOre is now named TransmuteOre - it has been re-written to function better
** Now has an option to force the player to be targeting a furnace
* Tumble - (NEW!)
** Passive skill that reduces fall damage
* Web
** Now makes a diamond shape rather than a +. Will extend 2 blocks each direction, it now also extends down 1 block in case the target square was in the air.
* Wisdom - (NEW!)
** Group buff that increases mana regeneration by a set multiplier.
* Wolf
** Fixed Wolves in unloaded chunks not despawning properly when a player exits
** Now reset properly when a player changes out of a class that has wolves
* Woodcutting - (NEW!)
** Passive skill that has a chance to give double-drops when chopping logs
