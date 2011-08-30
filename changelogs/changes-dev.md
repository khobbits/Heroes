Version Dev

Bug Fixes:

	ProjectileDamages properly use the entity-name for their damages now (ARROW, EGG, SNOWBALL)
	Fixed ALL: skill-setting causing issues when Administrators set it improperly.
	Bedhealing no longer immediately heals.
	Multi-World default settings populate properly now

General/API:

	Class-Based XP loss on death
	Added setting for experience loss to incur level-losses
	ExperienceGainEvent is now ExperienceChangeEvent
	HeroLevelEvent is now HeroChangeLevelEvent
	hero.gainExp now fully supports negative values
	ExperienceType.DEATH added
	HeroDamageCause is now added to the hero object when the hero is damaged.
	Added HeroDamageCause, HeroAttackDamageCause, HeroSkillDamageCause
	Added /cooldowns (/cd) command to get a list of cooldowns
	Mana regen is now configurable in the main configuration.
	Various improvements to optimize CPU usage.
	Skills now have an addSpellTarget method for simplification

Skills:

	Bite - (NEW!)
		- Short-Range Damaging attack.
	Blackjack
		- Blackjack now prevents the player from using all skills except invuln.
		- Optimized code a little
	Blink
		- now requires at least a 2 block vertical hole to move through
	ChainLightning
		- Now has a 250ms delay between bounces
		- Fixed skill not charging mana/cooldowns etc properly
	Construct - (NEW!)
		- Ability to construct items using non-standard amounts, and to get back non-standard amounts!
		- XP values can be given/taken to construction
	Deconstruct
		- Can now be set to require the player to be targeting a Workbench
		- XP can now be granted/taken from deconstruction
	ForcePull - (NEW!) - Thanks fghjconnor!
		- Pulls a target closer to you
		- Can be configured to deal damage
	ForcePush
		- Implemented fghjconnor's alterations
		- now checks if the target is pvpable
		- can now be configured to deal damage
	Hellgate
		- See Port Fix.
	LickWounds - (REWRITE!)
		- Requires SkillWolf now
		- Will only heal wolves summoned/tamed with SkillWolf
	Manaburn
		- Players can no longer manaburn themselves if they don't have a target.
	Megabolt - (renamed Multibolt)
		- Fixed skill not charging mana/cooldown properly
		- Megabolt no longer hits the player if they are too close to the radius
		- Fixed skill not reporting the target properly.
	Mark
		- Fixed mark charging mana/cooldown/reagents for using list
	MortalWound - (NEW!)
		- Must be bound to a weapon as defined in the weapons node for the skill
		- Applies a healing debuff to the target.
		- Deals Weapon damage + a bleed effect.
	Overgrowth
		- Fixed sapling resetting to default type when the skill fails
	Port
		- Will now properly teleport all party members instead of just some, or just yourself.
	Revive
		- now uses reagent/reagent cost for reagents. This means slime is no longer hard-coded into the cost
	Skeleton
		- Fixed bug that could cause improper targeting
		- Now summons at the players target, max distance default is 5
	Superheat
		- now drops blocks properly.
	Wolf - (REWRITE!)
		- Will now remove/respawn wolves when the player enters/exits the world
		- wolves can now be summoned to the players location at any time
		- wolves will de-spawn on player quit, and respawn when the player re-enters the game at the players location
		- now summons at the players target, max distance default is 5