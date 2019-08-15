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

import client.MapleCharacter;
import client.MapleClient;
import net.AbstractMaplePacketHandler;
import net.opcodes.SendOpcode;
import net.server.Server;
import net.server.guild.MapleAlliance;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;
import tools.data.output.MaplePacketLittleEndianWriter;

/**
 * @author XoticStory, Ronan
 */
public final class AllianceOperationHandler extends AbstractMaplePacketHandler {

   private static byte[] sendShowInfo(int allianceId, int playerId) {
      MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter();
      writer.writeShort(SendOpcode.ALLIANCE_OPERATION.getValue());
      writer.write(0x02);
      writer.writeInt(allianceId);
      writer.writeInt(playerId);
      return writer.getPacket();
   }

   private static byte[] sendInvitation(int allianceId, int playerId, final String guildName) {
      MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter();
      writer.writeShort(SendOpcode.ALLIANCE_OPERATION.getValue());
      writer.write(0x05);
      writer.writeInt(allianceId);
      writer.writeInt(playerId);
      writer.writeMapleAsciiString(guildName);
      return writer.getPacket();
   }

   private static byte[] sendChangeGuild(int allianceId, int playerId, int guildId, int option) {
      MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter();
      writer.writeShort(SendOpcode.ALLIANCE_OPERATION.getValue());
      writer.write(0x07);
      writer.writeInt(allianceId);
      writer.writeInt(guildId);
      writer.writeInt(playerId);
      writer.write(option);
      return writer.getPacket();
   }

   private static byte[] sendChangeLeader(int allianceId, int playerId, int victim) {
      MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter();
      writer.writeShort(SendOpcode.ALLIANCE_OPERATION.getValue());
      writer.write(0x08);
      writer.writeInt(allianceId);
      writer.writeInt(playerId);
      writer.writeInt(victim);
      return writer.getPacket();
   }

   private static byte[] sendChangeRank(int allianceId, int playerId, int int1, byte byte1) {
      MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter();
      writer.writeShort(SendOpcode.ALLIANCE_OPERATION.getValue());
      writer.write(0x09);
      writer.writeInt(allianceId);
      writer.writeInt(playerId);
      writer.writeInt(int1);
      writer.writeInt(byte1);
      return writer.getPacket();
   }

   @Override
   public final void handlePacket(SeekableLittleEndianAccessor accessor, MapleClient client) {
      MapleCharacter chr = client.getPlayer();

      chr.getGuild().ifPresentOrElse(guild -> {
         byte b = accessor.readByte();
         int allianceId = guild.getAllianceId();
         chr.getAlliance().ifPresentOrElse(
               alliance -> existingAllianceOperations(accessor, client, chr, b, allianceId, alliance),
               () -> registerGuildForAlliance(accessor, client, chr, b));
      }, () -> client.announce(MaplePacketCreator.enableActions()));
   }

   private void registerGuildForAlliance(SeekableLittleEndianAccessor accessor, MapleClient client, MapleCharacter chr, byte b) {
      if (b == 4) {
         acceptInvite(new AllianceAcceptedInviteData(accessor), client, chr);
      } else {
         client.announce(MaplePacketCreator.enableActions());
      }
   }

   private void existingAllianceOperations(SeekableLittleEndianAccessor accessor, MapleClient client, MapleCharacter chr, byte b, int allianceId, MapleAlliance alliance) {
      if (chr.getMGC().getAllianceRank() > 2 || !alliance.getGuilds().contains(chr.getGuildId())) {
         client.announce(MaplePacketCreator.enableActions());
         return;
      }

      // "alliance" is only null at case 0x04
      switch (b) {
         case 0x01:
            Server.getInstance().allianceMessage(alliance.getId(), sendShowInfo(allianceId, chr.getId()), -1, -1);
            alliance.saveToDB();
            break;
         case 0x02:
            leaveAlliance(allianceId, alliance, chr);
            break;
         case 0x03:
            sendInvite(new AllianceInviteData(accessor), client, alliance, chr);
            break;
         case 0x04:
            chr.dropMessage(5, "Your guild is already registered on a guild alliance.");
            client.announce(MaplePacketCreator.enableActions());
            break;
         case 0x06:
            expelGuild(new AllianceGuildExpelData(accessor), client, alliance, allianceId);
            break;
         case 0x07:
            changeAllianceLeader(new AllianceChangeLeaderData(accessor), client, alliance, chr);
            break;
         case 0x08:
            changeRanks(new AllianceRankData(accessor), alliance);
            break;
         case 0x09:
            changePlayerAllianceRank(new AlliancePlayerRankData(accessor), client, alliance);
            break;
         case 0x0A:
            setAllianceNotice(new AllianceNoticeData(accessor), alliance);
            break;
         default:
            chr.dropMessage("Feature not available");
      }
   }

