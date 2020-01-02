package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.autoban.AutoBanManager;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.manipulator.MapleInventoryManipulator;
import client.processor.PetProcessor;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.pet.PetFoodPacket;
import net.server.channel.packet.reader.PetFoodReader;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;

public final class PetFoodHandler extends AbstractPacketHandler<PetFoodPacket> {
   @Override
   public Class<PetFoodReader> getReaderClass() {
      return PetFoodReader.class;
   }

   @Override
   public void handlePacket(PetFoodPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      AutoBanManager abm = chr.getAutoBanManager();
      if (abm.getLastSpam(2) + 500 > currentServerTime()) {
         PacketCreator.announce(client, new EnableActions());
         return;
      }
      abm.spam(2);
      abm.setTimestamp(1, Server.getInstance().getCurrentTimestamp(), 3);
      if (chr.getNoPets() == 0) {
         PacketCreator.announce(client, new EnableActions());
         return;
      }
      int previousFullness = 100;
      byte slot = 0;
      MaplePet[] pets = chr.getPets();
      for (byte i = 0; i < 3; i++) {
         if (pets[i] != null) {
            if (pets[i].fullness() < previousFullness) {
               slot = i;
               previousFullness = pets[i].fullness();
            }
         }
      }

      MaplePet pet = chr.getPet(slot);
      if (pet == null) {
         return;
      }

      if (client.tryAcquireClient()) {
         try {
            MapleInventory useInv = chr.getInventory(MapleInventoryType.USE);
            useInv.lockInventory();
            try {
               Item use = useInv.getItem(packet.position());
               if (use == null || (packet.itemId() / 10000) != 212 || use.id() != packet.itemId() || use.quantity() < 1) {
                  return;
               }

               PetProcessor.getInstance().gainClosenessFullness(pet, chr, (pet.fullness() <= 75) ? 1 : 0, 30, 1);   // 25+ "emptiness" to get +1 closeness
               MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.USE, packet.position(), (short) 1, false);
            } finally {
               useInv.unlockInventory();
            }
         } finally {
            client.releaseClient();
         }
      }
   }
}
