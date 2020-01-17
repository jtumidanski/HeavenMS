package net.server.channel.handlers;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import config.YamlConfig;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.SpouseChatPacket;
import net.server.channel.packet.reader.SpouseChatReader;
import tools.LogHelper;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.I18nMessage;
import tools.packet.message.SpouseMessage;

public final class SpouseChatHandler extends AbstractPacketHandler<SpouseChatPacket> {
   @Override
   public Class<SpouseChatReader> getReaderClass() {
      return SpouseChatReader.class;
   }

   @Override
   public void handlePacket(SpouseChatPacket packet, MapleClient client) {

      int partnerId = client.getPlayer().getPartnerId();
      if (partnerId > 0) { // yay marriage
         Optional<MapleCharacter> spouse = client.getWorldServer().getPlayerStorage().getCharacterById(partnerId);
         if (spouse.isPresent()) {
            PacketCreator.announce(spouse.get(), new SpouseMessage(client.getPlayer().getName(), packet.message(), true));
            PacketCreator.announce(client, new SpouseMessage(client.getPlayer().getName(), packet.message(), true));
            if (YamlConfig.config.server.USE_ENABLE_CHAT_LOG) {
               LogHelper.logChat(client, "Spouse", packet.message());
            }
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("SPOUSE_OFFLINE"));
         }
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(client.getPlayer(), ServerNoticeType.PINK_TEXT, I18nMessage.from("SPOUSE_MISSING"));
      }
   }
}
