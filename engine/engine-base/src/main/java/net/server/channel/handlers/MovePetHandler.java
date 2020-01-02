package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.pet.PetMovementPacket;
import net.server.channel.packet.reader.PetMovementReader;
import tools.MasterBroadcaster;
import tools.packet.movement.MovePet;

public final class MovePetHandler extends AbstractPacketHandler<PetMovementPacket> {
   @Override
   public Class<PetMovementReader> getReaderClass() {
      return PetMovementReader.class;
   }

   @Override
   public void handlePacket(PetMovementPacket packet, MapleClient client) {
      if (packet == null || packet.commands() == null || packet.commands().isEmpty()) {
         return;
      }
      MapleCharacter player = client.getPlayer();
      byte slot = player.getPetIndex(packet.petId());
      if (slot == -1) {
         return;
      }
      player.getPet(slot).updatePosition(packet.commands());
      MasterBroadcaster.getInstance().sendToAllInMap(player.getMap(), new MovePet(player.getId(), packet.petId(), slot, packet.commands()), false, player);
   }
}
