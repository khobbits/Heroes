package com.herocraftonline.dev.heroes;

import com.herocraftonline.dev.heroes.api.HeroesEventListener;
import com.herocraftonline.dev.heroes.api.SkillUseEvent;
import com.herocraftonline.dev.heroes.skill.SkillType;
import com.herocraftonline.dev.heroes.util.Messaging;

public class HSkillListener extends HeroesEventListener {
    
    private Heroes plugin;
    
    public HSkillListener(Heroes plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onSkillUse(SkillUseEvent event) {
        if (event.isCancelled())
            return;
        String worldName = event.getPlayer().getWorld().getName();
        if (plugin.getConfigManager().getProperties().disabledWorlds.contains(worldName)) {
            Messaging.send(event.getPlayer(), "Skills have been disabled on this world!");
            event.setCancelled(true);
            return;
        }
        
        if (event.getHero().hasEffect("Stun")) {
            if (!(event.getSkill().isType(SkillType.COUNTER)))
                event.setCancelled(true);
        }
    }
}