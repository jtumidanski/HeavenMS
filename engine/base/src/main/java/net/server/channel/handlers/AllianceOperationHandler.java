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
import net.opcodes.SendOpcode;
import net.server.AbstractPacketHandler;
import net.server.PacketReader;
import net.server.Server;
import net.server.channel.packet.alliance.AcceptedInvitePacket;
import net.server.channel.packet.alliance.AllianceAlreadyRegisteredPacket;
import net.server.channel.packet.alliance.AllianceInvitePacket;
import net.server.channel.packet.alliance.AllianceMessagePacket;
import net.server.channel.packet.alliance.AllianceNoticePacket;
import net.server.channel.packet.alliance.AllianceOperationPacket;
import net.server.channel.packet.alliance.AlliancePlayerRankDataPacket;
import net.server.channel.packet.alliance.AllianceRankDataPacket;
import net.server.channel.packet.alliance.ChangeAllianceLeaderPacket;
import net.server.channel.packet.alliance.ExpelGuildPacket;
import net.server.channel.packet.alliance.LeaveAlliancePacket;
import net.server.channel.packet.reader.AllianceRegisterOperationReader;
import net.server.channel.packet.reader.ExistingAllianceOperationReader;
import net.server.guild.MapleAlliance;
import net.server.guild.MapleGuild;
import net.server.processor.MapleAllianceProcessor;
import tools.MaplePacketCreator;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.data.output.MaplePacketLittleEndianWriter;
import tools.packet.stat.EnableActions;

/**
 * @author XoticStory, Ronan
 */
public final class AllianceOperationHandler extends AbstractPacketHandler<AllianceOperationPacket> {
   @Override
   public Class<? extends PacketReader<AllianceOperationPacket>> getReaderClass(MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      if (chr.getGuild().isPresent()) {
         if (chr.getAlliance().isPresent()) {
            return ExistingAllianceOperationReader.class;
         } else {
            return AllianceRegisterOperationReader.class;
         }
      }
      return null;
   }

   @Override
   public Class<? extends PacketReader<AllianceOperationPacket>> getReaderClass() {
      return null;
   }

   private static byte[] sendShowInfo(int allianceId, int playerId) {
      MaplePacketLittleEndianWriter writer = new MaplePacketLittleEndianWriter();
      writer.writeShort(SendOpcode.ALLIANCE_OPERATION.getValue());
      writer.write(0x02);
      writer.writeInt(allianceId);
      writer.writeInt(playerId);
      return writer.getPacket();
   }

   @Override
   public void handlePacket(AllianceOperationPacket packet, MapleClient client) {
      MapleCharacter chr = client.getPlayer();
      Optional<MapleGuild> guild = chr.getGuild();
      Optional<MapleAlliance> alliance = chr.getAlliance();

      if (guild.isPresent() && alliance.isEmpty() && packet instanceof AcceptedInvitePacket) {
         acceptInvite(client, chr, ((AcceptedInvitePacket) packet).allianceId());
         return;
      } else {
         if (guild.isPresent() && alliance.isPresent()) {
            existingAllianceOperations(packet, client, chr, alliance.get().getId(), alliance.get());
         }
      }
      PacketCreator.announce(client, new EnableActions());
   }

   private void existingAllianceOperations(AllianceOperationPacket packet, MapleClient client, MapleCharacter chr, int allianceId, MapleAlliance alliance) {
      if (chr.getMGC().getAllianceRank() > 2 || !alliance.getGuilds().contains(chr.getGuildId())) {
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      if (packet instanceof AllianceMessagePacket) {
         Server.getInstance().allianceMessage(alliance.getId(), sendShowInfo(allianceId, chr.getId()), -1, -1);
         alliance.saveToDB();
      } else if (packet instanceof LeaveAlliancePacket) {
         leaveAlliance(allianceId, alliance, chr);
      } else if (packet instanceof AllianceInvitePacket) {
         sendInvite(client, alliance, chr, ((AllianceInvitePacket) packet).guildName());
      } else if (packet instanceof AllianceAlreadyRegisteredPacket) {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Your guild is already registered on a guild alliance.");
         PacketCreator.announce(client, new EnableActions());
      } else if (packet instanceof AllianceNoticePacket) {
         setAllianceNotice(alliance, ((AllianceNoticePacket) packet).notice());
      } else if (packet instanceof AlliancePlayerRankDataPacket) {
         changePlayerAllianceRank(client, alliance, ((AlliancePlayerRankDataPacket) packet).playerId(), ((AlliancePlayerRankDataPacket) packet).rankRaised());
      } else if (packet instanceof AllianceRankDataPacket) {
         changeRanks(alliance, ((AllianceRankDataPacket) packet).ranks());
      } else if (packet instanceof ChangeAllianceLeaderPacket) {
         changeAllianceLeader(client, alliance, chr, ((ChangeAllianceLeaderPacket) packet).playerId());
      } else if (packet instanceof ExpelGuildPacket) {
         expelGuild(client, alliance, allianceId, ((ExpelGuildPacket) packet).guildId(), ((ExpelGuildPacket) packet).allianceId());
      } else {
         MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.NOTICE, "Feature not available");
      }
   }

   private void setAllianceNotice(MapleAlliance alliance, String notice) {
      Server.getInstance().setAllianceNotice(alliance.getId(), notice);
      Server.getInstance().allianceMessage(alliance.getId(), MaplePacketCreator.allianceNotice(alliance.getId(), notice), -1, -1);

      MessageBroadcaster.getInstance().sendAllianceServerNotice(alliance, ServerNoticeType.PINK_TEXT, "* Alliance Notice : " + notice);
      alliance.saveToDB();
   }

