package com.herocraftonline.dev.heroes.util;

import java.util.Iterator;
import java.util.Set;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.persistence.HeroManager;

public class BedHealThread extends Thread {

	private Heroes plugin;
	private Properties props;
	private HeroManager heroManager;
	private Set<Hero> bedHealers;

	public BedHealThread(Heroes plugin) {
		this.plugin = plugin;
		props = plugin.getConfigManager().getProperties();
		heroManager = plugin.getHeroManager();
		bedHealers = heroManager.getBedHealers();
	}

	public void run() {
		boolean isEmpty = false;
		while(!isEmpty) {
			synchronized(this) {
				try {
					wait(props.healInterval * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}

			synchronized(bedHealers) {
				Iterator<Hero> iter = bedHealers.iterator();
				while (iter.hasNext()) {
					Hero hero = iter.next();
					double newHealth = hero.getHealth() + (hero.getMaxHealth() * props.healPercent / 100);
					if (newHealth >= hero.getMaxHealth()) {
						newHealth = hero.getMaxHealth();
						iter.remove();
					}
					plugin.getServer().getScheduler().callSyncMethod(plugin, hero.bedHeal(newHealth));
				}
				isEmpty = bedHealers.isEmpty();
			}
		}
	}
}
