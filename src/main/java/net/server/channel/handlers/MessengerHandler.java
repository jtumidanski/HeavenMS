/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package net.server.channel.handlers;

import java.util.Optional;

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.server.coordinator.MapleInviteCoordinator;
import net.server.coordinator.MapleInviteCoordinator.InviteResult;
import net.server.coordinator.MapleInviteCoordinator.InviteType;
import net.server.world.MapleMessenger;
import net.server.world.MapleMessengerCharacter;
import net.server.world.World;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.input.SeekableLittleEndianAccessor;

public final class MessengerHandler extends AbstractMaplePacketHandler {
   @Override
   public final void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
      if (c.tryAcquireClient()) {
         try {
            byte mode = slea.readByte();
            MapleCharacter player = c.getPlayer();
            World world = c.getWorldServer();
            //MapleMessenger messenger = player.getMessenger();
            switch (mode) {
               case 0x00:
                  joinMessenger(slea, player, world);
                  break;
               case 0x02:
                  player.closePlayerMessenger();
                  break;
               case 0x03:
                  messengerInvite(slea, c, player, world);
                  break;
               case 0x05:
                  String targeted = slea.readMapleAsciiString();
                  world.declineChat(targeted, player);
                  break;
               case 0x06:
                  player.getMessenger().ifPresent(messenger -> {
                     MapleMessengerCharacter messengerCharacter = new MapleMessengerCharacter(player, player.getMessengerPosition());
                     String input = slea.readMapleAsciiString();
                     world.messengerChat(messenger, input, messengerCharacter.getName());
                  });
                  break;
            }
         } finally {
            c.releaseClient();
         }
      }
   }

   private void messengerInvite(SeekableLittleEndianAccessor accessor, MapleClient c, MapleCharacter player, World world) {
      MapleMessenger messenger;
      if (player.getMessenger().isEmpty()) {
         c.announce(MaplePacketCreator.messengerChat(player.getName() + " : This Maple Messenger is currently unavailable. Please quit this chat."));
         return;
      } else {
         messenger = player.getMessenger().get();
      }

      if (messenger.getMembers().size() >= 3) {
         c.announce(MaplePacketCreator.messengerChat(player.getName() + " : You cannot have more than 3 people in the Maple Messenger"));
         return;
      }

      String input = accessor.readMapleAsciiString();

      Optional<MapleCharacter> target = c.getChannelServer().getPlayerStorage().getCharacterByName(input);
      if (target.isEmpty()) {
         if (world.find(input) > -1) {
            world.messengerInvite(c.getPlayer().getName(), messenger.getId(), input, c.getChannel());
         } else {
            c.announce(MaplePacketCreator.messengerNote(input, 4, 0));
         }
         return;
      }

      Optional<MapleMessenger> targetMessenger = target.get().getMessenger();
      if (targetMessenger.isPresent()) {
         c.announce(MaplePacketCreator.messengerChat(player.getName() + " : " + input + " is already using Maple Messenger"));
         return;
      }

      if (MapleInviteCoordinator.createInvite(InviteType.MESSENGER, c.getPlayer(), messenger.getId(), target.get().getId())) {
         target.get().getClient().announce(MaplePacketCreator.messengerInvite(c.getPlayer().getName(), messenger.getId()));
         c.announce(MaplePacketCreator.messengerNote(input, 4, 1));
      } else {
         c.announce(MaplePacketCreator.messengerChat(player.getName() + " : " + input + " is already managing a Maple Messenger invitation"));
      }
   }

   private void joinMessenger(SeekableLittleEndianAccessor accessor, MapleCharacter player, World world) {
      int messengerId = accessor.readInt();

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
                        player.message("Could not verify your Maple Messenger accept since the invitation rescinded.");
                     }
                  });
               }
            });
   }
}
