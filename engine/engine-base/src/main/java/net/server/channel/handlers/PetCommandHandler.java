package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.MaplePet;
import client.inventory.PetCommand;
import client.inventory.PetDataFactory;
import client.processor.PetProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.pet.PetCommandPacket;
import net.server.channel.packet.reader.PetCommandReader;
import tools.MasterBroadcaster;
import tools.Randomizer;
import tools.packet.pet.PetCommandResponse;

public final class PetCommandHandler extends AbstractPacketHandler<PetCommandPacket> {
   @Override
   public Class<PetCommandReader> getReaderClass() {
      return PetCommandReader.class;
   }

   @Override
   public void handlePacket(PetCommandPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      byte petIndex = chr.getPetIndex(packet.petId());
      MaplePet pet;
      if (petIndex == -1) {
         return;
      } else {
         pet = chr.getPet(petIndex);
      }
      PetCommand petCommand = PetDataFactory.getPetCommand(pet.id(), packet.command());
      if (petCommand == null) {
         return;
      }

      if (Randomizer.nextInt(100) < petCommand.probability()) {
         PetProcessor.getInstance().gainClosenessFullness(chr, petIndex, petCommand.increase(), 0, packet.command());
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new PetCommandResponse(chr.getId(), petIndex, false, packet.command(), false));
      } else {
         MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new PetCommandResponse(chr.getId(), petIndex, true, packet.command(), false));
      }
   }
}
