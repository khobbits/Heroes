package com.herocraftonline.dev.heroes.command.commands;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.command.BaseCommand;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.util.Messaging;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PartyUICommand extends BaseCommand {

    public PartyUICommand(Heroes plugin) {
        super(plugin);
        setName("Party Map UI");
        setDescription("Gives the Player a Map linked to the Party UI.");
        setUsage("/party ui");
        setMinArgs(0);
        setMaxArgs(0);
        getIdentifiers().add("party ui");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command needs to be run from a Player");
            return;
        }

        if (!this.plugin.getConfigManager().getProperties().mapUI) {
            Messaging.send(sender, "Map UI is not enabled so this command has been disabled.");
            return;
        }

        Player player = (Player) sender;
        ItemStack itemInHand = player.getItemInHand();

        if (itemInHand != null && itemInHand.getType() != Material.MAP) {
            Messaging.send(sender, "You need to have a Map equipped in your hand to turn it into the Party UI.");
            return;
        }

        if (itemInHand.getDurability() == this.plugin.getConfigManager().getProperties().mapID) {
            Messaging.send(sender, "This Map is already linked to the Party UI!");
            return;
        }

        itemInHand.setDurability(this.plugin.getConfigManager().getProperties().mapID);

        Messaging.send(sender, "Your map has been converted to the Party UI");

        Hero hero = this.plugin.getHeroManager().getHero(player);
        if (hero.getParty() != null) {
            hero.getParty().setUpdateMapDisplay(true);
        }
    }
}