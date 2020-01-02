package net.server.channel.handlers;

import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.UseItemPacket;
import net.server.channel.packet.reader.UseItemReader;
import server.MapleItemInformationProvider;
import server.life.MapleLifeFactory;
import tools.PacketCreator;
import tools.Randomizer;
import tools.packet.stat.EnableActions;

public final class UseSummonBagHandler extends AbstractPacketHandler<UseItemPacket> {
   @Override
   public Class<UseItemReader> getReaderClass() {
      return UseItemReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      if (!client.getPlayer().isAlive()) {
         PacketCreator.announce(client, new EnableActions());
         return false;
      }
      return true;
   }

   @Override
   public void handlePacket(UseItemPacket packet, MapleClient client) {
      //[4A 00][6C 4C F2 02][02 00][63 0B 20 00]
      Item toUse = client.getPlayer().getInventory(MapleInventoryType.USE).getItem(packet.slot());
      if (toUse != null && toUse.quantity() > 0 && toUse.id() == packet.itemId()) {
         MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.USE, packet.slot(), (short) 1, false);
         int[][] toSpawn = MapleItemInformationProvider.getInstance().getSummonMobs(packet.itemId());
         for (int[] toSpawnChild : toSpawn) {
            if (Randomizer.nextInt(100) < toSpawnChild[1]) {
               MapleLifeFactory.getMonster(toSpawnChild[0]).ifPresent(monster -> client.getPlayer().getMap().spawnMonsterOnGroundBelow(monster, client.getPlayer().position()));
            }
         }
      }
      PacketCreator.announce(client, new EnableActions());
   }
}
