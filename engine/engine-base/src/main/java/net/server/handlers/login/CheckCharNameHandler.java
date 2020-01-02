package net.server.handlers.login;

import client.MapleClient;
import client.processor.CharacterProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.reader.CheckCharacterNameReader;
import net.server.login.packet.CheckCharacterNamePacket;
import tools.PacketCreator;
import tools.packet.CharacterName;

public final class CheckCharNameHandler extends AbstractPacketHandler<CheckCharacterNamePacket> {
   @Override
   public Class<CheckCharacterNameReader> getReaderClass() {
      return CheckCharacterNameReader.class;
   }

   @Override
   public void handlePacket(CheckCharacterNamePacket packet, MapleClient client) {
      boolean nameUsed = !CharacterProcessor.getInstance().canCreateChar(packet.name());
      PacketCreator.announce(client, new CharacterName(packet.name(), nameUsed));
   }
}
