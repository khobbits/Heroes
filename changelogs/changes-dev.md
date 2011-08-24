Version Dev

Bug Fixes:

	ProjectileDamages properly use the entity-name for their damages now (ARROW, EGG, SNOWBALL)

General/API:

	Class-Based XP loss on death
	There is now a setting to allow XP loss to incur Level losses

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