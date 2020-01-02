package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.manipulator.MapleInventoryManipulator;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.UseSolomonPacket;
import net.server.channel.packet.reader.UseSolomonReader;
import server.MapleItemInformationProvider;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;

public final class UseSolomonHandler extends AbstractPacketHandler<UseSolomonPacket> {
   @Override
   public Class<UseSolomonReader> getReaderClass() {
      return UseSolomonReader.class;
   }

   @Override
   public void handlePacket(UseSolomonPacket packet, MapleClient client) {
      MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
      if (client.tryAcquireClient()) {
         try {
            MapleCharacter chr = client.getPlayer();
            MapleInventory inv = chr.getInventory(MapleInventoryType.USE);
            inv.lockInventory();
            try {
               Item slotItem = inv.getItem(packet.slot());
               if (slotItem == null) {
                  return;
               }

               long gachaponExp = ii.getExpById(packet.itemId());
               if (slotItem.id() != packet.itemId() || slotItem.quantity() <= 0 || chr.getLevel() > ii.getMaxLevelById(packet.itemId())) {
                  return;
               }
               if (gachaponExp + chr.getGachaponExperience() > Integer.MAX_VALUE) {
                  return;
               }
               chr.addGachaponExp((int) gachaponExp);
               MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.USE, packet.slot(), (short) 1, false);
            } finally {
               inv.unlockInventory();
            }
         } finally {
            client.releaseClient();
         }
      }

      PacketCreator.announce(client, new EnableActions());
   }
}
