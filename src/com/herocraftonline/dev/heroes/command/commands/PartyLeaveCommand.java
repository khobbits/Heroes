package com.herocraftonline.dev.heroes.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BasicCommand;
import com.herocraftonline.dev.heroes.party.HeroParty;
import com.herocraftonline.dev.heroes.persistence.Hero;

public class PartyLeaveCommand extends BasicCommand {
    private final Heroes plugin;

    public PartyLeaveCommand(Heroes plugin) {
        super("Party Leave");
        this.plugin = plugin;
        setDescription("Leaves your party");
        setUsage("/party leave");
        setArgumentRange(0, 0);
        setIdentifiers(new String[] { "party leave" });
    }

    @Override
    public boolean execute(CommandSender sender, String identifier, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        Hero hero = plugin.getHeroManager().getHero(player);
        if (hero.getParty() == null) return false;
        HeroParty heroParty = hero.getParty();
        heroParty.messageParty("$1 has left the party", player.getName());
        heroParty.removeMember(hero);
        if (heroParty.getMembers().size() == 0) {
            this.plugin.getPartyManager().removeParty(heroParty);
        }
        hero.setParty(null);
        return true;
    }

}
