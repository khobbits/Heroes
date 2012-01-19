Version 1.4.3

==== **Bug Fixes:** ====

* players with an enchanting container open are no longer able to toggle their xp bar
* /hero paths will now properly only list paths that are availabl to the hero
* Projectiles no longer ignore no-pvp and level-range settings

==== **General:** ====

* Debug timing will be removed as we continue to update to the new 1.1 event handling
** Bukkit now has internal event timings available

==== **API:** ====


==== **Skills:** ====

* All non-physical skills now issue Magic damage instead of ENTITY_ATTACK
* All reagent nodes have switched format to support damage values
** ID:DAMAGE - is the new format, be sure to update all your reagents!
* Blink
** added option to restrict ender-pearl use to only classes that have the skill