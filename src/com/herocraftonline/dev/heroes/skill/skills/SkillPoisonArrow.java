package com.herocraftonline.dev.heroes.skill.skills;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MobEffect;
import net.minecraft.server.Packet41MobEffect;
import net.minecraft.server.Packet42RemoveMobEffect;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.effects.PeriodicDamageEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;
import com.herocraftonline.dev.heroes.util.Setting;

public class SkillPoisonArrow extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillPoisonArrow(Heroes plugin) {
        super(plugin, "PoisonArrow");
        setDescription("You fire a poison arrow from your bow");
        setUsage("/skill parrow");
        setArgumentRange(0, 0);
        setIdentifiers("skill parrow", "skill poisonarrow");
        setTypes(SkillType.BUFF);

        registerEvent(Type.ENTITY_DAMAGE, new SkillDamageListener(this), Priority.Monitor);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("poison-duration", 10000); // 10 seconds in
        node.setProperty(Setting.DURATION.node(), 60000); // milliseconds
        node.setProperty(Setting.PERIOD.node(), 2000); // 2 seconds in milliseconds
        node.setProperty("tick-damage", 2);
        node.setProperty("attacks", 1); // How many attacks the buff lasts for.
        node.setProperty(Setting.APPLY_TEXT.node(), "%target% is poisoned!");
        node.setProperty(Setting.EXPIRE_TEXT.node(), "%target% has recovered from the poison!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, Setting.APPLY_TEXT.node(), "%target% is poisoned!").replace("%target%", "$1");
        expireText = getSetting(null, Setting.EXPIRE_TEXT.node(), "%target% has recovered from the poison!").replace("%target%", "$1");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        long duration = getSetting(hero.getHeroClass(), Setting.DURATION.node(), 600000);
        int numAttacks = getSetting(hero.getHeroClass(), "attacks", 1);
        hero.addEffect(new PoisonArrowBuff(this, duration, numAttacks));
        broadcastExecuteText(hero);
        return true;
    }

    public class ArrowPoison extends PeriodicDamageEffect {

        private MobEffect mobEffect = new MobEffect(19, 0, 0);
        
        public ArrowPoison(Skill skill, long period, long duration, int tickDamage, Player applier) {
            super(skill, "ArrowPoison", period, duration, tickDamage, applier);
            this.types.add(EffectType.POISON);
            this.mobEffect = new MobEffect(19, (int) (duration / 1000) * 20, 0);
        }

        @Override
        public void apply(Creature creature) {
            super.apply(creature);
            broadcast(creature.getLocation(), applyText, Messaging.getCreatureName(creature).toLowerCase());
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);
            Player player = hero.getPlayer();
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            entityPlayer.netServerHandler.sendPacket(new Packet41MobEffect(entityPlayer.id, this.mobEffect));
            broadcast(player.getLocation(), applyText, player.getDisplayName());
        }

        @Override
        public void remove(Creature creature) {
            super.remove(creature);
            broadcast(creature.getLocation(), expireText, Messaging.getCreatureName(creature).toLowerCase());
        }

        @Override
        public void remove(Hero hero) {
            super.remove(hero);
            Player player = hero.getPlayer();
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            entityPlayer.netServerHandler.sendPacket(new Packet42RemoveMobEffect(entityPlayer.id, this.mobEffect));
            broadcast(player.getLocation(), expireText, player.getDisplayName());
        }
    }

    public class PoisonArrowBuff extends ExpirableEffect {

        private int applicationsLeft = 1;

        public PoisonArrowBuff(Skill skill, long duration, int numAttacks) {
            super(skill, "PoisonArrowBuff", duration);
            this.applicationsLeft = numAttacks;
            this.types.add(EffectType.BENEFICIAL);
            this.types.add(EffectType.POISON);
        }

        /**
         * @return the applicationsLeft
         */
        public int getApplicationsLeft() {
            return applicationsLeft;
        }

        @Override
        public void remove(Hero hero) {
            Messaging.send(hero.getPlayer(), "Your arrows are no longer poisoned");
        }

        /**
         * @param applicationsLeft
         *            the applicationsLeft to set
         */
        public void setApplicationsLeft(int applicationsLeft) {
            this.applicationsLeft = applicationsLeft;
        }
    }

    public class SkillDamageListener extends EntityListener {

        private final Skill skill;

        public SkillDamageListener(Skill skill) {
            this.skill = skill;
        }

        @Override
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.isCancelled() || !(event instanceof EntityDamageByEntityEvent))
                return;

            EntityDamageByEntityEvent subEvent = (EntityDamageByEntityEvent) event;
            if (!(subEvent.getDamager() instanceof Arrow))
                return;

            Arrow arrow = (Arrow) subEvent.getDamager();
            if (!(arrow.getShooter() instanceof Player))
                return;

            Player player = (Player) arrow.getShooter();
            Hero hero = plugin.getHeroManager().getHero(player);

            if (hero.hasEffect("PoisonArrowBuff")) {
                long duration = getSetting(hero.getHeroClass(), "poison-duration", 10000);
                long period = getSetting(hero.getHeroClass(), Setting.PERIOD.node(), 2000);
                int tickDamage = getSetting(hero.getHeroClass(), "tick-damage", 2);
                ArrowPoison apEffect = new ArrowPoison(skill, period, duration, tickDamage, player);

                if (event.getEntity() instanceof Creature) {
                    plugin.getHeroManager().addCreatureEffect((Creature) event.getEntity(), apEffect);
                    checkBuff(hero);
                } else if (event.getEntity() instanceof Player) {
                    Hero target = plugin.getHeroManager().getHero((Player) event.getEntity());
                    target.addEffect(apEffect);
                    checkBuff(hero);
                }
            }
        }

        private void checkBuff(Hero hero) {
            PoisonArrowBuff paBuff = (PoisonArrowBuff) hero.getEffect("PoisonArrowBuff");
            paBuff.applicationsLeft -= 1;
            if (paBuff.applicationsLeft < 1) {
                hero.removeEffect(paBuff);
            }
        }
    }
}