   private void changePlayerAllianceRank(MapleClient c, MapleAlliance alliance, int playerId, boolean isRankRaised) {
      //Server.getInstance().allianceMessage(alliance.getId(), sendChangeRank(allianceId, chr.getId(), int1, byte1), -1, -1);
      Server.getInstance().getWorld(c.getWorld()).getPlayerStorage().getCharacterById(playerId)
            .ifPresent(character -> {
               changePlayerAllianceRank(alliance, character, isRankRaised);
               alliance.saveToDB();
            });
   }

   private void changeRanks(MapleAlliance alliance, String[] ranks) {

      Server.getInstance().setAllianceRanks(alliance.getId(), ranks);
      Server.getInstance().allianceMessage(alliance.getId(), MaplePacketCreator.changeAllianceRankTitle(alliance.getId(), ranks), -1, -1);
      alliance.saveToDB();
   }

   private void changeAllianceLeader(MapleClient c, MapleAlliance alliance, MapleCharacter chr, int newLeaderId) {
      if (chr.getGuildId() < 1) {
         return;
      }

      Server.getInstance().getWorld(c.getWorld()).getPlayerStorage().getCharacterById(newLeaderId)
            .filter(character -> character.getAllianceRank() == 2)
            .ifPresent(character -> {
               //Server.getInstance().allianceMessage(alliance.getId(), sendChangeLeader(allianceId, chr.getId(), slea.readInt()), -1, -1);
               changeLeaderAllianceRank(alliance, character);
               alliance.saveToDB();
            });
   }

   private void expelGuild(MapleClient c, MapleAlliance alliance, int allianceId, int guildIdToExpel, int allianceIdForGuild) {
      if (allianceId != allianceIdForGuild) {
         return;
      }


      Server.getInstance().getGuild(guildIdToExpel).ifPresent(guild -> {
         Server.getInstance().allianceMessage(alliance.getId(), MaplePacketCreator.removeGuildFromAlliance(alliance, guildIdToExpel, c.getWorld()), -1, -1);
         Server.getInstance().removeGuildFromAlliance(alliance.getId(), guildIdToExpel);

         Server.getInstance().allianceMessage(alliance.getId(), MaplePacketCreator.getGuildAlliances(alliance, c.getWorld()), -1, -1);
         Server.getInstance().allianceMessage(alliance.getId(), MaplePacketCreator.allianceNotice(alliance.getId(), alliance.getNotice()), -1, -1);
         Server.getInstance().guildMessage(guildIdToExpel, MaplePacketCreator.disbandAlliance(allianceIdForGuild));

         MessageBroadcaster.getInstance().sendAllianceServerNotice(alliance, ServerNoticeType.PINK_TEXT, "[" + guild.getName() + "] guild has been expelled from the union.");
         alliance.saveToDB();
      });
   }

   private void acceptInvite(MapleClient c, MapleCharacter chr, int allianceId) {
      chr.getGuild().ifPresent(guild -> {
         if (guild.getAllianceId() != 0 || chr.getGuildRank() != 1 || chr.getGuildId() < 1) {
            return;
         }

         Server.getInstance().getAlliance(allianceId).ifPresent(alliance -> {
            if (!MapleAllianceProcessor.getInstance().answerInvitation(c.getPlayer().getId(), guild.getName(), alliance.getId(), true)) {
               return;
            }

            if (alliance.getGuilds().size() == alliance.getCapacity()) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Your alliance cannot comport any more guilds at the moment.");
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
            MessageBroadcaster.getInstance().sendGuildServerNotice(guild, ServerNoticeType.PINK_TEXT, "Your guild has joined the [" + alliance.getName() + "] union.");
            alliance.saveToDB();
         });
      });
   }

   private void sendInvite(MapleClient client, MapleAlliance alliance, MapleCharacter character, String guildName) {
      if (alliance.getGuilds().size() == alliance.getCapacity()) {
         MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.PINK_TEXT, "Your alliance cannot comport any more guilds at the moment.");
      } else {
         MapleAllianceProcessor.getInstance().sendInvitation(client, guildName, alliance.getId());
      }
      alliance.saveToDB();
   }

   private void leaveAlliance(int allianceId, MapleAlliance alliance, MapleCharacter chr) {
      if (chr.getGuildId() < 1 || chr.getGuildRank() != 1) {
         return;
      }

      MapleAllianceProcessor.getInstance().removeGuildFromAlliance(allianceId, chr.getGuildId(), chr.getWorld());
      alliance.saveToDB();
   }

   private void changeLeaderAllianceRank(MapleAlliance alliance, MapleCharacter newLeader) {
      newLeader.getWorldServer().getPlayerStorage().getCharacterById(alliance.getLeader().getId()).ifPresent(currentLeader -> {
         currentLeader.getMGC().setAllianceRank(2);
         currentLeader.saveGuildStatus();

         newLeader.getMGC().setAllianceRank(1);
         newLeader.saveGuildStatus();

         Server.getInstance().allianceMessage(alliance.getId(), MaplePacketCreator.getGuildAlliances(alliance, newLeader.getWorld()), -1, -1);
         MessageBroadcaster.getInstance().sendAllianceServerNotice(alliance, ServerNoticeType.PINK_TEXT, "'" + newLeader.getName() + "' has been appointed as the new head of this Alliance.");
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
      MessageBroadcaster.getInstance().sendAllianceServerNotice(alliance, ServerNoticeType.PINK_TEXT, "'" + chr.getName() + "' has been reassigned to '" + alliance.getRankTitle(newRank) + "' in this Alliance.");
   }
}
