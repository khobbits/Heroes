Version 1.4.3

==== **Bug Fixes:** ====

* players with an enchanting container open are no longer able to toggle their xp bar
* /hero paths will now properly only list paths that are availabl to the hero
* Projectiles no longer ignore no-pvp and level-range settings
* Skills can no longer target dead entities
* Secondary xp Bar now updates properly

==== **General:** ====

* Debug timing will be removed as we continue to update to the new 1.1 event handling
** Bukkit now has internal event timings available
* How much potions restore per-tier is now configurable in damages.yml - see updated configuration
* Classes now support item-damage-level and projectile-damage-level settings
* recipe IDs are now in the format "ID:DAMAGE" instead of "ID,DAMAGE"
** please update to the new format if you're using any recipe blocks with special item data

==== **API:** ====

* new BlindEffect - it blinds the hero!

==== **Skills:** ====

* All non-physical skills now issue Magic damage instead of ENTITY_ATTACK
* All reagent nodes have switched format to support damage values
* Passive skills now disable properly on DisabledWorlds
** ID:DAMAGE - is the new format, be sure to update all your reagents!
* Assassin's Blade
** no longer triggers from damage checks
* Blink
** added option to restrict ender-pearl use to only classes that have the skill
* Charge
** velocities are now working as intended
* FireArrow
** Now launches flaming arrows as intended