   private void setAllianceNotice(AllianceNoticeData noticeData, MapleAlliance alliance) {
      Server.getInstance().setAllianceNotice(alliance.getId(), noticeData.getNotice());
      Server.getInstance().allianceMessage(alliance.getId(), MaplePacketCreator.allianceNotice(alliance.getId(), noticeData.getNotice()), -1, -1);

      alliance.dropMessage(5, "* Alliance Notice : " + noticeData.getNotice());
      alliance.saveToDB();
   }

   private void changePlayerAllianceRank(AlliancePlayerRankData playerRankData, MapleClient c, MapleAlliance alliance) {
      //Server.getInstance().allianceMessage(alliance.getId(), sendChangeRank(allianceId, chr.getId(), int1, byte1), -1, -1);
      Server.getInstance().getWorld(c.getWorld()).getPlayerStorage().getCharacterById(playerRankData.getPlayerId())
            .ifPresent(character -> {
               changePlayerAllianceRank(alliance, character, playerRankData.isRankRaised());
               alliance.saveToDB();
            });
   }

   private void changeRanks(AllianceRankData rankData, MapleAlliance alliance) {

      Server.getInstance().setAllianceRanks(alliance.getId(), rankData.getRanks());
      Server.getInstance().allianceMessage(alliance.getId(), MaplePacketCreator.changeAllianceRankTitle(alliance.getId(), rankData.getRanks()), -1, -1);
      alliance.saveToDB();
   }

   private void changeAllianceLeader(AllianceChangeLeaderData leaderData, MapleClient c, MapleAlliance alliance, MapleCharacter chr) {
      if (chr.getGuildId() < 1) {
         return;
      }

      Server.getInstance().getWorld(c.getWorld()).getPlayerStorage().getCharacterById(leaderData.getPlayerId())
            .filter(character -> character.getAllianceRank() == 2)
            .ifPresent(character -> {
               //Server.getInstance().allianceMessage(alliance.getId(), sendChangeLeader(allianceId, chr.getId(), slea.readInt()), -1, -1);
               changeLeaderAllianceRank(alliance, character);
               alliance.saveToDB();
            });
   }

   private void expelGuild(AllianceGuildExpelData expelData, MapleClient c, MapleAlliance alliance, int allianceId) {
      if (allianceId != expelData.getAllianceId()) {
         return;
      }


      Server.getInstance().getGuild(expelData.getGuildId()).ifPresent(guild -> {
         Server.getInstance().allianceMessage(alliance.getId(), MaplePacketCreator.removeGuildFromAlliance(alliance, expelData.getGuildId(), c.getWorld()), -1, -1);
         Server.getInstance().removeGuildFromAlliance(alliance.getId(), expelData.getGuildId());

         Server.getInstance().allianceMessage(alliance.getId(), MaplePacketCreator.getGuildAlliances(alliance, c.getWorld()), -1, -1);
         Server.getInstance().allianceMessage(alliance.getId(), MaplePacketCreator.allianceNotice(alliance.getId(), alliance.getNotice()), -1, -1);
         Server.getInstance().guildMessage(expelData.getGuildId(), MaplePacketCreator.disbandAlliance(expelData.getAllianceId()));

         alliance.dropMessage("[" + guild.getName() + "] guild has been expelled from the union.");
         alliance.saveToDB();
      });
   }

   private void acceptInvite(AllianceAcceptedInviteData inviteData, MapleClient c, MapleCharacter chr) {
      chr.getGuild().ifPresent(guild -> {
         if (guild.getAllianceId() != 0 || chr.getGuildRank() != 1 || chr.getGuildId() < 1) {
            return;
         }

         Server.getInstance().getAlliance(inviteData.getAllianceId()).ifPresent(alliance -> {
            if (!MapleAlliance.answerInvitation(c.getPlayer().getId(), guild.getName(), alliance.getId(), true)) {
               return;
            }

            if (alliance.getGuilds().size() == alliance.getCapacity()) {
               chr.dropMessage(5, "Your alliance cannot comport any more guilds at the moment.");
               return;
            }

            int guildId = chr.getGuildId();

            Server.getInstance().addGuildtoAlliance(alliance.getId(), guildId);
            Server.getInstance().resetAllianceGuildPlayersRank(guildId);

            chr.getMGC().setAllianceRank(2);
            guild.getMGC(chr.getId()).setAllianceRank(2);
            chr.saveGuildStatus();

            Server.getInstance().allianceMessage(alliance.getId(), MaplePacketCreator.addGuildToAlliance(alliance, guildId, c), -1, -1);
            Server.getInstance().allianceMessage(alliance.getId(), MaplePacketCreator.updateAllianceInfo(alliance, c.getWorld()), -1, -1);
            Server.getInstance().allianceMessage(alliance.getId(), MaplePacketCreator.allianceNotice(alliance.getId(), alliance.getNotice()), -1, -1);
            guild.dropMessage("Your guild has joined the [" + alliance.getName() + "] union.");
            alliance.saveToDB();
         });
      });
   }

