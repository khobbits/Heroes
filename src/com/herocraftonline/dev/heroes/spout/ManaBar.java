package com.herocraftonline.dev.heroes.spout;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.screen.ScreenResolutionChangeEvent;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.Gradient;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.hero.Hero;

public class ManaBar extends GenericContainer{
    final Hero hero;
    Label text = new GenericLabel();
    Gradient manaBar = new GenericGradient();
    Gradient backgroundBar;
    int lastTickMana = -1;
    int fullWidth;
    int halfWidth;
    boolean wasWearingArmor = false;
    
    int screenX = 427, screenY = 241;
    public ManaBar(Hero hero, Heroes plugin) {
        this.hero = hero;
        SpoutPlayer player = SpoutManager.getPlayer(hero.getPlayer());
        InGameHUD screen = player.getMainScreen();
        int mana = hero.getMana();
        
        lastTickMana = mana;
        fullWidth = 178;
        halfWidth = fullWidth / 2 - 10;
            
        manaBar.setX(124).setY(194).setWidth(fullWidth).setHeight(5);
        manaBar.setBottomColor(new Color(0, 0x33, 0xCC));
        manaBar.setTopColor(new Color(0x0000FF));
        manaBar.setPriority(RenderPriority.Low);
        manaBar.setAnchor(WidgetAnchor.TOP_LEFT);
        
        backgroundBar = (Gradient) manaBar.copy();
        backgroundBar.setColor(new Color(0, 0, 0));
        backgroundBar.setWidth(manaBar.getWidth() + 2).setHeight(manaBar.getHeight() + 2);
        backgroundBar.setX(manaBar.getX() - 1).setY(manaBar.getY() - 1);
        backgroundBar.setPriority(RenderPriority.High);
        
        text.setX(manaBar.getX() + manaBar.getWidth() / 2 - 8 + (mana < 10 ? 4 : 0)).setY(manaBar.getY() + manaBar.getHeight() - 9 ).setWidth(20).setHeight(12);
        text.setText(String.valueOf(mana)).setScale(1F).setTextColor(new Color(0, 0x33, 0xCC));
        text.setAnchor(WidgetAnchor.TOP_LEFT);
        text.setPriority(RenderPriority.Lowest);
        
        onTick();
        
        screen.attachWidgets(plugin, manaBar, backgroundBar, text, this);
    }
    
    @Override
    public void onResize(ScreenResolutionChangeEvent event) {
        screenX = event.getScreenResolutionX();
        screenY = event.getScreenResolutionY();
        if (wasWearingArmor) {
            halfLayout();
        }
        else {
            fullLayout();
        }
    }
    
    public void fullLayout() {
        int half = screenX / 2;
        int mana = hero.getMana();

        manaBar.setX(half - 90).setY(screenY - 47).setWidth(fullWidth).setHeight(5).setDirty(true);

        backgroundBar.setWidth(manaBar.getWidth() + 2).setHeight(manaBar.getHeight() + 2);
        backgroundBar.setX(manaBar.getX() - 1).setY(manaBar.getY() - 1).setDirty(true);
        
        text.setX(manaBar.getX() + (fullWidth / 2) - 8 + (mana < 10 ? 4 : 0)).setY(manaBar.getY() + manaBar.getHeight() - 9 ).setWidth(20).setHeight(12).setDirty(true);
        text.setText(String.valueOf(mana));
        
        float scale = mana / 100F;
        manaBar.setWidth((int)(fullWidth * scale));
    }
    
    public void halfLayout() {
        int half = screenX / 2;
        int mana = hero.getMana();

        manaBar.setX(half + 12).setY(screenY - 47).setWidth(halfWidth).setHeight(5).setDirty(true);
        
        backgroundBar.setWidth(manaBar.getWidth() + 2).setHeight(manaBar.getHeight() + 2);
        backgroundBar.setX(manaBar.getX() - 1).setY(manaBar.getY() - 1).setDirty(true);
        
        text.setX(manaBar.getX() + (halfWidth / 2) - 8 + (mana < 10 ? 4 : 0)).setY(manaBar.getY() + manaBar.getHeight() - 9).setWidth(20).setHeight(12).setDirty(true);
        text.setText(String.valueOf(mana));
        text.setX(manaBar.getX() + manaBar.getWidth() / 2 - 8);
        
        float scale = mana / 100F;
        manaBar.setWidth((int)(halfWidth * scale));
    }

    @Override
    public void onTick() {
        boolean wearingArmor = false;
        ItemStack[] armor = hero.getPlayer().getInventory().getArmorContents();
        for (int i = 0; i < armor.length; i++) {
            if (armor[i] != null && armor[i].getTypeId() != 0) {
                wearingArmor = true;
                break;
            }
        }
        
        int mana = hero.getMana();
        if (wearingArmor != wasWearingArmor || mana != lastTickMana) {
            if (wearingArmor) {
                halfLayout();
            }
            else {
                fullLayout();
            }
        }
        lastTickMana = mana;
        wasWearingArmor = wearingArmor;
    }

}
