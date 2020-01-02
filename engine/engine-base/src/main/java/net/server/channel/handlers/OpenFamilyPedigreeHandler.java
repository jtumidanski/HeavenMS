package net.server.channel.handlers;

import client.MapleClient;
import config.YamlConfig;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.family.OpenFamilyPedigreePacket;
import net.server.channel.packet.reader.OpenFamilyPedigreeReader;
import tools.PacketCreator;
import tools.packet.family.ShowPedigree;

public final class OpenFamilyPedigreeHandler extends AbstractPacketHandler<OpenFamilyPedigreePacket> {
   @Override
   public Class<OpenFamilyPedigreeReader> getReaderClass() {
      return OpenFamilyPedigreeReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      return YamlConfig.config.server.USE_FAMILY_SYSTEM;
   }

   @Override
   public void handlePacket(OpenFamilyPedigreePacket packet, MapleClient client) {
      client.getChannelServer().getPlayerStorage().getCharacterByName(packet.characterName()).ifPresent(target -> {
         if (target.getFamily() != null) {
            PacketCreator.announce(client, new ShowPedigree(target.getFamilyEntry()));
         }
      });
   }
}

