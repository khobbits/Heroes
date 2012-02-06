Version dev

==== **Bug Fixes:** ====

* Secondary classes with higher projectile damages now works intended
* Projectile damage per level is now evaluated properly rather than being ignored
* Zombies will now take damage properly from sources that should not be reduced

==== **General:** ====

* targetting should now be less restrictive, allowing the player to target closer to a mobs head, instead of feet
* root effects now simply apply a very strong slow rather than teleporting the player.
* slows now reduce jump height so players can not exploit spamming jump to ignore slows
* Recipe configuration allows ID:* for all subtypes - no longer necessary to type out all subids of an item

==== **API:** ====

* ImbueEffect has been re-written to be handled like FormEffects
** it no longer has applications etc.

==== **Skills:** ====

* Enchant
** allows reagent requirement for enchanting - amount is still static
* FireArrow, IceArrow & PoisonArrow
** re-written to use mana-per-shot regardless of hitting the target or not
** will drain the users mana as they stay active.  Using the skill will toggle it on/off.
* Repair
** unchant-chance - now has a chance to disenchant an item being repaired.
** unchant-chance-reduce - will reduce the unchant-chance by it's amount per-level
* Summon Chicken/Cow/Pig/Sheep
** thanks Multitallented!
** improved them so Mushroom cows spawn in Mushroom biomes
** improved sheep to spawn randomly colored sheeps