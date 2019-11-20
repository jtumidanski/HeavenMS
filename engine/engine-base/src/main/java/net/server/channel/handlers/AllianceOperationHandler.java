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
import net.server.processor.MapleGuildProcessor;
import tools.MasterBroadcaster;
import tools.MessageBroadcaster;
import tools.PacketCreator;
import tools.ServerNoticeType;
import tools.packet.alliance.AddGuildToAlliance;
import tools.packet.alliance.AllianceNotice;
import tools.packet.alliance.ChangeAllianceRankTitles;
import tools.packet.alliance.DisbandAlliance;
import tools.packet.alliance.GetAlliancePlayerInfo;
import tools.packet.alliance.GetGuildAlliances;
import tools.packet.alliance.RemoveGuildFromAlliance;
import tools.packet.alliance.UpdateAllianceInfo;
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
            existingAllianceOperations(packet, client, chr, alliance.get().id(), alliance.get());
         }
      }
      PacketCreator.announce(client, new EnableActions());
   }

   private void existingAllianceOperations(AllianceOperationPacket packet, MapleClient client, MapleCharacter chr, int allianceId, MapleAlliance alliance) {
      if (chr.getMGC().getAllianceRank() > 2 || !alliance.guilds().contains(chr.getGuildId())) {
         PacketCreator.announce(client, new EnableActions());
         return;
      }

      if (packet instanceof AllianceMessagePacket) {
         Server.getInstance().allianceMessage(alliance.id(), new GetAlliancePlayerInfo(allianceId, chr.getId()), -1, -1);
         MapleAllianceProcessor.getInstance().saveToDB(alliance);
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
      Server.getInstance().setAllianceNotice(alliance.id(), notice);
      Server.getInstance().allianceMessage(alliance.id(), new AllianceNotice(alliance.id(), notice), -1, -1);

      MessageBroadcaster.getInstance().sendAllianceServerNotice(alliance, ServerNoticeType.PINK_TEXT, "* Alliance Notice : " + notice);
      MapleAllianceProcessor.getInstance().saveToDB(alliance);
   }

   private void changePlayerAllianceRank(MapleClient c, MapleAlliance alliance, int playerId, boolean isRankRaised) {
      //Server.getInstance().allianceMessage(alliance.getId(), sendChangeRank(allianceId, chr.getId(), int1, byte1), -1, -1);
      Server.getInstance().getWorld(c.getWorld()).getPlayerStorage().getCharacterById(playerId)
            .ifPresent(character -> {
               changePlayerAllianceRank(alliance, character, isRankRaised);
               MapleAllianceProcessor.getInstance().saveToDB(alliance);
            });
   }

   private void changeRanks(MapleAlliance alliance, String[] ranks) {

      Server.getInstance().setAllianceRanks(alliance.id(), ranks);
      Server.getInstance().allianceMessage(alliance.id(), new ChangeAllianceRankTitles(alliance.id(), ranks), -1, -1);
      MapleAllianceProcessor.getInstance().saveToDB(alliance);
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
               MapleAllianceProcessor.getInstance().saveToDB(alliance);
            });
   }

   private void expelGuild(MapleClient c, MapleAlliance alliance, int allianceId, int guildIdToExpel, int allianceIdForGuild) {
      if (allianceId != allianceIdForGuild) {
         return;
      }


      Server.getInstance().getGuild(guildIdToExpel).ifPresent(guild -> {
         Server.getInstance().allianceMessage(alliance.id(), new RemoveGuildFromAlliance(alliance, guildIdToExpel, c.getWorld()), -1, -1);
         Server.getInstance().removeGuildFromAlliance(alliance.id(), guildIdToExpel);

         Server.getInstance().allianceMessage(alliance.id(), new GetGuildAlliances(alliance, c.getWorld()), -1, -1);
         Server.getInstance().allianceMessage(alliance.id(), new AllianceNotice(alliance.id(), alliance.notice()), -1, -1);
         MasterBroadcaster.getInstance().sendToGuild(guildIdToExpel, new DisbandAlliance(allianceIdForGuild));

         MessageBroadcaster.getInstance().sendAllianceServerNotice(alliance, ServerNoticeType.PINK_TEXT, "[" + guild.getName() + "] guild has been expelled from the union.");
         MapleAllianceProcessor.getInstance().saveToDB(alliance);
      });
   }

   private void acceptInvite(MapleClient c, MapleCharacter chr, int allianceId) {
      chr.getGuild().ifPresent(guild -> {
         if (guild.getAllianceId() != 0 || chr.getGuildRank() != 1 || chr.getGuildId() < 1) {
            return;
         }

         Server.getInstance().getAlliance(allianceId).ifPresent(alliance -> {
            if (!MapleAllianceProcessor.getInstance().answerInvitation(c.getPlayer().getId(), guild.getName(), alliance.id(), true)) {
               return;
            }

            if (alliance.guilds().size() == alliance.capacity()) {
               MessageBroadcaster.getInstance().sendServerNotice(chr, ServerNoticeType.PINK_TEXT, "Your alliance cannot comport any more guilds at the moment.");
               return;
            }

            int guildId = chr.getGuildId();

            Server.getInstance().addGuildtoAlliance(alliance.id(), guildId);
            MapleGuildProcessor.getInstance().resetAllianceGuildPlayersRank(guildId);

            chr.getMGC().setAllianceRank(2);
            guild.findMember(chr.getId()).ifPresent(guildCharacter -> guildCharacter.setAllianceRank(2));
            chr.saveGuildStatus();

            Server.getInstance().allianceMessage(alliance.id(), new AddGuildToAlliance(alliance, guildId, c.getWorld()), -1, -1);
            Server.getInstance().allianceMessage(alliance.id(), new UpdateAllianceInfo(alliance, c.getWorld()), -1, -1);
            Server.getInstance().allianceMessage(alliance.id(), new AllianceNotice(alliance.id(), alliance.notice()), -1, -1);
            MessageBroadcaster.getInstance().sendGuildServerNotice(guild, ServerNoticeType.PINK_TEXT, "Your guild has joined the [" + alliance.name() + "] union.");
            MapleAllianceProcessor.getInstance().saveToDB(alliance);
         });
      });
   }

   private void sendInvite(MapleClient client, MapleAlliance alliance, MapleCharacter character, String guildName) {
      if (alliance.guilds().size() == alliance.capacity()) {
         MessageBroadcaster.getInstance().sendServerNotice(character, ServerNoticeType.PINK_TEXT, "Your alliance cannot comport any more guilds at the moment.");
      } else {
         MapleAllianceProcessor.getInstance().sendInvitation(client, guildName, alliance.id());
      }
      MapleAllianceProcessor.getInstance().saveToDB(alliance);
   }

   private void leaveAlliance(int allianceId, MapleAlliance alliance, MapleCharacter chr) {
      if (chr.getGuildId() < 1 || chr.getGuildRank() != 1) {
         return;
      }

      MapleAllianceProcessor.getInstance().removeGuildFromAlliance(allianceId, chr.getGuildId(), chr.getWorld());
      MapleAllianceProcessor.getInstance().saveToDB(alliance);
   }

   private void changeLeaderAllianceRank(MapleAlliance alliance, MapleCharacter newLeader) {
      int leaderId = MapleAllianceProcessor.getInstance().getLeader(alliance).getId();
      newLeader.getWorldServer().getPlayerStorage().getCharacterById(leaderId).ifPresent(currentLeader -> {
         currentLeader.getMGC().setAllianceRank(2);
         currentLeader.saveGuildStatus();

         newLeader.getMGC().setAllianceRank(1);
         newLeader.saveGuildStatus();

         Server.getInstance().allianceMessage(alliance.id(), new GetGuildAlliances(alliance, newLeader.getWorld()), -1, -1);
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

      Server.getInstance().allianceMessage(alliance.id(), new GetGuildAlliances(alliance, chr.getWorld()), -1, -1);
      MessageBroadcaster.getInstance().sendAllianceServerNotice(alliance, ServerNoticeType.PINK_TEXT, "'" + chr.getName() + "' has been reassigned to '" + alliance.rankTitle(newRank) + "' in this Alliance.");
   }
}
