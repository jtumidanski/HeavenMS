package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.autoban.AutoBanFactory;
import client.command.CommandsExecutor;
import config.YamlConfig;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.GeneralChatPacket;
import net.server.channel.packet.reader.GeneralChatReader;
import tools.I18nMessage;
import tools.LogHelper;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.message.ChatText;
import tools.packet.stat.EnableActions;

public final class GeneralChatHandler extends AbstractPacketHandler<GeneralChatPacket> {
   @Override
   public Class<GeneralChatReader> getReaderClass() {
      return GeneralChatReader.class;
   }

   @Override
   public void handlePacket(GeneralChatPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      if (chr.getAutoBanManager().getLastSpam(7) + 200 > currentServerTime()) {
         PacketCreator.announce(client, new EnableActions());
         return;
      }
      if (packet.message().length() > Byte.MAX_VALUE && !chr.isGM()) {
         AutoBanFactory.PACKET_EDIT.alert(client.getPlayer(), client.getPlayer().getName() + " tried to packet edit in General Chat.");
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS, client.getPlayer().getName() + " tried to send text with length of " + packet.message().length());
         client.disconnect(true, false);
         return;
      }
      char heading = packet.message().charAt(0);
      if (CommandsExecutor.isCommand(client, packet.message())) {
         CommandsExecutor.getInstance().handle(client, packet.message());
      } else if (heading != '/') {
         if (chr.getMap().isMuted() && !chr.isGM()) {
            MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, I18nMessage.from("MUTED_MAP_ERROR"));
            return;
         }

         if (!chr.isHidden()) {
            MasterBroadcaster.getInstance().sendToAllInMap(chr.getMap(), new ChatText(chr.getId(), packet.message(), chr.getWhiteChat(), packet.show()));
            if (YamlConfig.config.server.USE_ENABLE_CHAT_LOG) {
               LogHelper.logChat(client, "General", packet.message());
            }
         } else {
            MasterBroadcaster.getInstance().sendToAllGMInMap(chr.getMap(), new ChatText(chr.getId(), packet.message(), chr.getWhiteChat(), packet.show()));
            if (YamlConfig.config.server.USE_ENABLE_CHAT_LOG) {
               LogHelper.logChat(client, "GM General", packet.message());
            }
         }

         chr.getAutoBanManager().spam(7);
      }
   }
}