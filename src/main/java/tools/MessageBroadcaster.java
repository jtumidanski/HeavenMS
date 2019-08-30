package tools;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import client.MapleCharacter;
import client.MapleFamilyEntry;
import net.server.PlayerStorage;
import net.server.Server;
import net.server.guild.MapleAlliance;
import net.server.guild.MapleGuild;
import net.server.guild.MapleGuildCharacter;
import server.maps.MapleMap;

public class MessageBroadcaster {
   private static MessageBroadcaster ourInstance = new MessageBroadcaster();

   public static MessageBroadcaster getInstance() {
      return ourInstance;
   }

   private MessageBroadcaster() {
   }

   public void sendWorldServerNotice(int worldId, int originChannel, ServerNoticeType noticeType, String message, boolean smegaEar) {
      sendWorldNotice(worldId, character -> MaplePacketCreator.serverNotice(noticeType.getValue(), originChannel, message, smegaEar));
   }

   public void sendWorldServerNotice(int worldId, ServerNoticeType noticeType, String message) {
      sendWorldNotice(worldId, character -> MaplePacketCreator.serverNotice(noticeType.getValue(), message));
   }

   public void sendWorldServerNotice(int worldId, ServerNoticeType noticeType, Function<MapleCharacter, Boolean> filter, String message) {
      sendWorldNotice(worldId, filter, character -> MaplePacketCreator.serverNotice(noticeType.getValue(), message));
   }

   private void sendWorldNotice(int worldId, Function<MapleCharacter, byte[]> packetCreator) {
      sendWorldNotice(worldId, null, packetCreator);
   }

   private void sendWorldNotice(int worldId, Function<MapleCharacter, Boolean> filter, Function<MapleCharacter, byte[]> packetCreator) {
      if (filter != null) {
         send(Server.getInstance().getWorld(worldId).getPlayerStorage().getAllCharacters().stream().filter(filter::apply).collect(Collectors.toList()), packetCreator);
      } else {
         send(Server.getInstance().getWorld(worldId).getPlayerStorage().getAllCharacters(), packetCreator);
      }
   }

   public void sendMapServerNotice(MapleMap map, ServerNoticeType noticeType, String message) {
      sendMapNotice(map, null, character -> MaplePacketCreator.serverNotice(noticeType.getValue(), message));
   }

   public void sendMapServerNotice(MapleMap map, ServerNoticeType noticeType, Function<MapleCharacter, Boolean> filter, String message) {
      sendMapNotice(map, filter, character -> MaplePacketCreator.serverNotice(noticeType.getValue(), message));
   }

   public void sendChannelServerNotice(int worldId, int channelId, ServerNoticeType noticeType, String message) {
      send(Server.getInstance().getChannel(worldId, channelId).getPlayerStorage().getAllCharacters(), character -> MaplePacketCreator.serverNotice(noticeType.getValue(), message));
   }

   public void sendGuildServerNotice(MapleGuild guild, ServerNoticeType noticeType, String message) {
      send(guild.getMembers().stream().map(MapleGuildCharacter::getCharacter).collect(Collectors.toList()), character -> MaplePacketCreator.serverNotice(noticeType.getValue(), message));
   }

   public void sendAllianceServerNotice(MapleAlliance alliance, ServerNoticeType noticeType, String message) {
      alliance.getGuilds().parallelStream()
            .map(guildId -> Server.getInstance().getGuild(guildId))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(guild -> sendGuildServerNotice(guild, noticeType, message));
   }

   public void sendServerNotice(Collection<MapleCharacter> recipients, ServerNoticeType noticeType, String message) {
      send(recipients, character -> MaplePacketCreator.serverNotice(noticeType.getValue(), message));
   }

   public void sendServerNotice(MapleCharacter character, ServerNoticeType noticeType, String message) {
      send(Collections.singletonList(character), recipient -> MaplePacketCreator.serverNotice(noticeType.getValue(), message));
   }

   public void sendServerNoticeToAcquaintances(MapleCharacter originator, ServerNoticeType noticeType, String message) {
      {
         PlayerStorage playerStorage = originator.getClient().getChannelServer().getPlayerStorage();
         Collection<MapleCharacter> buddies = Arrays.stream(originator.getBuddylist().getBuddyIds())
               .mapToObj(playerStorage::getCharacterById)
               .flatMap(Optional::stream)
               .filter(MapleCharacter::isLoggedinWorld)
               .collect(Collectors.toList());
         send(buddies, recipient -> MaplePacketCreator.serverNotice(noticeType.getValue(), message));
      }
      {
         Collection<MapleCharacter> familyMembers = originator.getFamily().getMembers().stream().map(MapleFamilyEntry::getChr).collect(Collectors.toList());
         send(familyMembers, recipient -> MaplePacketCreator.serverNotice(noticeType.getValue(), message));
      }
      {
         List<MapleCharacter> guildMembers = originator.getGuild()
               .map(guild -> guild.getMembers().stream()
                     .map(MapleGuildCharacter::getCharacter)
                     .collect(Collectors.toList()))
               .orElse(Collections.emptyList());
         send(guildMembers, recipient -> MaplePacketCreator.serverNotice(noticeType.getValue(), message));
      }

   }

   private void sendMapNotice(MapleMap map, Function<MapleCharacter, Boolean> filter, Function<MapleCharacter, byte[]> packetCreator) {
      if (filter == null) {
         send(map.getCharacters(), packetCreator);
      } else {
         send(map.getCharacters().stream().filter(filter::apply).collect(Collectors.toList()), packetCreator);
      }
   }

   private void send(Collection<MapleCharacter> recipients, Function<MapleCharacter, byte[]> packetCreator) {
      recipients.parallelStream().forEach(character -> character.announce(packetCreator.apply(character)));
   }
}
