package com.herocraftonline.dev.heroes.skill.skills;

import java.util.HashMap;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.persistence.Hero;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.skill.TargettedSkill;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillDisarm extends TargettedSkill {

	private String applyText;
	private String expireText;
	private HashMap<Hero,ItemStack> ismap = new HashMap<Hero,ItemStack>();

	public SkillDisarm(Heroes plugin) {
		super(plugin, "Disarm");
		setDescription("Disarm your target");
		setUsage("/skill disarm");
		setArgumentRange(0, 1);

		setTypes(SkillType.PHYSICAL, SkillType.DEBUFF);

		setIdentifiers(new String[] { "skill disarm" });
	}

	@Override
	public ConfigurationNode getDefaultConfig() {
		ConfigurationNode node = super.getDefaultConfig();
		node.setProperty(Setting.DURATION.node(), 500);
		node.setProperty(Setting.APPLY_TEXT.node(), "%target% was disarmed!");
		node.setProperty(Setting.EXPIRE_TEXT.node(), "%target% has found his weapon again!");
		return node;
	}

	public void init() {
		super.init();
		applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%target% has stopped regenerating mana!").replace("%target%", "$1");
		expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%target% is once again regenerating mana!").replace("%target%", "$1");
	}

	@Override
	public boolean use(Hero hero, LivingEntity target, String[] args) {

		Player player = hero.getPlayer();
		if (target.equals(player) || !(target instanceof Player)) {
			Messaging.send(player, "You must target another player!");
			return false;
		}

		Hero tHero = plugin.getHeroManager().getHero((Player) target);
		if (tHero == null) {
			return false;
		}

		if(tHero.getPlayer().getItemInHand() == null){
			Messaging.send(hero.getPlayer(), "You cannot disarm bare hands!");
			return false;
		}else{
			if(!ismap.containsKey(tHero)){
				int duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 500);
				tHero.addEffect(new DisarmEffect(this, duration));
				broadcastExecuteText(hero, target);
				return true;
			}else{
				Messaging.send(hero.getPlayer(), "%target% is already disarmed");
				return false;
			}
		}


	}

	public class DisarmEffect extends ExpirableEffect {

		public DisarmEffect(Skill skill, long duration) {
			super(skill, "Disarm", duration);
			this.types.add(EffectType.DISPELLABLE);
		}

		@Override
		public void apply(Hero hero) {
			Player player = hero.getPlayer();
			ItemStack is = player.getItemInHand();
			if(!ismap.containsKey(hero)){
				ismap.put(hero, is);
				player.setItemInHand(null);
				super.apply(hero);
				broadcast(player.getLocation(), applyText, player.getDisplayName());
			}

		}

		@Override
		public void remove(Hero hero) {
			Player player = hero.getPlayer();
			if(ismap.containsKey(hero)){
				ItemStack is = ismap.get(hero);
				player.getInventory().addItem(is);
				ismap.remove(hero);
			}
			broadcast(player.getLocation(), expireText, player.getDisplayName());
		}
	}

}


