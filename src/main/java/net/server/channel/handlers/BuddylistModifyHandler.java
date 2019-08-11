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

import static client.BuddyList.BuddyOperation.ADDED;

import java.util.Optional;

import client.BuddyList;
import client.BuddyList.BuddyAddResult;
import client.BuddyList.BuddyOperation;
import client.BuddylistEntry;
import client.CharacterNameAndId;
import client.MapleCharacter;
import client.MapleClient;
import client.database.administrator.BuddyAdministrator;
import client.database.provider.BuddyProvider;
import client.database.provider.CharacterProvider;
import client.database.data.CharNameAndIdData;
import net.AbstractMaplePacketHandler;
import net.server.world.World;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

public class BuddylistModifyHandler extends AbstractMaplePacketHandler {
   private void nextPendingRequest(MapleClient c) {
      CharacterNameAndId pendingBuddyRequest = c.getPlayer().getBuddylist().pollPendingRequest();
      if (pendingBuddyRequest != null) {
         c.announce(MaplePacketCreator.requestBuddylistAdd(pendingBuddyRequest.getId(), c.getPlayer().getId(), pendingBuddyRequest.getName()));
      }
   }

   @Override
   public void handlePacket(SeekableLittleEndianAccessor slea, MapleClient c) {
      int mode = slea.readByte();
      MapleCharacter player = c.getPlayer();
      BuddyList buddylist = player.getBuddylist();
      if (mode == 1) { // add
         String addName = slea.readMapleAsciiString();
         String group = slea.readMapleAsciiString();
         if (group.length() > 16 || addName.length() < 4 || addName.length() > 13) {
            return; //hax.
         }
         BuddylistEntry ble = buddylist.get(addName);
         if (ble != null && !ble.isVisible() && group.equals(ble.getGroup())) {
            c.announce(MaplePacketCreator.serverNotice(1, "You already have \"" + ble.getName() + "\" on your Buddylist"));
         } else if (buddylist.isFull() && ble == null) {
            c.announce(MaplePacketCreator.serverNotice(1, "Your buddylist is already full"));
         } else if (ble == null) {
            World world = c.getWorldServer();
            CharNameAndIdData charWithId;
            int channel;
            Optional<MapleCharacter> otherChar = c.getChannelServer().getPlayerStorage().getCharacterByName(addName);
            if (otherChar.isPresent()) {
               channel = c.getChannel();
               charWithId = new CharNameAndIdData(otherChar.get().getName(), otherChar.get().getId(), otherChar.get().getBuddylist().getCapacity());
            } else {
               channel = world.find(addName);
               charWithId = DatabaseConnection.withConnectionResult(connection -> CharacterProvider.getInstance().getCharacterInfoForName(connection, addName)).orElseThrow();
            }

            if (charWithId != null) {
               BuddyAddResult buddyAddResult = null;
               if (channel != -1) {
                  buddyAddResult = world.requestBuddyAdd(addName, c.getChannel(), player.getId(), player.getName());
               } else {
                  long count = DatabaseConnection.withConnectionResult(connection -> BuddyProvider.getInstance().getBuddyCount(connection, charWithId.getId())).orElse(0L);
                  if (count >= charWithId.getBuddyCapacity()) {
                     buddyAddResult = BuddyAddResult.BUDDYLIST_FULL;
                  }

                  boolean alreadyOnList = DatabaseConnection.withConnectionResult(connection -> BuddyProvider.getInstance().buddyIsPending(connection, charWithId.getId(), player.getId())).orElse(false);
                  if (alreadyOnList) {
                     buddyAddResult = BuddyAddResult.ALREADY_ON_LIST;
                  }
               }
               if (buddyAddResult == BuddyAddResult.BUDDYLIST_FULL) {
                  c.announce(MaplePacketCreator.serverNotice(1, "\"" + addName + "\"'s Buddylist is full"));
               } else {
                  int displayChannel;
                  displayChannel = -1;
                  int otherCid = charWithId.getId();
                  if (buddyAddResult == BuddyAddResult.ALREADY_ON_LIST && channel != -1) {
                     displayChannel = channel;
                     notifyRemoteChannel(c, channel, otherCid, ADDED);
                  } else if (buddyAddResult != BuddyAddResult.ALREADY_ON_LIST && channel == -1) {
                     DatabaseConnection.withConnection(connection -> BuddyAdministrator.getInstance().addBuddy(connection, charWithId.getId(), player.getId()));
                  }
                  buddylist.put(new BuddylistEntry(charWithId.getName(), group, otherCid, displayChannel, true));
                  c.announce(MaplePacketCreator.updateBuddylist(buddylist.getBuddies()));
               }
            } else {
               c.announce(MaplePacketCreator.serverNotice(1, "A character called \"" + addName + "\" does not exist"));
            }
         } else {
            ble.changeGroup(group);
            c.announce(MaplePacketCreator.updateBuddylist(buddylist.getBuddies()));
         }
      } else if (mode == 2) { // accept buddy
         int otherCid = slea.readInt();
         if (!buddylist.isFull()) {
            int channel = c.getWorldServer().find(otherCid);//worldInterface.find(otherCid);
            Optional<String> otherName = c.getChannelServer().getPlayerStorage().getCharacterById(otherCid)
                  .map(MapleCharacter::getName)
                  .or(() -> Optional.ofNullable(getCharacterNameFromDatabase(otherCid)));

            if (otherName.isPresent()) {
               buddylist.put(new BuddylistEntry(otherName.get(), "Default Group", otherCid, channel, true));
               c.announce(MaplePacketCreator.updateBuddylist(buddylist.getBuddies()));
               notifyRemoteChannel(c, channel, otherCid, ADDED);
            }
         }
         nextPendingRequest(c);
      } else if (mode == 3) { // delete
         int otherCid = slea.readInt();
         player.deleteBuddy(otherCid);
      }
   }

   private String getCharacterNameFromDatabase(int otherCid) {
      return DatabaseConnection.withConnectionResult(connection -> CharacterProvider.getInstance().getNameForId(connection, otherCid)).orElse(null);
   }

   private void notifyRemoteChannel(MapleClient c, int remoteChannel, int otherCid, BuddyOperation operation) {
      MapleCharacter player = c.getPlayer();
      if (remoteChannel != -1) {
         c.getWorldServer().buddyChanged(otherCid, player.getId(), player.getName(), c.getChannel(), operation);
      }
   }
}
