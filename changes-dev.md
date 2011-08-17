Bug Fixes:

    Never allow permission-skills to overwrite Official Skills
	All block Materials defined in properties now trigger Logging as an exp gain source rather than just Logs
	PermissionHandler is no longer registered if Permissions2 is detected. Heroes is NOT compatible with P2
	Fixed possible NPE during getSetting when some settings weren't defined for skills

General:

	SkillDamageEvent - now allows alteration of skill damages after the skill applies it's base damage
	SkillUseEvent - called before any ActiveSkill is used. - allows other skills/plugins to cancel the use of a skill.
	Created HeroesEventListener for simpler API hooking
	renamed HeroesWeaponDamageEvent it is now: WeaponDamageEvent
	HLevelListener has been moved to HEventListener - it now uses the HeroesEventListener for simplicity
	added getCreatureEffect to HeroManager for easy retrieval of an effect from a creature. Returns null if the effect can't be found.

Skills:

    All
        - Skills that damage entities have all been updated to generate SkillDamageEvents for the additional API
        - Added new Harmful and Beneficial effect types to improve Dispallables
    Backstab
        - Only messages affected players instead of broadcasting the backstab event
        - Added chances! You can now set a standard chance for a player to be effected by backstab, and a chance while they are sneaking.
    Curse - (NEW!)
        - Curses a player/creature giving them a chance to miss each of there physical attacks
    Dispel
        - Can now banish enemy summons!
        - Now Only removes Harmful effects from Party Members & yourself
        - Now only removes Beneficial effects from enemies!
        - new max-removals will limit the number of effects that dispel can remove per cast.
    Flameshield
        - Now also blocks damage from any skill with Fire or Flame in it's name (eg. Fireball, Firearrow)
    ForcePush
        - Added missing usage info
    IcyAura
        - Now limited to not change 'sensitive' blocks such as chests/doors
    Poison
        - Fixed bug where duration and period values were being swapped for the poison effect
    Port
        - Now supports multi-world
    Speed
    	- Renamed from One
    	- No longer sets velocity on canceled move events, also uses Priority.monitor now.
    Skeleton - (NEW!)
        - Skeletons are now released/removed properly on-death and when the player disconnects
        - Skeletons will no longer target the player, or party members
        - Skeletons can be 'banished' by being Dispelled!
        - Duration added so the 'summon' does not last forever.
    Summon
        - Renamed to Skeleton
    Web
        - Now limited to not change 'sensitive' blocks such as chests
