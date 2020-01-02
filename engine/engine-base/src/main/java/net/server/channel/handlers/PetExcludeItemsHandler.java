package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.MaplePet;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.pet.PetExcludeItemsPacket;
import net.server.channel.packet.reader.PetExcludeItemsReader;

public final class PetExcludeItemsHandler extends AbstractPacketHandler<PetExcludeItemsPacket> {
   @Override
   public Class<PetExcludeItemsReader> getReaderClass() {
      return PetExcludeItemsReader.class;
   }

   @Override
   public void handlePacket(PetExcludeItemsPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      byte petIndex = chr.getPetIndex(packet.petId());
      if (petIndex < 0) {
         return;
      }

      final MaplePet pet = chr.getPet(petIndex);
      if (pet == null) {
         return;
      }

      chr.resetExcluded(packet.petId());
      for (int i = 0; i < packet.amount(); i++) {
         chr.addExcluded(packet.petId(), packet.itemIds()[i]);
      }
      chr.commitExcludedItems();
   }
}
