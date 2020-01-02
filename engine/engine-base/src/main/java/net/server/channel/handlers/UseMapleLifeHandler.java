package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.processor.CharacterProcessor;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.UseMapleLifePacket;
import net.server.channel.packet.reader.UseMapleLifeReader;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.cashshop.SendMapleLife;
import tools.packet.cashshop.SendMapleLifeError;
import tools.packet.cashshop.SendMapleNameLifeError;
import tools.packet.stat.EnableActions;

public class UseMapleLifeHandler extends AbstractPacketHandler<UseMapleLifePacket> {
   @Override
   public Class<UseMapleLifeReader> getReaderClass() {
      return UseMapleLifeReader.class;
   }

   @Override
   public void handlePacket(UseMapleLifePacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      long timeNow = currentServerTime();

      if (timeNow - player.getLastUsedCashItem() < 3000) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Please wait a moment before trying again.");
         PacketCreator.announce(client, new SendMapleLifeError(3));
         PacketCreator.announce(client, new EnableActions());
         return;
      }
      player.setLastUsedCashItem(timeNow);

      if (CharacterProcessor.getInstance().canCreateChar(packet.name())) {
         PacketCreator.announce(client, new SendMapleLife());
      } else {
         PacketCreator.announce(client, new SendMapleNameLifeError());
      }
      PacketCreator.announce(client, new EnableActions());
   }
}
