package net.server.channel.handlers;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.messenger.BaseMessengerPacket;
import net.server.channel.packet.messenger.CloseMessenger;
import net.server.channel.packet.messenger.JoinMessengerPacket;
import net.server.channel.packet.messenger.MessengerChat;
import net.server.channel.packet.messenger.MessengerDecline;
import net.server.channel.packet.messenger.MessengerInvite;
import net.server.channel.packet.reader.MessengerReader;
import net.server.coordinator.world.MapleInviteCoordinator;
import net.server.coordinator.world.MapleInviteCoordinator.InviteResult;
import net.server.coordinator.world.MapleInviteCoordinator.InviteType;
import net.server.world.MapleMessenger;
import net.server.world.MapleMessengerCharacter;
import net.server.world.World;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.messenger.MessengerNote;

public final class MessengerHandler extends AbstractPacketHandler<BaseMessengerPacket> {
   @Override
   public Class<MessengerReader> getReaderClass() {
      return MessengerReader.class;
   }

   @Override
   public void handlePacket(BaseMessengerPacket packet, MapleClient client) {
      if (client.tryAcquireClient()) {
         try {
            MapleCharacter player = client.getPlayer();
            World world = client.getWorldServer();
            if (packet instanceof JoinMessengerPacket) {
               joinMessenger(player, world, ((JoinMessengerPacket) packet).messengerId());
            } else if (packet instanceof CloseMessenger) {
               closeMessenger(player);
            } else if (packet instanceof MessengerInvite) {
               messengerInvite(client, player, world, ((MessengerInvite) packet).input());
            } else if (packet instanceof MessengerDecline) {
               declineChat((MessengerDecline) packet, player, world);
            } else if (packet instanceof MessengerChat) {
               chat((MessengerChat) packet, player, world);
            }
         } finally {
            client.releaseClient();
         }
      }
   }

   private void chat(MessengerChat packet, MapleCharacter player, World world) {
      player.getMessenger().ifPresent(messenger -> {
         MapleMessengerCharacter messengerCharacter = new MapleMessengerCharacter(player, player.getMessengerPosition());
         world.messengerChat(messenger, packet.input(), messengerCharacter.getName());
      });
   }

   private void declineChat(MessengerDecline packet, MapleCharacter player, World world) {
      world.declineChat(packet.target(), player);
   }

   private void closeMessenger(MapleCharacter player) {
      player.closePlayerMessenger();
   }

   private void messengerInvite(MapleClient c, MapleCharacter player, World world, String input) {
      MapleMessenger messenger;
      if (player.getMessenger().isEmpty()) {
         PacketCreator.announce(c, new tools.packet.messenger.MessengerChat(player.getName() + " : This Maple Messenger is currently unavailable. Please quit this chat."));
         return;
      } else {
         messenger = player.getMessenger().get();
      }

      if (messenger.getMembers().size() >= 3) {
         PacketCreator.announce(c, new tools.packet.messenger.MessengerChat(player.getName() + " : You cannot have more than 3 people in the Maple Messenger"));
         return;
      }

      Optional<MapleCharacter> target = c.getChannelServer().getPlayerStorage().getCharacterByName(input);
      if (target.isEmpty()) {
         if (world.find(input) > -1) {
            world.messengerInvite(c.getPlayer().getName(), messenger.getId(), input, c.getChannel());
         } else {
            PacketCreator.announce(c, new MessengerNote(input, 4, 0));
         }
         return;
      }

      Optional<MapleMessenger> targetMessenger = target.get().getMessenger();
      if (targetMessenger.isPresent()) {
         PacketCreator.announce(c, new tools.packet.messenger.MessengerChat(player.getName() + " : " + input + " is already using Maple Messenger"));
         return;
      }

      if (MapleInviteCoordinator.createInvite(InviteType.MESSENGER, c.getPlayer(), messenger.getId(), target.get().getId())) {
         PacketCreator.announce(target.get(), new tools.packet.messenger.MessengerInvite(c.getPlayer().getName(), messenger.getId()));
         PacketCreator.announce(c, new MessengerNote(input, 4, 1));
      } else {
         PacketCreator.announce(c, new tools.packet.messenger.MessengerChat(player.getName() + " : " + input + " is already managing a Maple Messenger invitation"));
      }
   }

   private void joinMessenger(MapleCharacter player, World world, int messengerId) {
      player.getMessenger().ifPresentOrElse(messenger -> MapleInviteCoordinator.answerInvite(InviteType.MESSENGER, player.getId(), messengerId, false),
            () -> {
               if (messengerId == 0) {
                  MapleInviteCoordinator.removeInvite(InviteType.MESSENGER, player.getId());
                  MapleMessengerCharacter messengerCharacter = new MapleMessengerCharacter(player, 0);
                  MapleMessenger messenger = world.createMessenger(messengerCharacter);
                  player.setMessenger(messenger);
                  player.setMessengerPosition(0);
               } else {
                  world.getMessenger(messengerId).ifPresent(messenger -> {
                     MapleInviteCoordinator.MapleInviteResult inviteRes = MapleInviteCoordinator.answerInvite(InviteType.MESSENGER, player.getId(), messengerId, true);
                     InviteResult res = inviteRes.result;
                     if (res == InviteResult.ACCEPTED) {
                        int position = messenger.getLowestPosition();
                        MapleMessengerCharacter messengerCharacter = new MapleMessengerCharacter(player, position);
                        if (messenger.getMembers().size() < 3) {
                           player.setMessenger(messenger);
                           player.setMessengerPosition(position);
                           world.joinMessenger(messenger.getId(), messengerCharacter, player.getName(), messengerCharacter.getChannel());
                        }
                     } else {
                        MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.PINK_TEXT, "Could not verify your Maple Messenger accept since the invitation rescinded.");
                     }
                  });
               }
            });
   }
}
