package com.herocraftonline.dev.heroes.ui;

/**
 * Map API implementation for CraftBukkit. Do not use directly.
 */
public final class MapAPI {
/*
    
    public MapAPI() {}

    public WorldMap getWorldMap(World world, short mapId) {
        ItemStack nmsStack = new ItemStack(Material.MAP.getId(), 1, mapId);
        net.minecraft.server.World nmsWorld = ((CraftWorld) world).getHandle();
        return Item.MAP.a(nmsStack, nmsWorld);
    }

    public MapInfo loadMap(World world, short mapId) {
        WorldMap worldMap = getWorldMap(world, mapId);
        return new MapInfo(worldMap.map, worldMap.b, worldMap.c, worldMap.e, worldMap.f);
    }

    public void saveMap(World world, short mapId, MapInfo info) {
        WorldMap worldMap = getWorldMap(world, mapId);
        worldMap.b = info.getX();
        worldMap.c = info.getZ();
        worldMap.e = info.getScale();
        worldMap.f = info.getData();
        worldMap.map = info.getDimension();
        worldMap.a();
    }

    public void sendMap(Player player, short mapId, byte[] data, int interval) {
        
         * for (int col = 0; col < 128; ++col) {
         * byte[] raw = new byte[131];
         * raw[0] = 0;
         * raw[1] = (byte) col;
         * raw[2] = 0;
         * 
         * for (int row = 0; row < 128; ++row) {
         * raw[3 + row] = data[row * 128 + col];
         * }
         * 
         * sendRawData(player, mapId, raw);
         * }
         

        UIUpdater updater = new UIUpdater(((CraftPlayer) player).getHandle(), data, (byte) mapId, interval);
        updater.start();
    }

    public void sendRawData(Player player, short mapId, byte[] data) {
        // Packet packet = new Packet131((short) Material.MAP.getId(), mapId, data);
        // EntityPlayer entity = ((CraftPlayer) player).getHandle();
        // entity.netServerHandler.sendPacket(packet);
    }
    */
}
