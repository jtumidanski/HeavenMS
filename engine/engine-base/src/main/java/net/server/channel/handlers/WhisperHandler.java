package net.server.channel.handlers;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import client.autoban.AutoBanFactory;
import com.ms.logs.LogType;
import com.ms.logs.LoggerOriginator;
import com.ms.logs.LoggerUtil;
import config.YamlConfig;
import database.DatabaseConnection;
import database.provider.CharacterProvider;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.WhisperPacket;
import net.server.channel.packet.reader.WhisperReader;
import net.server.world.World;
import tools.LogHelper;
import tools.PacketCreator;
import tools.packet.message.BuddyFindReply;
import tools.packet.message.FindReply;
import tools.packet.message.Whisper;
import tools.packet.message.WhisperReply;

public final class WhisperHandler extends AbstractPacketHandler<WhisperPacket> {
   @Override
   public Class<WhisperReader> getReaderClass() {
      return WhisperReader.class;
   }

   @Override
   public void handlePacket(WhisperPacket packet, MapleClient client) {
      switch (packet.mode()) {
         case 6 -> whisper(packet, client);
         case 5 -> find(packet, client);
         case 0x44 -> buddyFind(packet, client);
      }
   }

   private void buddyFind(WhisperPacket packet, MapleClient client) {
      client.getWorldServer().getPlayerStorage().getCharacterByName(packet.recipient()).filter(player -> client.getPlayer().gmLevel() >= player.gmLevel()).ifPresent(player -> {
         if (player.getCashShop().isOpened()) {  // in CashShop
            PacketCreator.announce(client, new BuddyFindReply(player.getName(), -1, 2));
         } else if (player.isAwayFromWorld()) {  // in MTS
            PacketCreator.announce(client, new BuddyFindReply(player.getName(), -1, 0));
         } else if (player.getClient().getChannel() != client.getChannel()) { // in another channel
            PacketCreator.announce(client, new BuddyFindReply(player.getName(), player.getClient().getChannel() - 1, 3));
         } else {
            PacketCreator.announce(client, new BuddyFindReply(player.getName(), player.getMap().getId(), 1));
         }
      });
   }

   private void find(WhisperPacket packet, MapleClient client) {
      client.getWorldServer().getPlayerStorage().getCharacterByName(packet.recipient()).filter(victim -> client.getPlayer().gmLevel() >= victim.gmLevel()).ifPresentOrElse(victim -> {
         if (victim.getCashShop().isOpened()) {  // in CashShop
            PacketCreator.announce(client, new FindReply(victim.getName(), -1, 2));
         } else if (victim.isAwayFromWorld()) {  // in MTS
            PacketCreator.announce(client, new FindReply(victim.getName(), -1, 0));
         } else if (victim.getClient().getChannel() != client.getChannel()) {
            PacketCreator.announce(client, new FindReply(victim.getName(), victim.getClient().getChannel() - 1, 3));
         } else {
            PacketCreator.announce(client, new FindReply(victim.getName(), victim.getMap().getId(), 1));
         }
      }, () -> {
         if (client.getPlayer().isGM()) { // not found
            Optional<Integer> gmLevel = DatabaseConnection.getInstance().withConnectionResult(connection ->
                  CharacterProvider.getInstance().getGmLevel(connection, packet.recipient()).orElse(0));
            if (gmLevel.isPresent() && gmLevel.get() >= client.getPlayer().gmLevel()) {
               PacketCreator.announce(client, new WhisperReply(packet.recipient(), (byte) 0));
               return;
            }
            byte channel = (byte) (client.getWorldServer().find(packet.recipient()) - 1);
            if (channel > -1) {
               PacketCreator.announce(client, new FindReply(packet.recipient(), channel, 3));
            } else {
               PacketCreator.announce(client, new WhisperReply(packet.recipient(), (byte) 0));
            }
         } else {
            PacketCreator.announce(client, new WhisperReply(packet.recipient(), (byte) 0));
         }
      });
   }

   private void whisper(WhisperPacket packet, MapleClient client) {
      Optional<MapleCharacter> player = client.getChannelServer().getPlayerStorage().getCharacterByName(packet.recipient());
      if (client.getPlayer().getAutoBanManager().getLastSpam(7) + 200 > currentServerTime()) {
         return;
      }
      if (packet.message().length() > Byte.MAX_VALUE) {
         AutoBanFactory.PACKET_EDIT.alert(client.getPlayer(), client.getPlayer().getName() + " tried to packet edit with whispers.");
         LoggerUtil.printError(LoggerOriginator.ENGINE, LogType.EXPLOITS, client.getPlayer().getName() + " tried to send text with length of " + packet.message().length());
         client.disconnect(true, false);
         return;
      }
      if (player.isPresent()) {
         PacketCreator.announce(player.get(), new Whisper(client.getPlayer().getName(), client.getChannel(), packet.message()));
         if (YamlConfig.config.server.USE_ENABLE_CHAT_LOG) {
            LogHelper.logChat(client, "Whisper To " + player.get().getName(), packet.message());
         }
         if (player.get().isHidden() && player.get().gmLevel() >= client.getPlayer().gmLevel()) {
            PacketCreator.announce(client, new WhisperReply(packet.recipient(), (byte) 0));
         } else {
            PacketCreator.announce(client, new WhisperReply(packet.recipient(), (byte) 1));
         }
      } else {// not found
         World world = client.getWorldServer();
         if (world.isConnected(packet.recipient())) {
            world.whisper(client.getPlayer().getName(), packet.recipient(), client.getChannel(), packet.message());
            if (YamlConfig.config.server.USE_ENABLE_CHAT_LOG) {
               LogHelper.logChat(client, "Whisper To " + packet.recipient(), packet.message());
            }
            player = world.getPlayerStorage().getCharacterByName(packet.recipient());
            if (player.isPresent()) {
               if (player.get().isHidden() && player.get().gmLevel() >= client.getPlayer().gmLevel()) {
                  PacketCreator.announce(client, new WhisperReply(packet.recipient(), (byte) 0));
               } else {
                  PacketCreator.announce(client, new WhisperReply(packet.recipient(), (byte) 1));
               }
            }
         } else {
            PacketCreator.announce(client, new WhisperReply(packet.recipient(), (byte) 0));
         }
      }
      client.getPlayer().getAutoBanManager().spam(7);
   }
}
