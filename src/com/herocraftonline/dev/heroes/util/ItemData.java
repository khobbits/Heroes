package com.herocraftonline.dev.heroes.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemData {
    
    public final Material mat;
    public final int id;
    public final short subType;
    
    public ItemData(Material mat) {
        this(mat, (short) 0);
    }
    
    public ItemData(int id) {
        this(Material.getMaterial(id), (short) 0);
    }
    
    public ItemData(Material mat, short subType) {
        this.mat = mat;
        this.id = mat.getId();
        this.subType = subType;
    }
    
    public ItemData(int id, short subType) {
        this(Material.getMaterial(id), subType);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + subType;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        if (obj instanceof ItemStack) {
            ItemStack os = (ItemStack) obj;
            if (os.getType().getMaxDurability() > 16)
                return os.getTypeId() == id;
            else
                return os.getTypeId() == id && os.getDurability() == subType;
        }
        
        ItemData other = (ItemData) obj;
        return id == other.id && subType == other.subType;
    }
}
