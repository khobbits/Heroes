Version dev

==== **Bug Fixes:** ====

* /hero skills - now shows Permission-skills properly
* armor is removed properly when a player is about to take damage.
* /hero prof <path> can now be used when the player's primary class is Mastered.
* cooldowns are no longer called everytime a skill finishes (only when returning NORMAL)

==== **General:** ====

* Added a display message for players when they attempt to attack another player outside of their pvp level range.
* Added optional alternative cost for swapping back to a class that a player has xp in

==== **API:** ====

* Skill's use method now returns a SkillResult - to better identify different return conditions

==== **Skills:** ====

* Curse
** Now properly tagged as a debuff skill
* EscapeArtist - (NEW!)
** Removes all movement impairing effects (stuns, roots, slows)
* Telekinesis - (READDED!)
