package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import config.YamlConfig;
import net.server.AbstractPacketHandler;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import tools.PacketCreator;
import tools.packet.family.GetFamilyInfo;

public final class OpenFamilyHandler extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      return YamlConfig.config.server.USE_FAMILY_SYSTEM;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      PacketCreator.announce(client, new GetFamilyInfo(chr.getFamilyEntry()));
   }
}

