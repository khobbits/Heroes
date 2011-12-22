Version dev

==== **Bug Fixes:** ====

* Fixed multiple issues when mastering, or gaining exp on profession resulting in primary class having it's exp adjusted.
* Farming/Logging exp sources now award exp properly when a class also has Mining.
* Creature Health now adjusts properly and works with values above normal maximums
* A player will only have a skill if they are the proper level in the class that gets the skill at that level.

==== **General:** ====

* Fishing is now an experience source
* Now using Vault for Economy & Permission hooks. (It's required)
* Adjusted XP methods so that the Level display operates properly in 1.0
* MapPartyUI has been removed.
* The config.yml format has changed drastically. Some old options have been removed, or moved to make it easier.
* Wolves & Enderdragons are currently ignored (wolves are still buggy in vanilla)

==== **API:** ====

* removed hero.setExperience(double) and isMaster() - use isMaster(heroClass) and setExperience(heroClass, double)
* The MapUI has been removed
* All skills that damage a target must now use skill.entityDamage - bukkit no longer provides direct support for firing events via .damage anymore


==== **Skills:** ====

* Backstab - (Re-Added!)
* PickPocket - (NEW!)
** allows a player to steal an item from another 
** Will not steal armor/items in the hotbar
* Wolf 
** Now a passive that forces anyone on the server to have the skill to tame