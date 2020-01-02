package net.server.channel.handlers;

import client.MapleClient;
import client.processor.action.SpawnPetProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.SpawnPetPacket;
import net.server.channel.packet.reader.SpawnPetReader;

public final class SpawnPetHandler extends AbstractPacketHandler<SpawnPetPacket> {
   @Override
   public Class<SpawnPetReader> getReaderClass() {
      return SpawnPetReader.class;
   }

   @Override
   public void handlePacket(SpawnPetPacket packet, MapleClient client) {
      SpawnPetProcessor.processSpawnPet(client, packet.slot(), packet.lead());
   }
}
