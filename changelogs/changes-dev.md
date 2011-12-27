Version dev

==== **Bug Fixes:** ====

* Fixed multiple issues when mastering, or gaining exp on profession resulting in primary class having it's exp adjusted.
* Farming/Logging exp sources now award exp properly when a class also has Mining.
* Creature Health now adjusts properly and works with values above normal maximums
* A player will only have a skill if they are the proper level in the class that gets the skill at that level.
* Players that craft with shift-right-clicking will now gain xp for all items crafted
* Heroes can no longer re-use a skill with a warmaup to immediately use the skill

==== **General:** ====

* Fishing is now an experience source
* Now using Vault for Economy & Permission hooks. (It's required)
* Adjusted XP methods so that the Level display operates properly in 1.0
* MapPartyUI has been removed
* The config.yml format has changed drastically. Some old options have been removed, or moved to make it easier.
* Wolves & Enderdragons are currently ignored (wolves are still buggy in vanilla)
* Classes now have item crafting restrictions
** Restrict item groups based on level in recipes.yml
** add the recipe groups a class should gain in the classfile under the 'recipes' key
** supports denied-items to prevent users from crafting certain items, no matter what
* Party xp sharing now operates differently
** the partyBonus - value in the config.yml should now be between 0 and 1 - update accordingly!

==== **API:** ====

* removed hero.setExperience(double) and isMaster() - use isMaster(heroClass) and setExperience(heroClass, double)
* party xp now needs to be handled through the party API, not the Hero.
* The MapUI has been removed
* All skills that damage a target must now use skill.entityDamage - bukkit no longer provides direct support for firing events via .damage anymore


==== **Skills:** ====

* Backstab - (Re-Added!)
* Charge 
** now 'jumps' toward the targeted player and can be configured to deal damage, stun, slow, or root nearby enemies on langing, or all of the above.
* PickPocket - (NEW!)
** allows a player to steal an item from another 
** Will not steal armor/items in the hotbar
* FireArrow
** Fixed not expiring properly
* IceArrow - (Re-Added!)
** Fixed not expiring properly
* PoisonArrow - (Re-added!)
** fixed not expiring properly
* SuperJump - (NEW!) - Thanks Multitallented
** powerful version of jump that gives safe-fall
* Wolf 
** Now a passive that forces anyone on the server to have the skill to tame