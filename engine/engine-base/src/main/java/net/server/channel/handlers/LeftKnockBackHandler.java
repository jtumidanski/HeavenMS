package net.server.channel.handlers;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.packet.NoOpPacket;
import net.server.packet.reader.NoOpReader;
import tools.PacketCreator;
import tools.packet.character.CharacterKnockBack;
import tools.packet.stat.EnableActions;

public class LeftKnockBackHandler extends AbstractPacketHandler<NoOpPacket> {
   @Override
   public Class<NoOpReader> getReaderClass() {
      return NoOpReader.class;
   }

   @Override
   public void handlePacket(NoOpPacket packet, MapleClient client) {
      PacketCreator.announce(client, new CharacterKnockBack());
      PacketCreator.announce(client, new EnableActions());
   }
}
