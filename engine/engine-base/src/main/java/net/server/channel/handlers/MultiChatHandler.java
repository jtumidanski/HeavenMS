package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.autoban.AutoBanFactory;
import config.YamlConfig;
import net.server.AbstractPacketHandler;
import net.server.Server;
import net.server.channel.packet.MultiChatPacket;
import net.server.channel.packet.reader.MultiChatReader;
import net.server.processor.MapleGuildProcessor;
import net.server.world.World;
import tools.LogHelper;
import tools.LogType;
import tools.LoggerOriginator;
import tools.LoggerUtil;
import tools.packet.message.MultiChat;

public final class MultiChatHandler extends AbstractPacketHandler<MultiChatPacket> {
   @Override
   public Class<MultiChatReader> getReaderClass() {
      return MultiChatReader.class;
   }

   @Override
   public boolean successfulProcess(MapleClient client) {
      MapleCharacter player = client.getPlayer();
      return player.getAutoBanManager().getLastSpam(7) + 200 <= currentServerTime();
   }

   @Override
   public void handlePacket(MultiChatPacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();

      if (packet.message().length() > Byte.MAX_VALUE && !player.isGM()) {
         AutoBanFactory.PACKET_EDIT.alert(client.getPlayer(), client.getPlayer().getName() + " tried to packet edit chats.");
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS, client.getPlayer().getName() + " tried to send text with length of " + packet.message().length());
         client.disconnect(true, false);
         return;
      }
      World world = client.getWorldServer();
      if (packet.theType() == 0) {
         buddyChat(packet, client, player, world);
      } else if (packet.theType() == 1 && player.getParty().isPresent()) {
         partyChat(packet, client, player, world);
      } else if (packet.theType() == 2 && player.getGuildId() > 0) {
         guildChat(packet, client, player);
      } else if (packet.theType() == 3) {
         allianceChat(packet, client, player);
      }
      player.getAutoBanManager().spam(7);
   }

   private void allianceChat(MultiChatPacket packet, MapleClient client, MapleCharacter player) {
      player.getGuild().ifPresent(guild -> {
         int allianceId = guild.getAllianceId();
         if (allianceId > 0) {
            Server.getInstance().allianceMessage(allianceId, new MultiChat(player.getName(), packet.message(), 3), player.getId(), -1);
            if (YamlConfig.config.server.USE_ENABLE_CHAT_LOG) {
               LogHelper.logChat(client, "Ally", packet.message());
            }
         }
      });
   }

   private void guildChat(MultiChatPacket packet, MapleClient client, MapleCharacter player) {
      MapleGuildProcessor.getInstance().guildChat(player, packet.message());
      if (YamlConfig.config.server.USE_ENABLE_CHAT_LOG) {
         LogHelper.logChat(client, "Guild", packet.message());
      }
   }

   private void partyChat(MultiChatPacket packet, MapleClient client, MapleCharacter player, World world) {
      player.getParty().ifPresent(party -> world.partyChat(party, packet.message(), player.getName()));
      if (YamlConfig.config.server.USE_ENABLE_CHAT_LOG) {
         LogHelper.logChat(client, "Party", packet.message());
      }
   }

   private void buddyChat(MultiChatPacket packet, MapleClient client, MapleCharacter player, World world) {
      world.buddyChat(packet.recipientIds(), player.getId(), player.getName(), packet.message());
      if (YamlConfig.config.server.USE_ENABLE_CHAT_LOG) {
         LogHelper.logChat(client, "Buddy", packet.message());
      }
   }
}
