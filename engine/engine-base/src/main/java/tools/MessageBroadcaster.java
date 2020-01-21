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
import server.maps.MapleMap;
import tools.packet.message.ServerNotice;
import tools.packet.message.YellowTip;

public class MessageBroadcaster {
   private static MessageBroadcaster ourInstance = new MessageBroadcaster();

   public static MessageBroadcaster getInstance() {
      return ourInstance;
   }

   private MessageBroadcaster() {
   }

   public void sendWorldServerNotice(int worldId, int originChannel, ServerNoticeType noticeType, UserMessage message, boolean superMegaphone) {
      sendWorldNotice(worldId, character -> PacketCreator.create(new ServerNotice(noticeType.getValue(), originChannel, message.to(character).evaluate(), superMegaphone)));
   }

   public void sendWorldServerNotice(int worldId, ServerNoticeType noticeType, UserMessage message) {
      sendWorldNotice(worldId, character -> PacketCreator.create(new ServerNotice(noticeType.getValue(), message.to(character).evaluate())));
   }

   public void sendWorldServerNotice(int worldId, ServerNoticeType noticeType, Function<MapleCharacter, Boolean> filter, UserMessage message) {
      sendWorldNotice(worldId, filter, character -> PacketCreator.create(new ServerNotice(noticeType.getValue(), message.to(character).evaluate())));
   }

   private void sendWorldNotice(int worldId, Function<MapleCharacter, byte[]> packetCreator) {
      sendWorldNotice(worldId, null, packetCreator);
   }

   private void sendWorldNotice(int worldId, Function<MapleCharacter, Boolean> filter, Function<MapleCharacter, byte[]> packetCreator) {
      if (filter != null) {
         MasterBroadcaster.getInstance().send(Server.getInstance().getWorld(worldId).getPlayerStorage().getAllCharacters().stream().filter(filter::apply).collect(Collectors.toList()), packetCreator);
      } else {
         MasterBroadcaster.getInstance().send(Server.getInstance().getWorld(worldId).getPlayerStorage().getAllCharacters(), packetCreator);
      }
   }

   public void sendMapServerNotice(MapleMap map, ServerNoticeType noticeType, UserMessage message) {
      MasterBroadcaster.getInstance().sendToAllInMap(map, null, character -> PacketCreator.create(new ServerNotice(noticeType.getValue(), message.to(character).evaluate())));
   }

   public void sendMapServerNotice(MapleMap map, ServerNoticeType noticeType, Function<MapleCharacter, Boolean> filter, UserMessage message) {
      MasterBroadcaster.getInstance().sendToAllInMap(map, filter, character -> PacketCreator.create(new ServerNotice(noticeType.getValue(), message.to(character).evaluate())));
   }

   public void sendChannelServerNotice(int worldId, int channelId, ServerNoticeType noticeType, UserMessage message) {
      Collection<MapleCharacter> characters = Server.getInstance().getChannel(worldId, channelId).getPlayerStorage().getAllCharacters();
      MasterBroadcaster.getInstance().send(characters, character -> PacketCreator.create(new ServerNotice(noticeType.getValue(), message.to(character).evaluate())));
   }

   public void sendGuildServerNotice(MapleGuild guild, ServerNoticeType noticeType, UserMessage message) {
      List<MapleCharacter> guildMembers = guild.getMemberCharacters();
      MasterBroadcaster.getInstance().send(guildMembers, character -> PacketCreator.create(new ServerNotice(noticeType.getValue(), message.to(character).evaluate())));
   }

   public void sendAllianceServerNotice(MapleAlliance alliance, ServerNoticeType noticeType, UserMessage message) {
      alliance.guilds().parallelStream()
            .map(guildId -> Server.getInstance().getGuild(guildId))
            .flatMap(Optional::stream)
            .forEach(guild -> sendGuildServerNotice(guild, noticeType, message));
   }

   public void sendServerNotice(Collection<MapleCharacter> recipients, ServerNoticeType noticeType, UserMessage message) {
      MasterBroadcaster.getInstance().send(recipients, character -> PacketCreator.create(new ServerNotice(noticeType.getValue(), message.to(character).evaluate())));
   }

   public void sendServerNotice(MapleCharacter character, ServerNoticeType noticeType, UserMessage message) {
      MasterBroadcaster.getInstance().send(Collections.singletonList(character), recipient -> PacketCreator.create(new ServerNotice(noticeType.getValue(), message.to(recipient).evaluate())));
   }

   public void sendServerNoticeToAcquaintances(MapleCharacter originator, ServerNoticeType noticeType, UserMessage message) {
      {
         PlayerStorage playerStorage = originator.getClient().getChannelServer().getPlayerStorage();
         Collection<MapleCharacter> buddies = Arrays.stream(originator.getBuddyList().getBuddyIds())
               .mapToObj(playerStorage::getCharacterById)
               .flatMap(Optional::stream)
               .filter(MapleCharacter::isLoggedInWorld)
               .collect(Collectors.toList());
         MasterBroadcaster.getInstance().send(buddies, recipient -> PacketCreator.create(new ServerNotice(noticeType.getValue(), message.to(recipient).evaluate())));
      }
      {
         Collection<MapleCharacter> familyMembers = originator.getFamily().getMembers().stream()
               .map(MapleFamilyEntry::getChr)
               .collect(Collectors.toList());
         MasterBroadcaster.getInstance().send(familyMembers, recipient -> PacketCreator.create(new ServerNotice(noticeType.getValue(), message.to(recipient).evaluate())));
      }
      {
         List<MapleCharacter> guildMembers = originator.getGuild()
               .map(MapleGuild::getMemberCharacters)
               .orElse(Collections.emptyList());
         MasterBroadcaster.getInstance().send(guildMembers, recipient -> PacketCreator.create(new ServerNotice(noticeType.getValue(), message.to(recipient).evaluate())));
      }
   }

   public void yellowMessage(MapleCharacter character, UserMessage message) {
      MasterBroadcaster.getInstance().send(Collections.singletonList(character), recipient -> PacketCreator.create(new YellowTip(message.to(recipient).evaluate())));
   }

   public void sendServerNotice(MapleCharacter character, ServerNoticeType noticeType, String s) {
   }
}
