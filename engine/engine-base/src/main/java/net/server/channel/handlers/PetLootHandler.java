package net.server.channel.handlers;

import java.util.Set;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.MaplePet;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.pet.PetLootPacket;
import net.server.channel.packet.reader.PetLootReader;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import tools.PacketCreator;
import tools.packet.stat.EnableActions;

public final class PetLootHandler extends AbstractPacketHandler<PetLootPacket> {
   @Override
   public Class<PetLootReader> getReaderClass() {
      return PetLootReader.class;
   }

   @Override
   public void handlePacket(PetLootPacket packet, MapleClient c) {
      MapleCharacter chr = c.getPlayer();

      int petIndex = chr.getPetIndex(packet.petIndex());
      MaplePet pet = chr.getPet(petIndex);
      if (pet == null || !pet.summoned()) {
         PacketCreator.announce(c, new EnableActions());
         return;
      }

      MapleMapObject ob = chr.getMap().getMapObject(packet.objectId());
      try {
         MapleMapItem mapItem = (MapleMapItem) ob;
         if (mapItem.getMeso() > 0) {
            if (!chr.isEquippedMesoMagnet()) {
               PacketCreator.announce(c, new EnableActions());
               return;
            }

            if (chr.isEquippedPetItemIgnore()) {
               final Set<Integer> petIgnore = chr.getExcludedItems();
               if (!petIgnore.isEmpty() && petIgnore.contains(Integer.MAX_VALUE)) {
                  PacketCreator.announce(c, new EnableActions());
                  return;
               }
            }
         } else {
            if (!chr.isEquippedItemPouch()) {
               PacketCreator.announce(c, new EnableActions());
               return;
            }

            if (chr.isEquippedPetItemIgnore()) {
               final Set<Integer> petIgnore = chr.getExcludedItems();
               if (!petIgnore.isEmpty() && petIgnore.contains(mapItem.getItem().id())) {
                  PacketCreator.announce(c, new EnableActions());
                  return;
               }
            }
         }

         chr.pickupItem(ob, petIndex);
      } catch (NullPointerException | ClassCastException e) {
         PacketCreator.announce(c, new EnableActions());
      }
   }
}
