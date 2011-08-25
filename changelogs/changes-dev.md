Version Dev

Bug Fixes:

	ProjectileDamages properly use the entity-name for their damages now (ARROW, EGG, SNOWBALL)
	Fixed ALL: skill-setting causing issues when Administrators set it improperly.
	Bedhealing no longer immediately heals.
	Multi-World default settings populate properly now

General/API:

	Class-Based XP loss on death
	Added setting for Exp loss to incur level-losses
	Added setting for exp losses to incur level-losses
	ExperienceGainEvent is now ExperienceChangeEvent
	HeroLevelEvent is now HeroChangeLevelEvent
	hero.gainExp now fully supports negative values
	ExperienceType.DEATH added

Skills:

	Blackjack
		- No longer resets player yaw and pitch.
		- Blackjack now prevents the player from using all skills except invuln.
	ChainLightning
		- Now has a 250ms delay between bounces
		- Fixed skill not charging mana/cooldowns etc properly
	Construct - (NEW!)
		- Ability to construct items using non-standard amounts, and to get back non-standard amounts!
		- XP values can be assigned to construction
	Deconstruct
		- Can now be set to require the player to be targeting a Workbench
		- XP can now be gained from deconstruction
	Hellgate
		- See Port Fix.
	Manaburn
		- Players can no longer manaburn themselves if they don't have a target.
	Megabolt - (renamed Multibolt)
		- Fixed skill not charging mana/cooldown properly
	Mark
		- Fixed mark charging mana/cooldown/reagents for using list
	Port
		- Will now properly teleport all party members instead of just some, or just yourself.
	Skeleton
		- Fixed bug that could cause improper targeting