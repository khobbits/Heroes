Version 1.2.5

Bug Fixes:

	Fixed NPE when instantiating bedheal
	BedHealEffect wont expire as quickly
	Fixed cooldown command displaying skills that were ready to use not ones on cooldown.
	Fixed admin commands erroring when attempting to modify offline players.  They will simple warn the user now.
	Who & Reload commands now work from console as intended
	Paths command will no longer show Paths the player can not select
	Summons will no longer double-up/persist through a restart.
	Permission-Skills were sometimes not being resolved properly after an admin swapped a players class
	Fixed using 0 damage values not working as intended
	Hero is now saved when a player chooses a class
	
	
General/API:

	Updated Default classes.yml to be more descriptive
	Hero Help will no longer display commands the player does not have access to.
	DiseaseEffect - works just like Bleed/Poison effects
	Commented the Hero class
	Added EffectType Enum - removed Classes to determine an Effects type, please convert to enum for type checks.
	Added SkillType Enum - to provide better skill interactions
	Adjusted targetting for skills - should be slightly less buggy, and allow for less instances of through the wall
	Classes can now override the default max level. Valid values range from 1 to the default max
	Invuln, Root, Stun & Silence effects are now standardized as players can only logically have one at a time.
	Added Hero.hasEffectType to streamline effect checking
	Each class can be given its own cost
	Added the firstSwapFree option to the config
	Added spawnCampRadius/noSpawnCamp to config - server can now configure to deny xp near Spawners

Skills:
	
	All
		- can no longer target dead creatures/players
		- Fixed many instances where skills were not awarding the kill to the player
		- health-cost node added to Skills - will deduct the amount of health before using the skill
		- All skills now have SkillTypes
		- Stuns now Properly block skill usage
		- Disables now also prevent skill usage (except counters)
	Bandage
		- no longer inherently requires Paper, it is now set as a default reagent instead
	Blight - (NEW!)
		- Diseases the target, when they take damage from the blight effect, it will also damage all nearby players.
	Chakra - (NEW!) - Thanks Multitallented!
		- Heals nearby party members and dispells harmful effects!
	Chant - (NEW!)
		- Heals the target for the amount specified - defaults are lower than Pray - intended as a lower level Heal
		- This allows healer classes to have more than just pray as a flat heal
	Consume
		- Should now be case-insensetive when attempting to use the skill.
	Decay - (NEW!)
		- Dispellable periodic damage effect (similar to Bleed & Poison)
		- It's a disease - Noh Waih!
	Deconstruct
		- items can now be deconstructed if they are not in your hand via /skill deconstruct item
	DeepFreeze
		- roots the target in place and deals a small amount of initial damage
		- If the target takes fire damage while the effect is active they will 'shatter' doing massive damage and removing the root effect
	FireArmor - (NEW!)
		- Ignites a players armor causing attacks against them to have a chance to light them on fire, (melee only)
	ForcePush
		- Fixed invalid messaging causing NPEs
		- Fixed directionals
	Harmtouch
		- No longer deals double-damage to targets.
	Hellgate
		- Fixed group members not being teleported
	IronFist - (NEW!)
		- Short-range AoE that also performs a mild knockback.
	Plague - (NEW!)
		- Disease effect that spreads to nearby enemies when it deals damage!
	Pulse
		- Fixed it so if one target was immune it would still effect everyone else
	Recall
		- Can no longer be used when rooted
	Root
		- No longer circumvents PvP
		- Can now be applied to creatures
	Silence - (NEW!)
		- Affected hero will not be able to use skills that are 'silencable'
	SoulFire - (NEW!)
		- Ignites a players weapon causing attacks with it to have a chance to light their target on fire (Melee only)
	Wolf
		- Tamed wolves will now adhere to max-wolves restrictions
		- Tamed wolves will now have the proper statistics as defined in the skill configuration
		- Will no longer spawn underground and take damage