package com.herocraftonline.dev.heroes.util;

import java.util.Iterator;

import org.bukkit.entity.Player;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;

public class BedHealThread extends Thread {
	
	private Heroes plugin;
	Properties props;

	public BedHealThread(Heroes plugin) {
		this.plugin = plugin;
		props = plugin.getConfigManager().getProperties();
		
	}
	
	public void run() {
		while(!props.bedHealers.isEmpty()) {
			try {
				this.wait(props.healInterval * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			
			synchronized(props.bedHealers) {
				Iterator<Player> iter = props.bedHealers.iterator();
				while (iter.hasNext()) {
					Hero hero = plugin.getHeroManager().getHero(iter.next());
					double newHealth = hero.getHealth() + (hero.getMaxHealth() * props.healPercent / 100);
					if (newHealth >= hero.getMaxHealth()) {
						newHealth = hero.getMaxHealth();
						iter.remove();
					}
					plugin.getServer().getScheduler().callSyncMethod(plugin, hero.bedHeal(newHealth));
				}
			}
		}
	}
}
