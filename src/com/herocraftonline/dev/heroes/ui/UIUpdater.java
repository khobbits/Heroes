package com.herocraftonline.dev.heroes.ui;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet131;

public class UIUpdater extends Thread {

    private EntityPlayer player;
    private byte[] bytes;
    private byte mapid;

    public UIUpdater(EntityPlayer player, byte[] bytes, byte mapid) {
        this.player = player;
        this.bytes = bytes;
        this.mapid = mapid;
    }

    public void run() {
        for (int i = 0; i < 128; i++) {
            byte[] abyte = new byte[131];
            abyte[1] = (byte) i;
            for (int j = 0; j < 128; j++) {
                abyte[j + 3] = bytes[j * 128 + i];
            }
            Packet packet = new Packet131((short) net.minecraft.server.Item.MAP.id, (short) mapid, abyte); // Hard coded for Map ID 0 atm.
            if (packet != null) {
                player.netServerHandler.sendPacket(packet);
                try {
                    sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        interrupt();
    }
}
