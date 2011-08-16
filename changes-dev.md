Bug Fixes:
    Never allow permission-skills to overwrite Official Skills

General:
	SkillDamageEvent - now allows alteration of skill damages after the skill applies it's base damage
	Created HeroesEventListener for simpler API hooking
	renamed HeroesWeaponDamageEvent it is now: WeaponDamageEvent
	HLevelListener has been moved to HEventListener - it now uses the HeroesEventListener for simplicity
Skills:
    All
        - Skills that damage entities have all been updated to generate SkillDamageEvents for the additional API
    Curse - (NEW!)
        - Curses a player/creature giving them a chance to miss each of there physical attacks
    ForcePush
        - Added missing usage info
    Poison
        - Fixed bug where duration and period values were being swapped for the poison effect
    Speed
    	- Renamed from One
    	- No longer sets velocity on canceled move events, also uses Priority.monitor now.
    Summon
        - Summons are now released/removed properly on-death and when the player disconnects
        - Summons will no longer target the player, or party members
        - Currently on Skeletons can be summoned