for f in /cygdrive/c/users/david/herocraft/heroes/src/com/herocraftonline/dev/heroes/skill/skills/*.java; do
  cp $f $f.bkup
  sed 's/name = \(\".*\"\)/setName(\1)/;s/description = \(\".*\"\)/setDescription(\1)/;s/usage = \(\".*\"\)/setUsage(\1)/;s/minArgs = \(.*\);/setMinArgs(\1);/;s/maxArgs = \(.*\);/setMaxArgs(\1);/;s/identifiers\.add/getIdentifiers().add/;s/\(\W\)name\(\W\)/\1getName()\2/' $f > $f.tmp
  mv -f $f.tmp $f
done
