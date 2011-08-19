Version Dev

Bug Fixes:

	Fixed occurrences where permissions-skills would not be added to classes properly.

General/API:

	Removed EntityRegainHealthEvent calls from all skills - they now generate our own internal HeroRegainHealthEvent
	Permission-Skills will now report when they are trying to replace a base skill
	Massive simplification to how BedHealing works - now uses Effect system

Skills:

	Heals
		- Fixed heals healing the wrong amount
	Curse
	    - Fixed curse not triggering properly 
	Rejuvenate - (NEW!)
	    - Heal-over-Time ability.
	Telekinesis
		- Now ignores more blocks (snow was causing problems with targeting) to make it function better
	Web
		- Only changes Air, Water, or Lava blocks now
		- Optimized how it changes/stores block data
		- Fixed center web block being destructible