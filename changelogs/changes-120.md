Version 1.2.0

Bug Fixes:

    No longer try to set HP less than 0 when handling damage.
    Projectile code supports latest RB
    Fixed Heart-synching with Heroes HP that would result in showing no hearts or killing the hero even though they had HP left
    No longer detect multiple deaths from entities
    Ignore creepers throwing multiple damage events
    Players are no longer awarded XP for suicides
    Only update MapUI if a player was damaged
General:

    Classes can now be given all armors/weapon by adding the '*' node to the list of permitted items
    /mana - is now aliased to /mp
    Added default values for the Map Party UI to initial config
    New event! - HeroesWeaponDamageEvent - now allows Weapon damages to be adjusted in Skills
    getSetting can now be used to retrieve a boolean setting
    Removed BukkitContrib compatibility - replaced with new Spout!
    New command - /hero reset - wipes ALL of a players XP on ALL classes and resets the to the default! (Perm: heroes.reset )
    clearEffects method for hero to remove any effects from the hero
    /hero admin reload - will now force-save all heros to prevent XP loss
    Creatures can now have effects applied to them, this means Bleed/Poison now work on creatures!
    Party size dropped to 6 to better support the Map Party UI
Skill Changes:

    All:
        - Added new 'Dispellable' interface - skills can be coded as dispellable:
        - Skills made dispellable: Absorb, Confuse, Flameshield, Gills, Invuln, ManaFreeze, ManaShield, One, Piggify, Reflect, Root, SuperHeat
        - New PeriodicDamageEffect for skill effects
        - New Bleed effect interface for skill effects
        - New Poison effect interface for skill effects
        - added skillSettings to Heros which allows skills to persist data.
        - A class can now be given all skills by giving the class a skill named '*': or ALL:  
    Antidote - (NEW!)
        - Cures the player of any Poison effects
    Backstab - (NEW!)
        - Allows a damage multiplier on attacks done from behind an enemy
        - Can have an alternate damage multiplier for more damage if the player is also sneaking
    Bandage
        - Now cures any bleed effect on top of healing the player
    Bladegrasp
        - Not quite as spammy anymore
    Bolt
        - Is now a single-target only spell
        - Damage can now be altered
    Boltstorm - (NEW!)
        - Self-Buff that periodically strikes Bolts at a nearby creature/player
        - is Dispellable
    Dispel
        - Will now only remove effects that are 'dispellable'
    Fireball/FireArrow
        - Now awards experience in most situations
        - Fire-ticks are still considered Environment damage.
    Flameshield
        - No longer makes the player completely invulnerable
    Group Heal
        - Only heals party members within a configurable radius
    Hellgate
        - Complete recode!
        - Will now teleport all nearby party members to/from the nether in the configuration (it defaults to world/world_nether)
    Icebolt
        - Can now have it's damage modified
    IcyAura - (NEW!)
        - Self-buff that damages nearby enemies and turns the ground under them to ice
        - Ice blocks changed will revert to their original state when the effect wears off
        - is dispellable
    Might - (NEW!)
        - Dispellable party buff that increases damage they deal with weapons
    Overgrowth - (NEW! - Reworked from Multitallented)
        - Creates a random Tree of the same type as the targeted sapling
    Poison - (NEW!)
        - Works just like bleed, but instead applies a poison effect.
    Port
        - /skill port - will now list all port locations
    Root
        - Fixed the players viewport snapping back when being teleported back to the root location
    Recall - (NEW!)
        - /skill recall mark - saves the players current location
        - /skill recall - teleports the player back to their 'mark'ed location
        - /skill recall info - notifies the player of their 'mark'ed location
    Revive
        - Now only Allows revival of fallen party members
    Sneak - (NEW!)
        - Sets the player to 'sneak' mode without needing to hold shift!
        - Can be set to break when the player takes damage from other creatures/players or if the player takes any damage
    Telekenesis
        - Now uses CraftBukkit classes to alter state of levers/buttons - Should work now!
    Web - (NEW!)
        - Creates a + of webs around the targets feet until the effect expires.
        - webs are break-protected and will auto-remove when the effect expires