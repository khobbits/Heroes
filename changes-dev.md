Version Dev

Bug Fixes:

	Fixed occurrences where permissions-skills would not be added to classes properly.
	Properly check permission for /hero admin relaod 
	Fix ConcurrentModificationExceptions related to hero effects & changing classes

General/API:

	Removed EntityRegainHealthEvent calls from all skills - they now generate our own internal HeroRegainHealthEvent
	Permission-Skills will now report when they are trying to replace a base skill
	Massive simplification to how BedHealing works - now uses Effect system
	PeriodicHealEffect - just like the damage effect, but heals the target

Skills:

	Heals
		- Fixed heals healing the wrong amount
		- Made more heal fail options.
	Curse
	    - Fixed curse not triggering properly 
	Rejuvenate - (NEW!)
	    - Heal-over-Time ability.
	Skeleton
	    - no longer combust in day-light to make them more useful
	    - Will path toward the player if they get too far away
	    - Will defend the player and attack what the player attacks
	Telekinesis
		- Now ignores more blocks (snow was causing problems with targeting) to make it function better
	Web
		- Only changes Air, Water, or Lava blocks now
		- Optimized how it changes/stores block data
		- Fixed center web block being destructible