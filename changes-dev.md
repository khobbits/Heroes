Version Dev

Bug Fixes:

	Multiworld support re-done, Binding should now work again
	bedhealing percentages are no longer rounded down to 0, this fixes bedhealing
	BedHeal HP display now respects verbosity settings for the hero

General/API:

	SkillUseEvent now tracks manacost and reagentcost which can be adjusted during the event
	Added getSettingKeys to the skill configuration methods.
	HeroRegainManaEvent added to the API - called when a skill/mana regeneration is triggered
	

Skills:

	ALL
		- ActiveSkills can now be given 'reagent' and 'reagent-cost' nodes to require specific items during cast
		- All 'range' variables have been replaced with 'radius' if they were meant to be radius checks.
			- Blaze, Boltstorm, GroupHeal, Hellgate, IcyAura, Might, MultiBolt, Port, Pulse
		- All other instances of 'range' should now use the max-distance setting for limiting target distance
			- Antidote, Web
	Backstab
		- Is now limited to only the specific weapons on the weapon list (melee only)
	Bolt
		- Made targeting more verbose
	Consume - (NEW!)
		- Works very similar to Replenish, but allows multiple different materials to give mana
		- Configuration allows different materials to be granted at different levels
	Flameshield
		- Fixed report message for Skill blocking
	IcyAura
		- Implemented a much more limited set of block-types that IcyAura can turn into Ice.
	ManaFreeze
		- Now prevents the target player from regenerating mana
	Mark - (NEW!)
		- Marks a location for the hero to recall back to using Recall
	Multibolt - (NEW!)
		- AoE version of Bolt.
	Port
		- Now allows more than 1 item as a cost
		- Reminder - Port item costs must be ALL-CAPS.
		- Renamed item-cost/amount to "reagent" and "reagent-cost" for the skill settings
	Pulse
		- Radius is now configurable
	Recall
		- Split marking of recall locations onto a different skill called 'Mark'
	Replenish
		- Amount of mana returned to player is now configurable
	Skeleton
		- Will now teleport to the player if it gets too far away.
	Telekinesis
		- Target distance is now configurable through 'max-distance' node