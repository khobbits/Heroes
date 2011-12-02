Version dev

==== **Bug Fixes:** ====

* Fixed multiple issues when mastering, or gaining exp on profession resulting in primary class having it's exp adjusted.
* Farming/Logging exp sources now award exp properly when a class also has Mining.

==== **General:** ====

* Now using Vault for Economy & Permission hooks. (It's required)
* Adjusted XP methods so that the Level display operates properly in 1.0
* MapPartyUI has been removed.
* The config.yml format has changed drastically. Some old options have been removed, or moved to make it easier.

==== **API:** ====

* removed hero.setExperience(double) and isMaster() - use isMaster(heroClass) and setExperience(heroClass, double)
* The MapUI has been removed

==== **Skills:** ====

* PickPocket - (NEW!)
** allows a player to steal an item from another 
** Will not steal armor/items in the hotbar