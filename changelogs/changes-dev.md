Version dev

==== **Bug Fixes:** ====

* Fixed multiple issues when mastering, or gaining exp on profession resulting in primary class having it's exp adjusted.
* Farming/Logging exp sources now award exp properly when a class also has Mining.

==== **General:** ====

* Now using Vault for Economy & Permission hooks.
* Adjusted XP methods so that the Level display operates properly in 1.0

==== **API:** ====

* removed hero.setExperience(double) and isMaster() - use isMaster(heroClass) and setExperience(heroClass, double)

==== **Skills:** ====
