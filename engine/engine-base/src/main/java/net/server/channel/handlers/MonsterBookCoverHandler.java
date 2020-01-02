package net.server.channel.handlers;

import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.MonsterBookCoverPacket;
import net.server.channel.packet.reader.MonsterBookCoverReader;
import tools.PacketCreator;
import tools.packet.monster.book.ChangeCover;

public final class MonsterBookCoverHandler extends AbstractPacketHandler<MonsterBookCoverPacket> {
   @Override
   public Class<MonsterBookCoverReader> getReaderClass() {
      return MonsterBookCoverReader.class;
   }

   @Override
   public void handlePacket(MonsterBookCoverPacket packet, MapleClient client) {
      if (packet.coverId() == 0 || packet.coverId() / 10000 == 238) {
         client.getPlayer().setMonsterBookCover(packet.coverId());
         PacketCreator.announce(client, new ChangeCover(packet.coverId()));
      }
   }
}