   private void sendInvite(AllianceInviteData inviteData, MapleClient client, MapleAlliance alliance, MapleCharacter character) {
      if (alliance.getGuilds().size() == alliance.getCapacity()) {
         character.dropMessage(5, "Your alliance cannot comport any more guilds at the moment.");
      } else {
         MapleAlliance.sendInvitation(client, inviteData.getGuildName(), alliance.getId());
      }
      alliance.saveToDB();
   }

   private void leaveAlliance(int allianceId, MapleAlliance alliance, MapleCharacter chr) {
      if (chr.getGuildId() < 1 || chr.getGuildRank() != 1) {
         return;
      }

      MapleAlliance.removeGuildFromAlliance(allianceId, chr.getGuildId(), chr.getWorld());
      alliance.saveToDB();
   }

   private void changeLeaderAllianceRank(MapleAlliance alliance, MapleCharacter newLeader) {
      newLeader.getWorldServer().getPlayerStorage().getCharacterById(alliance.getLeader().getId()).ifPresent(currentLeader -> {
         currentLeader.getMGC().setAllianceRank(2);
         currentLeader.saveGuildStatus();

         newLeader.getMGC().setAllianceRank(1);
         newLeader.saveGuildStatus();

         Server.getInstance().allianceMessage(alliance.getId(), MaplePacketCreator.getGuildAlliances(alliance, newLeader.getWorld()), -1, -1);
         alliance.dropMessage("'" + newLeader.getName() + "' has been appointed as the new head of this Alliance.");
      });
   }

   private void changePlayerAllianceRank(MapleAlliance alliance, MapleCharacter chr, boolean raise) {
      int newRank = chr.getAllianceRank() + (raise ? -1 : 1);
      if (newRank < 3 || newRank > 5) {
         return;
      }

      chr.getMGC().setAllianceRank(newRank);
      chr.saveGuildStatus();

      Server.getInstance().allianceMessage(alliance.getId(), MaplePacketCreator.getGuildAlliances(alliance, chr.getWorld()), -1, -1);
      alliance.dropMessage("'" + chr.getName() + "' has been reassigned to '" + alliance.getRankTitle(newRank) + "' in this Alliance.");
   }

   private class AllianceNoticeData {
      private String notice;

      public AllianceNoticeData(String notice) {
         this.notice = notice;
      }

      public AllianceNoticeData(SeekableLittleEndianAccessor accessor) {
         this.notice = accessor.readMapleAsciiString();
      }

      public String getNotice() {
         return notice;
      }
   }

   private class AlliancePlayerRankData {
      private int playerId;

      private boolean rankRaised;

      public AlliancePlayerRankData(int playerId, boolean rankRaised) {
         this.playerId = playerId;
         this.rankRaised = rankRaised;
      }

      public AlliancePlayerRankData(SeekableLittleEndianAccessor accessor) {
         this.playerId = accessor.readInt();
         this.rankRaised = accessor.readByte() > 0;
      }

      public int getPlayerId() {
         return playerId;
      }

      public boolean isRankRaised() {
         return rankRaised;
      }
   }

   private class AllianceRankData {
      private String[] ranks;

      public AllianceRankData(String[] ranks) {
         this.ranks = ranks;
      }

      public AllianceRankData(SeekableLittleEndianAccessor accessor) {
         ranks = new String[5];
         for (int i = 0; i < 5; i++) {
            ranks[i] = accessor.readMapleAsciiString();
         }
      }

      public String[] getRanks() {
         return ranks;
      }
   }

   private class AllianceChangeLeaderData {
      private int playerId;

      public AllianceChangeLeaderData(int playerId) {
         this.playerId = playerId;
      }

      public AllianceChangeLeaderData(SeekableLittleEndianAccessor accessor) {
         this.playerId = accessor.readInt();
      }

      public int getPlayerId() {
         return playerId;
      }
   }

   private class AllianceGuildExpelData {
      private int guildId;

      private int allianceId;

      public AllianceGuildExpelData(int guildId, int allianceId) {
         this.guildId = guildId;
         this.allianceId = allianceId;
      }

      public AllianceGuildExpelData(SeekableLittleEndianAccessor accessor) {
         this.guildId = accessor.readInt();
         this.allianceId = accessor.readInt();
      }

      public int getGuildId() {
         return guildId;
      }

      public int getAllianceId() {
         return allianceId;
      }
   }

   private class AllianceAcceptedInviteData {
      private int allianceId;

      private String guildName;

      public AllianceAcceptedInviteData(int allianceId, String guildName) {
         this.allianceId = allianceId;
         this.guildName = guildName;
      }

      public AllianceAcceptedInviteData(SeekableLittleEndianAccessor accessor) {
         this.allianceId = accessor.readInt();
         //slea.readMapleAsciiString();  //recruiter's guild name
      }

      public int getAllianceId() {
         return allianceId;
      }
   }

   private class AllianceInviteData {
      private String guildName;

      public AllianceInviteData(String guildName) {
         this.guildName = guildName;
      }

      public AllianceInviteData(SeekableLittleEndianAccessor accessor) {
         this.guildName = accessor.readMapleAsciiString();
      }

      public String getGuildName() {
         return guildName;
      }
   }
}
