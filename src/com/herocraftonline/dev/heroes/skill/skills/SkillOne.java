package com.herocraftonline.dev.heroes.skill.skills;

import com.herocraftonline.dev.heroes.skill.SkillType;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MobEffect;
import net.minecraft.server.Packet41MobEffect;
import net.minecraft.server.Packet42RemoveMobEffect;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.effects.EffectType;
import com.herocraftonline.dev.heroes.effects.ExpirableEffect;
import com.herocraftonline.dev.heroes.hero.Hero;
import com.herocraftonline.dev.heroes.skill.ActiveSkill;
import com.herocraftonline.dev.heroes.skill.Skill;

public class SkillOne extends ActiveSkill {

    private String applyText;
    private String expireText;

    public SkillOne(Heroes plugin) {
        super(plugin, "One");
        setDescription("Provides a short burst of speed");
        setUsage("/skill one");
        setArgumentRange(0, 0);
        setIdentifiers(new String[]{"skill one"});
        setTypes(SkillType.BUFF, SkillType.MOVEMENT, SkillType.SILENCABLE);
    }

    @Override
    public ConfigurationNode getDefaultConfig() {
        ConfigurationNode node = super.getDefaultConfig();
        node.setProperty("speed-multiplier", 2);
        node.setProperty("duration", 15000);
        node.setProperty("apply-text", "%hero% gained a burst of speed!");
        node.setProperty("expire-text", "%hero% returned to normal speed!");
        return node;
    }

    @Override
    public void init() {
        super.init();
        applyText = getSetting(null, "apply-text", "%hero% gained a burst of speed!").replace("%hero%", "$1");
        expireText = getSetting(null, "expire-text", "%hero% returned to normal speed!").replace("%hero%", "$1");
    }

    @Override
    public boolean use(Hero hero, String[] args) {
        broadcastExecuteText(hero);

        int duration = getSetting(hero.getHeroClass(), "duration", 5000);
        int multiplier = getSetting(hero.getHeroClass(), "speed-multiplier", 2);
        if (multiplier > 20) {
            multiplier = 20;
        }
        hero.addEffect(new OneEffect(this, duration, multiplier));

        return true;
    }

    public class OneEffect extends ExpirableEffect {

        int amplifier = 0;
        int duration = 0;

        MobEffect mobEffect = new MobEffect(1, 0, 0);

        public OneEffect(Skill skill, long duration, int amplifier) {
            super(skill, "One", duration);
            this.types.add(EffectType.DISPELLABLE);
            this.amplifier = amplifier;
            this.duration = (int) (duration / 1000) * 20;
        }

        @Override
        public void apply(Hero hero) {
            super.apply(hero);

            this.mobEffect = new MobEffect(1, this.duration, this.amplifier);

            Player player = hero.getPlayer();
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            entityPlayer.netServerHandler.sendPacket(new Packet41MobEffect(entityPlayer.id, this.mobEffect));

            broadcast(player.getLocation(), applyText, player.getDisplayName());
        }

        @Override
        public void remove(Hero hero) {
            Player player = hero.getPlayer();
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            entityPlayer.netServerHandler.sendPacket(new Packet42RemoveMobEffect(entityPlayer.id, this.mobEffect));

            broadcast(player.getLocation(), expireText, player.getDisplayName());
        }

    }
}