Version dev

==== **Bug Fixes:** ====

* damage will be taken from the greater of either the secondclass, or the primary class as intended.
* moved all enchantment table checks to skill enchant so it doesn't interfere if a server removed the skill

==== **General:** ====

* Targetted skills with delays should now store their target and attempt to re-cast when finished.
* added better default classes
* dumpLevelExp is a new setting, it will generate a levels.txt file in the data directory if set to true, this is to easily dump the values required for levels

==== **API:** ====

* Skills now have a getDescription(hero) so they can return stat-displaying descriptions

==== **Skills:** ====

* All skill descriptions updated with new information
* Skills with warmups now have the option to slow their user
* Enchant
** xp lost is now based on the total levels of all enchants being added.