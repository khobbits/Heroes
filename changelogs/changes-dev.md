Version dev

==== **Bug Fixes:** ====

* Fix party commands erroring when Spout is not detected
* Fix allowhatsplugin option being case sensetive so default value wasn't loading.
* Only alert players of denied XP via block-break if they have the source of XP they are breaking the block of
* Removed startup warning for empty XP mappings
* Cancel damage from fire sources if the player has the fire resist mob effect
* Fixed /hero admin prof changing a players primary class to a secondary one.

==== **General:** ====

* Added /hero admin heal <playername>
** heals a player to full health
** Requires heroes.admin.heal permission node


==== **API:** ====

==== **Skills:** ====

* Enchant
** fixed oddities when a player had Enchanting xp source, but not the skill
** SkillEnchant will also now block a player from opening the enchanting table if they don't have the skill
** Will now properly re-synch player inventory when an enchant is blocked
* Wolf
** Fixed usage text still indicating it was a usable skill rather than a passive