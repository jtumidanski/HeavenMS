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

import client.BuddyList;
import client.BuddyListAddResult;
import client.BuddyListEntry;
import client.BuddyListOperation;
import client.CharacterNameAndId;
import client.MapleCharacter;
import client.MapleClient;
import client.database.administrator.BuddyAdministrator;
import client.database.data.CharNameAndIdData;
import client.database.provider.BuddyProvider;
import client.database.provider.CharacterProvider;
import net.server.AbstractPacketHandler;
import net.server.channel.packet.buddy.AcceptBuddyPacket;
import net.server.channel.packet.buddy.AddBuddyPacket;
import net.server.channel.packet.buddy.BaseBuddyPacket;
import net.server.channel.packet.buddy.DeleteBuddyPacket;
import net.server.channel.packet.reader.BuddyReader;
import net.server.world.World;
import scala.Option;
import tools.DatabaseConnection;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.buddy.RequestAddBuddy;
import tools.packet.buddy.UpdateBuddyList;

public class BuddyListModifyHandler extends AbstractPacketHandler<BaseBuddyPacket> {
   @Override
   public Class<BuddyReader> getReaderClass() {
      return BuddyReader.class;
   }

   private void nextPendingRequest(MapleClient c) {
      Option<CharacterNameAndId> pendingBuddyRequest = c.getPlayer().getBuddylist().pollPendingRequest();
      if (pendingBuddyRequest.isDefined()) {
         PacketCreator.announce(c, new RequestAddBuddy(pendingBuddyRequest.get().id(), c.getPlayer().getId(), pendingBuddyRequest.get().name()));
      }
   }

   @Override
   public void handlePacket(BaseBuddyPacket packet, MapleClient client) {
      MapleCharacter player = client.getPlayer();
      BuddyList buddylist = player.getBuddylist();
      if (packet instanceof AddBuddyPacket) {
         add(client, player, buddylist, ((AddBuddyPacket) packet).name(), ((AddBuddyPacket) packet).group());
      } else if (packet instanceof AcceptBuddyPacket) {
         accept(client, buddylist, ((AcceptBuddyPacket) packet).otherCharacterId());
      } else if (packet instanceof DeleteBuddyPacket) {
         delete(player, ((DeleteBuddyPacket) packet).otherCharacterId());
      }
   }

   private void delete(MapleCharacter player, int otherCid) {
      player.deleteBuddy(otherCid);
   }

   private void accept(MapleClient c, BuddyList buddylist, int otherCid) {
      if (!buddylist.isFull()) {
         int channel = c.getWorldServer().find(otherCid);//worldInterface.find(otherCid);
         Optional<String> otherName = c.getChannelServer().getPlayerStorage().getCharacterById(otherCid)
               .map(MapleCharacter::getName)
               .or(() -> Optional.ofNullable(getCharacterNameFromDatabase(otherCid)));

         if (otherName.isPresent()) {
            buddylist.put(new BuddyListEntry(otherName.get(), "Default Group", otherCid, channel, true));
            PacketCreator.announce(c, new UpdateBuddyList(buddylist.getBuddies()));
            notifyRemoteChannel(c, channel, otherCid, BuddyListOperation.ADDED);
         }
      }
      nextPendingRequest(c);
   }

   private void add(MapleClient c, MapleCharacter player, BuddyList buddylist, String addName, String group) {
      if (group.length() > 16 || addName.length() < 4 || addName.length() > 13) {
         return; //hax.
      }
      BuddyListEntry ble = buddylist.get(addName);
      if (ble != null && !ble.visible() && group.equals(ble.group())) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, "You already have \"" + ble.name() + "\" on your Buddylist");
      } else if (buddylist.isFull() && ble == null) {
         MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, "Your buddylist is already full");
      } else if (ble == null) {
         World world = c.getWorldServer();
         CharNameAndIdData charWithId;
         int channel;
         Optional<MapleCharacter> otherChar = c.getChannelServer().getPlayerStorage().getCharacterByName(addName);
         if (otherChar.isPresent()) {
            channel = c.getChannel();
            charWithId = new CharNameAndIdData(otherChar.get().getName(), otherChar.get().getId(), otherChar.get().getBuddylist().capacity());
         } else {
            channel = world.find(addName);
            charWithId = DatabaseConnection.getInstance().withConnectionResult(connection -> CharacterProvider.getInstance().getCharacterInfoForName(connection, addName)).orElseThrow();
         }

         if (charWithId != null) {
            BuddyListAddResult buddyAddResult = null;
            if (channel != -1) {
               buddyAddResult = world.requestBuddyAdd(addName, c.getChannel(), player.getId(), player.getName());
            } else {
               long count = DatabaseConnection.getInstance().withConnectionResult(connection -> BuddyProvider.getInstance().getBuddyCount(connection, charWithId.id())).orElse(0L);
               if (count >= charWithId.buddyCapacity()) {
                  buddyAddResult = BuddyListAddResult.FULL;
               }

               boolean alreadyOnList = DatabaseConnection.getInstance().withConnectionResult(connection -> BuddyProvider.getInstance().buddyIsPending(connection, charWithId.id(), player.getId())).orElse(false);
               if (alreadyOnList) {
                  buddyAddResult = BuddyListAddResult.ALREADY_ON_LIST;
               }
            }
            if (buddyAddResult == BuddyListAddResult.FULL) {
               MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, "\"" + addName + "\"'s Buddylist is full");
            } else {
               int displayChannel;
               displayChannel = -1;
               int otherCid = charWithId.id();
               if (buddyAddResult == BuddyListAddResult.ALREADY_ON_LIST && channel != -1) {
                  displayChannel = channel;
                  notifyRemoteChannel(c, channel, otherCid, BuddyListOperation.ADDED);
               } else if (buddyAddResult != BuddyListAddResult.ALREADY_ON_LIST && channel == -1) {
                  DatabaseConnection.getInstance().withConnection(connection -> BuddyAdministrator.getInstance().addBuddy(connection, charWithId.id(), player.getId()));
               }
               buddylist.put(new BuddyListEntry(charWithId.name(), group, otherCid, displayChannel, true));
               PacketCreator.announce(c, new UpdateBuddyList(buddylist.getBuddies()));
            }
         } else {
            MessageBroadcaster.getInstance().sendServerNotice(player, ServerNoticeType.POP_UP, "A character called \"" + addName + "\" does not exist");
         }
      } else {
         ble.group_$eq(group);
         PacketCreator.announce(c, new UpdateBuddyList(buddylist.getBuddies()));
      }
   }

   private String getCharacterNameFromDatabase(int otherCid) {
      return DatabaseConnection.getInstance().withConnectionResult(connection -> CharacterProvider.getInstance().getNameForId(connection, otherCid)).orElse(null);
   }

   private void notifyRemoteChannel(MapleClient c, int remoteChannel, int otherCid, BuddyListOperation operation) {
      MapleCharacter player = c.getPlayer();
      if (remoteChannel != -1) {
         c.getWorldServer().buddyChanged(otherCid, player.getId(), player.getName(), c.getChannel(), operation);
      }
   }
}
